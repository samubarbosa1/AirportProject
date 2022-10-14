import java.sql.*;
import java.util.Vector;

import javax.swing.JOptionPane;

public class DataBase {

    private Connection conn = null;

    /*Conectando ao MySql*/
    public void connectToMysql(){
        try {
            System.out.println("Connecting to Data Base...");
            String url = "jdbc:mysql://localhost:3306/airportsdb";
            String user = "root";
            String password = "StartingTh1sToAJ@vaProject";
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Application has connected to Data Base successfully!");
        } catch (SQLException error) {
            JOptionPane.showMessageDialog(null, error.getMessage());
        }
    }

    /* Função para retornar os elementos de uma coluna */
    public Vector<String> getCollumn(String colName){
        Vector<String> colVec = new Vector<>();
        try{
            Statement stat = conn.createStatement();
            ResultSet result = stat.executeQuery("select * from brazilian_airports_data");
            while(result.next()){
                colVec.add(result.getString(colName));
            }
        }   catch (SQLException error){
            JOptionPane.showMessageDialog(null, error.getMessage());
        }

        return colVec;
    }

    public void writeData(String tableName, String dataToSave){
        try{
            Statement stat = conn.createStatement();
            stat.executeUpdate("INSERT INTO " + tableName + " VALUES ('" + dataToSave + "')");
        }   catch (SQLException error){
            JOptionPane.showMessageDialog(null, error.getMessage());
        }
    }

}