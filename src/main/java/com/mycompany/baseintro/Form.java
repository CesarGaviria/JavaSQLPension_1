package com.mycompany.baseintro;

import java.awt.event.KeyEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Form extends javax.swing.JFrame {

    Conexion conectar = Conexion.getInstance();
    DefaultTableModel modelo = new DefaultTableModel();

    public Form() {
        initComponents();
        modelo = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;//tabla no editable 
            }
        };
        tblDetail.setModel(modelo);
        //Definir columnas
        modelo.addColumn("ID");
        modelo.addColumn("Document Type");
        modelo.addColumn("Number");
        modelo.addColumn("Name");
        modelo.addColumn("Birth Date");
        modelo.addColumn("Gender");
        modelo.addColumn("Pension");
        modelo.addColumn("Health");
        tblDetail.getTableHeader().setReorderingAllowed(false);
        cargarDatos();
        int[] anchoCol = {20, 30, 30, 120, 30, 15, 50, 50};
        for (int i = 0; i < anchoCol.length; i++) {
            tblDetail.getColumnModel().getColumn(i).setPreferredWidth(anchoCol[i]);

        }
    }

    private void cargarDatos() {
        Connection conexion = null;
        PreparedStatement listar = null;
        ResultSet consulta = null;
        try {
            conexion = conectar.connect();
            modelo.setRowCount(0);
            listar = conexion.prepareStatement("SELECT * FROM person");
            consulta = listar.executeQuery();
            while (consulta.next()) {
                Object[] fila = new Object[8];
                fila[0] = consulta.getInt("id");
                fila[1] = consulta.getString("documentType");
                fila[2] = consulta.getString("number");
                fila[3] = consulta.getString("name");
                fila[4] = consulta.getString("BirthDate");
                fila[5] = consulta.getString("Gender");
                fila[6] = consulta.getString("pension");
                fila[7] = consulta.getString("health");
                modelo.addRow(fila);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al cargar datos: " + e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (consulta != null) {
                    consulta.close();
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Algo a salido mal");
            }
        }
    }

    private boolean validarCampos() {
        if (cbxDocumentType.getSelectedIndex() == 0 || cbxDocumentType.getSelectedItem().toString().equals("Seleccione")) {
            mostrarError("Seleccione un tipo de documento");
            cbxDocumentType.requestFocus();
            return false;
        }
        if (txtDocumentNumber.getText().trim().isEmpty()) {
            mostrarError("El numero de documento es requerido");
            return false;
        }
        if (txtName.getText().trim().isEmpty()) {
            mostrarError("El nombre es requerido");
            return false;
        }
        if (dtBirthDate.getDate() == null) {
            mostrarError("El cumpleaños es requerido");
            return false;
        }
        if (gender == null) {
            mostrarError("El genero es requerido");
            return false;
        }
        if (fondo == null) {
            mostrarError("El fondo es requerido");
            return false;
        }
        if (health == null) {
            mostrarError("La salud es requerida");
            return false;
        }

        return true;

    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de validacion", JOptionPane.ERROR_MESSAGE);
    }

    private boolean validarFormatoDocumento(String tipo, String numero) {
        numero = numero.trim();
        tipo = tipo.trim();

        switch (tipo) {
            case "Tarjeta Identidad":
            case "Cedula Ciudadania":
            case "Registro Civil":
                if (!numero.matches("\\d{7,11}")) {
                    mostrarError(tipo + " debe tener entre 8 y 11 dígitos numéricos");
                    return false;
                }
                break;
            case "Pasaporte":
                if (!numero.matches("[A-Z]{2}\\d{6,7}")) {
                    mostrarError("El pasaporte debe comenzar con 2 letras mayúsculas seguidas de 6 o 7 números");
                    return false;
                }
                break;
            case "Cedula Extranjeria":
                if (!numero.matches("[A-Z]{2}\\d{7}")) {
                    mostrarError("la cédula de extranjería debe comenzar con dos letras mayúsculas seguidas de 7 números");
                    return false;
                }
                break;
            default:
                mostrarError("Tipo de documento no válido");
                return false;
        }
        return true;
    }

    public void guardarPersona() {
        if (!validarCampos()) {
            return;
        }
        String numeroDocumento = txtDocumentNumber.getText().trim();
        String tipoDocumento = cbxDocumentType.getSelectedItem().toString();
        if (!validarFormatoDocumento(tipoDocumento, numeroDocumento)) {
            return;
        }
        Date fechaNacimiento = (Date) dtBirthDate.getDate();
        if (!validarFechaNacimiento(fechaNacimiento)) {
            return;
        }
        try (Connection conexion = conectar.conectar()) {
            String sql = "INSERT INTO persona(documentType, number,name,"
            + "birthDate ,gender, pension, health"
            + "VALUES (?,?,?,?,?,?,?)";
            try (PreparedStatement guardar = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                guardar.setString(1, tipoDocumento);
                guardar.setString(2, numeroDocumento);
                guardar.setString(3, txtName.getText().trim());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                guardar.setString(4, sdf.format(fechaNacimiento));
                guardar.setString(5, genderSelected);
                guardar.setString(6, fondo);
                guardar.setString(7, health);

                int filasAfectadas = guardar.executeUpdate();

                if (filasAfectadas > 0) {
                    try (ResultSet generatedKeys = guardar.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            actualizarTablaYLimpiar();
                            JOptionPane.showMessageDialog(this, "Datos registrados Exitosamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Datos NO registrados", "ERROR", JOptionPane.INFORMATION_MESSAGE);

                }
            }
        } catch (SQLException e) {
            Logger.getLogger(Form.class.getName()).log(Level.SEVERE, "Error al guardar");
            JOptionPane.showMessageDialog(this, "Error al guardar", "ERROR", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        gender = new javax.swing.ButtonGroup();
        pensiones = new javax.swing.ButtonGroup();
        salud = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        chkColpensiones = new javax.swing.JCheckBox();
        chkFondoPrivado = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        chkNuevaEPS = new javax.swing.JCheckBox();
        chkCompensar = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtDocumentNumber = new javax.swing.JTextField();
        cbxDocumentType = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        tbMasculino = new javax.swing.JRadioButton();
        rbFemenino = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        dtBirthDate = new com.toedter.calendar.JDateChooser();
        jPanel4 = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClean = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblDetail = new javax.swing.JTable();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Afiliacion");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder()), "Afiliaciones"));

        jLabel5.setText("Birth date");

        pensiones.add(chkColpensiones);
        chkColpensiones.setText("Colpensiones");
        chkColpensiones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkColpensionesActionPerformed(evt);
            }
        });

        pensiones.add(chkFondoPrivado);
        chkFondoPrivado.setText("Fondo Privado");
        chkFondoPrivado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFondoPrivadoActionPerformed(evt);
            }
        });

        jLabel8.setText("Salud");

        salud.add(chkNuevaEPS);
        chkNuevaEPS.setText("NuevaEPS");
        chkNuevaEPS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkNuevaEPSActionPerformed(evt);
            }
        });

        salud.add(chkCompensar);
        chkCompensar.setText("Compensar");
        chkCompensar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCompensarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(chkFondoPrivado)
                        .addGap(18, 18, 18)
                        .addComponent(chkColpensiones))
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(chkNuevaEPS)
                        .addGap(18, 18, 18)
                        .addComponent(chkCompensar)))
                .addContainerGap(171, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkFondoPrivado)
                    .addComponent(chkColpensiones))
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkNuevaEPS)
                    .addComponent(chkCompensar))
                .addContainerGap(161, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder()), "Datos Personales"));

        jLabel1.setText("id");

        txtId.setEditable(false);

        jLabel2.setText("Number");

        jLabel3.setText("Document type");

        txtDocumentNumber.setEditable(false);
        txtDocumentNumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDocumentNumberKeyTyped(evt);
            }
        });

        cbxDocumentType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Selections:", "TI", "CC", "CE", "PP", "RC" }));

        jLabel4.setText("Name");

        txtName.setEditable(false);
        txtName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNameKeyTyped(evt);
            }
        });

        jLabel6.setText("Gender");

        gender.add(tbMasculino);
        tbMasculino.setText("Masculino");
        tbMasculino.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbMasculinoActionPerformed(evt);
            }
        });

        gender.add(rbFemenino);
        rbFemenino.setText("Femenino");
        rbFemenino.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbFemeninoActionPerformed(evt);
            }
        });

        jLabel7.setText("Birth date");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel7))
                                .addGap(50, 50, 50)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtId)
                                    .addComponent(txtDocumentNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                                    .addComponent(txtName, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(dtBirthDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(8, 8, 8))
                                    .addComponent(cbxDocumentType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(34, 34, 34)
                                .addComponent(tbMasculino)
                                .addGap(18, 18, 18)
                                .addComponent(rbFemenino))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jLabel1)))
                .addGap(0, 10, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cbxDocumentType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtDocumentNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(dtBirthDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(tbMasculino)
                    .addComponent(rbFemenino))
                .addContainerGap(97, Short.MAX_VALUE))
        );

        btnSave.setText("Save");

        btnEdit.setText("Edit");

        btnDelete.setText("Delete");

        btnClean.setText("Clean");

        btnExit.setText("Exit");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnClean, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(108, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(btnClean)
                    .addComponent(btnExit)
                    .addComponent(btnEdit)
                    .addComponent(btnDelete))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        tblDetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(tblDetail);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane3)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(53, 53, 53))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tbMasculinoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbMasculinoActionPerformed
        genderSelected = "M";
    }//GEN-LAST:event_tbMasculinoActionPerformed

    private void rbFemeninoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbFemeninoActionPerformed
        genderSelected = "F";
    }//GEN-LAST:event_rbFemeninoActionPerformed

    private void chkFondoPrivadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFondoPrivadoActionPerformed
        fondo = "Fondo privado";

    }//GEN-LAST:event_chkFondoPrivadoActionPerformed

    private void chkColpensionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkColpensionesActionPerformed
        fondo = "Colpensiones";
    }//GEN-LAST:event_chkColpensionesActionPerformed

    private void chkNuevaEPSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkNuevaEPSActionPerformed
        health = "Nueva eps";
    }//GEN-LAST:event_chkNuevaEPSActionPerformed

    private void chkCompensarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCompensarActionPerformed
        health = "Compensar";
    }//GEN-LAST:event_chkCompensarActionPerformed

    private void txtDocumentNumberKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDocumentNumberKeyTyped
        char c = evt.getKeyChar();
        if (!Character.isDigit(c)) {
            evt.consume();
        }
    }//GEN-LAST:event_txtDocumentNumberKeyTyped

    private void txtNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNameKeyTyped
       char c = evt.getKeyChar();
        if (!Character.isLetter(c) && c != ' ' && c != KeyEvent.VK_BACK_SPACE) {
            evt.consume();
        }
    }//GEN-LAST:event_txtNameKeyTyped

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Form().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClean;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<String> cbxDocumentType;
    private javax.swing.JCheckBox chkColpensiones;
    private javax.swing.JCheckBox chkCompensar;
    private javax.swing.JCheckBox chkFondoPrivado;
    private javax.swing.JCheckBox chkNuevaEPS;
    private com.toedter.calendar.JDateChooser dtBirthDate;
    private javax.swing.ButtonGroup gender;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.ButtonGroup pensiones;
    private javax.swing.JRadioButton rbFemenino;
    private javax.swing.ButtonGroup salud;
    private javax.swing.JRadioButton tbMasculino;
    private javax.swing.JTable tblDetail;
    private javax.swing.JTextField txtDocumentNumber;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtName;
    // End of variables declaration//GEN-END:variables
    private String genderSelected;
    private String fondo;
    private String health;
}
