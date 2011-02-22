// File:          StringSpectrumHelper.java
// Created:       2003-12-11 18:00:00, poncet
// By:            <poncet@esrf.fr>
//
// $Id$
//
// Description:

package fr.esrf.tangoatk.core.attribute;

import fr.esrf.tangoatk.core.*;

import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.DeviceAttribute;

public class StringSpectrumHelper implements java.io.Serializable
{
  IAttribute attribute;
  EventSupport propChanges;

  public StringSpectrumHelper(IAttribute attribute)
  {
    init(attribute);
  }

  void init(IAttribute attribute)
  {
    setAttribute(attribute);
    propChanges = ((AAttribute) attribute).getPropChanges();
  }
  
  
  public void setAttribute(IAttribute attribute)
  {
    this.attribute = attribute;
  }

  public IAttribute getAttribute()
  {
    return attribute;
  }

  protected void setProperty(String name, Number value)
  {
    attribute.setProperty(name, value);
    attribute.storeConfig();
  }

  protected void setProperty(String name, Number value, boolean writable)
  {
    attribute.setProperty(name, value, writable);
  }


  void fireValueChanged(String[] newValue, long timeStamp)
  {
    propChanges.fireStringSpectrumEvent((IStringSpectrum) attribute,
      newValue, timeStamp);
  }

  void insert(String[] strSpect)
  {
      attribute.getAttribute().insert(strSpect);
  }

  String[] extract() throws DevFailed
  {
    return attribute.getAttribute().extractStringArray();
  }

  String[] getStringSpectrumValue(DeviceAttribute attValue)  throws DevFailed {
    return attValue.extractStringArray();
  }

  void addStringSpectrumListener(IStringSpectrumListener l)
  {
      propChanges.addStringSpectrumListener(l);
  }

  
  void removeStringSpectrumListener(IStringSpectrumListener l)
  {
      propChanges.removeStringSpectrumListener(l);
  }


  public String getVersion() {
    return "$Id$";
  }

}
