package com.db;
import java.sql.*;
/*
 * 用于对数据库的基本操作
 */

public class DBManager {
//	数据库连接量
	public static final String DRIVER="com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static final String USER="zys_user";
	public static final String PASS="biaozhunban123";
	public static final String URL="jdbc:sqlserver://localhost:1434;databaseName=zys_erp";
// 	单例设计模式
	private static DBManager per=null;
	private Connection conn=null;
	private Statement stmt=null;
	
//	懒汉式单例设计模式
	
	private DBManager(){
		
	}
	public static DBManager createInstance(){
		if(per==null){
			per=new DBManager();
			per.initDB();
			
		}
		return per;
	}
	//加载驱动d
	public void initDB(){
			try {
				Class.forName(DRIVER);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
	//连接数据库 获取句柄和对象
	 public void connectDB() {
	        System.out.println("Connecting to database...");
	        try {
	            conn = DriverManager.getConnection(URL, USER, PASS);
	            stmt = (Statement)conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        
	    }
	 // 关闭数据库 关闭对象，释放句柄
	    public void closeDB() {
	        System.out.println("Close connection to database..");
	        try {
	        	if(stmt!=null)
	            stmt.close();
	        	if(conn!=null)
	            conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        System.out.println("Close connection successful");
	    }
	 // 查询
	    public ResultSet executeQuery(String sql) {
	        ResultSet rs = null;
	        try {
	            rs = stmt.executeQuery(sql);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return rs;
	    }
	 // 增添/删除/修改
	    public int executeUpdate(String sql) {
	        int ret = 0;
	        try {
	            ret = stmt.executeUpdate(sql);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return ret;
	    }
	

}
