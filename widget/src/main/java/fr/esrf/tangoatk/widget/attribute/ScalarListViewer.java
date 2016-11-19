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
 
/*
 * ScalarListViewer.java
 *
 * Created on July 30, 2003, 4:45 PM
 */

/**
 *
 * @author  poncet
 */
package fr.esrf.tangoatk.widget.attribute;
 
import fr.esrf.tangoatk.core.IAttribute;
import fr.esrf.tangoatk.core.IBooleanScalar;
import fr.esrf.tangoatk.core.IDevStateScalar;
import fr.esrf.tangoatk.core.IEnumScalar;
import fr.esrf.tangoatk.core.INumberScalar;
import fr.esrf.tangoatk.core.IStringScalar;

import fr.esrf.tangoatk.widget.util.JSmoothLabel;
import fr.esrf.tangoatk.widget.util.JAutoScrolledText;
import fr.esrf.tangoatk.widget.util.JAutoScrolledTextListener;
import fr.esrf.tangoatk.widget.properties.LabelViewer;


import java.util.Vector;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;


public class ScalarListViewer extends javax.swing.JPanel
             implements JAutoScrolledTextListener
{
    public static final String      BOOLEAN_DEFAULT_SETTER = "None";
    public static final String      BOOLEAN_COMBO_SETTER = "BooleanComboEditor";


    protected Vector<IAttribute>    listModel;
    protected Vector<LabelViewer>   scalarLabels;
    protected Vector<JComponent>    scalarViewers;
    protected Vector<JComponent>    scalarSetters;
    protected Vector<JButton>       scalarPropButtons;

    protected SimplePropertyFrame   propFrame=null;
        

    /* The bean properties */
    protected java.awt.Font    theFont;
    private boolean          labelVisible;
    private boolean          setterVisible;
    private boolean          propertyButtonVisible;
    private boolean          propertyListEditable;
    private boolean          unitVisible;
    private String           booleanSetterType;
    private Color            arrowColor;
    private String           toolTipDisplay;
    private boolean          setterEnabled;
    private boolean          noBorder;
    
    /* Deprecated bean properties: the setter type is automatically selected
       according the valueList present or not. */
    private String           numberSetterType = "deprecated";
    private String           stringSetterType = "deprecated";
    public static final String      NUMBER_DEFAULT_SETTER = "WheelEditor";
    public static final String      NUMBER_COMBO_SETTER = "ComboEditor";
    public static final String      STRING_DEFAULT_SETTER = "StringScalarEditor";
    public static final String      STRING_COMBO_SETTER = "StringComboEditor";
    
    public static final String      TOOLTIP_DISPLAY_NONE = "None";
    public static final String      TOOLTIP_DISPLAY_NAME_ONLY = "Name";
    public static final String      TOOLTIP_DISPLAY_ALL = "All";

    /** Creates new form ScalarListViewer */
    public ScalarListViewer()
    {
        listModel = null;
	scalarLabels = null;
	scalarViewers = null;
	scalarSetters = null;
	scalarPropButtons = null;
	arrowColor = null;
	propFrame = new SimplePropertyFrame();
	
	//theFont = new java.awt.Font("Lucida Bright", java.awt.Font.BOLD, 22);
	//theFont = new java.awt.Font("Lucida Bright", java.awt.Font.BOLD, 20);
	//theFont = new java.awt.Font("Lucida Bright", java.awt.Font.BOLD, 14);
	//theFont = new java.awt.Font("Lucida Bright", java.awt.Font.PLAIN, 14);
        theFont = new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12);
	//theFont = new java.awt.Font("Lucida Bright", java.awt.Font.PLAIN, 10);
	labelVisible = true;
	setterVisible = true;
	propertyButtonVisible = true;
	propertyListEditable = true;
	unitVisible = true;
        noBorder = false;
	booleanSetterType = BOOLEAN_DEFAULT_SETTER;
	toolTipDisplay = TOOLTIP_DISPLAY_NONE;
        setterEnabled = true;
        setLayout(new java.awt.GridBagLayout());
	
	setVisible(false);
    }
    
    
    public void setModel(fr.esrf.tangoatk.core.AttributeList scalarList)
    {
	int                          nbAtts, idx;
	boolean                      containsScalarAtt;
	Object                       elem;
	
        
        if (listModel != null)
        {
            removeComponents();
            listModel = null;
        }
               
        if (scalarList == null)
	{
	   return;
	}
        
	nbAtts = scalarList.getSize();

	if (nbAtts <= 0)
	   return;

	containsScalarAtt = false;

	for (idx=0; idx < nbAtts; idx++)
	{
	   elem = scalarList.getElementAt(idx);
	   if (     (elem instanceof INumberScalar)
	        ||  (elem instanceof IStringScalar)
		||  (elem instanceof IBooleanScalar)
		||  (elem instanceof IEnumScalar) 
                ||  (elem instanceof IDevStateScalar))
	   {
	      containsScalarAtt = true;
	      break;
	   }
	}

	if (containsScalarAtt == false)
	  return;

	initComponents(scalarList);

	setVisible(true);
	
    }
    
    
    
    
    protected void removeComponents()
    {
       int                             indRow, nbRows;
       IAttribute                      iatt = null;
       JComponent                      jcomp = null;
       INumberScalar                   ins;
       IStringScalar                   iss;
       IBooleanScalar                  ibs;
       IEnumScalar                     ies;
       IDevStateScalar                 idss;
       LabelViewer                     scalarLabel=null;
       SimpleScalarViewer              ssViewer=null;
       BooleanScalarCheckBoxViewer     bsViewer=null;
       SimpleEnumScalarViewer          esViewer=null;
       SimpleStateViewer               stateViewer=null;
       NumberScalarWheelEditor         wheelSetter=null;
       NumberScalarComboEditor         nComboSetter=null;
       StringScalarEditor              stringSetter=null;
       StringScalarComboEditor         sComboSetter=null;
       BooleanScalarComboEditor        bComboSetter=null;
       EnumScalarComboEditor           eComboSetter=null;
       DevStateScalarComboEditor       stateComboSetter=null;
       JButton                         propertyButton=null;


       propFrame = null;
       propFrame = new SimplePropertyFrame();
       
       nbRows = listModel.size();
       for (indRow=0; indRow < nbRows; indRow++)
       {
	  try
	  {
	     // Find the scalar attribute model
	     ins = null;
	     iss = null;
	     ibs = null;
	     ies = null;
             idss = null;
	     iatt = listModel.get(indRow);
	     if (iatt instanceof INumberScalar)
	     {
		ins = (INumberScalar) iatt;
	     }
	     else
	        if (iatt instanceof IStringScalar)
	           iss = (IStringScalar) iatt;
		else
	           if (iatt instanceof IBooleanScalar)
	              ibs = (IBooleanScalar) iatt;
		   else
	              if (iatt instanceof IEnumScalar)
	        	 ies = (IEnumScalar) iatt;
                      else
                          if (iatt instanceof IDevStateScalar)
                              idss = (IDevStateScalar) iatt;
		
	     if ( (ins != null) || (iss != null) || (ibs != null) || (ies != null) || (idss != null) ) // if attribute model found
	     {
	        // remove this model from all viewers
	        scalarLabel = scalarLabels.get(indRow);
		scalarLabel.setModel(null);
		
		jcomp = scalarViewers.get(indRow);
		if (jcomp instanceof SimpleScalarViewer)
		{
		   ssViewer = (SimpleScalarViewer) jcomp;
		   ssViewer.clearModel();
		}
		else
		   if (jcomp instanceof BooleanScalarCheckBoxViewer)
		   {
		      bsViewer = (BooleanScalarCheckBoxViewer) jcomp;
		      bsViewer.clearModel();
		   }
		   else
		      if (jcomp instanceof SimpleEnumScalarViewer)
		      {
			 esViewer = (SimpleEnumScalarViewer) jcomp;
			 esViewer.clearModel();
		      }
                      else
                         if (jcomp instanceof SimpleStateViewer)
                         {
                            stateViewer = (SimpleStateViewer) jcomp;
                            stateViewer.clearModel();
                         }
                          
		
		
		jcomp = scalarSetters.get(indRow);
		
		if (jcomp != null)
		{
		   if (jcomp instanceof NumberScalarWheelEditor)
		   {
		      wheelSetter = (NumberScalarWheelEditor) jcomp;
		      if (ins != null)
	        	  if (ins.isWritable())
		             wheelSetter.setModel(null);
		   }
		   else
		   {
		      if (jcomp instanceof NumberScalarComboEditor)
		      {
			 nComboSetter = (NumberScalarComboEditor) jcomp;
			 if (ins != null)
	        	     if (ins.isWritable())
		        	nComboSetter.setNumberModel(null);
		      }
		      else
		      {
			 if (jcomp instanceof StringScalarEditor)
			 {
			    stringSetter = (StringScalarEditor) jcomp;
			    if (iss != null)
	                	if (iss.isWritable())
				   stringSetter.setModel(null);
			 }
			 else
			 {
			    if (jcomp instanceof StringScalarComboEditor)
			    {
			       sComboSetter = (StringScalarComboEditor) jcomp;
			       if (iss != null)
	                	   if (iss.isWritable())
				      sComboSetter.setStringModel(null);
			    }
			    else
			    {
			       if (jcomp instanceof BooleanScalarComboEditor)
			       {
				  bComboSetter = (BooleanScalarComboEditor) jcomp;
				  if (ibs != null)
	                	      if (ibs.isWritable())
					 bComboSetter.setAttModel(null);
			       }
			       else
			       {
				  if (jcomp instanceof EnumScalarComboEditor)
				  {
				     eComboSetter = (EnumScalarComboEditor) jcomp;
				     if (ies != null)
	                		 if (ies.isWritable())
					    eComboSetter.setEnumModel(null);
				  }
                                  else
                                     if (jcomp instanceof DevStateScalarComboEditor)
                                     {
                                        stateComboSetter = (DevStateScalarComboEditor) jcomp;
                                        if (idss != null)
                                            if (idss.isWritable())
                                               stateComboSetter.clearModel();
                                     }
			       }
			    }
			 }
		      }
		   }
		}
	     }
	  }
	  catch (Exception e)
	  {
	    System.out.println("ScalarListViewer : setTheFont : Caught exception  "+e.getMessage());
	  }
       }
       
       scalarLabels.removeAllElements();
       scalarViewers.removeAllElements();
       scalarSetters.removeAllElements();
       scalarPropButtons.removeAllElements();
       listModel.removeAllElements();
       this.removeAll();
       listModel = null;
       scalarLabels = null;
       scalarViewers = null;
       scalarSetters = null;
       scalarPropButtons = null;
    }
    
    


    public java.awt.Font getTheFont()
    {
       return(theFont);
    }
    

    public void setTheFont(java.awt.Font  ft)
    {
       int                             indRow, nbRows;
       LabelViewer                     scalarLabel=null;
       JComponent                      viewer = null;
       JComponent                      setter = null;
       JButton                         propertyButton=null;


       if (ft != null)
       {
	  
	  theFont = ft;
	  
          if (listModel != null)
	  {
	     nbRows = listModel.size();
	     for (indRow=0; indRow<nbRows; indRow++)
	     {
		try
		{
	           scalarLabel = scalarLabels.get(indRow);
		   scalarLabel.setFont(theFont);

	           viewer = scalarViewers.get(indRow);
		   if (viewer != null)
		   {
		      if (   (viewer instanceof SimpleScalarViewer)
			  || (viewer instanceof BooleanScalarCheckBoxViewer)
			  || (viewer instanceof SimpleEnumScalarViewer)
                          || (viewer instanceof SimpleStateViewer) )
		      {
			  viewer.setFont(theFont);
		      }
		   }

	           setter = scalarSetters.get(indRow);
		   if (setter != null)
		   {
		      if (   (setter instanceof NumberScalarWheelEditor)
			  || (setter instanceof NumberScalarComboEditor)
			  || (setter instanceof StringScalarEditor)
			  || (setter instanceof StringScalarComboEditor)
			  || (setter instanceof BooleanScalarComboEditor)
			  || (setter instanceof EnumScalarComboEditor)
                          || (setter instanceof DevStateScalarComboEditor) )
		      {
			  setter.setFont(theFont);
		      }
		   }

	           propertyButton = scalarPropButtons.get(indRow);
		   propertyButton.setFont(theFont);
		}
		catch (Exception e)
		{
		  System.out.println("ScalarListViewer : setTheFont : Caught exception  "+e.getMessage());
		}
	     }
	     
	  } // if listModel != null
	  
       } // if ft != null

    }
    

    
    public boolean getLabelVisible()
    {
       return(labelVisible);
    }
    
    public void setLabelVisible(boolean  lv)
    {
        if (labelVisible != lv)
	{
	   labelVisible = lv;
	   changeLabelVisibility();
	}
    }
    
    private void changeLabelVisibility()
    {
       int                        indRow, nbRows;
       LabelViewer                scalarLabel=null;


       if (scalarLabels != null)
       {
	  nbRows = scalarLabels.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        scalarLabel = scalarLabels.get(indRow);
		scalarLabel.setVisible(labelVisible);
	     }
	     catch (Exception e)
	     {
	       System.out.println("ScalarListViewer : changeLabelVisibility : Caught exception  "+e.getMessage());
	     }
	  }
       } // if scalarLabels != null

    }

    

    public boolean getSetterVisible()
    {
       return(setterVisible);
    }
    

    public void setSetterVisible(boolean  sv)
    {
        if (setterVisible != sv)
	{
	   setterVisible = sv;
	   changeSetterVisibility();
	}
    }

    
    private void changeSetterVisibility()
    {
       int                             indRow, nbRows;
       JComponent                      setter = null;


       if (scalarSetters != null)
       {
	  nbRows = scalarSetters.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        setter = scalarSetters.get(indRow);
		if (setter != null)
		   if (   (setter instanceof NumberScalarWheelEditor)
		       || (setter instanceof NumberScalarComboEditor)
		       || (setter instanceof StringScalarEditor)
		       || (setter instanceof StringScalarComboEditor)
		       || (setter instanceof BooleanScalarComboEditor)
		       || (setter instanceof EnumScalarComboEditor)
                       || (setter instanceof DevStateScalarComboEditor) )
		   {
		       setter.setVisible(setterVisible);
		   }
	     }
	     catch (Exception e)
	     {
	       System.out.println("ScalarListViewer : changeSetterVisibility : Caught exception  "+e.getMessage());
	     }
	  }
       } // if scalarSetters != null

    }
    
    

    public boolean getSetterEnabled() {
        return setterEnabled;
    }


    public void setSetterEnabled(boolean setterEnabled)
    {
        int                  indRow, nbRows;
        JComponent           comp = null;
        
        if (this.setterEnabled == setterEnabled) return;
        
        this.setterEnabled = setterEnabled;


        if (scalarSetters != null)
        {
            nbRows = scalarSetters.size();
            for (indRow=0; indRow<nbRows; indRow++)
            {
                try
                {
                    comp = scalarSetters.get(indRow);
                    if (comp != null)
                    if (   (comp instanceof NumberScalarWheelEditor)
                        || (comp instanceof NumberScalarComboEditor)
                        || (comp instanceof StringScalarEditor)
                        || (comp instanceof StringScalarComboEditor)
                        || (comp instanceof BooleanScalarComboEditor)
                        || (comp instanceof EnumScalarComboEditor)
                        || (comp instanceof DevStateScalarComboEditor) )
                    {
                        comp.setEnabled(setterEnabled);
                    }
              }
              catch (Exception e)
              {
                System.out.println("ScalarListViewer : setSetterEnabled : Caught exception  "+e.getMessage());
              }
            }
       } // if scalarSetters != null
        
       if (scalarViewers != null)
       {
            nbRows = scalarViewers.size();
            for (indRow=0; indRow<nbRows; indRow++)
            {
                try
                {
                    comp = scalarViewers.get(indRow);
                    if (comp != null)
                    if (comp instanceof BooleanScalarCheckBoxViewer)
                        comp.setEnabled(setterEnabled);
                }
                catch (Exception e)
                {
                    System.out.println("ScalarListViewer : setSetterEnabled : Caught exception  "+e.getMessage());
                }
            }
       } // if scalarViewers != null
    }
   
    public boolean getPropertyButtonVisible()
    {
       return(propertyButtonVisible);
    }
    
    public void setPropertyButtonVisible(boolean  pv)
    {
        if (propertyButtonVisible != pv)
	{
	   propertyButtonVisible = pv;
	   changePropButtonVisibility();
	}
    }
    
    private void changePropButtonVisibility()
    {
       int                             indRow, nbRows;
       JButton                         propertyButton=null;


       if (scalarPropButtons != null)
       {
	  nbRows = scalarPropButtons.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        propertyButton = scalarPropButtons.get(indRow);
		propertyButton.setVisible(propertyButtonVisible);
	     }
	     catch (Exception e)
	     {
	       System.out.println("ScalarListViewer : changePropButtonVisibility : Caught exception  "+e.getMessage());
	     }
	  }
       } // if scalarPropButtons != null

    }
    
    

    public boolean getPropertyListEditable()
    {
       return(propertyListEditable);
    }
    
    public void setPropertyListEditable(boolean  pv)
    {
        /*if (propertyListEditable != pv)
	{
	   propertyListEditable = pv;
	   changePropertyListEditable();
	}*/
    }
    
    

    public boolean getUnitVisible()
    {
       return(unitVisible);
    }
    
    public void setUnitVisible(boolean  uv)
    {
        if (unitVisible != uv)
	{
	   unitVisible = uv;
	   changeUnitVisibility();
	}
    }
    

    private void changeUnitVisibility()
    {
       int                             indRow, nbRows;
       JComponent                      jcomp = null;
       SimpleScalarViewer              viewer=null;
       NumberScalarComboEditor         setter=null;


       if (scalarViewers != null)
       {
	  nbRows = scalarViewers.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        jcomp = scalarViewers.get(indRow);
		if (jcomp instanceof SimpleScalarViewer)
		{
		   viewer = (SimpleScalarViewer) jcomp;
		   viewer.setUnitVisible(unitVisible);
		}
		jcomp = scalarSetters.get(indRow);
		if (jcomp != null)
		   if (jcomp instanceof NumberScalarComboEditor)
		   {
		      setter = (NumberScalarComboEditor) jcomp;
		      setter.setUnitVisible(unitVisible);
		   }
	     }
	     catch (Exception e)
	     {
	       System.out.println("ScalarListViewer : changeUnitVisibility : Caught exception  "+e.getMessage());
	     }
	  }
       } // if scalarViewers != null

    }

    
    public boolean getNoBorder()
    {
       return(noBorder);
    }
    
    public void setNoBorder(boolean  nb)
    {
        if (noBorder != nb)
	{
	   noBorder = nb;
	   changeBorder();
	}
    }


     

    private void changeBorder()
    {
       int                             indRow, nbRows;
       JComponent                      jcomp = null;
       SimpleScalarViewer              viewer=null;
       NumberScalarComboEditor         setter=null;


       if (scalarViewers != null)
       {
	  nbRows = scalarViewers.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        jcomp = scalarViewers.get(indRow);
		if (jcomp instanceof SimpleScalarViewer)
		{
		   viewer = (SimpleScalarViewer) jcomp;
                   if (noBorder)
                       viewer.setBorder(BorderFactory.createEmptyBorder());
                   else
                       viewer.setBorder(BorderFactory.createLoweredBevelBorder());
		}
	     }
	     catch (Exception e)
	     {
	       System.out.println("ScalarListViewer : changeBorder : Caught exception  "+e.getMessage());
	     }
	  }
       } // if scalarViewers != null

    }
   
    
   /**
    * Returns the current BooleanSetterType used for all BooleanScalar attributes
    * @see #setBooleanSetterType
    */
    public String getBooleanSetterType()
    {
         return booleanSetterType;
    }
    
   /**
    * Sets the current BooleanSetterType used for all BooleanScalar attributes
    * @see #getBooleanSetterType
    */
    public void setBooleanSetterType(String  setType)
    {
	if (listModel != null)
	   return;
	   
        if (setType.equalsIgnoreCase(BOOLEAN_DEFAULT_SETTER))
	   booleanSetterType = BOOLEAN_DEFAULT_SETTER;
	else
	   if (setType.equalsIgnoreCase(BOOLEAN_COMBO_SETTER))
	       booleanSetterType = BOOLEAN_COMBO_SETTER;
	   else
	       booleanSetterType = BOOLEAN_DEFAULT_SETTER;
    }
     
     
   /**
    * Returns the current toolTipDisplay
    * @see #setToolTipDisplay
    */
    public String getToolTipDisplay()
    {
         return toolTipDisplay;
    }
    
   /**
    * Sets the current toolTipDisplay. This property should be set before the call to setModel()
    * @see #getToolTipDisplay
    */
    public void setToolTipDisplay(String  ttType)
    {
	if (listModel != null)
	   return;
	   
        if (ttType.equalsIgnoreCase(TOOLTIP_DISPLAY_ALL))
	   toolTipDisplay = TOOLTIP_DISPLAY_ALL;
	else
	   if (ttType.equalsIgnoreCase(TOOLTIP_DISPLAY_NAME_ONLY))
	       toolTipDisplay = TOOLTIP_DISPLAY_NAME_ONLY;
	   else
	       toolTipDisplay = TOOLTIP_DISPLAY_NONE;
    }
     
     
    /**
     * @deprecated As of ATKWidget-2.5.8 and higher
     * The method getNumberSetterType should not be used.
     * The setterType for each NumberScalar attribute is selected automatically by
     * the ScalarListViewer.
     */
    public String getNumberSetterType()
    {
         return numberSetterType;
    }
    
    /**
     * @deprecated As of ATKWidget-2.5.8 and higher this method has no effect.
     * The setterType for each NumberScalar attribute is selected automatically by
     * the ScalarListViewer.
     */
    public void setNumberSetterType(String  setType)
    {
        /* deprecated
	if (listModel != null)
	   return;
	   
        if (setType.equalsIgnoreCase(NUMBER_DEFAULT_SETTER))
	   numberSetterType = NUMBER_DEFAULT_SETTER;
	else
	   if (setType.equalsIgnoreCase(NUMBER_COMBO_SETTER))
	       numberSetterType = NUMBER_COMBO_SETTER;
	   else
	       numberSetterType = NUMBER_DEFAULT_SETTER;
	*/
    }
     
     
     
    /**
     * @deprecated As of ATKWidget-2.5.8 and higher
     * The method getStringSetterType should not be used.
     * The setterType for each StringScalar attribute is selected automatically by
     * the ScalarListViewer.
     */
    public String getStringSetterType()
    {
         return stringSetterType;
    }
    
    
    /**
     * @deprecated As of ATKWidget-2.5.8 and higher this method has no effect.
     * The setterType for each StringScalar attribute is selected automatically by
     * the ScalarListViewer.
     */
    public void setStringSetterType(String  setType)
    {
        /* deprecated
	if (listModel != null)
	   return;
	   
        if (setType.equalsIgnoreCase(STRING_DEFAULT_SETTER))
	   stringSetterType = STRING_DEFAULT_SETTER;
	else
	   if (setType.equalsIgnoreCase(STRING_COMBO_SETTER))
	       stringSetterType = STRING_COMBO_SETTER;
	   else
	       stringSetterType = STRING_DEFAULT_SETTER;
	 */
    }




   /**
    * Returns the current arrowButton colour for the WheelEditor used as number setter
    * @see #setArrowColor
    */
    public Color getArrowColor()
    {
      if (arrowColor == null)
         return (getBackground());
      else
         return(arrowColor);
    }


  /**
   * Sets the current arrowButton colour for the WheelEditor used as number setter
   * @param java.awt.Color  ac
   */
     public void setArrowColor( Color  ac)
     {
          if (ac == arrowColor)
	     return;

	  changeArrowColors(ac);

	  arrowColor = ac;   
     }


    
    private void changeArrowColors(Color  ac)
    {
       int                             indRow, nbRows;
       JComponent                      jcomp = null;
       NumberScalarWheelEditor         setter=null;


       if (scalarSetters != null)
       {
	  nbRows = scalarSetters.size();
	  for (indRow=0; indRow<nbRows; indRow++)
	  {
	     try
	     {
	        jcomp = scalarSetters.get(indRow);
		if (jcomp instanceof NumberScalarWheelEditor)
		{
		   setter = (NumberScalarWheelEditor) jcomp;
		   if (ac == null)
		      setter.setButtonColor(setter.getBackground());
		   else
		      setter.setButtonColor(ac);
		}
	     }
	     catch (Exception e)
	     {
	       System.out.println("ScalarListViewer : changeArrowColors : Caught exception  "+e.getMessage());
	     }
	  }
       } // if scalarSetters != null

    }

    
    
    /* Method for JAutoScrolledTextListener interface */
    public void textExceedBounds(JAutoScrolledText source)
    {
       this.revalidate();
    }
    
    public void setCheckBoxStrings(IBooleanScalar ibs, String trueLabel, String falseLabel)
    {
    
       int                          ibsIndex=-1;
       JComponent                   jcomp=null;
       
              
       if (ibs == null)
          return;

       if (listModel == null)
	  return;

       ibsIndex = listModel.indexOf(ibs);
       if ( ibsIndex < 0)
          return;
	  
       
       jcomp = scalarViewers.get(ibsIndex);
       if (jcomp == null) return;
	  
       
       if ( !(jcomp instanceof BooleanScalarCheckBoxViewer) )
          return;
	  
       BooleanScalarCheckBoxViewer cb = (BooleanScalarCheckBoxViewer) jcomp;
       
       if (trueLabel == null)
          cb.setTrueLabel(new String());
       else
          cb.setTrueLabel(trueLabel);
       
       
       if (falseLabel == null)
          cb.setFalseLabel(new String());
       else
          cb.setFalseLabel(falseLabel);
 
       this.revalidate();

    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    protected void initComponents(fr.esrf.tangoatk.core.AttributeList scalarList)
    {
	int                             nbAtts, idx, viewerRow, nbScalarViewers;
	boolean                         containsNumberScalar;
	Object                          elem;
	INumberScalar                   ins;
	IStringScalar                   iss;
	IBooleanScalar                  ibs;
	IEnumScalar                     ies;
        IDevStateScalar                 idss;
        java.awt.GridBagConstraints     gridBagConstraints;
	
	LabelViewer                     scalarLabel=null;
        
	SimpleScalarViewer              ssViewer=null;
	BooleanScalarCheckBoxViewer     boolViewer=null;
	SimpleEnumScalarViewer          enumViewer=null;
        SimpleStateViewer               stateViewer=null;
        
	NumberScalarWheelEditor         wheelSetter=null;
	NumberScalarComboEditor         comboSetter=null;
	StringScalarEditor              stringSetter=null;
	StringScalarComboEditor         stringComboSetter=null;
	BooleanScalarComboEditor        boolComboSetter=null;
	EnumScalarComboEditor           enumComboSetter=null;
        DevStateScalarComboEditor       stateComboSetter=null;
        
	JComponent                      jcomp=null;
	JComponent                      viewer=null;
	JComponent                      setter=null;
	JButton                         propertyButton=null;
	
	int                             maxRowElementHeight;
	int                             currH;
	int                             hMargin;
	boolean                         insHasValueList, issHasValueList;


	listModel = new Vector<IAttribute> ();
	scalarLabels = new Vector<LabelViewer> ();
	scalarViewers = new Vector<JComponent> ();
	scalarSetters = new Vector<JComponent> ();
	scalarPropButtons = new Vector<JButton> ();
	
	
	viewerRow = 0;
	nbAtts = scalarList.size();
	maxRowElementHeight = 0;
	
	for (idx=0; idx < nbAtts; idx++)
	{
           scalarLabel = null;
           viewer = null;
	   boolViewer=null;
           boolComboSetter = null;
	   enumViewer=null;
           enumComboSetter = null;
	   ssViewer=null;
           stateViewer=null;
           
           wheelSetter = null;
           comboSetter = null;
	   setter = null;
	   stringSetter = null;
           stringComboSetter = null;
           enumComboSetter=null;
           stateComboSetter=null;
           
           propertyButton = null;
	   
	   elem = scalarList.getElementAt(idx);
	   if (    (elem instanceof INumberScalar)
		|| (elem instanceof IBooleanScalar)
		|| (elem instanceof IEnumScalar)
		|| (elem instanceof IDevStateScalar)
	        || (elem instanceof IStringScalar)  )
	   {
	      ins = null;
	      iss = null;
	      ibs = null;
	      ies = null;
              idss = null;
	      
	      // Create setter
	      if (elem instanceof INumberScalar)
	      {
                 ssViewer = new SimpleScalarViewer();
		 java.awt.Insets  marge = ssViewer.getMargin();
		 marge.left = marge.left + 2;
		 marge.right = marge.right + 2;
		 ssViewer.setMargin(marge);
		 
		 viewer = ssViewer;
	         ins = (INumberScalar) elem;
		 insHasValueList = false;
		 if (ins.getPossibleValues() != null)
		    if (ins.getPossibleValues().length > 0)
		        insHasValueList = true;
		 if (insHasValueList)
		 {
         	    comboSetter = new NumberScalarComboEditor();
        	    comboSetter.setFont(theFont);
        	    comboSetter.setBackground(getBackground());
	            comboSetter.setUnitVisible(unitVisible);
		    if (ins.isWritable())
		    {
		       comboSetter.setNumberModel(ins);
        	       comboSetter.setVisible(setterVisible);
                       comboSetter.setEnabled(setterEnabled);
                       setter = comboSetter;
		    }
		    else
		       setter = null;
                    
                    scalarSetters.add(setter);
		 }
		 else
		 {
         	    wheelSetter = new NumberScalarWheelEditor();
        	    wheelSetter.setFont(theFont);
        	    wheelSetter.setBackground(getBackground());
		    if (ins.isWritable())
		    {
		       wheelSetter.setModel(ins);
        	       wheelSetter.setVisible(setterVisible);
                       wheelSetter.setEnabled(setterEnabled);
                       setter = wheelSetter;
		    }
		    else
		       setter = null;

	            scalarSetters.add(setter);
		 }
	      }
	      else
		 if (elem instanceof IBooleanScalar)
		 {
	            ibs = (IBooleanScalar) elem;
                    boolViewer = new BooleanScalarCheckBoxViewer();
		    boolViewer.setTrueLabel(new String());
		    boolViewer.setFalseLabel(new String());
                    boolViewer.setEnabled(setterEnabled);
		    viewer = boolViewer;
		    if (ibs.isWritable())
		    {
		       if (booleanSetterType.equalsIgnoreCase(BOOLEAN_COMBO_SETTER))
		       {
		           boolComboSetter = new BooleanScalarComboEditor();
        		   boolComboSetter.setFont(theFont);
        		   boolComboSetter.setBackground(getBackground());
			   boolComboSetter.setAttModel(ibs);
        		   boolComboSetter.setVisible(setterVisible);
                           boolComboSetter.setEnabled(setterEnabled);
			   setter = boolComboSetter;
		       }
		       else
			   setter = null;
		    }
		    else
		    {
		       setter = null;
		    }
		    scalarSetters.add(setter);
		 }
		 else
		    if (elem instanceof IEnumScalar)
		    {
	               ies = (IEnumScalar) elem;
                       enumViewer = new SimpleEnumScalarViewer();
		       viewer = enumViewer;
		       if (ies.isWritable())
		       {
		          enumComboSetter = new EnumScalarComboEditor();
        		  enumComboSetter.setFont(theFont);
        		  enumComboSetter.setBackground(getBackground());
			  enumComboSetter.setEnumModel(ies);
        		  enumComboSetter.setVisible(setterVisible);
                          enumComboSetter.setEnabled(setterEnabled);
			  setter = enumComboSetter;
		       }
		       else
		       {
			  setter = null;
		       }
		       scalarSetters.add(setter);
		    }
                    else
                        if (elem instanceof IDevStateScalar)
                        {
                           idss = (IDevStateScalar) elem;
                           stateViewer = new SimpleStateViewer();
                           viewer = stateViewer;
                           if (idss.isWritable())
                           {
                              stateComboSetter = new DevStateScalarComboEditor();
                              stateComboSetter.setFont(theFont);
                              stateComboSetter.setBackground(getBackground());
                              stateComboSetter.setStateModel(idss);
                              stateComboSetter.setVisible(setterVisible);
                              stateComboSetter.setEnabled(setterEnabled);
                              setter = stateComboSetter;
                           }
                           else
                           {
                              setter = null;
                           }
                           scalarSetters.add(setter);
                        }
                        else //IStringScalar
                        {
                           ssViewer = new SimpleScalarViewer();
                           java.awt.Insets  marge = ssViewer.getMargin();
                           marge.left = marge.left + 2;
                           marge.right = marge.right + 2;
                           ssViewer.setMargin(marge);
                           viewer = ssViewer;
                           iss = (IStringScalar) elem;
                           issHasValueList = false;
                           if (iss.getPossibleValues() != null)
                              if (iss.getPossibleValues().length > 0)
                                  issHasValueList = true;
                           if (issHasValueList)
                           {
                              stringComboSetter = new StringScalarComboEditor();
                              stringComboSetter.setFont(theFont);
                              //stringComboSetter.setBackground(getBackground());
                              stringComboSetter.setEnabled(setterEnabled);
                              if (iss.isWritable())
                              {
                                 stringComboSetter.setStringModel(iss);
                                 stringComboSetter.setVisible(setterVisible);
                              }
                              else
                                 stringComboSetter.setVisible(false);

                              scalarSetters.add(stringComboSetter);
                              setter = stringComboSetter;
                           }
                           else
                           {
                              stringSetter = new StringScalarEditor();
                              stringSetter.setFont(theFont);
                              stringSetter.setEnabled(setterEnabled);
                              //stringSetter.setBackground(getBackground());
                              if (iss.isWritable())
                              {
                                 stringSetter.setModel(iss);
                                 stringSetter.setVisible(setterVisible);
                              }
                              else
                                 stringSetter.setVisible(false);

                              scalarSetters.add(stringSetter);
                              setter = stringSetter;
                           }
                        }
	      
              scalarLabel = new LabelViewer();
              propertyButton = new javax.swing.JButton();

	      // Set the Label Viewer properties
	      scalarLabel.setFont(theFont);
	      scalarLabel.setHorizontalAlignment(JSmoothLabel.RIGHT_ALIGNMENT);
	      scalarLabel.setBackground(getBackground());
	      //scalarLabel.setValueOffsets(0, -5);
          
	      if (ins != null)
	         scalarLabel.setModel(ins);
	      else
	         if (iss != null)
		     scalarLabel.setModel(iss);
		 else
	            if (ibs != null)
	               scalarLabel.setModel(ibs);
		    else
	               if (ies != null)
	        	  scalarLabel.setModel(ies);
		       else
	                  if (idss != null)
	        	     scalarLabel.setModel(idss);
	      	 

	      // Set the Viewer properties
	      if (ssViewer != null) // SimpleScalarViewer
	      {
		 if (toolTipDisplay.equalsIgnoreCase(TOOLTIP_DISPLAY_ALL))
		 {
		    ssViewer.setHasToolTip(true);
		    ssViewer.setQualityInTooltip(true);
		 }
		 else
		    if (toolTipDisplay.equalsIgnoreCase(TOOLTIP_DISPLAY_NAME_ONLY))
		    {
		       ssViewer.setHasToolTip(true);
		       ssViewer.setQualityInTooltip(false);
		    }
		    else
		       {
			  ssViewer.setHasToolTip(false);
			  ssViewer.setQualityInTooltip(false);
		       }
                 ssViewer.setFont(theFont);
		 ssViewer.setUnitVisible(unitVisible);
        	 ssViewer.setBackgroundColor(getBackground());
                 if (noBorder)
                     ssViewer.setBorder(BorderFactory.createEmptyBorder());
                 else
                     ssViewer.setBorder(BorderFactory.createLoweredBevelBorder());
		 ssViewer.setAlarmEnabled(true);
	         ssViewer.addTextListener(this);		 
		 //ssViewer.setValueOffsets(0, -5);
		 if (ins != null)
	            ssViewer.setModel(ins);
		 else
	            ssViewer.setModel(iss);
	      }
	      else // should be a BooleanScalarCheckBoxViewer or SimpleEnumScalarViewer or SimpleStateViewer
	      {
	         if (boolViewer != null)
		 {
		    if (     toolTipDisplay.equalsIgnoreCase(TOOLTIP_DISPLAY_ALL)
		         ||  toolTipDisplay.equalsIgnoreCase(TOOLTIP_DISPLAY_NAME_ONLY))
		       boolViewer.setHasToolTip(true);
		    else
		        boolViewer.setHasToolTip(false);
		     boolViewer.setAttModel(ibs);
        	     boolViewer.setBackground(getBackground());
		 }
		 else // should a SimpleEnumScalarViewer or SimpleStateViewer
		 {
	            if (enumViewer != null)
		    {
                        if (toolTipDisplay.equalsIgnoreCase(TOOLTIP_DISPLAY_ALL))
                        {
                           enumViewer.setHasToolTip(true);
                           enumViewer.setQualityInTooltip(true);
                        }
                        else
                           if (toolTipDisplay.equalsIgnoreCase(TOOLTIP_DISPLAY_NAME_ONLY))
                           {
                              enumViewer.setHasToolTip(true);
                              enumViewer.setQualityInTooltip(false);
                           }
                           else
                           {
                              enumViewer.setHasToolTip(false);
                              enumViewer.setQualityInTooltip(false);
                           }
                	enumViewer.setFont(theFont);
        		enumViewer.setBackgroundColor(getBackground());
        		enumViewer.setBorder(javax.swing.BorderFactory.createLoweredBevelBorder());
			enumViewer.setAlarmEnabled(true);
	        	enumViewer.addTextListener(this);		 
			enumViewer.setModel(ies);
		    }
                    else
                    {
                        if (stateViewer != null)
                        {
                            stateViewer.setFont(theFont);
                            stateViewer.setText("    ");
                            stateViewer.setStateClickable(false);
                            stateViewer.setModel(idss);
                        }                        
                    }
		 }
	      }
	         
	      
              propertyButton.setFont(theFont);
              propertyButton.setBackground(getBackground());
              propertyButton.setText(" ... ");
              propertyButton.setMargin(new java.awt.Insets(-3, 0, 3, 0));
              propertyButton.setToolTipText("Attribute Properties");	      
	      propertyButton.addActionListener(
	         new java.awt.event.ActionListener() 
		       {
	                  public void actionPerformed(java.awt.event.ActionEvent evt)
			  {
	                     propertyButtonActionPerformed(evt);
	                  }
	               });
		    
	      	 
	      // Set the Label, PropertyButton visibility
	      scalarLabel.setVisible(labelVisible);
	      propertyButton.setVisible(propertyButtonVisible);
		    
	      
	      // to enable the viewers / setters to be correctly sized!
	      if (ins != null)
	         ins.refresh();
	      else
	         if (iss != null)
	            iss.refresh();
		 else
	            if (ibs != null)
		       ibs.refresh();
		    else
	               if (ies != null)
			  ies.refresh();
                       else
                           if (idss != null)
                               idss.refresh();
		
	      // Compute the height of the "highest" element of the CURRENT row
	      // apply vertical margin to the viewer and setters if needed
	      maxRowElementHeight = 0;
	      currH = scalarLabel.getPreferredSize().height+4;
	      if (currH > maxRowElementHeight)
	         maxRowElementHeight = currH;
		 
	      currH = viewer.getPreferredSize().height+4;
	      if (currH > maxRowElementHeight)
	         maxRowElementHeight = currH;
	      
	      if (setter != null)
	      {
		 if (setter.isVisible())
		 { 
		    currH = setter.getPreferredSize().height+4;
		    if (currH > maxRowElementHeight)
	               maxRowElementHeight = currH;
		 }
	      }
	      
	      
	      // Add all these viewers to the panel	      
              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.gridx = 0;
              gridBagConstraints.gridy = viewerRow;
              gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
              gridBagConstraints.insets = new java.awt.Insets(2,6,2,1);
              add(scalarLabel, gridBagConstraints);

              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.gridx = 1;
              gridBagConstraints.gridy = viewerRow;
              if ( (viewer instanceof SimpleStateViewer) || (viewer instanceof BooleanScalarCheckBoxViewer) )
              {
                  gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
              }
              else
                  gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
              
              gridBagConstraints.insets = new java.awt.Insets(2,3,2,1);
              add(viewer, gridBagConstraints);
	      
              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.gridx = 2;
              gridBagConstraints.gridy = viewerRow;
              gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;

	      if (setter != null)
	      {
      		 if (ins != null)
     		 {
           	    gridBagConstraints.insets = new java.awt.Insets(1,3,1,1);
                    add(setter, gridBagConstraints);
   		 }
   		 else
   		    if (iss != null)
   		    {
           	       gridBagConstraints.insets = new java.awt.Insets(2,3,2,1);
                       add(setter, gridBagConstraints);
   		    }
   		    else
   		       if (ibs != null)
   		       {
           		  gridBagConstraints.insets = new java.awt.Insets(2,3,2,1);
                   	  add(setter, gridBagConstraints);
   		       }
   		       else
   			  if (ies != null)
   			  {
           		     gridBagConstraints.insets = new java.awt.Insets(2,3,2,1);
                   	     add(setter, gridBagConstraints);
   			  }
                          else
                             if (idss != null)
                             {
                                 gridBagConstraints.insets = new java.awt.Insets(2,3,2,1);
                                 add(setter, gridBagConstraints);
                             }
   	      }
	      
              gridBagConstraints = new java.awt.GridBagConstraints();
              gridBagConstraints.gridx = 3;
              gridBagConstraints.gridy = viewerRow;
              gridBagConstraints.insets = new java.awt.Insets(2,3,2,6);
              add(propertyButton, gridBagConstraints);
	      
	      // Add to the vectors
	      if (ins != null)
		 listModel.add(ins);
	      else
	         if (iss != null)
		    listModel.add(iss);
		 else
		    if (ibs != null)
		       listModel.add(ibs);
		    else
		       if (ies != null)
			  listModel.add(ies);
                       else
                          if (idss != null)
                             listModel.add(idss);
              
	      scalarLabels.add(scalarLabel);
	      scalarViewers.add(viewer);
	      scalarPropButtons.add(propertyButton);
	      
	      // Apply Vertical Margins if needed

	      if (viewer instanceof SimpleScalarViewer)
	      {
	          SimpleScalarViewer  sv = (SimpleScalarViewer) viewer;
		  currH = viewer.getPreferredSize().height;
		  if (currH < maxRowElementHeight)
	             hMargin = (maxRowElementHeight - currH) / 2;
		  else
		     hMargin = 0;
		  java.awt.Insets  marge = sv.getMargin();
		  marge.top = marge.top + hMargin;
		  marge.bottom = marge.bottom + hMargin;
		  marge.left = marge.left+2;
		  marge.right = marge.right+2;
		  sv.setMargin(marge);
	      }
	      else	      
		  if (viewer instanceof SimpleEnumScalarViewer)
		  {
	              SimpleEnumScalarViewer  sesv = (SimpleEnumScalarViewer) viewer;
	              currH = viewer.getPreferredSize().height;
		      if (currH < maxRowElementHeight)
	        	 hMargin = (maxRowElementHeight - currH) / 2;
		      else
			 hMargin = 0;
		      java.awt.Insets  marge = sesv.getMargin();
		      marge.top = marge.top + hMargin;
		      marge.bottom = marge.bottom + hMargin;
		      marge.left = marge.left+2;
		      marge.right = marge.right+2;
		      sesv.setMargin(marge);
		  } 
	      
	      if ( (setter instanceof StringScalarEditor) && (setter.isVisible()) )
	      {
	          StringScalarEditor   sse = (StringScalarEditor) setter;
		  currH = setter.getPreferredSize().height;
		  if (currH < maxRowElementHeight)
	             hMargin = (maxRowElementHeight - currH) / 2;
		  else
		     hMargin = 0;
		  java.awt.Insets  marge = sse.getMargin();
		  marge.top = marge.top + hMargin;
		  marge.bottom = marge.bottom + hMargin;
		  marge.left = marge.left+2;
		  marge.right = marge.right+2;
		  sse.setMargin(marge);
	      } 

	      viewerRow++;
	   }
	}
    }
    
    
    private void propertyButtonActionPerformed (java.awt.event.ActionEvent evt)
    {
        int              buttonIndex=-1;
	int              ind, nbButtons;
	JButton          propertyButton;
	IAttribute       iatt;
	INumberScalar    ins;
	IStringScalar    iss;
	IBooleanScalar   ibs;
	IEnumScalar      ies;
	
	
	if (scalarPropButtons == null)
	   return;
	
	if (listModel == null)
	   return;
	   
	nbButtons = scalarPropButtons.size();
	
	// Look for the button in the vector
	for (ind=0; ind<nbButtons; ind++)
	{
	   try
	   {
	      propertyButton = scalarPropButtons.get(ind);
	      if (propertyButton.equals(evt.getSource()))
	      {
		 buttonIndex = ind;
		 break;
	      }
	   }
	   catch (Exception e)
	   {
	     System.out.println("ScalarListViewer : propertyButtonActionPerformed : Caught exception  "+e.getMessage());
	     return;
	   }
	}
	
	if (buttonIndex < 0)
	   return;
	
	// find the Scalar attribute corresponding to the button

	ins = null;
	iss = null;
	ibs = null;
	ies = null;
	
	try
	{
	   iatt = listModel.get(buttonIndex);
	   if (iatt instanceof INumberScalar)
	      ins = (INumberScalar) iatt;
	   else
	      if (iatt instanceof IStringScalar)
	         iss = (IStringScalar) iatt;
	      else
		 if (iatt instanceof IBooleanScalar)
	            ibs = (IBooleanScalar) iatt;
		 else
		    if (iatt instanceof IEnumScalar)
	               ies = (IEnumScalar) iatt;
	}
	catch (Exception e)
	{
	}
	
	if ((ins == null) && (iss == null) && (ibs == null) && (ies == null))
	   return;
	

	if (propFrame != null)
	{
	   if (ins != null)
	      propFrame.setModel(ins);
	   else
	      if (iss != null)
		 propFrame.setModel(iss);
	      else
		 if (ibs != null)
		    propFrame.setModel(ibs);
		 else
		    if (ies != null)
		       propFrame.setModel(ies);
		       	      
	   propFrame.setVisible(true);
	}
	
    }


    
    public static void main(String[] args)
    {
       final fr.esrf.tangoatk.core.AttributeList  attList = new fr.esrf.tangoatk.core.AttributeList();
       ScalarListViewer               scalarlv = new ScalarListViewer();
       INumberScalar                        attn;
       IStringScalar                        attstr;
       IBooleanScalar                       attbool, attcomp, attrefsubst;
       IEnumScalar                          attEnum;
       IAttribute                           iatt;
       JFrame                               mainFrame;
       

       //scalarlv.setBackground(Color.white);
       //scalarlv.setForeground(Color.black);
       scalarlv.setTheFont(new java.awt.Font("Lucida Bright", java.awt.Font.PLAIN, 18));
       //scalarlv.setLabelVisible(false);
       //scalarlv.setSetterVisible(false);
       //scalarlv.setPropertyButtonVisible(false);
       scalarlv.setBooleanSetterType(ScalarListViewer.BOOLEAN_COMBO_SETTER);
//       scalarlv.setNoBorder(true);

       // Connect to a list of scalar attributes
       try
       {
          iatt = (IAttribute) attList.add("sys/tg_test/1/double_scalar");
          iatt = (IAttribute) attList.add("sys/tg_test/1/string_scalar");
          iatt = (IAttribute) attList.add("sys/tg_test/1/boolean_scalar");
          iatt = (IAttribute) attList.add("sys/tg_test/1/short_scalar");

	  scalarlv.setModel(attList);
	  
       }
       catch (Exception ex)
       {
          System.out.println("caught exception : "+ ex.getMessage());
	  ex.printStackTrace();
	  System.exit(-1);
       }
       
//       scalarlv.setNoBorder(true);
       
       mainFrame = new JFrame();
       mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
       mainFrame.addWindowListener(
	       new java.awt.event.WindowAdapter()
			  {
			      public void windowActivated(java.awt.event.WindowEvent evt)
			      {
				 // To be sure that the refresher (an independente thread)
				 // will begin when the the layout manager has finished
				 // to size and position all the components of the window
				 attList.startRefresher();
			      }
			  }
                                     );
                                     
       mainFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
       mainFrame.getContentPane().setLayout(new java.awt.GridBagLayout());

       java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
       gridBagConstraints.gridx = 0;
       gridBagConstraints.gridy = 0;
       gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
       gridBagConstraints.weightx = 1.0;
       gridBagConstraints.weighty = 1.0; 
       gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
       mainFrame.getContentPane().add(scalarlv, gridBagConstraints);

       javax.swing.JButton jButton1 = new javax.swing.JButton();
       jButton1.setText("Atk Diagnostic");
       jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fr.esrf.tangoatk.widget.util.ATKDiagnostic.showDiagnostic();
            }
       });
       gridBagConstraints.gridx = 0;
       gridBagConstraints.gridy = 1;
       gridBagConstraints.fill = java.awt.GridBagConstraints.NONE;
       gridBagConstraints.weightx = 0.0;
       gridBagConstraints.weighty = 0.0; 
       gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
       mainFrame.getContentPane().add(jButton1, gridBagConstraints);

       attList.startRefresher();
       mainFrame.pack();

       mainFrame.setVisible(true);
       
    } // end of main ()
        
        
}
