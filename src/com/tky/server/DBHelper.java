package com.tky.server;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBHelper {

	public static String IP = "192.168.137.102:1433;DatabaseName=TKYAPP";
	public static String SA = "sa";
	public static String PASSWORD = "111111";
	
	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn =DriverManager.getConnection("jdbc:sqlserver://"+DBHelper.IP,DBHelper.SA,DBHelper.PASSWORD);// 创建本地数据连接;
			System.out.println("connection create success");
		} catch (Exception e) {
			System.out.println("connection create failed");
		}
		return conn;
	}
}
