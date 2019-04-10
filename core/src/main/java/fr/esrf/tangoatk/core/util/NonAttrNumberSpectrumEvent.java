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
 
package fr.esrf.tangoatk.core.util;

/*
 * NonAttrNumberSpectrumEvent.java
 *
 * Created on 12 septembre 2003, 11:02
 */



import java.util.EventObject;


/**
 *
 * @author  OUNSY
 */
public class NonAttrNumberSpectrumEvent extends EventObject {
    
  /**
   * Creates a new instance of NonAttrNumberSpectrumEvent
   * @param source Source
   * @param xvalue X values
   * @param yvalue Y values
   */
    public NonAttrNumberSpectrumEvent(INonAttrNumberSpectrum source, double[] xvalue, double[] yvalue) {
        super(source);
	setValue(xvalue,yvalue);
    }

    public double[] getXValue() {
	return xvalue;
    }

    public double[] getYValue() {
	return yvalue;
    }

    public void setValue(double [] xvalue,double [] yvalue) {
	this.xvalue = xvalue;
	this.yvalue = yvalue;
    }

    public void setSource(INonAttrNumberSpectrum source) {
	this.source = source;
    }

    
    private double [] xvalue;  
    private double [] yvalue;  
}
