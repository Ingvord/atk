// File:          DevStateScalar.java
// Created:       2005-02-03 10:45:00, poncet
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

public class DevStateScalar extends AAttribute
  implements IDevStateScalar {

  DevStateScalarHelper   devStateHelper;
  String                 devStateValue = null;

  public DevStateScalar()
  {
    devStateHelper = new DevStateScalarHelper(this);
  }


  public int getXDimension() {
    return 1;
  }

  public int getMaxXDimension() {
    return 1;
  }
  
  
  public String getValue()
  {
     return devStateValue;
  }
  
  
  public String getDeviceValue()
  {
      String readVal;
      try
      {
	  readVal = fr.esrf.tangoatk.core.Device.toString(readValueFromNetwork().extractState());
	  devStateValue = readVal;
      }
      catch (DevFailed e)
      {
	  // Fire error event
	  readAttError(e.getMessage(), new AttributeReadException(e));
      }
      catch (Exception e)
      {
	  // Code failure
	  System.out.println("DevStateScalar.getDeviceValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("DevStateScalar.getDeviceValue()------------------------------------------------");
      } // end of catch

      return devStateValue;
  }


  public void refresh()
  {
      DeviceAttribute           att = null;
      
      
      if (skippingRefresh) return;
      try 
      {
	  try 
	  {
	      // Read the attribute from device cache (readValueFromNetwork)
	      att = readValueFromNetwork();
	      if (att == null) return;
	      
	      // Retreive the read value for the attribute
	      devStateValue = fr.esrf.tangoatk.core.Device.toString(att.extractState());
	      
	      // Fire valueChanged
	      fireValueChanged(devStateValue);
	  }
	  catch (DevFailed e)
	  {
	      // Fire error event
	      readAttError(e.getMessage(), new AttributeReadException(e));
	  }
      }
      catch (Exception e)
      {
	  // Code failure
	  System.out.println("DevStateScalar.refresh() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("DevStateScalar.refresh()------------------------------------------------");
      }
  }
  
  
  

  public boolean isWritable()
  {
    return super.isWritable();
  }

  protected void fireValueChanged(String newValue) {
    devStateHelper.fireValueChanged(newValue, timeStamp);
  }

  public void addDevStateScalarListener(IDevStateScalarListener l) {
    devStateHelper.addDevStateScalarListener(l);
    addStateListener(l);
  }

  public void removeDevStateScalarListener(IDevStateScalarListener l) {
    devStateHelper.removeDevStateScalarListener(l);
    removeStateListener(l);
  }



  public String getDevStateValue()
  {
      return devStateValue;
  }
  
  
  
  // Implement the method of ITangoPeriodicListener
  public void periodic (TangoPeriodicEvent evt) 
  {
      DeviceAttribute     da=null;
//System.out.println("DevStateScalar.periodic() called for : " + getName() );
      
      try
      {
          da = evt.getValue();
      }
      catch (DevFailed  dfe)
      {
//System.out.println("DevStateScalar.periodic() caught DevFailed for : " + getName());
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
//System.out.println("DevStateScalar.periodic() caught heartbeat DevFailed : " + getName());
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
//System.out.println("DevStateScalar.periodic() caught other DevFailed : " + getName() );
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
	  System.out.println("DevStateScalar.periodic.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("DevStateScalar.periodic.getValue()------------------------------------------------");
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
              devStateValue = fr.esrf.tangoatk.core.Device.toString(da.extractState());
              // Fire valueChanged
              fireValueChanged(devStateValue);
          }
	  catch (DevFailed dfe)
	  {
              // Tango error
              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
          }
	  catch (Exception e) // Code failure
          {
              System.out.println("DevStateScalar.periodic: Device.toString(extractState()) Exception caught ------------------------------");
              e.printStackTrace();
              System.out.println("DevStateScalar.periodic: Device.toString(extractState())------------------------------------------------");
          } // end of catch
      }
      
  }
 
  
  
  
  // Implement the method of ITangoChangeListener
  public void change (TangoChangeEvent evt) 
  {
      DeviceAttribute     da=null;
//System.out.println("DevStateScalar.change() called for : " + getName() );
      
      try
      {
          da = evt.getValue();
      }
      catch (DevFailed  dfe)
      {
//System.out.println("DevStateScalar.change() caught DevFailed for : " + getName());
          if (dfe.errors[0].reason.equals("API_EventTimeout")) //heartbeat error
	  {
//System.out.println("DevStateScalar.change() caught heartbeat DevFailed : " + getName());
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
	  else // For the moment the behaviour for all DevFailed is the same
	  {
//System.out.println("DevStateScalar.change() caught other DevFailed : " + getName() );
	      // Tango error
	      // Fire error event
	      readAttError(dfe.getMessage(), new AttributeReadException(dfe));
	  }
          return;
      }
      catch (Exception e) // Code failure
      {
	  System.out.println("DevStateScalar.change.getValue() Exception caught ------------------------------");
	  e.printStackTrace();
	  System.out.println("DevStateScalar.change.getValue()------------------------------------------------");
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
              devStateValue = fr.esrf.tangoatk.core.Device.toString(da.extractState());
              // Fire valueChanged
              fireValueChanged(devStateValue);
          }
	  catch (DevFailed dfe)
	  {
              // Tango error
              // Fire error event
              readAttError(dfe.getMessage(), new AttributeReadException(dfe));
          }
	  catch (Exception e) // Code failure
          {
              System.out.println("DevStateScalar.change: Device.toString(extractState()) Exception caught ------------------------------");
              e.printStackTrace();
              System.out.println("DevStateScalar.change: Device.toString(extractState())------------------------------------------------");
          } // end of catch
      }
      
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


}
