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
 
// $Id$
//
// Description:

package fr.esrf.tangoatk.core;

import fr.esrf.Tango.*;

/**
 * A base class to handle error in ATK.
 */
public class ATKException extends Exception {

  private DevError[] errors = new DevError[0];
  private Object source=null;

  public static String[] severity = {"WARNING", "ERROR", "PANIC"};

  public static final int WARNING = 0;
  public static final int ERROR = 1;
  public static final int PANIC = 2;

  /**
   * Constructs an empty ATK exception.
   */
  public ATKException() {
    super();
    source=this;
  }

  /**
   * Constructs an ATK exception containing a single message.
   * @param s Exception message.
   */
  public ATKException(String s) {
    super(s);
    source=this;
  }

  /**
   * Constructs an ATK exception from a Tango DevFailed exception.
   * @param e Tango exception
   */
  public ATKException(DevFailed e) {
    // Copy the stack trace
    setStackTrace(e.getStackTrace());
    source=e;

    if(e.errors!=null) {
      errors = e.errors;
    } else {
      System.out.println("ATKException.ATKException() : Cannot handle DevFailed with null stack.");
    }
  }

  /**
   * Constructs an ATK exception from a Tango DevError error stack with a message.
   * @param s Error message
   * @param errs Error stack
   */
  public ATKException(String s, DevError[] errs) {
    // Copy the stack trace
    super(s);
    source=this;

    if(errs!=null) {
      errors = errs;
    } else {
      System.out.println("ATKException.ATKException() : Cannot handle ATKException with null error stack.");
    }
  }

  /*
   * Constructs an ATK exception from a Java exception.
   */
  public ATKException(Exception e) {
    super(e.getMessage());
    source=e;

    // Copy the stack trace
    setStackTrace(e.getStackTrace());
  }

  /**
   * Apply the given Tango DevFailed exception to this exception.
   * @param e Tango exception
   */
  public void setError(DevFailed e) {
    // Copy the stack trace
    setStackTrace(e.getStackTrace());
    source=e;

    if(e.errors!=null) {
      errors = e.errors;
    } else {
      System.out.println("ATKException.setError() : Cannot handle DevFailed with null stack.");
    }
  }

  /**
   * Returns the error stack.
   * @return Error message
   */
  public DevError[] getErrors() {
    return errors;
  }

  /**
   * Returns the Exception message. (Not from the stack)
   * @return Message
   */
  public String getMessage() {
    String s = toString();
    if(s==null) {
      return getSourceName();
    } else {
      return s;
    }
  }

  /**
   * Returns the severity of this exception.
   * @return Severity level
   */
  public int getSeverity() {
    return getSeverity(0);
  }

  /**
   * Gets the sevrity at the given stack level of this exception.
   * @param i Stack level
   * @return Severity level
   * @see #WARNING
   * @see #ERROR
   * @see #PANIC
   */
  public int getSeverity(int i) {

    try {
      return errors[i].severity.value();
    } catch (Exception e) {
      return 1;
    } // end of try-catch

  }

  /**
   * Returns the description at the top level of the stack or the
   * message if no stack is present.
   * @return Description message
   */
  public String getDescription() {
    if( errors.length==0 ) {
      return getMessage();
    } else {
      return getDescription(0);
    }
  }

  /**
   * Returns the description at the specified level of the stack.
   * @param i Stack level
   * @return Description message
   */
  public String getDescription(int i) {
    try {
      return errors[i].desc.trim();
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Returns the origin at the top level of the stack.
   * @return Origin message
   */
  public String getOrigin() {
    return getOrigin(0);
  }

  /**
   * Returns the origin at the specified level of the stack.
   * @param i Stack level
   * @return Origin message
   */
  public String getOrigin(int i) {
    try {
      return errors[i].origin.trim();
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Returns the reason at the top level of the stack.
   * @return Reason message
   */
  public String getReason() {
    return getReason(0);
  }

  /**
   * Returns the reason at the specified level of the stack.
   * @param i Stack level
   * @return Reason message
   */
  public String getReason(int i) {
    try {
      return errors[i].reason.trim();
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Returns the tango stack length.
   * @return Stack Length
   */
  public int getStackLength() {
    return errors.length;
  }


  /**
   * Get the class name of the source exception.
   * @return Class name
   */
  public String getSourceName() {
    String ret = source.getClass().toString();
    return ret.substring(6);
  }

  public String toString() {

    if (errors.length==0) return super.getMessage();

    StringBuffer buff = new StringBuffer();
    for (int i = 0; i < errors.length; i++) {
      DevError e = errors[i];
      buff.append("Severity: ");
      buff.append(severity[e.severity.value()]);
      buff.append("\nOrigin: ");
      buff.append(e.origin.trim());
      buff.append("\nDescription: ");
      buff.append(e.desc.trim());
      buff.append("\nReason: ");
      buff.append(e.reason.trim());
      buff.append("\n");
    }
    return buff.toString();
  }

  public String getVersion() {
    return "$Id$";
  }

}
