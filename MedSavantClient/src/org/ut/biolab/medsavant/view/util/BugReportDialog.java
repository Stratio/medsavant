/*
 *    Copyright 2010-2012 University of Toronto
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.ut.biolab.medsavant.view.util;

import java.awt.BorderLayout;
import java.awt.Dialog;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

import org.ut.biolab.medsavant.util.Mail;
import org.ut.biolab.medsavant.settings.VersionSettings;
import org.ut.biolab.medsavant.util.ClientMiscUtils;


/**
 *
 * @author mfiume
 */
public class BugReportDialog extends JDialog {
    private final PathField pathField;

    public BugReportDialog(String description, String path) {
        super(DialogUtils.getFrontWindow(), Dialog.ModalityType.APPLICATION_MODAL);
        initComponents();
        setLocationRelativeTo(getParent());
        attachmentPanel.setLayout(new BorderLayout());
        pathField = new PathField(JFileChooser.OPEN_DIALOG);
        attachmentPanel.add(pathField, BorderLayout.CENTER);
        if (path != null) {
            pathField.setPath(path);
        }
        if (description != null) {
            descriptionField.setText(description);
            descriptionField.setCaretPosition(0);
        }
        getRootPane().setDefaultButton(sendButton);
        ClientMiscUtils.registerCancelButton(cancelButton);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        descriptionField = new javax.swing.JTextArea();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        sendButton = new javax.swing.JButton();
        nameField = new javax.swing.JTextField();
        emailField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        institutionField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        typeCombo = new javax.swing.JComboBox();
        javax.swing.JScrollPane jScrollPane3 = new javax.swing.JScrollPane();
        javax.swing.JTextArea jTextArea3 = new javax.swing.JTextArea();
        javax.swing.JSeparator jSeparator1 = new javax.swing.JSeparator();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        attachmentPanel = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane4 = new javax.swing.JScrollPane();
        javax.swing.JTextArea jTextArea4 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Report an Issue");
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel1.setText("Name *");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel2.setText("Email address *");

        descriptionField.setColumns(20);
        descriptionField.setLineWrap(true);
        descriptionField.setRows(5);
        descriptionField.setWrapStyleWord(true);
        jScrollPane1.setViewportView(descriptionField);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel3.setText("Description of issue *");

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        sendButton.setText("Send Report");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel4.setText("Institution   ");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel5.setText("Issue Type *");

        typeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Database", "Importing", "Visualizing", "Other" }));

        jScrollPane3.setBorder(null);
        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane3.setFocusable(false);
        jScrollPane3.setOpaque(false);

        jTextArea3.setBackground(java.awt.SystemColor.control);
        jTextArea3.setColumns(20);
        jTextArea3.setEditable(false);
        jTextArea3.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jTextArea3.setLineWrap(true);
        jTextArea3.setRows(5);
        jTextArea3.setText("We are constantly improving MedSavant. Please report any issues you encounter, so that we can address them. We will contact you once we have corrected the problem.");
        jTextArea3.setWrapStyleWord(true);
        jTextArea3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jScrollPane3.setViewportView(jTextArea3);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel6.setText("Attachment   ");

        attachmentPanel.setBackground(new java.awt.Color(255, 51, 51));

        javax.swing.GroupLayout attachmentPanelLayout = new javax.swing.GroupLayout(attachmentPanel);
        attachmentPanel.setLayout(attachmentPanelLayout);
        attachmentPanelLayout.setHorizontalGroup(
            attachmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 543, Short.MAX_VALUE)
        );
        attachmentPanelLayout.setVerticalGroup(
            attachmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 29, Short.MAX_VALUE)
        );

        jScrollPane4.setBorder(null);
        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane4.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane4.setFocusable(false);
        jScrollPane4.setOpaque(false);

        jTextArea4.setBackground(java.awt.SystemColor.control);
        jTextArea4.setColumns(20);
        jTextArea4.setEditable(false);
        jTextArea4.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        jTextArea4.setLineWrap(true);
        jTextArea4.setRows(5);
        jTextArea4.setText("10MB maximum. You may attach (1) a screenshot demonstrating the issue or (2) a file, if you are having trouble formatting it. Please ensure that you have permission before attaching private data.");
        jTextArea4.setWrapStyleWord(true);
        jTextArea4.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jScrollPane4.setViewportView(jTextArea4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
                    .addComponent(attachmentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(sendButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
                    .addComponent(emailField, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
                    .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
                    .addComponent(institutionField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
                    .addComponent(typeCombo, 0, 543, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(institutionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(typeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel6))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(attachmentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelButton)
                            .addComponent(sendButton)))
                    .addComponent(jLabel3))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        if (validateForm()) {

            String name = nameField.getText();
            String email = emailField.getText();
            String type = typeCombo.getSelectedItem().toString();
            String institution = institutionField.getText();
            String description = descriptionField.getText();
            String version = VersionSettings.getVersionString();
            String jdk = getProperty("java.version");
            String os = getProperty("os.name") + " " + getProperty("os.arch") + " " + getProperty("os.version");

            String subject = "[MedSavant Bug Report] from " + name;
            String message = ""
                + "Name: " + name + "\n\n"
                + "Email: " + email + "\n\n"
                + "Type: " + type + "\n\n"
                + "Institution: " + institution + "\n\n"
                + "MedSavant Version: " + version + "\n\n"
                + "JDK Version: " + jdk + "\n\n"
                + "OS Version: " + os + "\n\n"
                + "Description:\n" + description + "\n";

            sendButton.setText("Sending...");
            sendButton.setEnabled(false);
            cancelButton.setEnabled(false);

            boolean result;
            if (this.pathField.getPath().equals("")) {
                result = Mail.sendEmailToDevelopers(subject, message,null);
            } else {
                result = Mail.sendEmail(name, subject, message, pathField.getFile());
            }

            if (result) {
                DialogUtils.displayMessage("Report sent. Thank you for reporting your issue!");
            } else {
                DialogUtils.displayError("Error sending report. Check your internet connection or try again later.");
            }
            setVisible(false);
        }
    }//GEN-LAST:event_sendButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel attachmentPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextArea descriptionField;
    private javax.swing.JTextField emailField;
    private javax.swing.JTextField institutionField;
    private javax.swing.JTextField nameField;
    private javax.swing.JButton sendButton;
    private javax.swing.JComboBox typeCombo;
    // End of variables declaration//GEN-END:variables

    private String getProperty(String propertyName){
        try {
            String value = System.getProperty(propertyName);
            if (value == null) return "unknown";
            else return value;
        } catch (SecurityException e){
            return "unknown";
        }
    }

    private boolean validateForm() {
        if (nameField.getText().equals("")){
            DialogUtils.displayMessage("Enter your name.");
            nameField.requestFocus();
            return false;
        } else if (!emailField.getText().contains("@")) {
            DialogUtils.displayMessage("Enter a valid email address.");
            emailField.requestFocus();
            return false;
        } else if (!pathField.getPath().equals("") && !pathField.getFile().exists()) {
            DialogUtils.displayMessage("The attachment does not exist at that path.");
            this.pathField.requestFocus();
            return false;
        } else if (pathField.getFile().length() > 10000000) {
            DialogUtils.displayMessage("The attachment is too large.\nIf you are having issues working with this file,\nyou could attach a file that contains the first few lines (e.g. 30 lines). That is often enough to diagnose the problem.");
            this.pathField.requestFocus();
            return false;
        } else if (descriptionField.getText().equals("")) {
            DialogUtils.displayMessage("Enter a description of the problems.");
            descriptionField.requestFocus();
            return false;
        }
        return true;
    }
}
