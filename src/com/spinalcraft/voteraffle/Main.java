package com.spinalcraft.voteraffle;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Main {
	private static final String DB_URL = "jdbc:mysql://localhost";
	
	public static void main(String[] args){
		if(args.length < 2){
			System.err.println("Invalid number of arguments.");
			System.exit(1);
		}
		
		int month = Integer.parseInt(args[0]);
		int year = Integer.parseInt(args[1]);
		
		String query = "SELECT username, c FROM (SELECT username, uuid, COUNT(*) as c "
				+ "FROM Spinalcraft.Votes "
				+ "WHERE MONTH(FROM_UNIXTIME(date)) = ? AND YEAR(FROM_UNIXTIME(DATE)) = ? AND uuid IS NOT NULL "
				+ "GROUP BY uuid "
				+ "ORDER BY username) as V";
		
		try {
			Connection conn  = DriverManager.getConnection(DB_URL, "root", "password");
			PreparedStatement stmt = conn.prepareStatement(query);
			stmt.setInt(1, month);
			stmt.setInt(2, year);
			ResultSet rs = stmt.executeQuery();
			HashMap<String, Integer> hashmap = new HashMap<String, Integer>();
			while(rs.next()){
				hashmap.put(rs.getString("username"), rs.getInt("c"));
			}
			
			FileOutputStream fos = new FileOutputStream("hashmap.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(hashmap);
			
			oos.close();
			fos.close();
			
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}	
	}
}
