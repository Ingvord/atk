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
 * TrendSelectionNode.java
 *
 * Created on May 13, 2002, 4:28 PM
 */

package fr.esrf.tangoatk.widget.attribute;

import fr.esrf.Tango.DevState;
import fr.esrf.tangoatk.core.*;
import fr.esrf.tangoatk.widget.util.chart.*;

import java.awt.*;
import java.util.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.tree.*;

/*
 * @author  pons
 */

class TrendSelectionNode extends DefaultMutableTreeNode implements INumberScalarListener,IBooleanScalarListener,ISpectrumListener,PropertyChangeListener,IEnumScalarListener,IDevStateScalarListener {

  // Local declaration
  private String devname = "";
  private INumberScalar model;
  private IEnumScalar modele;
  private IBooleanScalar modelb;
  private IDevStateScalar modelst;
  private INumberSpectrum models;
  private int spectrumIdx;
  private int selected;
  private JLDataView data;
  private JLDataView minAlarmData;
  private JLDataView maxAlarmData;
  private long lastErrorTime = 0;
  private double minAlarm;
  private double maxAlarm;
  private boolean showMinAlarm;
  private boolean showMaxAlarm;
  
  // Global
  static java.util.GregorianCalendar calendar = new java.util.GregorianCalendar();
  static java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("HH:mm:ss");
  Trend chart;

  // *****************************************************************************************************************
  // Contrcut the root Node
  public TrendSelectionNode(Trend g) {
    // Root node
    this.devname = "Trend";
    this.model = null;
    this.modele = null;
    this.modelb = null;
    this.modelst = null;
    this.models = null;
    data = null;
    chart = g;
  }

  // Contruct a device node
  public TrendSelectionNode(Trend g, String name) {
    // Device node
    this.devname = name;
    data = null;
    this.model = null;
    this.modele = null;
    this.modelb = null;
    this.modelst = null;
    this.models = null;
    chart = g;
  }

  // Construct an number scalar attribute node (model cannot be null !!)
  public TrendSelectionNode(Trend g, String name, INumberScalar model, int selection, Color c) {

    // Attribute node
    this.devname = name;
    this.model = model;
    this.modele = null;
    this.modelb = null;
    this.modelst = null;
    this.models = null;
    this.selected = selection;
    this.showMinAlarm = false;
    this.showMaxAlarm = false;
    chart = g;

    data = new JLDataView();
    data.setColor(c);
    data.setMarkerColor(c);

    minAlarmData = new JLDataView();
    minAlarmData.setColor(c);
    minAlarmData.setMarkerColor(c);
    minAlarmData.setStyle(JLDataView.STYLE_DASH);

    maxAlarmData = new JLDataView();
    maxAlarmData.setColor(c);
    maxAlarmData.setMarkerColor(c);
    maxAlarmData.setStyle(JLDataView.STYLE_DASH);

    // Retrieve attribute history
    INumberScalarHistory[] history = model.getNumberScalarHistory();
    if (history != null) {
      //System.out.println(name + ":Reading "+history.length+ " values");
      for (int i = 0; i < history.length; i++)
        data.add(history[i].getTimestamp(), history[i].getValue());
      //chart.getChart().garbageData(data);
    }

    // Register attribute
    model.addNumberScalarListener(this);

    // Register on property change
    model.getProperty("label").addPresentationListener(this);
    model.getProperty("unit").addPresentationListener(this);
    model.getProperty("format").addPresentationListener(this);
    model.getProperty("min_alarm").addPresentationListener(this);
    model.getProperty("max_alarm").addPresentationListener(this);

  }

  // Construct an enum scalar attribute node (model cannot be null !!)
  public TrendSelectionNode(Trend g, String name, IEnumScalar model, int selection, Color c) {

    // Attribute node
    this.devname = name;
    this.model = null;
    this.modele = model;
    this.modelb = null;
    this.modelst = null;
    this.models = null;
    this.selected = selection;
    this.showMinAlarm = false;
    this.showMaxAlarm = false;
    chart = g;

    data = new JLDataView();
    data.setColor(c);
    data.setMarkerColor(c);

    minAlarmData = null;
    maxAlarmData = null;

    // Register attribute
    modele.addEnumScalarListener(this);

    // Register on property change
    model.getProperty("label").addPresentationListener(this);
    model.getProperty("unit").addPresentationListener(this);

  }

  // Construct a boolean scalar attribute node (model cannot be null !!)
  public TrendSelectionNode(Trend g, String name, IBooleanScalar model, int selection, Color c) {

    // Attribute node
    this.devname = name;
    this.model = null;
    this.modele = null;
    this.modelb = model;
    this.modelst = null;
    this.models = null;
    this.selected = selection;
    this.showMinAlarm = false;
    this.showMaxAlarm = false;
    chart = g;

    data = new JLDataView();
    data.setColor(c);
    data.setMarkerColor(c);

    minAlarmData = null;
    maxAlarmData = null;

    // Register attribute
    modelb.addBooleanScalarListener(this);

    // Register on property change
    modelb.getProperty("label").addPresentationListener(this);
    modelb.getProperty("unit").addPresentationListener(this);
    modelb.getProperty("format").addPresentationListener(this);

  }

  // Construct a devstate scalar attribute node (model cannot be null !!)
  public TrendSelectionNode(Trend g, String name, IDevStateScalar model, int selection, Color c) {

    // Attribute node
    this.devname = name;
    this.model = null;
    this.modele = null;
    this.modelb = null;
    this.modelst = model;
    this.models = null;
    this.selected = selection;
    this.showMinAlarm = false;
    this.showMaxAlarm = false;
    chart = g;

    data = new JLDataView();
    data.setColor(c);
    data.setMarkerColor(c);

    // Default for State attribute (no propertychange)
    if (chart.displayDeviceNames()) {
      data.setName(devname + "/" + modelst.getLabel());
    } else {
      data.setName(modelst.getLabel());
    }

    minAlarmData = null;
    maxAlarmData = null;

    // Register attribute
    modelst.addDevStateScalarListener(this);

    // Register on property change
    modelst.getProperty("label").addPresentationListener(this);

  }

  // Construct a spectrum attribute node (model cannot be null !!)
  public TrendSelectionNode(Trend g, String name, INumberSpectrum model, int sIdx, int selection, Color c) {

    // Attribute node
    this.devname = name;
    this.model = null;
    this.modele = null;
    this.modelb = null;
    this.modelst = null;
    this.models = model;
    this.spectrumIdx = sIdx;
    this.selected = selection;
    this.showMinAlarm = false;
    this.showMaxAlarm = false;
    chart = g;

    data = new JLDataView();
    data.setColor(c);
    data.setMarkerColor(c);

    minAlarmData = null;
    maxAlarmData = null;

    // Register attribute
    models.addSpectrumListener(this);

    // Register on property change
    models.getProperty("label").addPresentationListener(this);
    models.getProperty("unit").addPresentationListener(this);
    models.getProperty("format").addPresentationListener(this);

  }

  // Refresh node after a property change
  public void refreshNode() {

    String name;

    if (model != null) {

      data.setUnit(model.getUnit());
      data.setUserFormat(model.getFormat());

      // Set the chart label
      if (model.getLabel().length() > 0 && !model.getLabel().equalsIgnoreCase("not specified")) {
        if (chart.displayDeviceNames()) {
          name = devname + "/" + model.getLabel();
          data.setName(name);
          minAlarmData.setName(name + " [Min alarm]");
          maxAlarmData.setName(name + " [Max alarm]");
        } else {
          name = model.getLabel();
          data.setName(name);
          minAlarmData.setName(name + " [Min alarm]");
          maxAlarmData.setName(name + " [Max alarm]");
        }
      } else {
        name = model.getName();
        data.setName(name);
        minAlarmData.setName(name + " [Min alarm]");
        maxAlarmData.setName(name + " [Max alarm]");
      }

      minAlarm = model.getMinAlarm();
      maxAlarm = model.getMaxAlarm();

    }

    if( modele != null ) {

      data.setUnit(modele.getUnit());
      data.setUserFormat("%d");

      // Set the chart label
      if (modele.getLabel().length() > 0 && !modele.getLabel().equalsIgnoreCase("not specified")) {
        if (chart.displayDeviceNames()) {
          name = devname + "/" + modele.getLabel();
          data.setName(name);
        } else {
          name = modele.getLabel();
          data.setName(name);
        }
      } else {
        name = modele.getName();
        data.setName(name);
      }

    }

    if( modelb != null ) {

      data.setUnit(modelb.getUnit());
      data.setUserFormat(modelb.getFormat());

      // Set the chart label
      if (modelb.getLabel().length() > 0 && !modelb.getLabel().equalsIgnoreCase("not specified")) {
        if (chart.displayDeviceNames()) {
          name = devname + "/" + modelb.getLabel();
          data.setName(name);
        } else {
          name = modelb.getLabel();
          data.setName(name);
        }
      } else {
        name = modelb.getName();
        data.setName(name);
      }

    }

    if( modelst != null ) {

      // Set the chart label
      if (modelst.getLabel().length() > 0 && !modelst.getLabel().equalsIgnoreCase("not specified")) {
        if (chart.displayDeviceNames()) {
          name = devname + "/" + modelst.getLabel();
          data.setName(name);
        } else {
          name = modelst.getLabel();
          data.setName(name);
        }
      } else {
        name = modelst.getName();
        data.setName(name);
      }

    }

    if( models != null ) {

      data.setUnit(models.getUnit());
      data.setUserFormat(models.getFormat());

      // Set the chart label
      if (models.getLabel().length() > 0 && !models.getLabel().equalsIgnoreCase("not specified")) {
        if (chart.displayDeviceNames()) {
          name = devname + "/" + models.getLabel();
          data.setName(name + " (" + Integer.toString(spectrumIdx) + ")");
        } else {
          name = models.getLabel();
          data.setName(name + " (" + Integer.toString(spectrumIdx) + ")");
        }
      } else {
        name = models.getName();
        data.setName(name + " (" + Integer.toString(spectrumIdx) + ")");
      }

    }

  }

  

  public void propertyChange(PropertyChangeEvent evt) {

    if (model != null || modelb != null || modele != null || modelst != null || models != null) {
      refreshNode();
      chart.refreshNode(this);
    }

  }

  // *****************************************************************************************************************
  // Selection stuff
  public int getSelected() {
    return selected;
  }

  public void showMinAlarm() {

    if (model != null) {

      showMinAlarm = true;
      switch (selected) {
        case Trend.SEL_Y1:
          chart.getChart().getY1Axis().addDataView(minAlarmData);
          break;
        case Trend.SEL_Y2:
          chart.getChart().getY2Axis().addDataView(minAlarmData);
          break;
      }

    }

  }

  public void hideMinAlarm() {

    if (model != null) {

      showMinAlarm = false;
      switch (selected) {
        case Trend.SEL_Y1:
          chart.getChart().getY1Axis().removeDataView(minAlarmData);
          break;
        case Trend.SEL_Y2:
          chart.getChart().getY2Axis().removeDataView(minAlarmData);
          break;
      }

    }

  }

  public boolean isShowingMinAlarm() {
    return showMinAlarm;
  }

  public void showMaxAlarm() {

    if (model != null) {

      showMaxAlarm = true;
      switch (selected) {
        case Trend.SEL_Y1:
          chart.getChart().getY1Axis().addDataView(maxAlarmData);
          break;
        case Trend.SEL_Y2:
          chart.getChart().getY2Axis().addDataView(maxAlarmData);
          break;
      }

    }

  }

  public void hideMaxAlarm() {

    if (model != null) {

      showMaxAlarm = false;
      switch (selected) {
        case Trend.SEL_Y1:
          chart.getChart().getY1Axis().removeDataView(maxAlarmData);
          break;
        case Trend.SEL_Y2:
          chart.getChart().getY2Axis().removeDataView(maxAlarmData);
          break;
      }

    }

  }

  public boolean isShowingMaxAlarm() {
    return showMaxAlarm;
  }

  public void setSelected(int s) {

    switch (s) {

      case Trend.SEL_NONE:
        switch (selected) {
          case Trend.SEL_X:
            chart.getChart().getXAxis().removeDataView(data);
            break;
          case Trend.SEL_Y1:
            chart.getChart().getY1Axis().removeDataView(data);
            if(showMinAlarm) chart.getChart().getY1Axis().removeDataView(minAlarmData);
            if(showMaxAlarm) chart.getChart().getY1Axis().removeDataView(maxAlarmData);
            break;
          case Trend.SEL_Y2:
            chart.getChart().getY2Axis().removeDataView(data);
            if(showMinAlarm) chart.getChart().getY2Axis().removeDataView(minAlarmData);
            if(showMaxAlarm) chart.getChart().getY2Axis().removeDataView(maxAlarmData);
            break;
        }
        break;

      case Trend.SEL_X:
        switch (selected) {
          case Trend.SEL_Y1:
            chart.getChart().getY1Axis().removeDataView(data);
            if(showMinAlarm) chart.getChart().getY1Axis().removeDataView(minAlarmData);
            if(showMaxAlarm) chart.getChart().getY1Axis().removeDataView(maxAlarmData);
            break;
          case Trend.SEL_Y2:
            chart.getChart().getY2Axis().removeDataView(data);
            if(showMinAlarm) chart.getChart().getY2Axis().removeDataView(minAlarmData);
            if(showMaxAlarm) chart.getChart().getY2Axis().removeDataView(maxAlarmData);
            break;
        }
        chart.getChart().getXAxis().addDataView(data);
        break;

      case Trend.SEL_Y1:
        switch (selected) {
          case Trend.SEL_X:
            chart.getChart().getXAxis().removeDataView(data);
            break;
          case Trend.SEL_Y2:
            chart.getChart().getY2Axis().removeDataView(data);
            if(showMinAlarm) chart.getChart().getY2Axis().removeDataView(minAlarmData);
            if(showMaxAlarm) chart.getChart().getY2Axis().removeDataView(maxAlarmData);
            break;
        }
        chart.getChart().getY1Axis().addDataView(data);
        if(showMinAlarm) chart.getChart().getY1Axis().addDataView(minAlarmData);
        if(showMaxAlarm) chart.getChart().getY1Axis().addDataView(maxAlarmData);
        break;

      case Trend.SEL_Y2:
        switch (selected) {
          case Trend.SEL_X:
            chart.getChart().getXAxis().removeDataView(data);
            break;
          case Trend.SEL_Y1:
            chart.getChart().getY1Axis().removeDataView(data);
            if(showMinAlarm) chart.getChart().getY1Axis().removeDataView(minAlarmData);
            if(showMaxAlarm) chart.getChart().getY1Axis().removeDataView(maxAlarmData);
            break;
        }
        chart.getChart().getY2Axis().addDataView(data);
        if(showMinAlarm) chart.getChart().getY2Axis().addDataView(minAlarmData);
        if(showMaxAlarm) chart.getChart().getY2Axis().addDataView(maxAlarmData);
        break;

    }

    selected = s;

  }

  // *****************************************************************************************************************
  // Return true when tree node is a Leaf
  public boolean isLeaf() {
    return (model != null || modele != null || modelb != null || modelst != null || models!=null);
  }

  // *****************************************************************************************************************
  // Return the model
  public INumberScalar getModel() {
    return model;
  }

  public IBooleanScalar getBooleanModel() {
    return modelb;
  }

  public IDevStateScalar getDevStateModel() {
    return modelst;
  }

  public IEnumScalar getEnumModel() {
    return modele;
  }

  public INumberSpectrum getSpectrumModel() {
    return models;
  }

  public boolean hasModel() {
    return (model!=null) || (modele!=null) || (modelb!=null) || (modelst!=null) || (models!=null);
  }

  // *****************************************************************************************************************
  // Return handle to dataView
  public JLDataView getData() {
    return data;
  }

  public JLDataView getMinAlarmData() {
    return minAlarmData;
  }

  public JLDataView getMaxAlarmData() {
    return maxAlarmData;
  }

  // ****************************************************************
  // Delete an item in the list
  public void delItem(INumberScalar model) {

    int i = 0,j = 0;
    int nb = getChildCount();

    boolean found = false;
    String attname = model.getName();
    String devname = attname.substring(0, attname.lastIndexOf('/'));

    //Look fo devname
    while (i < nb && !found) {
      found = (devname.equals(getChild(i).toString()));
      if (!found) i++;
    }

    if (found) {

      TrendSelectionNode dev = (TrendSelectionNode) getChildAt(i);
      found = false;
      j = 0;

      // Look for the att name
      int nb_att = dev.getChildCount();
      while (j < nb_att && !found) {
        found = (attname.equals(dev.getChild(j).getModelName()));
        if (!found) j++;
      }

      if (found) {
        dev.getChild(j).setSelected(Trend.SEL_NONE);
        dev.getChild(j).clearModel();

        // remove the node
        dev.remove(j);

        if (nb_att == 1) {
          // Remove device node
          remove(i);
        }
      }
    }
  }

  // ********************************************************************
  // Add a new item in the Tree
  public TrendSelectionNode addItem(Trend g, INumberScalar model, Color c) {

    int i = 0;
    int nb = getChildCount();

    boolean found = false;
    String attname = model.getName();
    String devname = attname.substring(0, attname.lastIndexOf('/'));

    //Look fo devname
    while (i < nb && !found) {
      found = (devname.equals(getChild(i).toString()));
      if (!found) i++;
    }

    TrendSelectionNode nn = new TrendSelectionNode(g, devname, model, Trend.SEL_NONE, c);

    if (found) {
      // add the attribute
      getChild(i).add(nn);
    } else {
      TrendSelectionNode n = new TrendSelectionNode(g, devname);
      add(n);
      n.add(nn);
    }

    return nn;
  }

  public TrendSelectionNode addItem(Trend g, IEnumScalar model, Color c) {

    int i = 0;
    int nb = getChildCount();

    boolean found = false;
    String attname = model.getName();
    String devname = attname.substring(0, attname.lastIndexOf('/'));

    //Look fo devname
    while (i < nb && !found) {
      found = (devname.equals(getChild(i).toString()));
      if (!found) i++;
    }

    TrendSelectionNode nn = new TrendSelectionNode(g, devname, model, Trend.SEL_NONE, c);

    if (found) {
      // add the attribute
      getChild(i).add(nn);
    } else {
      TrendSelectionNode n = new TrendSelectionNode(g, devname);
      add(n);
      n.add(nn);
    }

    return nn;
  }

  public TrendSelectionNode addItem(Trend g, IBooleanScalar model, Color c) {

    int i = 0;
    int nb = getChildCount();

    boolean found = false;
    String attname = model.getName();
    String devname = attname.substring(0, attname.lastIndexOf('/'));

    //Look fo devname
    while (i < nb && !found) {
      found = (devname.equals(getChild(i).toString()));
      if (!found) i++;
    }

    TrendSelectionNode nn = new TrendSelectionNode(g, devname, model, Trend.SEL_NONE, c);

    if (found) {
      // add the attribute
      getChild(i).add(nn);
    } else {
      TrendSelectionNode n = new TrendSelectionNode(g, devname);
      add(n);
      n.add(nn);
    }

    return nn;
  }

  public TrendSelectionNode addItem(Trend g, IDevStateScalar model, Color c) {

    int i = 0;
    int nb = getChildCount();

    boolean found = false;
    String attname = model.getName();
    String devname = attname.substring(0, attname.lastIndexOf('/'));

    //Look fo devname
    while (i < nb && !found) {
      found = (devname.equals(getChild(i).toString()));
      if (!found) i++;
    }

    TrendSelectionNode nn = new TrendSelectionNode(g, devname, model, Trend.SEL_NONE, c);

    if (found) {
      // add the attribute
      getChild(i).add(nn);
    } else {
      TrendSelectionNode n = new TrendSelectionNode(g, devname);
      add(n);
      n.add(nn);
    }

    return nn;
  }

  public TrendSelectionNode addItem(Trend g, INumberSpectrum model, int sIdx,Color c) {

    int i = 0;
    int nb = getChildCount();

    boolean found = false;
    String attname = model.getName();
    String devname = attname.substring(0, attname.lastIndexOf('/'));

    //Look fo devname
    while (i < nb && !found) {
      found = (devname.equals(getChild(i).toString()));
      if (!found) i++;
    }

    TrendSelectionNode nn = new TrendSelectionNode(g, devname, model, sIdx,Trend.SEL_NONE, c);

    if (found) {
      // add the attribute
      getChild(i).add(nn);
    } else {
      TrendSelectionNode n = new TrendSelectionNode(g, devname);
      add(n);
      n.add(nn);
    }

    return nn;
  }

  // *****************************************************************************************************************
  // Return all selectable items in a vector
  public Vector getSelectableItems() {
    Vector v;

    if (isLeaf()) {
      v = new Vector();
      v.add(this);
    } else {
      int i;
      int nb = getChildCount();

      v = new Vector();
      for (i = 0; i < nb; i++) {
        TrendSelectionNode child = getChild(i);
        v.addAll(child.getSelectableItems());
      }

    }

    return v;
  }

  // *****************************************************************************************************************
  // Return the child at the specified postion
  public void showOptions() {

    if (data != null) {
      chart.getChart().showDataOptionDialog(data);
    }

  }

  // *****************************************************************************************************************
  // Return the full attribute name
  public String getModelName() {
    if (model != null)
      return model.getName();
    else if ( modelb != null )
      return modelb.getName();
    else if ( modele != null )
      return modele.getName();
    else if ( modelst != null )
      return modelst.getName();
    else if ( models != null )
      return models.getName()+"/"+Integer.toString(spectrumIdx);
    else
      return "";
  }

  // *****************************************************************************************************************
  // Unregister the node
  public void clearModel() {

    if( model!=null ) {
      model.removeNumberScalarListener(this);
      model.getProperty("label").removePresentationListener(this);
      model.getProperty("unit").removePresentationListener(this);
      model.getProperty("format").removePresentationListener(this);
      model.getProperty("min_alarm").removePresentationListener(this);
      model.getProperty("max_alarm").removePresentationListener(this);
    }
    if( modele!=null ) {
      modele.removeEnumScalarListener(this);
      modele.getProperty("label").removePresentationListener(this);
      modele.getProperty("unit").removePresentationListener(this);
    }
    if( modelb!=null ) {
      modelb.removeBooleanScalarListener(this);
      modelb.getProperty("label").removePresentationListener(this);
      modelb.getProperty("unit").removePresentationListener(this);
      modelb.getProperty("format").removePresentationListener(this);
    }
    if( modelst!=null ) {
      modelst.removeDevStateScalarListener(this);
      modelst.getProperty("label").removePresentationListener(this);
    }
    if( models!=null ) {
      models.removeSpectrumListener(this);
      models.getProperty("label").removePresentationListener(this);
      models.getProperty("unit").removePresentationListener(this);
      models.getProperty("format").removePresentationListener(this);
    }
    data = null;
    minAlarmData = null;
    maxAlarmData = null;
    model = null;
    modelb = null;
    modelst = null;
    models = null;

  }


  // *****************************************************************************************************************
  // Return the child at the specified postion
  public TrendSelectionNode getChild(int id) {
    return (TrendSelectionNode) (getChildAt(id));
  }

  // *****************************************************************************************************************
  // INumberScalarListener
  public void numberScalarChange(NumberScalarEvent evt) {

    if( model==null )
      return;

    // Add data to the dataView
    boolean ok = true;
    DataList lv = data.getLastValue();

    double x = (double) evt.getTimeStamp();
    double y = evt.getValue();

    if (lv != null) ok = (lv.x != x) || (lv.y != y);

    if (ok) {

      if (chart.getChart().getXAxis().getPercentScrollback() == 0.0) {

        // No percent scrollback, keep the default behavior
        data.add((double) evt.getTimeStamp(), evt.getValue());
        chart.getChart().garbageData(data);
        if (showMinAlarm) {
          minAlarmData.add((double) evt.getTimeStamp(), minAlarm);
          chart.getChart().garbageData(minAlarmData);
        }
        if (showMaxAlarm) {
          maxAlarmData.add((double) evt.getTimeStamp(), maxAlarm);
          chart.getChart().garbageData(maxAlarmData);
        }

      } else {

        // percent scrollback
        chart.getChart().addData(data,(double) evt.getTimeStamp(), evt.getValue());
        if (showMinAlarm) {
          chart.getChart().addData(minAlarmData,(double) evt.getTimeStamp(), minAlarm);
        }
        if (showMaxAlarm) {
          chart.getChart().addData(maxAlarmData,(double) evt.getTimeStamp(), maxAlarm);
        }

      }

    }
    
  }

  public void booleanScalarChange(BooleanScalarEvent evt) {

    if( modelb==null )
      return;

    // Add data to the dataView
    boolean ok = true;
    DataList lv = data.getLastValue();

    double x = (double) evt.getTimeStamp();
    double y = evt.getValue()?1.0:0.0;

    if (lv != null) ok = (lv.x != x) || (lv.y != y);

    if (ok) {

      if (chart.getChart().getXAxis().getPercentScrollback() == 0.0) {

        // No percent scrollback, keep the default behavior
        data.add((double) evt.getTimeStamp(), y);
        chart.getChart().garbageData(data);

      } else {

        // percent scrollback
        chart.getChart().addData(data,(double) evt.getTimeStamp(), y);

      }

    }

  }

  public void devStateScalarChange(DevStateScalarEvent evt) {

    if( modelst==null )
      return;

    // Add data to the dataView
    boolean ok = true;
    DataList lv = data.getLastValue();

    double x = (double) evt.getTimeStamp();
    double y = modelst.getDevState().value();

    if (lv != null) ok = (lv.x != x) || (lv.y != y);

    // For state attribute, we create an histogram
    if (ok) {

      if (chart.getChart().getXAxis().getPercentScrollback() == 0.0) {

        // No percent scrollback, keep the default behavior
        if(lv!=null) {
          if(lv.y != y )
            data.add((double) evt.getTimeStamp(), lv.y);
        }
        data.add((double) evt.getTimeStamp(), y);
        chart.getChart().garbageData(data);

      } else {

        // percent scrollback
        if(lv!=null) {
          if(lv.y != y )
            chart.getChart().addData(data, (double) evt.getTimeStamp(), lv.y);
        }
        chart.getChart().addData(data,(double) evt.getTimeStamp(), y);

      }

    }

  }

  public void enumScalarChange(EnumScalarEvent evt) {

    if( modele==null )
      return;

    // Add data to the dataView
    boolean ok = true;
    DataList lv = data.getLastValue();

    double x = (double) evt.getTimeStamp();
    double y = (double) modele.getShortValueFromEnumScalar(evt.getValue());

    if (lv != null) ok = (lv.x != x) || (lv.y != y);

    if (ok) {

      if (chart.getChart().getXAxis().getPercentScrollback() == 0.0) {

        // No percent scrollback, keep the default behavior
        data.add((double) evt.getTimeStamp(), y);
        chart.getChart().garbageData(data);

      } else {

        // percent scrollback
        chart.getChart().addData(data,(double) evt.getTimeStamp(), y);

      }

    }

  }

  public void spectrumChange(NumberSpectrumEvent evt) {

    if( models==null )
      return;

    // Add data to the dataView
    boolean ok = true;
    DataList lv = data.getLastValue();

    double x = (double) evt.getTimeStamp();
    int lgth = evt.getValue().length;
    double y;
    if( spectrumIdx>=0 && spectrumIdx<lgth )
      y = evt.getValue()[spectrumIdx];
    else
      y = Double.NaN;

    if (lv != null) ok = (lv.x != x) || (lv.y != y);

    if (ok) {

      if (chart.getChart().getXAxis().getPercentScrollback() == 0.0) {

        // No percent scrollback, keep the default behavior
        data.add((double) evt.getTimeStamp(), y);
        chart.getChart().garbageData(data);

      } else {

        // percent scrollback
        chart.getChart().addData(data,(double) evt.getTimeStamp(), y);

      }

    }

  }

  public void errorChange(ErrorEvent evt) {

    if (model == null)
      return;

    // Add data to the dataView
    if (lastErrorTime != evt.getTimeStamp()) {

      lastErrorTime = evt.getTimeStamp();

      if (chart.getChart().getXAxis().getPercentScrollback() == 0.0) {

        // No percent scrollback, keep the default behavior
        data.add((double) lastErrorTime, Double.NaN);
        chart.getChart().garbageData(data);
        if (showMinAlarm) {
          minAlarmData.add((double) evt.getTimeStamp(), minAlarm);
          chart.getChart().garbageData(minAlarmData);
        }
        if (showMaxAlarm) {
          maxAlarmData.add((double) evt.getTimeStamp(), maxAlarm);
          chart.getChart().garbageData(maxAlarmData);
        }

      } else {

        // No percent scrollback, keep the default behavior
        chart.getChart().addData(data,(double) lastErrorTime, Double.NaN);
        if (showMinAlarm) {
          chart.getChart().addData(minAlarmData,(double) evt.getTimeStamp(), minAlarm);
        }
        if (showMaxAlarm) {
          chart.getChart().addData(maxAlarmData,(double) evt.getTimeStamp(), maxAlarm);
        }

      }


    }
  }

  public void stateChange(AttributeStateEvent evt) {
  }

  // *****************************************************************************************************************
  // Return string representation of the node
  public String toString() {

    if (model != null) {
      if(model.getLabel().length()>0 && !model.getLabel().equalsIgnoreCase("not specified"))
        return model.getLabel();
      else
        return model.getNameSansDevice();
    } else if (modele!=null) {
      if(modele.getLabel().length()>0 && !modele.getLabel().equalsIgnoreCase("not specified"))
        return modele.getLabel();
      else
        return modele.getNameSansDevice();
    } else if (modelb!=null) {
      if(modelb.getLabel().length()>0 && !modelb.getLabel().equalsIgnoreCase("not specified"))
        return modelb.getLabel();
      else
        return modelb.getNameSansDevice();
    } else if (modelst!=null) {
      if(modelst.getLabel().length()>0 && !modelst.getLabel().equalsIgnoreCase("not specified"))
        return modelst.getLabel();
      else
        return modelst.getNameSansDevice();
    } else if (models!=null) {
      if(models.getLabel().length()>0 && !models.getLabel().equalsIgnoreCase("not specified"))
        return models.getLabel() + " (" + Integer.toString(spectrumIdx) + ")";
      else
        return models.getNameSansDevice() + " (" + Integer.toString(spectrumIdx) + ")";
    } else {
      return devname;
    }

  }

}
