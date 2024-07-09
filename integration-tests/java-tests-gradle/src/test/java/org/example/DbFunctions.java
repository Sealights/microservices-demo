package org.example;
import java.sql.*;

public class DbFunctions {
    public Connection connect_to_db(String dbname,String user,String pass){
        Connection conn=null;
        try{
            Class.forName("org.postgresql.Driver");
            conn= DriverManager.getConnection("jdbc:postgresql://btq-automation.colnnovkdyzx.us-west-2.rds.amazonaws.com/"+dbname,user,pass);
            if(conn!=null){
                System.out.println("Connection Established");
            }
            else{
                System.out.println("Connection Failed");
            }

        }catch (Exception e){
            System.out.println(e);
        }
        return conn;
    }
    public void createTable(Connection conn,String table_name) {
        Statement statement = null;
        try{
            String query="create table if not exists "+table_name+"(id SERIAL,lab_id varchar(200),test_name varchar(200),result varchar(200),primary key(id));";
            statement=conn.createStatement();
            statement.executeUpdate(query);
            statement.close();
            System.out.println("Table Created");
        }catch (Exception e){
            System.out.println(e);
        }

    }

    public void insert_row(Connection conn,String table_name,String lab_id,String test_name, String result) {
        String sql = "insert into " + table_name + "(lab_id,test_name,result) values(?,?,?)";


        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, lab_id);
            preparedStatement.setString(2, test_name);
            preparedStatement.setString(3, result);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Data inserted successfully!");
            } else {
                System.out.println("Failed to insert data.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        public void read_data(Connection conn, String table_name){
        Statement statement;
        ResultSet rs=null;
        try {
            String query=String.format("select * from %s",table_name);
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            while(rs.next()){
                System.out.print(rs.getString("empid")+" ");
                System.out.print(rs.getString("name")+" ");
                System.out.println(rs.getString("Address")+" ");
            }

        }
        catch (Exception e){
            System.out.println(e);
        }
    }
    public void update_name(Connection conn,String table_name, String old_name,String new_name){
        Statement statement;
        try {
            String query=String.format("update %s set name='%s' where name='%s'",table_name,new_name,old_name);
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Data Updated");
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void search_by_name(Connection conn, String table_name,String name){
        Statement statement;
        ResultSet rs=null;
        try {
            String query=String.format("select * from %s where name= '%s'",table_name,name);
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            while (rs.next()){
                System.out.print(rs.getString("empid")+" ");
                System.out.print(rs.getString("name")+" ");
                System.out.println(rs.getString("address"));

            }
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void search_by_id(Connection conn, String table_name,int id){
        Statement statement;
        ResultSet rs=null;
        try {
            String query=String.format("select * from %s where empid= %s",table_name,id);
            statement=conn.createStatement();
            rs=statement.executeQuery(query);
            while (rs.next()){
                System.out.print(rs.getString("empid")+" ");
                System.out.print(rs.getString("name")+" ");
                System.out.println(rs.getString("address"));

            }
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void delete_row_by_name(Connection conn,String table_name, String name){
        Statement statement;
        try{
            String query=String.format("delete from %s where name='%s'",table_name,name);
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Data Deleted");
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void delete_row_by_id(Connection conn,String table_name, int id){
        Statement statement;
        try{
            String query=String.format("delete from %s where empid= %s",table_name,id);
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Data Deleted");
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void delete_table(Connection conn, String table_name){
        Statement statement;
        try {
            String query= String.format("drop table %s",table_name);
            statement=conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Table Deleted");
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
