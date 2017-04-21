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

import fr.esrf.tangoatk.core.DeviceFactory;
import fr.esrf.tangoatk.core.Device;
import fr.esrf.tangoatk.core.ConnectionException;
import fr.esrf.tangoatk.core.attribute.AttributeFactory;
import fr.esrf.tangoatk.core.attribute.AAttribute;
import fr.esrf.tangoatk.core.attribute.PolledAttributeFactory;
import fr.esrf.tangoatk.core.command.CommandFactory;
import fr.esrf.tangoatk.core.command.ACommand;
import fr.esrf.Tango.DevFailed;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

/** ATK Diagnostic tools, provides dialog to check ATK Factories. */
public class ATKDiagnostic {

  private static final String[] deviceColNames = {"Device name","IDL version","Events support","Listeners","","Polling count"};
  private static final String[] attributeColNames = {"Attribute name","Event enabled","Event src","Listeners","","Polling","Change","Periodic","Config"};
  private static final String[] polledAttributeColNames = {"Attribute name","Listeners","","Polling count"};
  private static final String[] commandColNames = {"Command name" , "Execution count"};
  private static String[] evtType = {"Unknown" , "notifd" , "zmq"};

  private long refreshTime;
  private long lastRefreshTime;

  /** Contruct and displays the ATK diagnostic window (allows
   * this panel to be instancied by the TangoSynopticHandler).
   */
  public ATKDiagnostic(String dummy) {
    refreshTime = System.currentTimeMillis();
    lastRefreshTime = refreshTime;
    JFrame f = new DiagPanel();
    ATKGraphicsUtils.centerFrameOnScreen(f);
    f.setVisible(true);
  }

  /** Displays the ATK diagnostic window. */
  public static void showDiagnostic() {
    new ATKDiagnostic((String)null);
  }

  public static void main(String[] args) {
    try {
      AttributeFactory.getInstance().getAttribute("sys/machstat/tango/sig_current");
      AttributeFactory.getInstance().getAttribute("test/d-libera-bpm/srtm-2/SaA");
    } catch (ConnectionException e) {
      ErrorPane.showErrorMessage(null,"Error","",e);
    } catch (DevFailed e) {
      ErrorPane.showErrorMessage(null,"Error","",e);
    }
    ATKDiagnostic.showDiagnostic();
  }

// -----------------------------------------------------------------
// Device table
// -----------------------------------------------------------------

  class MyCellRenderer implements TableCellRenderer {

    JButton listenerBtn;

    public MyCellRenderer() {
      listenerBtn = new JButton();
      listenerBtn.setMargin(new Insets(2,4,2,4));
      listenerBtn.setFont(ATKConstant.labelFont);
      listenerBtn.setText("...");
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
      return listenerBtn;
    }

  }

  class DeviceTableModel extends AbstractTableModel {

    private Device[]              allDevices = new Device[0];

    public DeviceTableModel() {
      refresh();
    }

    public void refresh() {
      allDevices = DeviceFactory.getInstance().getDevices();
    }

    public Device getDevice(int idx) {
      return allDevices[idx];
    }

    public int getRowCount() {
      return allDevices.length;
    }

    public int getColumnCount() {
      return deviceColNames.length;
    }

    public String getColumnName(int columnIndex) {
      return deviceColNames[columnIndex];
    }

    public Class getColumnClass(int columnIndex) {
      if(columnIndex==4)
        return JButton.class;
      else
        return String.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
      switch(columnIndex) {
        case 0:
          return allDevices[rowIndex].getName();
        case 1:
          return Integer.toString(allDevices[rowIndex].getIdlVersion());
        case 2:
          return Boolean.toString(allDevices[rowIndex].doesEvent());
        case 3:
          return Integer.toString(allDevices[rowIndex].getPropChanges().getListenerCount());
        case 4:
          // Details button
          return "";
        case 5:
          return Long.toString(allDevices[rowIndex].getRefreshCount());
      }
      return "";
    }

  }

// -----------------------------------------------------------------
// Attribute table
// -----------------------------------------------------------------

  class AttributeTableModel extends AbstractTableModel {

    private AAttribute[] allAttributes = new AAttribute[0];

    public AttributeTableModel() {
      refresh();
    }

    public void refresh() {
      allAttributes = AttributeFactory.getInstance().getAttributes();
    }

    public AAttribute getAttribute(int idx) {
      return allAttributes[idx];
    }

    public int getRowCount() {
      return allAttributes.length;
    }

    public int getColumnCount() {
      return attributeColNames.length;
    }

    public String getColumnName(int columnIndex) {
      return attributeColNames[columnIndex];
    }

    public Class getColumnClass(int columnIndex) {
      if(columnIndex==4)
        return JButton.class;
      else
        return String.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
      switch(columnIndex) {
        case 0:
          return allAttributes[rowIndex].getName();
        case 1:
          return Boolean.toString(allAttributes[rowIndex].hasEvents());
        case 2:
          return evtType[allAttributes[rowIndex].getEventType()];
        case 3:
          return Integer.toString(allAttributes[rowIndex].getPropChanges().getListenerCount());
        case 4:
          // Details button
          return "";
        case 5: {
          String suffix = "";
          long diff = allAttributes[rowIndex].getRefreshCount() - allAttributes[rowIndex].lastRefreshCount;
          allAttributes[rowIndex].lastRefreshCount = allAttributes[rowIndex].getRefreshCount();
          double diffT = (refreshTime - lastRefreshTime)/1000.0;
          if(diffT>0.0)
            suffix = " (" + String.format("%.1f",(double)diff/diffT) + " Hz)";
          return Long.toString(allAttributes[rowIndex].getRefreshCount()) + suffix;
        }
        case 6: {
          String suffix = "";
          long diff = allAttributes[rowIndex].getChangeCount() - allAttributes[rowIndex].lastChangeCount;
          allAttributes[rowIndex].lastChangeCount = allAttributes[rowIndex].getChangeCount();
          double diffT = (refreshTime - lastRefreshTime)/1000.0;
          if(diffT>0.0)
            suffix = " (" + String.format("%.1f",(double)diff/diffT) + " Hz)";
          return Long.toString(allAttributes[rowIndex].getChangeCount()) + suffix;
        }
        case 7: {
          String suffix = "";
          long diff = allAttributes[rowIndex].getPeriodicCount() - allAttributes[rowIndex].lastPeriodicCount;
          allAttributes[rowIndex].lastPeriodicCount = allAttributes[rowIndex].getPeriodicCount();
          double diffT = (refreshTime - lastRefreshTime)/1000.0;
          if(diffT>0.0)
            suffix = " (" + String.format("%.1f",(double)diff/diffT) + " Hz)";
          return Long.toString(allAttributes[rowIndex].getPeriodicCount()) + suffix;
        }
        case 8:
          return Long.toString(allAttributes[rowIndex].getConfigCount());
      }
      return "";
    }

  }
// -----------------------------------------------------------------
// Polled Attribute table
// -----------------------------------------------------------------

  class PolledAttributeTableModel extends AbstractTableModel {

    private AAttribute[] allAttributes = new AAttribute[0];

    public PolledAttributeTableModel() {
      refresh();
    }

    public void refresh() {
      allAttributes = PolledAttributeFactory.getPolledInstance().getAttributes();
    }

    public AAttribute getAttribute(int idx) {
      return allAttributes[idx];
    }

    public int getRowCount() {
      return allAttributes.length;
    }

    public int getColumnCount() {
      return polledAttributeColNames.length;
    }

    public String getColumnName(int columnIndex) {
      return polledAttributeColNames[columnIndex];
    }

    public Class getColumnClass(int columnIndex) {
      if(columnIndex==2)
        return JButton.class;
      else
        return String.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
      switch(columnIndex) {
        case 0:
          return allAttributes[rowIndex].getName();
        case 1:
          return Integer.toString(allAttributes[rowIndex].getPropChanges().getListenerCount());
        case 2:
          // Details button
          return "";
        case 3:
          return Long.toString(allAttributes[rowIndex].getRefreshCount());
      }
      return "";
    }

  }

// -----------------------------------------------------------------
// Command table
// -----------------------------------------------------------------

  class CommandTableModel extends AbstractTableModel {

    private ACommand[]            allCommands = new ACommand[0];

    public CommandTableModel() {
      refresh();
    }

    public void refresh() {
      allCommands = CommandFactory.getInstance().getCommands();
    }

    public int getRowCount() {
      return allCommands.length;
    }

    public int getColumnCount() {
      return commandColNames.length;
    }

    public String getColumnName(int columnIndex) {
      return commandColNames[columnIndex];
    }

    public Class getColumnClass(int columnIndex) {
      return String.class;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return false;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
      switch(columnIndex) {
        case 0:
          return allCommands[rowIndex].getName();
        case 1:
          return Integer.toString(allCommands[rowIndex].getExecutionCount());
      }
      return "";
    }

  }

// -----------------------------------------------------------------

  class DiagPanel extends JFrame implements ActionListener,MouseListener {

    private JButton refreshButton;
    private JButton dismissButton;
    private boolean isUpdating = false;

    // Device panel
    private JTable deviceTable;
    private DeviceTableModel deviceModel;
    private JButton startRefresherBtn;
    private JButton stopRefresherBtn;
    private JTextField refreshPeriodText;
    private JLabel refreshPeriodLabel;
    private JLabel refreshStateLabel;

    // Attribute panel
    private JTable attributeTable;
    private AttributeTableModel attributeModel;
    private JTextField attributeEvtText;

    // Polled Attribute panel
    private JTable polledAttributeTable;
    private PolledAttributeTableModel polledAttributeModel;

    // Command panel
    private CommandTableModel commandModel;

    DiagPanel() {

      JPanel innerPanel = new JPanel(new BorderLayout());

      // ------------- Main panel ---------------------
      JTabbedPane pane = new JTabbedPane();

      JPanel devicePanel = new JPanel(new BorderLayout());
      deviceModel = new DeviceTableModel();
      deviceTable = new JTable(deviceModel);
      deviceTable.setDefaultRenderer(JButton.class,new MyCellRenderer());
      deviceTable.getColumnModel().getColumn(0).setPreferredWidth(150);
      deviceTable.getColumnModel().getColumn(4).setMaxWidth(25);
      deviceTable.addMouseListener(this);
      JScrollPane deviceView = new JScrollPane(deviceTable);
      deviceView.setPreferredSize(new Dimension(700,400));
      devicePanel.add(deviceView,BorderLayout.CENTER);
      JPanel dFacPanel = new JPanel();
      dFacPanel.setLayout(null);
      dFacPanel.setPreferredSize(new Dimension(0,40));

      refreshStateLabel = new JLabel();
      refreshStateLabel.setFont(ATKConstant.labelFont);
      refreshStateLabel.setBounds(10,10,180,25);
      dFacPanel.add(refreshStateLabel);

      startRefresherBtn = new JButton("Start");
      startRefresherBtn.setFont(ATKConstant.labelFont);
      startRefresherBtn.addActionListener(this);
      startRefresherBtn.setBounds(200,10,70,25);
      dFacPanel.add(startRefresherBtn);

      stopRefresherBtn = new JButton("Stop");
      stopRefresherBtn.setFont(ATKConstant.labelFont);
      stopRefresherBtn.addActionListener(this);
      stopRefresherBtn.setBounds(280,10,70,25);
      dFacPanel.add(stopRefresherBtn);

      refreshPeriodLabel = new JLabel();
      refreshPeriodLabel.setFont(ATKConstant.labelFont);
      refreshPeriodLabel.setBounds(360,10,100,25);
      refreshPeriodLabel.setText("Period (ms)");
      dFacPanel.add(refreshPeriodLabel);

      refreshPeriodText = new JTextField();
      refreshPeriodText.setFont(ATKConstant.labelFont);
      refreshPeriodText.setMargin(new Insets(0,0,0,0));
      refreshPeriodText.setBounds(470,10,80,25);
      refreshPeriodText.addActionListener(this);
      dFacPanel.add(refreshPeriodText);

      devicePanel.add(dFacPanel,BorderLayout.SOUTH);

      pane.add(devicePanel,"Device");

      JPanel attributePanel = new JPanel(new BorderLayout());
      attributeModel = new AttributeTableModel();
      attributeTable = new JTable(attributeModel);
      attributeTable.setDefaultRenderer(JButton.class,new MyCellRenderer());
      attributeTable.getColumnModel().getColumn(0).setPreferredWidth(150);
      attributeTable.getColumnModel().getColumn(4).setMaxWidth(25);
      attributeTable.addMouseListener(this);
      JScrollPane attributeView = new JScrollPane(attributeTable);
      attributePanel.add(attributeView,BorderLayout.CENTER);

      attributeEvtText = new JTextField();
      attributeEvtText.setText(" ");
      attributePanel.add(attributeEvtText,BorderLayout.SOUTH);
      pane.add(attributePanel,"Attribute");

      attributeEvtText = new JTextField();
      attributeEvtText.setText(" ");
      attributePanel.add(attributeEvtText,BorderLayout.SOUTH);
      pane.add(attributePanel,"Attribute");

      JPanel polledAttributePanel = new JPanel(new BorderLayout());
      polledAttributeModel = new PolledAttributeTableModel();
      polledAttributeTable = new JTable(polledAttributeModel);
      polledAttributeTable.setDefaultRenderer(JButton.class,new MyCellRenderer());
      polledAttributeTable.getColumnModel().getColumn(0).setPreferredWidth(150);
      polledAttributeTable.getColumnModel().getColumn(2).setMaxWidth(25);
      polledAttributeTable.addMouseListener(this);
      JScrollPane polledAttributeView = new JScrollPane(polledAttributeTable);
      polledAttributePanel.add(polledAttributeView,BorderLayout.CENTER);
      pane.add(polledAttributePanel,"Polled Attribute");

      JPanel commandPanel = new JPanel(new BorderLayout());
      commandModel = new CommandTableModel();
      JTable commandTable = new JTable(commandModel);
      commandTable.getColumnModel().getColumn(0).setPreferredWidth(150);
      JScrollPane commandView = new JScrollPane(commandTable);
      commandPanel.add(commandView,BorderLayout.CENTER);
      pane.add(commandPanel,"Command");

      innerPanel.add(pane,BorderLayout.CENTER);

      // ------------- Button panel -------------------
      FlowLayout fl = new FlowLayout();
      fl.setAlignment(FlowLayout.RIGHT);
      JPanel btnPanel = new JPanel(fl);
      refreshButton = new JButton("Update fields");
      refreshButton.setFont(ATKConstant.labelFont);
      refreshButton.addActionListener(this);
      btnPanel.add(refreshButton);
      dismissButton = new JButton("Dismiss");
      dismissButton.setFont(ATKConstant.labelFont);
      dismissButton.addActionListener(this);
      btnPanel.add(dismissButton);
      innerPanel.add(btnPanel,BorderLayout.SOUTH);

      // --------------------- Frame ------------------
      setContentPane(innerPanel);
      setTitle("ATK Diagnostic");
      refreshControls();

    }

    private void refreshControls() {

      isUpdating = true;

      if(DeviceFactory.getInstance().isRefreshing())
        refreshStateLabel.setText("Refresher is Running");
      else
        refreshStateLabel.setText("Refresher is Stopped");

      refreshPeriodText.setText(Long.toString(DeviceFactory.getInstance().getRefreshInterval()));
      refreshPeriodText.setCaretPosition(0);

      isUpdating = false;

    }

    public void actionPerformed(ActionEvent e) {

      if(isUpdating)
        return;

      Object src = e.getSource();

      if(src==dismissButton) {
        setVisible(false);
      } else if(src==refreshButton) {
        lastRefreshTime = refreshTime;
        refreshTime = System.currentTimeMillis();
        deviceModel.refresh();
        deviceModel.fireTableDataChanged();
        attributeModel.refresh();
        attributeModel.fireTableDataChanged();
        polledAttributeModel.refresh();
        polledAttributeModel.fireTableDataChanged();
      } else if(src==refreshPeriodText ) {
        String nPeriod = refreshPeriodText.getText();
        try {
          int p = Integer.parseInt(nPeriod);
          DeviceFactory.getInstance().setRefreshInterval(p);
        } catch (NumberFormatException ex) {
          JOptionPane.showMessageDialog(null,"Invalid period:\n"+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
      } else if (src==startRefresherBtn) {
        DeviceFactory.getInstance().startRefresher();
      } else if (src==stopRefresherBtn) {
        DeviceFactory.getInstance().stopRefresher();
      }

      refreshControls();

    }

    public void mouseClicked(MouseEvent e) {
      Object src = e.getSource();
      if(src == attributeTable) {

        int sRow = attributeTable.getSelectedRow();
        int sCol = attributeTable.getSelectedColumn();
        if (sRow != -1) {
          String err = attributeModel.getAttribute(sRow).getSubscriptionError();
          attributeEvtText.setText(err);
        }
        if(sCol==4) {
          AAttribute a = attributeModel.getAttribute(sRow);
          String info = a.getPropChanges().getListenerInfo();
          JOptionPane.showMessageDialog(this,info,"Listeners registered for "+a.getName(),JOptionPane.INFORMATION_MESSAGE);
        }

      } else if(src == polledAttributeTable) {

        int sRow = polledAttributeTable.getSelectedRow();
        int sCol = polledAttributeTable.getSelectedColumn();
        if(sCol==2) {
          AAttribute a = polledAttributeModel.getAttribute(sRow);
          String info = a.getPropChanges().getListenerInfo();
          JOptionPane.showMessageDialog(this,info,"Listeners registered for "+a.getName(),JOptionPane.INFORMATION_MESSAGE);
        }

      } else if (src == deviceTable ) {
        int sRow = deviceTable.getSelectedRow();
        int sCol = deviceTable.getSelectedColumn();
        if(sCol==4) {
          Device d = deviceModel.getDevice(sRow);
          String info = d.getPropChanges().getListenerInfo();
          JOptionPane.showMessageDialog(this,info,"Listeners registered for "+d.getName(),JOptionPane.INFORMATION_MESSAGE);
        }
      }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

  }

}



