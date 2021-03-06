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
 * TitleUpdatedListener.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 */

package com.weka.gui.scripting.event;

/**
 * Interface for frames/dialogs that listen to changes of the title.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface TitleUpdatedListener {
  
  /**
   * Gets called when the title of the frame/dialog needs updating.
   * 
   * @param event	the event that got sent
   */
  public void titleUpdated(TitleUpdatedEvent event);
}
