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
 *    CrossValidationFoldMakerBeanInfo.java
 *    Copyright (C) 2002-2012 University of Waikato, Hamilton, New Zealand
 *
 */

package com.weka.gui.beans;

import java.beans.BeanDescriptor;
import java.beans.PropertyDescriptor;

/**
 * BeanInfo class for the cross validation fold maker bean
 *
 * @author <a href="mailto:mhall@cs.waikato.ac.nz">Mark Hall</a>
 * @version $Revision$
 */
public class CrossValidationFoldMakerBeanInfo 
  extends AbstractTrainAndTestSetProducerBeanInfo {
  
  /**
   * Return the property descriptors for this bean
   *
   * @return a <code>PropertyDescriptor[]</code> value
   */
  public PropertyDescriptor[] getPropertyDescriptors() {
    try {
      PropertyDescriptor p1;
      PropertyDescriptor p2;
      PropertyDescriptor p3;
      p1 = new PropertyDescriptor("folds", CrossValidationFoldMaker.class);
      p2 = new PropertyDescriptor("seed", CrossValidationFoldMaker.class);
      p3 = new PropertyDescriptor("preserveOrder", CrossValidationFoldMaker.class);
      PropertyDescriptor [] pds = { p1, p2, p3 };
      return pds;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

  /**
   * Return the bean descriptor for this bean
   *
   * @return a <code>BeanDescriptor</code> value
   */
  public BeanDescriptor getBeanDescriptor() {
    return new BeanDescriptor(com.weka.gui.beans.CrossValidationFoldMaker.class,
			      CrossValidationFoldMakerCustomizer.class);
  }
}
