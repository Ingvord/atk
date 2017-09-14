/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.esrf.tangoatk.widget.util;

import javax.swing.JFrame;

/**
 *
 * @author poncet
 */
public class FilePreviewWindow extends JFrame
{
    private javax.swing.JButton DismissButton;
    private javax.swing.JPanel btnPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea textArea;
    
  /**
   * Creates new form ConfigFilePreview
   */
    public FilePreviewWindow()
    {
        initComponents();
    }

    public void setTitle(String title)
    {
        super.setTitle("Settings File Preview [" + title + "]");
    }

    public void setText(String text)
    {
        textArea.setText(text);
    }
    
    
  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
    private void initComponents()
    {
        jScrollPane1 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        btnPanel = new javax.swing.JPanel();
        DismissButton = new javax.swing.JButton();

        jScrollPane1.setPreferredSize(new java.awt.Dimension(640, 480));

        textArea.setEditable(false);
        textArea.setColumns(20);
        textArea.setRows(5);
        jScrollPane1.setViewportView(textArea);

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        btnPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        DismissButton.setText("Dismiss");
        DismissButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                DismissButtonActionPerformed(evt);
            }
        });
        btnPanel.add(DismissButton);

        getContentPane().add(btnPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }
    

    private void DismissButtonActionPerformed(java.awt.event.ActionEvent evt)
    {
        this.setVisible(false);
    }

  /**
   * @param args the command line arguments
   */
    public static void main(String args[])
    {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                FilePreviewWindow fpw = new FilePreviewWindow();
                fpw.setVisible(true);
            }
        });
    }

    
}
