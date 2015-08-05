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
 * SimpleSynopticAppli.java
 *
 * Created on May 25, 2005
 */

package fr.esrf.tangoatk.widget.jdraw;

import fr.esrf.tangoatk.core.AttributeList;
import fr.esrf.tangoatk.core.AttributePolledList;
import fr.esrf.tangoatk.core.ConnectionException;
import fr.esrf.tangoatk.core.IDevStateScalar;
import fr.esrf.tangoatk.core.IEntity;
import fr.esrf.tangoatk.core.IEntityFilter;
import fr.esrf.tangoatk.core.INumberScalar;
import fr.esrf.tangoatk.widget.attribute.Trend;
import java.io.*;
import java.util.*;
import fr.esrf.tangoatk.widget.util.ErrorHistory;
import fr.esrf.tangoatk.widget.util.ATKGraphicsUtils;
import fr.esrf.tangoatk.widget.util.SplashTimer;
import fr.esrf.tangoatk.widget.util.jdraw.JDFileFilter;

import javax.swing.*;

/**
 *
 * @author  PONCET
 */
public class SimpleSynopticAppli extends javax.swing.JFrame {

    private final SplashTimer       splash = new SplashTimer(10000, 200);  // progress during 10s with steps of 200ms
    
    private  ErrorHistory        errorHistory;
    private  boolean	         standAlone = false;
    private  boolean             fileLoaded = false;
    
    private  AttributePolledList  numberAndState_scalar_atts; /* used in the global trend */
    private  JFrame               trendFrame;
    private  Trend                globalTrend=null;

    /** Creates new form SimpleSynopticAppli */
    public SimpleSynopticAppli()
    {
	fileLoaded = false;
	standAlone = false;
        errorHistory = new ErrorHistory();
	splash.setTitle("SimpleSynopticAppli  ");
	splash.setCopyright("(c) ESRF 2003-2015");
	splash.setMessage("Loading synoptic ...");
	splash.initProgress();
        splash.setVisible(true);
        
        
        numberAndState_scalar_atts = new fr.esrf.tangoatk.core.AttributePolledList();
        numberAndState_scalar_atts.setFilter( new IEntityFilter () 
                         {
                            public boolean keep(IEntity entity)
			    {
                               if (    (entity instanceof INumberScalar)
			            || (entity instanceof IDevStateScalar) )
			       {
                                 return true;
                               }
                               return false;
                            }
                         });
	
        trendFrame = new JFrame();
	javax.swing.JPanel jpanel = new javax.swing.JPanel();
        trendFrame.getContentPane().add(jpanel, java.awt.BorderLayout.CENTER);
	jpanel.setPreferredSize(new java.awt.Dimension(600, 300));
	jpanel.setLayout(new java.awt.GridBagLayout());

        java.awt.GridBagConstraints trendGbc;
        trendGbc = new java.awt.GridBagConstraints();
        trendGbc.gridx = 0;
        trendGbc.gridy = 0;
        trendGbc.fill = java.awt.GridBagConstraints.BOTH;
        trendGbc.weightx = 1.0;
        trendGbc.weighty = 1.0;
        globalTrend = new Trend(trendFrame);
	jpanel.add(globalTrend, trendGbc);
	trendFrame.pack();
        
        initComponents();
    }
    
    public SimpleSynopticAppli(String jdrawFullFileName)
    {
	this();	
        try
        {
            tangoSynopHandler.setSynopticFileName(jdrawFullFileName);
            splash.setMessage("Synoptic file loaded ...");
            tangoSynopHandler.setToolTipMode(TangoSynopticHandler.TOOL_TIP_NAME);
	    tangoSynopHandler.setAutoZoom(true);
        }
        catch (FileNotFoundException  fnfEx)
        {
            javax.swing.JOptionPane.showMessageDialog(
             null, "Cannot find the synoptic file : " + jdrawFullFileName + ".\n"
                  + "Check the file name you entered;"
                  + " Application will abort ...\n"
                  + fnfEx,
                  "No such file",
                  javax.swing.JOptionPane.ERROR_MESSAGE);
            //System.exit(-1); don't exit if not standalone
            splash.setVisible(false);
	    return;
        }
        catch (IllegalArgumentException  illEx)
        {
            javax.swing.JOptionPane.showMessageDialog(
             null, "Cannot parse the synoptic file : " + jdrawFullFileName + ".\n"
                  + "Check if the file is a Jdraw file."
                  + " Application will abort ...\n"
                  + illEx,
                  "Cannot parse the file",
                  javax.swing.JOptionPane.ERROR_MESSAGE);
            //System.exit(-1); don't exit if not standalone
            splash.setVisible(false);
	    return;
        }
        catch (MissingResourceException  mrEx)
        {
            javax.swing.JOptionPane.showMessageDialog(
             null, "Cannot parse the synoptic file : " + jdrawFullFileName + ".\n"
                  + " Application will abort ...\n"
                  + mrEx,
                  "Cannot parse the file",
                  javax.swing.JOptionPane.ERROR_MESSAGE);
            //System.exit(-1); don't exit if not standalone
            splash.setVisible(false);
	    return;
        }
        setTrendAttributeList();
        splash.setVisible(false);
	
	fileLoaded = true;
	setTitle(jdrawFullFileName);
        pack();
	setVisible(true);
    }
    
    public SimpleSynopticAppli(String jdrawFullFileName, boolean stand)
    {
	this(jdrawFullFileName);	
	standAlone = stand;
    }
    
    private void setTrendAttributeList()
    {
        AttributeList  attl = tangoSynopHandler.getAttributeList();
        
        for (int i=0; i<attl.getSize(); i++)
        {
            IEntity ie = (IEntity) attl.get(i);
            try
            {
                numberAndState_scalar_atts.add(ie.getName());
            } 
            catch (ConnectionException ex) {}
        }
        globalTrend.setModel(numberAndState_scalar_atts);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        tangoSynopHandler = new fr.esrf.tangoatk.widget.jdraw.TangoSynopticHandler();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileJMenu = new javax.swing.JMenu();
        quitJMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        trendMenuItem = new javax.swing.JMenuItem();
        errHistMenuItem = new javax.swing.JMenuItem();
        diagtMenuItem = new javax.swing.JMenuItem();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jPanel1.setLayout(new java.awt.GridBagLayout());

        if (errorHistory != null)
        {
            try
            {
                tangoSynopHandler.setErrorHistoryWindow(errorHistory);
            }
            catch (Exception setErrwExcept)
            {
                System.out.println("Cannot set Error History Window");
            }
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(tangoSynopHandler, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanel1, gridBagConstraints);

        fileJMenu.setText("File");
        quitJMenuItem.setText("Quit");
        quitJMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitJMenuItemActionPerformed(evt);
            }
        });

        fileJMenu.add(quitJMenuItem);

        jMenuBar1.add(fileJMenu);

        viewMenu.setText("View");
	
	trendMenuItem.setText("Numeric & State Trend ");
	trendMenuItem.addActionListener(
	     new java.awt.event.ActionListener()
	         {
		    public void actionPerformed(java.awt.event.ActionEvent evt)
		    {
		       viewTrendActionPerformed(evt);
		    }
		 });
	viewMenu.add(trendMenuItem);

        viewMenu.add(errHistMenuItem);
	
        errHistMenuItem.setText("Error History ...");
        errHistMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                errHistMenuItemActionPerformed(evt);
            }
        });

        viewMenu.add(errHistMenuItem);

        diagtMenuItem.setText("Diagnostic ...");
        diagtMenuItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    fr.esrf.tangoatk.widget.util.ATKDiagnostic.showDiagnostic();
                }
            });

        viewMenu.add(diagtMenuItem);
        jMenuBar1.add(viewMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }//GEN-END:initComponents

    private void viewTrendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewTrendActionPerformed
        // Add your handling code here:
	fr.esrf.tangoatk.widget.util.ATKGraphicsUtils.centerFrame(getRootPane(), trendFrame);
        trendFrame.setVisible(true);
    }//GEN-LAST:event_viewTrendActionPerformed

    private void errHistMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_errHistMenuItemActionPerformed
        // TODO add your handling code here:
	errorHistory.setVisible(true);        
    }//GEN-LAST:event_errHistMenuItemActionPerformed

    private void quitJMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitJMenuItemActionPerformed
        // TODO add your handling code here:
        stopSimpleSynopticAppli();
    }//GEN-LAST:event_quitJMenuItemActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        stopSimpleSynopticAppli();
    }//GEN-LAST:event_exitForm

    public void stopSimpleSynopticAppli()
    {
        if (standAlone == true)
	   System.exit(0);
	else
	{
	   tangoSynopHandler.getAttributeList().stopRefresher();

           if (globalTrend != null)
              globalTrend.clearModel();
	   this.dispose();
	}
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
	String fullFileName = null;
	SimpleSynopticAppli syApp = null;
	String arg0 = null, arg1 = null;

	if (args.length >= 2) // The synoptic file name and the directory are specified
	{
	    arg0 = args[0];
	    arg1 = args[1];

	    if (arg0 == null)
               fullFileName = arg1;
	    else
	       if (arg0.length() <= 0)
        	  fullFileName = arg1;
	       else
        	  fullFileName = arg0 + "/" + arg1;
	} 
	else
	    if (args.length == 1) // Only the synoptic absolute file name is specified
	    {
	       fullFileName = args[0];
	    };

	// If Synoptic file name is not specified, launch a file chooser window to let the user select the file name
	if (fullFileName == null)
	{
	    JFileChooser chooser = new JFileChooser(".");
	    chooser.setDialogTitle("[SimpleSynopticAppli] Open a synoptic file");
	    JDFileFilter jdwFilter = new JDFileFilter("JDraw synoptic",new String[]{"jdw"});
	    chooser.addChoosableFileFilter(jdwFilter);
	    int returnVal = chooser.showOpenDialog(null);
	    if (returnVal == JFileChooser.APPROVE_OPTION)
	    {
               File f = chooser.getSelectedFile();
               fullFileName = f.getAbsolutePath();
	    }
	    else
	    {
               System.exit(0);
	    }
	}

	syApp = new SimpleSynopticAppli(fullFileName, true);
	
	if (!syApp.fileLoaded) // failed to load the synoptic file : constructor failure
           System.exit(-1);
	   
	//syApp.setTitle("Simple Synoptic Application");
	ATKGraphicsUtils.centerFrameOnScreen(syApp);
	//syApp.setVisible(true);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem trendMenuItem;
    private javax.swing.JMenuItem errHistMenuItem;
    private javax.swing.JMenuItem diagtMenuItem;
    private javax.swing.JMenu fileJMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JMenuItem quitJMenuItem;
    private fr.esrf.tangoatk.widget.jdraw.TangoSynopticHandler tangoSynopHandler;
    private javax.swing.JMenu viewMenu;
    // End of variables declaration//GEN-END:variables
    
}
