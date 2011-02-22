/*
 *  Copyright (C) :	2009
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
 
// File:          ULongSpectrumHelper.java
// Created:       2009-03-11 11:06:28, poncet
// By:            Faranguiss PONCET
//
// $Id$
//
package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;


import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;

public class ULongSpectrumHelper extends ANumberSpectrumHelper
{

  public ULongSpectrumHelper(IAttribute attribute)
  {
     init(attribute);
  }


  void insert(double[] d)
  {
     double   dUnitFactor=1.0;
     long[]   tmp = new long[d.length];
      
     dUnitFactor = this.attribute.getDisplayUnitFactor();
     DeviceAttribute da = this.attribute.getAttribute();
      
     for (int i = 0; i < tmp.length; i++)
        tmp[i] = (long) (d[i] / dUnitFactor);

     da.insert_ul(tmp);
  }


  void setMinAlarm(double d)
  {
     setProperty("min_alarm", new Long((long) d));
  }

  void setMaxAlarm(double d)
  {
     setProperty("max_alarm", new Long((long) d));
  }

  void setMinValue(double d)
  {
     setProperty("min_value", new Long((long) d));
  }

  void setMaxValue(double d)
  {
     setProperty("max_value", new Long((long) d));
  }

  void setMinWarning(double d)
  {
     setProperty("min_warning", new Long((long) d));
  }

  void setMaxWarning(double d)
  {
     setProperty("max_warning", new Long((long) d));
  }

  void setDeltaT(double d)
  {
     setProperty("delta_t", new Long((long) d));
  }

  void setDeltaVal(double d)
  {
     setProperty("delta_val", new Long((long) d));
  }

  void setMinWarning(double d, boolean writable)
  {
     setProperty("min_warning", new Long((long) d), writable);
  }

  void setMaxWarning(double d, boolean writable)
  {
     setProperty("max_warning", new Long((long) d), writable);
  }

  void setDeltaT(double d, boolean writable)
  {
     setProperty("delta_t", new Long((long) d), writable);
  }

  void setDeltaVal(double d, boolean writable)
  {
     setProperty("delta_val", new Long((long) d), writable);
  }

  void setMinAlarm(double d, boolean writable)
  {
     setProperty("min_alarm", new Long((long) d), writable);
  }

  void setMaxAlarm(double d, boolean writable)
  {
     setProperty("max_alarm", new Long((long) d), writable);
  }

  void setMinValue(double d, boolean writable)
  {
     setProperty("min_value", new Long((long) d), writable);
  }

  void setMaxValue(double d, boolean writable)
  {
     setProperty("max_value", new Long((long) d), writable);
  }

  double[] getNumberSpectrumValue(DeviceAttribute da) throws DevFailed
  {
      long[]     tmp = da.extractULongArray();
      int        nbReadElements = da.getNbRead();
      double[]   retval = new double[nbReadElements];
      for (int i = 0; i < nbReadElements; i++)
          retval[i] = (double) tmp[i];

      return retval;
  }

  double[] getNumberSpectrumSetPoint(DeviceAttribute da) throws DevFailed
  {
      long[]     tmp = da.extractULongArray();
      int        nbReadElements = da.getNbRead();
      int        nbSetElements = tmp.length - nbReadElements;
      
      // The attributes WRITE (WRITE ONLY) return their setPoint in the first sequence of elements
      // In all cases when no "set" element sequence is returned, return the read elements for setPoint
      if (nbSetElements <= 0)
      {
          return getNumberSpectrumValue(da);
      }
      else
      {
         double[] retval = new double[nbSetElements];
         int j = 0;
         for (int i = nbReadElements; i < tmp.length; i++)
         {
             retval[j] = (double) tmp[i];
             j++;
         }
         return retval;        
      }
  }

  double[] getNumberSpectrumDisplayValue(DeviceAttribute da) throws DevFailed
  {
      long[]    tmp;
      double    dUnitFactor;
      double[]  retSpectVal;

      tmp = da.extractULongArray();
      dUnitFactor = this.attribute.getDisplayUnitFactor();
      int   nbReadElements = da.getNbRead();
      retSpectVal = new double[nbReadElements];
     
      for (int i = 0; i < nbReadElements; i++)
         retSpectVal[i] = (double) tmp[i] * dUnitFactor; //return the value in the display unit

      return retSpectVal;
  }
  
  
  double[] getNumberSpectrumDisplaySetPoint(DeviceAttribute da) throws DevFailed
  {
      double     dUnitFactor;
      long[]     tmp = da.extractULongArray();
      int        nbReadElements = da.getNbRead();
      int        nbSetElements = tmp.length - nbReadElements;

      dUnitFactor = this.attribute.getDisplayUnitFactor();
      
      // The attributes WRITE (WRITE ONLY) return their setPoint in the first elements
      // In all cases when no "set" element sequence is returned, return the read elements for setPoint
      if (nbSetElements <= 0)
      {
          return getNumberSpectrumDisplayValue(da);
      }
      else
      {
         double[] retval = new double[nbSetElements];
         int j = 0;
         for (int i = nbReadElements; i < tmp.length; i++)
         {
             retval[j] = (double) tmp[i] * dUnitFactor; //return the value in the display unit
             j++;
         }
         return retval;        
      }
  }

  public String getVersion()
  {
     return "$Id$";
  }

}
