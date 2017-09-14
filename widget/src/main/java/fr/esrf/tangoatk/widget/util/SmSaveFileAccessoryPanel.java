/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.esrf.tangoatk.widget.util;

import java.awt.Dimension;

/**
 *
 * @author poncet
 */
public class SmSaveFileAccessoryPanel extends javax.swing.JPanel
{
    private int       width, height;

    /**
     * Creates new form SmSaveFileAccessoryPanel
     */
    public SmSaveFileAccessoryPanel(int w, int h)
    {
        width = w;
        height = h;
        initComponents();
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(width, height);
    }
    
    
    
    public String getAuthorText()
    {
        return authorJTextField.getText();
    }
    
    public void setAuthorText(String auth)
    {
        authorJTextField.setText(auth);
    }
    
    public String getCommentsText()
    {
        return commentsJTextArea.getText();
    }
    
    public void setCommentsText(String com)
    {
        commentsJTextArea.setText(com);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        authorJLabel = new javax.swing.JLabel();
        authorJTextField = new javax.swing.JTextField();
        commentsJLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        commentsJTextArea = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        authorJLabel.setText("Author : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 3);
        add(authorJLabel, gridBagConstraints);

        authorJTextField.setText("jTextField1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        add(authorJTextField, gridBagConstraints);

        commentsJLabel.setText("Comments : ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 3, 5);
        add(commentsJLabel, gridBagConstraints);

        commentsJTextArea.setColumns(20);
        commentsJTextArea.setRows(5);
        jScrollPane1.setViewportView(commentsJTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 3, 5);
        add(jScrollPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel authorJLabel;
    private javax.swing.JTextField authorJTextField;
    private javax.swing.JLabel commentsJLabel;
    private javax.swing.JTextArea commentsJTextArea;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

}
