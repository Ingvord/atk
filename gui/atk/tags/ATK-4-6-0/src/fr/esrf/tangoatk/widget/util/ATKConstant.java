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
 
package fr.esrf.tangoatk.widget.util;

import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.awt.*;
import javax.swing.ImageIcon;

import fr.esrf.tangoatk.core.IDevice;
import fr.esrf.tangoatk.core.IAttribute;

/**
 * Contains various constants used in ATK.
 */
public class ATKConstant {

  private static Map qualityMap;
  private static Map stateMap;

  private static Map qualityLightMap;
  private static Map stateLightMap;

  /** Default font used by some ATK viewers */
  public static final Font labelFont = new Font("Dialog", Font.PLAIN, 12);

  static {

    qualityMap = new HashMap();
    qualityMap.put(IAttribute.INVALID, Color.gray);
    qualityMap.put(IAttribute.ALARM, Color.orange);
    qualityMap.put(IAttribute.VALID, Color.green);
    qualityMap.put(IAttribute.UNKNOWN, Color.gray);
    qualityMap.put(IAttribute.WARNING, Color.orange);
    qualityMap.put(IAttribute.CHANGING, new java.awt.Color(128, 160, 255));

    stateMap = new HashMap();
    stateMap.put(IDevice.ON, new java.awt.Color(0, 255, 0));          // Green
    stateMap.put(IDevice.OFF, new java.awt.Color(255, 255, 255));     // White
    stateMap.put(IDevice.CLOSE, new java.awt.Color(255, 255, 255));   // White
    stateMap.put(IDevice.OPEN, new java.awt.Color(0, 255, 0));        // Green
    stateMap.put(IDevice.INSERT, new java.awt.Color(255, 255, 255));  // White
    stateMap.put(IDevice.EXTRACT, new java.awt.Color(0, 255, 0));     // Green
    stateMap.put(IDevice.MOVING, new java.awt.Color(128, 160, 255));  // Light Blue
    stateMap.put(IDevice.STANDBY, new java.awt.Color(255, 255, 0));   // Yellow
    stateMap.put(IDevice.FAULT, new java.awt.Color(255, 0, 0));       // Red
    stateMap.put(IDevice.INIT, new java.awt.Color(204, 204, 122));    // Beige
    stateMap.put(IDevice.RUNNING, new java.awt.Color(0, 125, 0)); // Dark Green
    stateMap.put(IDevice.ALARM, new java.awt.Color(255, 140, 0));     // Orange
    stateMap.put(IDevice.DISABLE, new java.awt.Color(255, 0, 255));   // Magenta
    stateMap.put(IDevice.UNKNOWN, new java.awt.Color(155, 155, 155)); // Grey




    qualityLightMap = new HashMap();
    qualityLightMap.put( IAttribute.INVALID, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledDarkGray.gif")) );
    qualityLightMap.put( IAttribute.ALARM, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledLightOrange.gif")) );
    qualityLightMap.put( IAttribute.VALID, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledGreen.gif")) );
    qualityLightMap.put( IAttribute.UNKNOWN, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledDarkGray.gif")) );
    qualityLightMap.put( IAttribute.WARNING, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledLightOrange.gif")) );
    qualityLightMap.put( IAttribute.CHANGING, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledBlue.gif")) );


    stateLightMap = new HashMap();
    stateLightMap.put( IDevice.ON, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledGreen.gif")) );         // Green
    stateLightMap.put( IDevice.OFF, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledWhite.gif")) );        // White
    stateLightMap.put( IDevice.CLOSE, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledWhite.gif")) );      // White
    stateLightMap.put( IDevice.OPEN, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledGreen.gif")) );       // Green
    stateLightMap.put( IDevice.INSERT, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledWhite.gif")) );     // White
    stateLightMap.put( IDevice.EXTRACT, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledGreen.gif")) );    // Green
    stateLightMap.put( IDevice.MOVING, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledBlue.gif")) );      // Light Blue
    stateLightMap.put( IDevice.STANDBY, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledYellow.gif")) );   // Yellow
    stateLightMap.put( IDevice.FAULT, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledRed.gif")) );        // Red
    stateLightMap.put( IDevice.INIT, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledBrownGray.gif")) );   // Beige
    stateLightMap.put( IDevice.RUNNING, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledDarkGreen.gif")) );     // Dark Green
    stateLightMap.put( IDevice.ALARM, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledDarkOrange.gif")) ); // Orange
    stateLightMap.put( IDevice.DISABLE, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledPink.gif")) );     // Magenta
    stateLightMap.put( IDevice.UNKNOWN, new ImageIcon(ATKConstant.class.getResource("/fr/esrf/tangoatk/widget/icons/ledGray.gif")) );     // Gray

    Locale.setDefault(Locale.US);

  }

  /**
   * Return the default background color according to the given device state.
   * @param state Attribute state.
   * @return Background color.
   */
  public static Color getColor4State(String state) {
   return (Color)stateMap.get(state);
  }

  /**
   * Return the default background color according to the given device state.
   * @param state Attribute state.
   * @param invertOpenClose The boolean which indicates if the colors should be inverted for open and close.
   * @param invertInsertExtract The boolean which indicates if the colors should be inverted for insert and extract.
   * @return Background color.
   */
  public static Color getColor4State(String state, boolean invertOpenClose, boolean invertInsertExtract)
  {
        if (invertOpenClose)
        {
            if (state.equalsIgnoreCase(IDevice.OPEN))
                return getColor4State(IDevice.CLOSE);
            if (state.equalsIgnoreCase(IDevice.CLOSE))
                return getColor4State(IDevice.OPEN);
        }
        
        if (invertInsertExtract)
        {
            if (state.equalsIgnoreCase(IDevice.INSERT))
                return getColor4State(IDevice.EXTRACT);
            if (state.equalsIgnoreCase(IDevice.EXTRACT))
                return getColor4State(IDevice.INSERT);
        }
        return getColor4State(state);
  }

  /**
   * Sets the default background color for the given device state.
   * Affects all viewer running in this JVM. If the state does not already exists,
   * a new entry in the correspondance table is created.
   * @param state value
   * @param c New color
   */
  public static void setColor4State(String state,Color c) {
    stateMap.put(state,c);
  }

  /**
   * Return the default background color according to the given attribute state.
   * @param quality Attribute quality factor (can be "INVALID","WARNING","ALARM","VALID","CHANGING")
   * @return Background color.
   */
  public static Color getColor4Quality(String quality) {
    return (Color)qualityMap.get(quality);
  }

  /**
   * Sets the default background color for the attribute quality factor.
   * Affects all viewer running in this JVM.
   * @param quality value (can be "INVALID","WARNING","ALARM","VALID","CHANGING")
   * @param c New color
   */
  public static void setColor4Quality(String quality,Color c) {
    qualityMap.put(quality,c);
  }



  /**
   * Return the default ImageIcon according to the given device state.
   * @param state Attribute state.
   * @return ImageIcon.
   */
  public static ImageIcon getIcon4State(String state) {
   return (ImageIcon)stateLightMap.get(state);
  }

  /**
   * Sets the default ImageIcon for the given device state.
   * Affects all viewer running in this JVM. If the state does not already exists,
   * a new entry in the correspondance table is created.
   * @param state value
   * @param c New ImageIcon
   */
  public static void setIcon4State(String state,ImageIcon c) {
    stateLightMap.put(state,c);
  }

  /**
   * Return the default ImageIcon according to the given attribute state.
   * @param quality Attribute quality factor (can be "INVALID","WARNING","ALARM","VALID","CHANGING")
   * @return ImageIcon.
   */
  public static ImageIcon getIcon4Quality(String quality) {
    return (ImageIcon)qualityLightMap.get(quality);
  }

  /**
   * Sets the default ImageIcon for the attribute quality factor.
   * Affects all viewer running in this JVM.
   * @param quality value (can be "INVALID","WARNING","ALARM","VALID","CHANGING")
   * @param c New ImageIcon
   */
  public static void setIcon4Quality(String quality,ImageIcon c) {
    qualityLightMap.put(quality,c);
  }



}

