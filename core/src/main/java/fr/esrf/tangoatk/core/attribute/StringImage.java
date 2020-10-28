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
 
// File:          StringImage.java
// Created:       2007-05-03 10:46:10, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       

package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;

import fr.esrf.Tango.*;
import fr.esrf.TangoApi.*;
import fr.esrf.TangoApi.events.*;

public class StringImage extends AAttribute implements IStringImage
{

  StringImageHelper    imageHelper;
  String[][]           imageValue = null;

  public StringImage()
  {
    imageHelper = new StringImageHelper(this);
  }



  public String[][] getValue()
  {
      return imageValue;
  }



  public void setValue(String[][] sImage)
  {
      try
      {
          DeviceAttribute da = new DeviceAttribute(getNameSansDevice());
          checkDimensions(sImage);
          imageHelper.insert(da,sImage);
          writeAtt(da);
          imageHelper.fireImageValueChanged(sImage, System.currentTimeMillis());
      }
      catch (DevFailed df)
      {
	  setAttError("Couldn't set value", new AttributeSetException(df));
      }
      catch (Exception ex)
      {
	  setAttError("Couldn't set value", new ATKException(ex));
      }
  }

  public void refresh()
  {
      DeviceAttribute att = null;
//      if (skippingRefresh) return;
      refreshCount++;
      try
      {
          try
          {
              // Read the attribute from device cache (readValueFromNetwork)
              att = readValueFromNetwork();
              if (att == null)
              {
                  return;
              }

              // Retreive the read value for the attribute
              imageValue = imageHelper.getStringImageValue(att);

              // Fire valueChanged
              fireValueChanged(imageValue);
          }
          catch (DevFailed e)
          {
              // Fire error event
              readAttError(e.getMessage(), new AttributeReadException(e));
          }
          catch (java.lang.Error err)
          {
              // Fire error event
              readAttError(err.getMessage(), new AttributeReadException(err));
          }
      }
      catch (Throwable th)
      {
          // Code failure
          System.out.println("StringImage.refresh() Throwable caught ------------------------------");
          th.printStackTrace();
          System.out.println("StringImage.refresh()------------------------------------------------");
      }
  }

  public void dispatch(DeviceAttribute attValue) {

//    if (skippingRefresh) return;
    refreshCount++;
    try {
      try {
        // symetric with refresh
        if (attValue == null) return;
        attribute = attValue;

        setState(attValue);
        timeStamp = attValue.getTimeValMillisSec();        

        // Retreive the read value for the attribute
        imageValue = imageHelper.getStringImageValue(attValue);

        // Fire valueChanged
        fireValueChanged(imageValue);
      } catch (DevFailed e) {

        dispatchError(e);

      }
    } catch (Exception e) {
      // Code failure
      System.out.println("StringImage.dispatch() Exception caught ------------------------------");
      e.printStackTrace();
      System.out.println("StringImage.dispatch()------------------------------------------------");
    }

  }

  public void dispatchError(DevFailed e) {

    imageValue = null;
    // Fire error event
    readAttError(e.getMessage(), new AttributeReadException(e));

  }


  public boolean isWritable()
  {
    return super.isWritable();
  }

  protected void fireValueChanged(String[][] newValue) {
    imageHelper.fireImageValueChanged(newValue, timeStamp);
  }

  public void addStringImageListener(IStringImageListener l) {
    imageHelper.addStringImageListener(l);
    addStateListener(l);
  }

  public void removeStringImageListener(IStringImageListener l) {
    imageHelper.removeStringImageListener(l);
    removeStateListener(l);
  }
  
  
  
  // Implement the method of ITangoPeriodicListener
  public void periodic (TangoPeriodicEvent evt) 
  {
      periodicCount++;
      if(evt.isZmqEvent()) eventType=2; else eventType=1;
      DeviceAttribute     da=null;
      long t0 = System.currentTimeMillis();

      trace(DeviceFactory.TRACE_PERIODIC_EVENT, "StringImage.periodic method called for " + getName(), t0);
      try
      {
          da = evt.getValue();
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "StringImage.periodicEvt.getValue(" + getName() + ") success", t0);
      }
      catch (DevFailed  dfe)
      {
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "StringImage.periodicEvt.getValue(" + getName() + ") failed, caught DevFailed", t0);
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
              trace(DeviceFactory.TRACE_PERIODIC_EVENT, "StringImage.periodicEvt.getValue(" + getName() + ") failed, got heartbeat error", t0);
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
              trace(DeviceFactory.TRACE_PERIODIC_EVENT, "StringImage.periodicEvt.getValue(" + getName() + ") failed, got other error", t0);
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
          trace(DeviceFactory.TRACE_PERIODIC_EVENT, "StringImage.periodicEvt.getValue(" + getName() + ") failed, caught Exception, code failure", t0);
	  System.out.println("StringImage.periodic.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("StringImage.periodic.getValue()------------------------------------------------");
          return;
      } // end of catch


      // read the attribute value from the received event!      
      if (da != null)
      {
          try
	  {
              setState(da); // To set the quality factor and fire AttributeState event
              attribute = da;
              timeStamp = da.getTimeValMillisSec();
	      // Retreive the read value for the attribute
	      imageValue = imageHelper.getStringImageValue(da);

	      // Fire valueChanged
	      fireValueChanged(imageValue);
          }
	  catch (DevFailed dfe)
	  {
              // Tango error
              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
          }
	  catch (Exception e) // Code failure
          {
              System.out.println("StringImage.periodic.extractString() Exception caught ------------------------------");
              e.printStackTrace();
              System.out.println("StringImage.periodic.extractString()------------------------------------------------");
          } // end of catch
      }
      
  }
 
  
  
  
  // Implement the method of ITangoChangeListener
  public void change (TangoChangeEvent evt) 
  {
      changeCount++;
      if(evt.isZmqEvent()) eventType=2; else eventType=1;
      DeviceAttribute     da=null;
      long t0 = System.currentTimeMillis();

      trace(DeviceFactory.TRACE_CHANGE_EVENT, "StringImage.change method called for " + getName(), t0);
      try
      {
          da = evt.getValue();
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "StringImage.changeEvt.getValue(" + getName() + ") success", t0);
      }
      catch (DevFailed  dfe)
      {
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "StringImage.changeEvt.getValue(" + getName() + ") failed, caught DevFailed", t0);
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
              trace(DeviceFactory.TRACE_CHANGE_EVENT, "StringImage.changeEvt.getValue(" + getName() + ") failed, got heartbeat error", t0);
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
              trace(DeviceFactory.TRACE_CHANGE_EVENT, "StringImage.changeEvt.getValue(" + getName() + ") failed, got other error", t0);
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
          trace(DeviceFactory.TRACE_CHANGE_EVENT, "StringImage.changeEvt.getValue(" + getName() + ") failed, caught Exception, code failure", t0);
	  System.out.println("StringImage.change.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("StringImage.change.getValue()------------------------------------------------");
          return;
      } // end of catch


      // read the attribute value from the received event!      
      if (da != null)
      {
          try
	  {
              setState(da); // To set the quality factor and fire AttributeState event
              attribute = da;
              timeStamp = da.getTimeValMillisSec();
	      // Retreive the read value for the attribute
	      imageValue = imageHelper.getStringImageValue(da);

	      // Fire valueChanged
	      fireValueChanged(imageValue);
          }
	  catch (DevFailed dfe)
	  {
              // Tango error
              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
          }
	  catch (Exception e) // Code failure
          {
              System.out.println("StringImage.change.extractString() Exception caught ------------------------------");
              e.printStackTrace();
              System.out.println("StringImage.change.extractString()------------------------------------------------");
          } // end of catch
      }
      
  }


  private void trace(int level,String msg,long time)
  {
    DeviceFactory.getInstance().trace(level,msg,time);
  }
  
  
  

  public String getVersion() {
    return "$Id$";
  }

  private void readObject(java.io.ObjectInputStream in)
    throws java.io.IOException, ClassNotFoundException {
    System.out.print("Loading attribute ");
    in.defaultReadObject();
    serializeInit();
  }

  public boolean isScalar() {
    return false;
  }

  public boolean isSpectrum(){
    return false;
  }

  public boolean isImage(){
    return true;
  }

}
