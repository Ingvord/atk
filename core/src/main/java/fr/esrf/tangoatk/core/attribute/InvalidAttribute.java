/*
 *  Copyright (C) :	2002,2003,2004,2005,2006,2007,2008,2009
 *			European Synchrotron Radiation Facility
 *			BP 220, Grenoble 38043
 *			FRANCE
 * 
 *  This file is part of Tango.
 * 
 *  Tango is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Tango is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Tango.  If not, see <http://www.gnu.org/licenses/>.
 */
 
// File:          InvalidAttribute.java
// Created:       2002-01-17 16:56:27, assum
// By:            <assum@esrf.fr>
// Time-stamp:    <2002-06-24 18:30:33, assum>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.attribute;
import fr.esrf.Tango.*;
import fr.esrf.TangoApi.events.*;
import fr.esrf.TangoApi.DeviceAttribute;

public class InvalidAttribute extends AAttribute {

    public void setMinValue(double d, boolean b) {;}

    public void setMaxValue(double d, boolean b) {;}

    public void setMinAlarm(double d, boolean b) {;}

    public void setMaxAlarm(double d, boolean b) {;}

    public void refresh() {;}

    public void dispatch(DeviceAttribute attVal) {}

    public void dispatchError(DevFailed e) {}

    // Implement the method of ITangoPeriodicListener
    public void periodic (TangoPeriodicEvent evt) {;}
    // Implement the method of  ITangoChangeListener
    public void change (TangoChangeEvent evt) {;}

    protected void init(fr.esrf.tangoatk.core.Device d, String name,
	AttributeConfig config) {
	this.name = "Invalid(" + name + ")";
    }

    public void insert(String[][] s) { ; }

    public String[][] extract() { return null; }
    
    public String getVersion() {
	return "$Id$";
    }

    public String getName() {
	return "Invalid " + name;
    }

  public boolean isScalar() {
    return false;
  }

  public boolean isSpectrum(){
    return false;
  }

  public boolean isImage(){
    return false;
  }
}
