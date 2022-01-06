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
 *    KFGraphicalEnvironmentCommandHandler.java
 *    Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 *
 */

package com.weka.gui.knowledgeflow;

import com.weka.core.PluginManager;
import com.weka.core.WekaException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Default Knowledge Flow graphical command handler
 *
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 * @version $Revision: $
 */
public class KFGraphicalEnvironmentCommandHandler implements
  GraphicalEnvironmentCommandHandler {

  /** The main perspective */
  protected MainKFPerspective m_mainPerspective;

  /** All availabel commands */
  protected Map<String, AbstractGraphicalCommand> m_commands = new HashMap<>();

  static {
    // register built-in commands
    PluginManager.addPlugin("com.weka.gui.knowledgeflow.AbstractGraphicalCommand",
      "com.weka.gui.knowledgeflow.GetPerspectiveNamesGraphicalCommand",
      "com.weka.gui.knowledgeflow.GetPerspectiveNamesGraphicalCommand", true);
    PluginManager.addPlugin("com.weka.gui.knowledgeflow.AbstractGraphicalCommand",
      "com.weka.gui.knowledgeflow.SendToPerspectiveGraphicalCommand",
      "com.weka.gui.knowledgeflow.SendToPerspectiveGraphicalCommand", true);
  }

  /**
   * Constructor
   *
   * @param mainPerspective the main perspective of the GUI
   */
  public KFGraphicalEnvironmentCommandHandler(MainKFPerspective mainPerspective) {
    m_mainPerspective = mainPerspective;

    // plugin commands
    Set<String> commands =
      PluginManager
        .getPluginNamesOfType("com.weka.gui.knowledgeflow.AbstractGraphicalCommand");
    try {
      for (String commandClass : commands) {
        AbstractGraphicalCommand impl =
          (AbstractGraphicalCommand) PluginManager.getPluginInstance(
            "com.weka.gui.knowledgeflow.AbstractGraphicalCommand", commandClass);
        m_commands.put(impl.getCommandName(), impl);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Perform a command
   *
   * @param commandName the name of the command to execute
   * @param commandArgs the optional arguments
   * @param <T> the type of the return value
   * @return a result, or null if the command does not return a result
   * @throws WekaException if a problem occurs
   */
  @Override
  public <T> T performCommand(String commandName, Object... commandArgs)
    throws WekaException {

    AbstractGraphicalCommand command = m_commands.get(commandName);
    if (command != null) {
      command.setGraphicalEnvironment(m_mainPerspective);
      return command.performCommand(commandArgs);
    } else {
      throw new WekaException("Unknown graphical command '" + commandName + "'");
    }
  }
}
