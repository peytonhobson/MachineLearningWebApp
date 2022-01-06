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
 *    KnowledgeFlow.java
 *    Copyright (C) 2005-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package com.weka.gui.beans;

import com.weka.core.Copyright;
import com.weka.core.Version;

import java.util.Arrays;
import java.util.List;

/**
 * Startup class for the KnowledgeFlow. Displays a splash screen.
 *
 * @author Mark Hall
 * @version $Revision$
 */
public class KnowledgeFlow {

  /**
   * Static method that can be called from a running program to launch the
   * KnowledgeFlow
   */
  public static void startApp() {
    KnowledgeFlowApp.addStartupListener(new StartUpListener() {
      public void startUpComplete() {
        com.weka.gui.SplashWindow.disposeSplash();
      }
    });

    List<String> message =
      Arrays.asList("WEKA Knowledge Flow", "Version " + Version.VERSION,
        "(c) " + Copyright.getFromYear() + " - " + Copyright.getToYear(),
        "The University of Waikato", "Hamilton, New Zealand");
    com.weka.gui.SplashWindow.splash(
      ClassLoader.getSystemResource("weka/gui/weka_icon_new.png"),
      message);

    Thread nt = new Thread() {
      public void run() {
        com.weka.gui.SplashWindow.invokeMethod("com.weka.gui.beans.KnowledgeFlowApp",
          "createSingleton", null);
      }
    };
    nt.start();
  }

  /**
   * Shows the splash screen, launches the application and then disposes the
   * splash screen.
   * 
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    com.weka.core.logging.Logger.log(com.weka.core.logging.Logger.Level.INFO,
      "Logging started");
    List<String> message =
      Arrays.asList("WEKA Knowledge Flow", "Version " + Version.VERSION,
        "(c) " + Copyright.getFromYear() + " - " + Copyright.getToYear(),
        "The University of Waikato", "Hamilton, New Zealand");
    com.weka.gui.SplashWindow.splash(ClassLoader.
      // getSystemResource("com.weka/gui/beans/icons/splash.jpg"));
      getSystemResource("weka/gui/weka_icon_new.png"), message);
    com.weka.gui.SplashWindow.invokeMain("com.weka.gui.beans.KnowledgeFlowApp", args);
    com.weka.gui.SplashWindow.disposeSplash();
  }

}
