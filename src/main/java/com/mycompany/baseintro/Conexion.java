/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.baseintro;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author PC_10
 */
public class Conexion {
    public Conexion(){
        
    }
    
    private static Connection conexion;
    private static Conexion instance;
    
    public static final String URL = "jdbc:mysql://localhost:3306/information";
    public static final String USERNAME ="root";
    public static final String PASSWORD = "";
    
    public Connection connect(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(URL,USERNAME,PASSWORD);
            return conexion;
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,"Error:"+e);
        }
        return conexion;
    }
    
    public void closeConexion() throws SQLException{
    try{
        conexion.close();
    }catch(Exception e){
    JOptionPane.showMessageDialog(null,"Error:"+e);
    conexion.close();
    }}

    public static Conexion getInstance(){
    if(instance == null){
        instance = new Conexion();
    }
    return instance;
    }
           
    
}
