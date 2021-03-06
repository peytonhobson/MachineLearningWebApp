/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *    Job.java
 *    Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 *
 */

package com.weka.knowledgeflow.steps;

import com.weka.core.Environment;
import com.weka.core.OptionMetadata;
import com.weka.core.Settings;
import com.weka.core.WekaException;
import com.weka.gui.FilePropertyMetadata;
import com.weka.gui.knowledgeflow.KFGUIConsts;
import com.weka.knowledgeflow.BaseExecutionEnvironment;
import com.weka.knowledgeflow.Data;
import com.weka.knowledgeflow.Flow;
import com.weka.knowledgeflow.FlowExecutor;
import com.weka.knowledgeflow.FlowRunner;
import com.weka.knowledgeflow.JSONFlowLoader;
import com.weka.knowledgeflow.JobEnvironment;
import com.weka.knowledgeflow.KFDefaults;
import com.weka.knowledgeflow.LogManager;
import com.weka.knowledgeflow.LoggingLevel;
import com.weka.knowledgeflow.StepManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Step that executes another flow as a "job". Typically, you would parameterize
 * the flow to be executed with variables (in steps that support variables) and
 * then use a a data source connected to a SetVariables step to change the
 * values of the variables dynamically at runtime. For example, a DataGrid could
 * be used to define a set of instances with a string variable containing paths
 * to ARFF files to process; SetVariables can be used to transfer these file
 * paths from the incoming instances generated by DataGrid to the values of
 * variables; then the Job step can execute it's sub-flow for each configuration
 * of variables received, thus processing a different ARFF file (if the subflow
 * uses an ArffLoader step).
 * 
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 * @version $Revision: $
 */
@KFStep(name = "Job", category = "Flow",
  toolTipText = "Execute a flow as a 'job' and wait for it to finish",
  iconPath = KFGUIConsts.BASE_ICON_PATH + "Job.gif")
public class Job extends BaseStep {
  private static final long serialVersionUID = -8684065684979500325L;

  protected File m_flowToRun = new File("--NONE--");

  protected transient FlowExecutor m_flowExecutor;

  protected LoggingLevel m_logLevel = LoggingLevel.BASIC;

  @FilePropertyMetadata(fileChooserDialogType = KFGUIConsts.OPEN_DIALOG,
    directoriesOnly = false)
  @OptionMetadata(displayName = "Flow file",
    description = "The flow to execute", displayOrder = 0)
  public void setFlowFile(File flowFile) {
    m_flowToRun = flowFile;
  }

  public File getFlowFile() {
    return m_flowToRun;
  }

  @Override
  public void stepInit() throws WekaException {
    String fileName = m_flowToRun.toString();
    if (fileName.equals("--NONE--")) {
      throw new WekaException("No flow to execute specified!");
    }
    fileName = getStepManager().environmentSubstitute(fileName);
    m_flowExecutor =
      ((BaseExecutionEnvironment) getStepManager().getExecutionEnvironment())
        .getDefaultFlowExecutor();
    Settings settings = new Settings("weka", KFDefaults.APP_ID);
    try {
      settings.loadSettings();
    } catch (IOException ex) {
      throw new WekaException(ex);
    }
    settings.applyDefaults(new KFDefaults());
    m_flowExecutor.setSettings(settings);
    // setting it on the flow executor
    Environment env =
      new Environment(getStepManager().getExecutionEnvironment()
        .getEnvironmentVariables());
    m_flowExecutor.getExecutionEnvironment().setEnvironmentVariables(env);
    Flow flowToRun = null;
    if (new File(fileName).exists()) {
      flowToRun = Flow.loadFlow(new File(fileName), getStepManager().getLog());
    } else {
      String fileNameWithCorrectSeparators =
        fileName.replace(File.separatorChar, '/');

      if (this.getClass().getClassLoader()
        .getResource(fileNameWithCorrectSeparators) != null) {
        flowToRun = Flow.loadFlow(
          this.getClass().getClassLoader()
            .getResourceAsStream(fileNameWithCorrectSeparators),
          new JSONFlowLoader());
      }
    }
    m_flowExecutor.setFlow(flowToRun);
    final String flowToRunName = flowToRun.getFlowName();
    m_flowExecutor.setLogger(new FlowRunner.SimpleLogger() {
      @Override
      public void logMessage(String lm) {
        if (lm.contains("[Low]")) {
          getStepManager().logLow(
            lm.replace("[Low]", "<sub-flow:" + flowToRunName + ">"));
        } else if (lm.contains("[Basic]")) {
          getStepManager().logBasic(
            lm.replace("[Basic]", "<sub-flow:" + flowToRunName + ">"));
        } else if (lm.contains("[Detailed]")) {
          getStepManager().logDetailed(
            lm.replace("[Detailed]", "<sub-flow:" + flowToRunName + ">"));
        } else if (lm.contains("[Debugging]")) {
          getStepManager().logDebug(
            lm.replace("[Debugging]", "<sub-flow:" + flowToRunName + ">"));
        } else if (lm.contains("[Warning]")) {
          getStepManager().logWarning(
            lm.replace("[Warning]", "<sub-flow:" + flowToRunName + ">"));
        } else {
          getStepManager().logBasic("<sub-flow>" + lm);
        }
      }
    });
  }

  @Override
  public void start() throws WekaException {
    if (getStepManager().numIncomingConnections() == 0) {
      getStepManager().logBasic("Launching as a start point");
      runFlow(null, null, null);
    }
  }

  @Override
  public void processIncoming(Data data) throws WekaException {
    if (!getStepManager().isStreamFinished(data)) {
      Map<String, String> varsToSet =
        data.getPayloadElement(StepManager.CON_AUX_DATA_ENVIRONMENT_VARIABLES);
      Map<String, Map<String, String>> propsToSet =
        data.getPayloadElement(StepManager.CON_AUX_DATA_ENVIRONMENT_PROPERTIES);
      Map<String, LinkedHashSet<Data>> results =
        data.getPayloadElement(StepManager.CON_AUX_DATA_ENVIRONMENT_RESULTS);
      if (varsToSet != null) {
        getStepManager().logBasic(
          "Received variables (" + varsToSet.size() + " key-value pairs)");
      }
      if (propsToSet != null) {
        getStepManager().logBasic(
          "Received properties (" + propsToSet.size() + " target steps)");
      }
      if (results != null) {
        getStepManager()
          .logBasic(
            "Received results containing " + results.size()
              + " connection types");
      }
      getStepManager().logBasic("Launching sub-flow");
      runFlow(varsToSet, propsToSet, results);
    }
  }

  /**
   * Run the sub-flow using the supplied environment variables (if any)
   *
   * @param varsToSet variables to set before executing the sub-flow. Can be
   *          null.
   * @param propsToSet property values for target steps (only scheme-based steps
   *          can be targets)
   * @param results results (if any) to pass in to the sub-flow
   * @throws WekaException if a problem occurs
   */
  protected void runFlow(Map<String, String> varsToSet,
    Map<String, Map<String, String>> propsToSet,
    Map<String, LinkedHashSet<Data>> results) throws WekaException {
    getStepManager().processing();

    JobEnvironment env =
      new JobEnvironment(getStepManager().getExecutionEnvironment()
        .getEnvironmentVariables());
    m_flowExecutor.getExecutionEnvironment().setEnvironmentVariables(env);
    if (varsToSet != null) {
      for (Map.Entry<String, String> e : varsToSet.entrySet()) {
        env.addVariable(e.getKey(), e.getValue());
      }
    }
    if (propsToSet != null) {
      env.addToStepProperties(propsToSet);
    }
    if (results != null) {
      env.addAllResults(results);
    }

    getStepManager().statusMessage(
      "Executing flow '" + m_flowExecutor.getFlow().getFlowName() + "'");
    try {
      m_flowExecutor.runParallel();
      m_flowExecutor.waitUntilFinished();
      // just give the executor a bit longer in order to
      // complete shutdown of executor services
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        // ignore
      }
      Data success =
        new Data(StepManager.CON_JOB_SUCCESS, getName()
          + " completed successfully.");
      success.setPayloadElement(StepManager.CON_AUX_DATA_IS_INCREMENTAL, true);
      if (varsToSet != null) {
        success.setPayloadElement(
          StepManager.CON_AUX_DATA_ENVIRONMENT_VARIABLES, varsToSet);
      }

      if (propsToSet != null) {
        success.setPayloadElement(
          StepManager.CON_AUX_DATA_ENVIRONMENT_PROPERTIES, propsToSet);
      }

      JobEnvironment flowEnv =
        (JobEnvironment) m_flowExecutor.getExecutionEnvironment()
          .getEnvironmentVariables();

      if (flowEnv.getResultData() != null) {
        success.setPayloadElement(StepManager.CON_AUX_DATA_ENVIRONMENT_RESULTS,
          flowEnv.getResultData());
      }

      getStepManager().outputData(success);
    } catch (WekaException ex) {
      Data failure =
        new Data(StepManager.CON_JOB_FAILURE, LogManager.stackTraceToString(ex));
      failure.setPayloadElement(StepManager.CON_AUX_DATA_IS_INCREMENTAL, true);
      if (varsToSet != null) {
        failure.setPayloadElement(
          StepManager.CON_AUX_DATA_ENVIRONMENT_VARIABLES, varsToSet);
      }

      if (propsToSet != null) {
        failure.setPayloadElement(
          StepManager.CON_AUX_DATA_ENVIRONMENT_PROPERTIES, propsToSet);
      }

      getStepManager().outputData(failure);
    }
    getStepManager().finished();
  }

  @Override
  public List<String> getIncomingConnectionTypes() {
    List<String> result = new ArrayList<>();
    if (getStepManager().numIncomingConnections() == 0) {
      return Arrays.asList(StepManager.CON_ENVIRONMENT,
        StepManager.CON_JOB_SUCCESS, StepManager.CON_JOB_FAILURE);
    }

    return result;
  }

  @Override
  public List<String> getOutgoingConnectionTypes() {
    return Arrays.asList(StepManager.CON_JOB_SUCCESS,
      StepManager.CON_JOB_FAILURE);
  }

  /**
   * Get the custom editor for this step
   *
   * @return the fully qualified class name of the clustom editor for this step
   */
  @Override
  public String getCustomEditorForStep() {
    return "com.weka.gui.knowledgeflow.steps.JobStepEditorDialog";
  }
}
