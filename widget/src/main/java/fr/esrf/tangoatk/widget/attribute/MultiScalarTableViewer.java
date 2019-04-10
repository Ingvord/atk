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
 
// File:          MultiScalarTableViewer.java
// Created:       2007-05-09 15:03:37, poncet
// By:            <poncet@esrf.fr>
// 
// $Id$
// 
// Description:       
package fr.esrf.tangoatk.widget.attribute;


import java.util.*;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.Component;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.event.*;

import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;


/** A MultiScalarTableViewer is a Swing JTable which displays the "read value" of 
 * several scalar attributes each of them in one cell of the table.
 * The table cells are not editable, but a double click on any cell will display a
 * panel to set the attribute value only if the attribute is writable.
 *
 */
public class MultiScalarTableViewer extends JTable
{

   protected int                   nbRows=0;
   protected int                   nbColumns=0;   
   protected String[]              columnIdents=null;
   protected String[]              rowIdents=null;
   protected IEntity[][]           entityModels=null;

   protected MultiScalarViewerTableModel       tabModel=null;
   protected MultiScalarCellRendererAndEditor  cellRendererAndEditor=null;
   private RowIdentsCellRenderer               rowIdentsRenderer=null;
   private ColHeaderCellRenderer               colHeadRenderer=null;
   
   private long                           firstClickTime;
   protected boolean                 alarmEnabled=true;
   protected boolean                 unitVisible=true;
   protected Color                   panelBackground=null;
   
   private RootPaneContainer              rpcParent = null;
   private JDialog                        attSetDialWindow=null;
   private ScalarAttributeSetPanel        attSetPanel=null;
   
   protected boolean                        noAttModel=true;

   // ---------------------------------------------------
   // Contruction
   // ---------------------------------------------------
   public MultiScalarTableViewer()
   {
       firstClickTime = 0;
       tabModel = new MultiScalarViewerTableModel();
       setModel(tabModel);
       cellRendererAndEditor = new MultiScalarCellRendererAndEditor(this);
       rowIdentsRenderer = new RowIdentsCellRenderer();
       colHeadRenderer = new ColHeaderCellRenderer();
       //panelBackground = rowIdentsRenderer.getBackground();
       panelBackground = new java.awt.Color(235,235,235);
       //setAutoResizeMode(AUTO_RESIZE_OFF);
       setRowMargin(0);
       getColumnModel().setColumnMargin(getColumnModel().getColumnMargin()+2);
       this.addMouseListener(cellRendererAndEditor);
   }
   
   //override the getCellRenderer method of JTable
   public TableCellRenderer getCellRenderer(int row, int column)
   {
       if (tabModel.getHasRowLabels())
       {
	  if (column == 0)
	     return rowIdentsRenderer;
       }

       Object obj=tabModel.getValueAt(row, column);

       if (obj != null)
	  if (obj instanceof SimpleScalarViewer)
	      return cellRendererAndEditor;

       if (obj != null)
	  if (obj instanceof SimpleEnumScalarViewer)
	      return cellRendererAndEditor;

       if (obj != null)
	  if (obj instanceof BooleanScalarCheckBoxViewer)
	      return cellRendererAndEditor;

       return super.getCellRenderer(row, column);
   } 

    @Override
    public TableCellEditor getCellEditor(int row, int column)
    {

       Object obj=tabModel.getValueAt(row, column);

       if (obj != null)
	  if (obj instanceof SimpleScalarViewer)
	      return cellRendererAndEditor;

       if (obj != null)
	  if (obj instanceof SimpleEnumScalarViewer)
	      return cellRendererAndEditor;

       if (obj != null)
	  if (obj instanceof BooleanScalarCheckBoxViewer)
	      return cellRendererAndEditor;
        
        return super.getCellEditor(row, column);
    }
   
   
   
   
    @Override
    public boolean isCellEditable(int row, int column)
    {
       return tabModel.isCellEditable(row, column);        
    }
   
   //Should override the setModel method of JTable because it is not autorized to call the setModel method of the superclass
   // But overriding this method make a bug in NetBeans IDE when trying to add
   // MultiScalarTableViewer from the palette inside the form editor.
   // the issue has been submitted to http://www.netbeans.org/community/issues.html
   
   //public void setModel(TableModel  tm)
   //{
   //}
   
   public Color getPanelBackground()
   {
      return panelBackground;
   }
   
   public void setPanelBackground(Color bg)
   {
       panelBackground = bg;
       attSetDialWindow.setBackground(panelBackground);
       attSetPanel.setBackground(panelBackground);
   }

   
   private void doEdit(int r, int c )
   {
       int  col = c;
       if (tabModel.getHasRowLabels())
          col = c-1;
	  
       if ((r < 0) || (col < 0)) return;
       if ((r >= nbRows) || (col >= nbColumns)) return;
       
       IAttribute iatt = (IAttribute) entityModels[r][col];
       if (iatt == null) return;
       if (!iatt.isWritable() ) return;
	
       if (    !(iatt instanceof INumberScalar)
	    && !(iatt instanceof IStringScalar)
	    && !(iatt instanceof IEnumScalar) )
	   return;
       
       if ( (attSetDialWindow == null) || (attSetPanel == null) )
       {
	     creatScalarSetWindows();	  
       }
       
       if ( (attSetDialWindow == null) || (attSetPanel == null) )
          return;
	  	  
       if (attSetPanel.getAttModel() != iatt)
       {
	  attSetDialWindow.setVisible(false); // To repaint properly when changing attribute type
          attSetPanel.setAttModel(iatt);
	  attSetDialWindow.setTitle(iatt.getName());
	  attSetDialWindow.pack();
       }
       ATKGraphicsUtils.centerDialog(attSetDialWindow);
       attSetDialWindow.setVisible(true);
   }
   
   
   private void creatScalarSetWindows()
   {
	Component   parent = this;
	while (rpcParent == null)
	{
	    parent = parent.getParent();
	    if (parent == null)
	    {
	       break;
	    }
	    if (parent instanceof RootPaneContainer)
	    {
	        rpcParent = (RootPaneContainer) parent;
		break;
	    }
	}
	
	if (rpcParent != null)
	   System.out.println("MultiScalarTableViewer : the parent class (implementing RootPaneContainer) is : "+rpcParent.getClass().getName());


	if (rpcParent == null)
           attSetDialWindow = new JDialog();
	else
	   if (rpcParent instanceof Frame)
	      attSetDialWindow = new JDialog( (Frame) rpcParent );
	   else
	      if (rpcParent instanceof Dialog)
	         attSetDialWindow = new JDialog( (Dialog) rpcParent );
	      else
	         attSetDialWindow = new JDialog();
		 
	attSetDialWindow.getContentPane().setLayout(new java.awt.GridBagLayout());
        attSetDialWindow.getContentPane().setBackground(panelBackground);
	
	attSetPanel = new ScalarAttributeSetPanel();
        attSetPanel.setBackground(panelBackground);
	attSetPanel.setFont(getFont());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = java.awt.GridBagConstraints.BOTH;
	gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        attSetDialWindow.getContentPane().add(attSetPanel, gbc);

        JButton dismissButton = new JButton();
        dismissButton.setText("Dismiss");
        dismissButton.addActionListener(new java.awt.event.ActionListener()
			      {
        			  public void actionPerformed(java.awt.event.ActionEvent evt) {
                		      attSetDialWindow.setVisible(false);
        			  }
        		      });
	
        gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
	gbc.anchor = java.awt.GridBagConstraints.CENTER;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        attSetDialWindow.getContentPane().add(dismissButton, gbc);
   }


   // ---------------------------------------------------
   // Property stuff
   // ---------------------------------------------------

   /**
    *<code>getNbRows</code> returns the number of rows
    * 
    * @return a <code>int[]</code> Number of rows
    */
   public int getNbRows ()
   {
       return nbRows;
   }

   /**
    * <code>setNbRows</code> sets the number of rows. The number of rows can only be set
    * when there is no attribute model for the viewer. No call to setModelAt yet.
    *
    * @param nr Number of rows
    */
   public void setNbRows (int nr)
   {
      if (nr <= 0) return;

      if (entityModels != null) return;
      if (rowIdents != null)
         if (nr != rowIdents.length)
	    return;

      nbRows = nr;
   }
   /**
    *<code>getNbColumns</code> returns the number of columns
    * 
    * @return a <code>int[]</code> Number of columns
    */
   public int getNbColumns ()
   {
       return nbColumns;
   }

   /**
    * <code>setNbColumns</code> sets the number of columns. The number of columns can only be set
    * when there is no attribute model for the viewer. No call to setModelAt yet.
    *
    * @param nc Number of columns
    */
   public void setNbColumns (int nc)
   {
      if (nc <= 0) return;

      if (entityModels != null) return;
      if (columnIdents != null)
         if (nc != columnIdents.length)
	    return;

      nbColumns = nc;
   }


   /**
    *<code>getColumnIdents</code> returns a String Array corresponding to the column identifiers
    * 
    * @return a <code>String[]</code> value
    */
   public String[] getColumnIdents ()
   {
       return columnIdents;
   }

   /**
    * <code>setColumnIdents</code> sets the table's column identifiers
    * The size of the colIds array must be exactly the same as the number of columns for
    * the attribute models.If the attribute model is not set yet the size of the colIds will change
    * the nbColumns property as well.
    *
    * @param colIds Column Ids
    */
   public void setColumnIdents (String[] colIds)
   {
      if (colIds == null)
      {
         columnIdents = null;
	 return;
      }

      if (entityModels != null)
         if (entityModels[0].length != colIds.length)
	    return;

      if (entityModels == null)
         nbColumns = colIds.length;

      columnIdents = colIds;
      if ((noAttModel) && (nbRows > 0))
         initAttModels();
   }


   /**
    *<code>getRowIdents</code> returns a String Array corresponding to the row identifiers
    * 
    * @return a <code>String[]</code> value
    */
   public String[] getRowIdents ()
   {
       return rowIdents;
   }

   /**
    * <code>setRowIdents</code> sets the table's row identifiers
    * The RowIdents can only be set when there is no attribute model for the viewer. No call to setModelAt yet.If the attribute model is not set yet the size of the rowIds will change
    * the nbRow sproperty as well.
    *
    * @param rowIds Row ids
    */
   public void setRowIdents (String[] rowIds)
   {
      if (rowIds == null)
      {
         rowIdents = null;
	 return;
      }

      if (entityModels != null)
         if (entityModels.length != rowIds.length)
	    return;

      if (entityModels == null)
         nbRows = rowIds.length;

      rowIdents = rowIds;
      if ((noAttModel) && (nbColumns > 0))
         initAttModels();
   }


   /**
    *<code>getAlarmEnabled</code> returns a boolean : true if the quality factor is displayed in the scalarviewers
    * 
    * @return a <code>boolean</code> value
    */
   public boolean getAlarmEnabled ()
   {
       return alarmEnabled;
   }

   /**
    * <code>setAlarmEnabled</code> sets the quality factor display to on or off
    *
    * @param alarm <code>boolean</code> if true the attribute quality factor will be displayed as the background colour of the cell
    */
   public void setAlarmEnabled (boolean alarm)
   {
      if (alarmEnabled == alarm)
	 return;
	 
      alarmEnabled = alarm;
      
      if (entityModels == null)
          return;

      for (int i=0; i<entityModels.length; i++)
          for (int j=0; j<entityModels[i].length; j++)
	  {
	      int col = j;
	      if (tabModel.getHasRowLabels())
	          col = j+1;
	      Object obj = tabModel.getValueAt(i, col);
	      if (obj instanceof SimpleScalarViewer)
	      {
	         SimpleScalarViewer ssv = (SimpleScalarViewer) obj;
		 ssv.setAlarmEnabled(alarmEnabled);
	      }
	  }
   }

   /**
    * Detemines whether the unit is visible
    * @return true if unit is visible
    */
   public boolean getUnitVisible()
   {
      return unitVisible;
   }

   /**
    * Displays or hides the unit.
    * @param b true to display the unit, false otherwise
    */
   public void setUnitVisible(boolean b)
   {
      if (unitVisible == b)
	 return;
	 
      unitVisible = b;

      if (attSetPanel != null) attSetPanel.setUnitVisible(unitVisible);
      
      if (entityModels == null)
          return;

      for (int i=0; i<entityModels.length; i++)
          for (int j=0; j<entityModels[i].length; j++)
	  {
	      int col = j;
	      if (tabModel.getHasRowLabels())
	          col = j+1;
	      Object obj = tabModel.getValueAt(i, col);
	      if (obj instanceof SimpleScalarViewer)
	      {
	         SimpleScalarViewer ssv = (SimpleScalarViewer) obj;
		 ssv.setUnitVisible(unitVisible);
	      }
	  }
   }
   
   public JLabel getRowIdCellRenderer()
   {
       return rowIdentsRenderer;
   }



   public IEntity[][] getEntityModels()
   {
      return entityModels;
   }


   public void setModelAt( IAttribute iatt, int r, int c )
   {
       boolean supportedAttribute = false;
       
       if ((nbRows <= 0) || (nbColumns <= 0))
       {
          System.out.println("Please set the number of columns and rows of the table before calling setModelAt.");
	  return;
       }
       
       if (    (iatt instanceof INumberScalar)
            || (iatt instanceof IStringScalar)
            || (iatt instanceof IEnumScalar)
	    || (iatt instanceof IBooleanScalar) )
	    supportedAttribute = true;
	    
       if (!supportedAttribute)
       {
          System.out.println("Unsupported type of attribute; setModelAt failed.");
	  return;
       }
       
       if (entityModels == null)
       {
          initAttModels();
       }
       
       if (noAttModel) noAttModel=false;
       
       if ((r < 0) || (c < 0)) return;
       if ((r >= nbRows) || (c >= nbColumns)) return;
       
       clearModelAt(r, c);
       
       if (iatt == null)
          return;
	  
       entityModels[r][c] = iatt;
       
       if (iatt instanceof INumberScalar)
       {
           INumberScalar ins = (INumberScalar) iatt;
	   addAttributeAt(ins, r, c);
	   return;
       }
       
       if (iatt instanceof IStringScalar)
       {
           IStringScalar iss = (IStringScalar) iatt;
	   addAttributeAt(iss, r, c);
	   return;
       }
       
       if (iatt instanceof IEnumScalar)
       {
           IEnumScalar ies = (IEnumScalar) iatt;
	   addAttributeAt(ies, r, c);
	   return;
       }
       
       if (iatt instanceof IBooleanScalar)
       {
           IBooleanScalar ibs = (IBooleanScalar) iatt;
	   addAttributeAt(ibs, r, c);
	   return;
       }
   }


   private void addAttributeAt( INumberScalar ins, int r, int c )
   {       
       SimpleScalarViewer  ssViewer = new SimpleScalarViewer();
       ssViewer.setBackgroundColor(getBackground());
       ssViewer.setFont(getFont());
       ssViewer.setModel(ins);
       ssViewer.setAlarmEnabled(alarmEnabled);
       ssViewer.setUnitVisible(unitVisible);
       ssViewer.setHasToolTip(true);
       
       if ( ((double) rowHeight) < ssViewer.getPreferredSize().getHeight() )
          rowHeight = (int) ssViewer.getPreferredSize().getHeight();
       tabModel.addAttributeAt(r,c,ins,ssViewer);
   }


   private void addAttributeAt( IStringScalar iss, int r, int c )
   { 
       SimpleScalarViewer  ssViewer = new SimpleScalarViewer();
       ssViewer.setBackgroundColor(getBackground());
       ssViewer.setFont(getFont());
       ssViewer.setModel(iss);
       ssViewer.setAlarmEnabled(alarmEnabled);
       ssViewer.setHasToolTip(true);
       
       if ( ((double) rowHeight) < ssViewer.getPreferredSize().getHeight() )
          rowHeight = (int) ssViewer.getPreferredSize().getHeight();
       tabModel.addAttributeAt(r,c,iss,ssViewer);
   }


   private void addAttributeAt( IEnumScalar ies, int r, int c )
   { 
       SimpleEnumScalarViewer  enumViewer = new SimpleEnumScalarViewer();
       enumViewer.setBackgroundColor(getBackground());
       enumViewer.setFont(getFont());
       enumViewer.setModel(ies);
       enumViewer.setAlarmEnabled(alarmEnabled);
       
       if ( ((double) rowHeight) < enumViewer.getPreferredSize().getHeight() )
          rowHeight = (int) enumViewer.getPreferredSize().getHeight();
       tabModel.addAttributeAt(r,c,ies,enumViewer);
   }


   private void addAttributeAt( IBooleanScalar ibs, int r, int c )
   { 
       BooleanScalarCheckBoxViewer  boolCbViewer = new BooleanScalarCheckBoxViewer();
       boolCbViewer.setBorderPainted(true);
       boolCbViewer.setBorder(javax.swing.plaf.BorderUIResource.getEtchedBorderUIResource());
       boolCbViewer.setBackground(getBackground());
       boolCbViewer.setFont(getFont());
       boolCbViewer.setAttModel(ibs);
       //boolCbViewer.setQualityEnabled(alarmEnabled);
       boolCbViewer.setTrueLabel(new String());
       boolCbViewer.setFalseLabel(new String());
       boolCbViewer.setHasToolTip(true);
       boolCbViewer.setHorizontalAlignment(SwingConstants.CENTER);

       
       if ( ((double) rowHeight) < boolCbViewer.getPreferredSize().getHeight() )
          rowHeight = (int) boolCbViewer.getPreferredSize().getHeight();
       tabModel.addAttributeAt(r,c,ibs,boolCbViewer);
   }
   
   public void clearModelAt( int r, int c )
   {
       if (entityModels == null) return;
       if ((nbRows <= 0) || (nbColumns <= 0))
       {
          System.out.println("Please set the number of columns and rows before calling clearModelAt.");
	  return;
       }
       
       if ((r < 0) || (c < 0)) return;
       if ((r >= nbRows) || (c >= nbColumns)) return;
       
       if (entityModels[r][c] == null) return;
       
       if (entityModels[r][c] instanceof IAttribute)
       {
           tabModel.removeAttributeAt(r,c);
	   entityModels[r][c] = null;
       } 
       
   }


   public void clearModel()
   {
       if (entityModels == null) return;
       
       for (int i=0; i<entityModels.length; i++)
       {
          for (int j=0; j<entityModels[i].length; j++)
	       clearModelAt(i,j);
       }
       
       entityModels = null;
       noAttModel=true;
       columnIdents=null;
       rowIdents=null;
       nbRows=0;
       nbColumns=0;

       tabModel.setColumnCount(0);
       tabModel.setRowCount(0);
       
       tabModel = new MultiScalarViewerTableModel();
       setModel(tabModel);
   }


   protected void initAttModels()
   {
      if ((nbRows <= 0) || (nbColumns <= 0))
      {
         System.out.println("Please set the number of columns and rows before calling initAttModels.");
	 return;
      }
      
      // The following block of code has been added because NetBeans sets the JTable model
      // just after instantiation of MultiScalarTableViewer. So we should take the occasion
      // of initAttModels to restore the normal MultiScalarViewerTableModel. The best solution would
      // be to override the inherited method setModel but this will lead to a bug in NetBeans IDE
      // The following 6 lines are a workaround to this problem.
      TableModel   tm = super.getModel();
      if (! (tm instanceof MultiScalarViewerTableModel) )
      {
	  tabModel = new MultiScalarViewerTableModel();
	  setModel(tabModel);
      }

      entityModels = new IEntity[nbRows][nbColumns];
      for (int i=0; i<nbRows; i++)
          for (int j=0; j<nbColumns; j++)
	       entityModels[i][j] = null;
      if (columnIdents == null)
         columnIdents = new String[nbColumns];
      tabModel.init();
      initColumnHeaderRenderers();
   }
   
   protected void initColumnHeaderRenderers()
   {
      if (columnIdents == null)
         return;
      
      for (int i=0; i<columnIdents.length; i++)
      {
          try
	  {
	     TableColumn tc= getColumn(columnIdents[i]);
	     tc.setHeaderRenderer(colHeadRenderer);
	  }
	  catch (IllegalArgumentException iae)
	  {
	  }
      }
   }
    

       protected class MultiScalarViewerTableModel extends DefaultTableModel
                                                   implements INumberScalarListener,
					                      IStringScalarListener,
					                      IEnumScalarListener,
						              IBooleanScalarListener
       {
	   protected  boolean                                   hasRowLabels = false;
	   protected  HashMap<IEntity, ArrayList<Integer>>      entityMap = null;
           protected  Object[][]                                tableData = null;

	   /** Creates a new instance of MSviewerTableModel */
	   protected MultiScalarViewerTableModel()
	   {
	       entityMap = new HashMap<IEntity, ArrayList<Integer>> ();
	   }
	   
	   public boolean isCellEditable(int row, int column)
	   {
               IEntity  ient = getEntity(row, column);
               if (ient == null) return false;
               
               if (ient instanceof IAttribute)
               {
                   IAttribute iatt = (IAttribute) ient;
                   if (iatt.isWritable())
                       return true;
               }
               return false;
	   }
           
           private IEntity getEntity(int row, int column)
           {
               if ((row < 0) || (column < 0)) return null;
               if (tableData == null) return null;
               if (entityModels == null) return null;
               
               int col = column;
               if (row < entityModels.length)
               {
                   if (hasRowLabels)
                       col = col - 1;
                   if (col < entityModels[0].length)
                   {
                       IEntity  ie = entityModels[row][col];
                       return ie;
                   }
               }               
               return null;               
           }

	   protected void init ()
	   {
               if (entityModels == null) return;
	       if (entityModels.length != nbRows)
		  nbRows = entityModels.length;
	       if (entityModels[0].length != nbColumns)
		  nbColumns = entityModels[0].length;

	       if (rowIdents != null)
		  if (rowIdents.length != nbRows)
	             rowIdents = null;
               if (rowIdents != null)
	       {
		  hasRowLabels = true;
		  
		  String[]  colIds=null;
		  if (columnIdents != null)
		  {
		      colIds = new String[columnIdents.length+1];
		      colIds[0]=" ";
		      for (int j=0; j<columnIdents.length; j++)
		          colIds[j+1] = columnIdents[j];
		  }
                  else
                  {
                      colIds = new String[nbColumns+1];
                      for (int j=0; j<nbColumns+1; j++)
		          colIds[j] =" ";
                  }
		  tableData= new Object[entityModels.length][entityModels[0].length+1];
                  setDataVector(tableData, colIds);
		  
		  for (int i=0; i<nbRows; i++)
		      setValueAt(rowIdents[i], i, 0);
		  
	       }
	       else
               {
		  tableData= new Object[entityModels.length][entityModels[0].length];
                  this.setDataVector(tableData, columnIdents);
               }
               //this.fireTableStructureChanged();
               //this.fireTableDataChanged();
               //doLayout();
	   }

	   void addAttributeAt(int r, int c, IAttribute iatt, SimpleScalarViewer ssViewer)
	   {
              int              col;
	      INumberScalar    ins=null;
	      IStringScalar    iss=null;
	      
	      if (iatt instanceof INumberScalar)
	         ins = (INumberScalar) iatt;
	      else
	         if (iatt instanceof IStringScalar)
		    iss = (IStringScalar) iatt;
	      
	      if ( (ins == null) && (iss == null) )
	         return;
		 
	      if (ins != null)
	         ins.addNumberScalarListener(this);
	      else
	         if (iss != null)
	            iss.addStringScalarListener(this);
	      
	      col = c;
	      if (hasRowLabels)
	         col = c+1;
	      setValueAt(ssViewer, r, col);
	      ArrayList<Integer>  attIndexes = new ArrayList<Integer> ();
	      attIndexes.add(0, new Integer(r));
	      attIndexes.add(1, new Integer(col));
	      if (!entityMap.containsKey(iatt))
	         entityMap.put(iatt, attIndexes);
              fireTableDataChanged();
	   }

	   void addAttributeAt(int r, int c, IAttribute iatt, SimpleEnumScalarViewer enumViewer)
	   {
              int              col;
	      IEnumScalar      ies=null;
	      
	      if (iatt instanceof IEnumScalar)
	         ies = (IEnumScalar) iatt;

	      if (ies == null)
	         return;
	      
	      col = c;
	      if (hasRowLabels)
	         col = c+1;
		 
	      ies.addEnumScalarListener(this);
	      setValueAt(enumViewer, r, col);
	      
	      ArrayList<Integer>  attIndexes = new ArrayList<Integer> ();
	      attIndexes.add(0, new Integer(r));
	      attIndexes.add(1, new Integer(col));
	      if (!entityMap.containsKey(iatt))
	         entityMap.put(iatt, attIndexes);
              fireTableDataChanged();
	   }

	   void addAttributeAt(int r, int c, IAttribute iatt, BooleanScalarCheckBoxViewer boolViewer)
	   {
              int              col;
	      IBooleanScalar   ibs=null;
	      
	      if (iatt instanceof IBooleanScalar)
	         ibs = (IBooleanScalar) iatt;

	      if (ibs == null)
	         return;
	      
	      col = c;
	      if (hasRowLabels)
	         col = c+1;
		 
	      ibs.addBooleanScalarListener(this);
	      setValueAt(boolViewer, r, col);
	      
	      ArrayList<Integer>  attIndexes = new ArrayList<Integer> ();
	      attIndexes.add(0, new Integer(r));
	      attIndexes.add(1, new Integer(col));
	      if (!entityMap.containsKey(iatt))
	         entityMap.put(iatt, attIndexes);
              fireTableDataChanged();
	   }

	   protected void removeAttributeAt(int r, int c)
	   {
              int col = c;
	      if (hasRowLabels)
		 col = c+1;

	      Object obj = getValueAt(r, col);

	      if (obj == null) return;

	      if (obj instanceof SimpleScalarViewer)
	      {
		  SimpleScalarViewer  ssv = (SimpleScalarViewer) obj;
		  
		  removeAttributeAt(ssv, r, col);
		  return;
	      }

	      if (obj instanceof SimpleEnumScalarViewer)
	      {
		  SimpleEnumScalarViewer  enumv = (SimpleEnumScalarViewer) obj;
		  
		  removeAttributeAt(enumv, r, col);
		  return;
	      }

	      if (obj instanceof BooleanScalarCheckBoxViewer)
	      {
		  BooleanScalarCheckBoxViewer  boolv = (BooleanScalarCheckBoxViewer) obj;
		  
		  removeAttributeAt(boolv, r, col);
		  return;
	      }
	   }
	   
	   private void removeAttributeAt(SimpleScalarViewer ssv, int r, int c)
	   {
	      INumberScalar ins = ssv.getNumberModel();
	      IStringScalar iss = ssv.getStringModel();
	      if (ins != null)
	      {
		 if (entityMap.containsKey(ins))
		    entityMap.remove(ins);
		 ins.removeNumberScalarListener(this);
	      }
	      else
		 if (iss != null)
		 {
		    if (entityMap.containsKey(iss))
		       entityMap.remove(iss);
	            iss.removeStringScalarListener(this);
		 }
	      ssv.clearModel();
	      ssv=null;
	      setValueAt(null, r, c);
              fireTableDataChanged();
	   }
	   
	   private void removeAttributeAt(SimpleEnumScalarViewer enumv, int r, int c)
	   {
	      IEnumScalar ies = enumv.getModel();
	      if (ies != null)
	      {
		 if (entityMap.containsKey(ies))
		    entityMap.remove(ies);
		 ies.removeEnumScalarListener(this);
	      }
	      enumv.clearModel();
	      enumv=null;
	      setValueAt(null, r, c);
              fireTableDataChanged();
	   }
	   
	   private void removeAttributeAt(BooleanScalarCheckBoxViewer boolv, int r, int c)
	   {
	      IBooleanScalar ibs = boolv.getAttModel();
	      if (ibs != null)
	      {
		 if (entityMap.containsKey(ibs))
		    entityMap.remove(ibs);
		 ibs.removeBooleanScalarListener(this);
	      }
	      boolv.clearModel();
	      boolv=null;
	      setValueAt(null, r, c);
              fireTableDataChanged();
	   }
	   
	   boolean getHasRowLabels()
	   {
	       return hasRowLabels;
	   }
	   
	   
	   // -------------------------------------------------------------
	   // Any attribute listener interface
	   // -------------------------------------------------------------
	   public void stateChange(AttributeStateEvent evt)
	   {
	       IAttribute iatt = (IAttribute) evt.getSource();
	       doUpdateAttCell(iatt);
	   }

	   public void errorChange(ErrorEvent evt)
	   {
	       Object src = evt.getSource();
	       if (src instanceof IAttribute)
	       {
	           IAttribute  ia = (IAttribute) src;
		   doUpdateAttCell(ia);
	       }
	   }
	   
	   // -------------------------------------------------------------
	   // Number scalar listener interface
	   // -------------------------------------------------------------
	   public void numberScalarChange(NumberScalarEvent evt)
	   {
	       INumberScalar ins = evt.getNumberSource();
	       doUpdateAttCell(ins);
	   }
	   
	   // -------------------------------------------------------------
	   // String scalar listener interface
	   // -------------------------------------------------------------
	   public void stringScalarChange(StringScalarEvent evt)
	   {
	       IStringScalar iss = (IStringScalar) evt.getSource();
	       doUpdateAttCell(iss);
	   }
	   
	   // -------------------------------------------------------------
	   // Enum Scalar listener interface
	   // -------------------------------------------------------------
	   public void enumScalarChange(EnumScalarEvent evt)
	   {
	       IEnumScalar ies = (IEnumScalar) evt.getSource();
	       doUpdateAttCell(ies);
	   }
	   
	   // -------------------------------------------------------------
	   // Boolean Scalar listener interface
	   // -------------------------------------------------------------
	   public void booleanScalarChange(BooleanScalarEvent evt)
	   {
	       IBooleanScalar ibs = (IBooleanScalar) evt.getSource();
	       doUpdateAttCell(ibs);
	   }
	   
	   protected void doUpdateAttCell(IAttribute  iatt)
	   {
	       if (!entityMap.containsKey(iatt))
	          return;
		  
	       ArrayList<Integer> attIndexes = entityMap.get(iatt);
	       if (attIndexes == null)
	          return;
		  
	       if (attIndexes.size() >= 2)
	       {
		  Integer  indObj = attIndexes.get(0);
		  int row = indObj.intValue();
		  
		  indObj = attIndexes.get(1);
		  int col = indObj.intValue();
		  
		  fireTableCellUpdated(row, col);
	       }
	   }

       }
       
       
       protected class MultiScalarCellRendererAndEditor extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, MouseListener
       {
           protected JTable      table;
           protected Component   rendererComp;
           protected Component   editorComp;
           protected Object      editorValue;

     /** Creates a new instance of MultiScalarViewerCellRenderer
      * @param tbl Table to use for this renderer
      */
	   protected MultiScalarCellRendererAndEditor(JTable tbl)
	   {
               table = tbl;
	   }
	   
	   public Component getTableCellRendererComponent(JTable table, Object value,
                                                	  boolean isSelected, boolean hasFocus, int row, int column)
	   {
	       SimpleScalarViewer             ssv;
	       SimpleEnumScalarViewer         enumv;
	       BooleanScalarCheckBoxViewer    boolv;
	       
	       if (value instanceof SimpleScalarViewer)
	       {
		  ssv = (SimpleScalarViewer) value;
                  rendererComp = ssv;
		  return rendererComp;
	       }
	       
	       if (value instanceof SimpleEnumScalarViewer)
	       {
		  enumv = (SimpleEnumScalarViewer) value;
                  rendererComp = enumv;
		  return rendererComp;
	       }
	       
	       if (value instanceof BooleanScalarCheckBoxViewer)
	       {
		  boolv = (BooleanScalarCheckBoxViewer) value;
                  rendererComp = boolv;
		  return rendererComp;
	       }

	       return new JLabel("Unsupported Class");
	    }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
            {
                if (value instanceof Component)
                {
                    editorComp = (Component) value;
                    this.editorValue = value;
                    return editorComp;
                }
                return new JLabel("Unsupported Condition");
            }
            
            @Override
            public Object getCellEditorValue()
            {
                return editorValue;
            }

            @Override
            public void mouseClicked(MouseEvent e)
            {
            }

            @Override
            public void mousePressed(MouseEvent e)
            {
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                boolean ed = table.isEditing();
                                        TableCellEditor tce = table.getCellEditor();

                if ((table.getCellEditor() == this) && table.isEditing())
                {
                    table.getCellEditor().stopCellEditing();
//                    System.out.println("released");
//                    System.out.println("tableMouseReleased : row="+getSelectedRow()+" column="+getSelectedColumn());
                   doEdit(getSelectedRow(), getSelectedColumn());
                }
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
            }

       }
       
       class RowIdentsCellRenderer extends JLabel implements TableCellRenderer
       {
	   /** Creates a new instance of RowIdentsCellRenderer */
	   RowIdentsCellRenderer()
	   {
	       setHorizontalAlignment(LEFT);
               setOpaque(true);
	       setBackground(new java.awt.Color(220,220,220));
	       //setBorder(javax.swing.plaf.BorderUIResource.getRaisedBevelBorderUIResource());
	       setBorder(javax.swing.plaf.BorderUIResource.getEtchedBorderUIResource());
	   }
	   
	   public Component getTableCellRendererComponent(JTable table, Object value,
                                                	  boolean isSelected, boolean hasFocus, int row, int column)
	   {
	       String    rowId;
	       
	       if (value instanceof String)
	       {
		  rowId = (String) value;
		  setText(rowId);
		  return this;
	       }
	       else
		  return new JLabel("Unsupported row id Class");
	   }
       }
       
       class ColHeaderCellRenderer extends RowIdentsCellRenderer
       {
	   /** Creates a new instance of ColHeaderCellRenderer */
	   ColHeaderCellRenderer()
	   {
	       super();
	       setHorizontalAlignment(CENTER);
	   }
	   
	   public Component getTableCellRendererComponent(JTable table, Object value,
                                                	  boolean isSelected, boolean hasFocus, int row, int column)
	   {
	       String    colId;
	       
	       if (value instanceof String)
	       {
		  colId = (String) value;
		  setText(colId);
		  return this;
	       }
	       else
		  return new JLabel("Unsupported column header Class");
	   }
       }
   
   
   
   // ---------------------------------------------------
   // Main test fucntion
   // ---------------------------------------------------
   static public void main(String args[])
   {
        IAttribute                att;
	String[]                  colLabs = {"att_un", "att_deux", "att_trois", "att_cinq", "att_six", "att_bool"};
	String[]                  rowLabs = {"jlp/test/1", "jlp/test/2"};
	
	AttributeList             attl = new AttributeList();
	JFrame                    f = new JFrame();
	MultiScalarTableViewer    mstv = new MultiScalarTableViewer();
	
        IAttribute[][]            attArray=null;
	//mstv.setAlarmEnabled(false);
	mstv.setUnitVisible(false);
        mstv.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 16));
	//mstv.getRowIdCellRenderer().setBackground(f.getBackground());
        //mstv.setNbRows(2);
	//mstv.setNbColumns(6);
        //mstv.setRowIdents(rowLabs);
	//mstv.setColumnIdents(colLabs);
        mstv.setNbRows(2);
	mstv.setNbColumns(6);
        mstv.setRowIdents(rowLabs);
	mstv.setColumnIdents(colLabs); 
        attArray = new IAttribute[2][6];
	
	try
	{
	   att = (IAttribute) attl.add("jlp/test/1/att_un");
           attArray[0][0] = att;
	   mstv.setModelAt(att, 0, 0);
           att = (IAttribute) attl.add("jlp/test/1/att_deux");
           attArray[0][1] = att;
	   mstv.setModelAt(att, 0, 1);
           att = (IAttribute) attl.add("jlp/test/1/att_trois");
           attArray[0][2] = att;
	   mstv.setModelAt(att, 0, 2);
           //att = (IAttribute) attl.add("jlp/test/1/att_quatre");
           att = (IAttribute) attl.add("jlp/test/1/att_cinq");
           //att = (IAttribute) attl.add("fp/test/1/string_scalar");
           attArray[0][3] = att;
	   mstv.setModelAt(att, 0, 3);
           att = (IAttribute) attl.add("jlp/test/1/att_six");
           attArray[0][4] = att;
	   mstv.setModelAt(att, 0, 4);
           att = (IAttribute) attl.add("jlp/test/1/att_boolean");
           attArray[0][5] = att;
	   mstv.setModelAt(att, 0, 5);
	   att = (IAttribute) attl.add("jlp/test/2/att_un");
           attArray[1][0] = att;
	   mstv.setModelAt(att, 1, 0);
           att = (IAttribute) attl.add("jlp/test/2/att_deux");
           attArray[1][1] = att;
	   mstv.setModelAt(att, 1, 1);
           att = (IAttribute) attl.add("jlp/test/2/att_trois");
           attArray[1][2] = att;
	   mstv.setModelAt(att, 1, 2);
           //att = (IAttribute) attl.add("jlp/test/2/att_quatre");
           att = (IAttribute) attl.add("jlp/test/2/att_cinq");
           //att = (IAttribute) attl.add("fp/test/2/string_scalar");
           attArray[1][3] = att;
	   mstv.setModelAt(att, 1, 3);
           att = (IAttribute) attl.add("jlp/test/2/att_six");
           attArray[1][4] = att;
	   mstv.setModelAt(att, 1, 4);
           att = (IAttribute) attl.add("jlp/test/2/att_boolean");
           attArray[1][5] = att;
	   mstv.setModelAt(att, 1, 5);
	}
	catch (Exception ex)
	{
           ex.printStackTrace();
	   System.out.println("Cannot connect to jlp/test/1");
	}

        attl.startRefresher();

	
	// It is necessary to put the table inside a JScrollPane. The JTable does not
	// display the column names if the JTable is not in a scrollPane!!!
        mstv.setPreferredScrollableViewportSize(new java.awt.Dimension(700, 70));
        JScrollPane scrollPane = new JScrollPane(mstv);
	
	f.setContentPane(scrollPane);
	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//mstv.doLayout();
	f.pack();
	f.setVisible(true);
        //mstv.setModelAt(attArray[0][2], 0, 2);

        try
	{
	   System.in.read();
	}
	catch (Exception ex)
	{
	   System.out.println("cannot read");
	}
        
	mstv.clearModel();
        //mstv.setNbRows(6);
	//mstv.setNbColumns(2);
        //mstv.setRowIdents(colLabs);
	//mstv.setColumnIdents(rowLabs); 
        mstv.setNbRows(2);
	mstv.setNbColumns(6);
        mstv.setRowIdents(rowLabs);
	mstv.setColumnIdents(colLabs);
        
        //mstv.setModelAt(attArray[1][4], 4, 1);
   }
    

}
