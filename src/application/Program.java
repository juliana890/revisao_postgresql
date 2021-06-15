package application;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import DB.DB;

public class Program {

	public static void main(String[] args) throws SQLException {
		
		Connection conn = DB.getConnection();
		
		Statement st = conn.createStatement();
		
		ResultSet rs = st.executeQuery("SELECT * FROM tb_product");

		while(rs.next()) {
			System.out.println(rs.getInt("id") + ", " +rs.getString("name"));
		}
		
		DB.closeConnection();
		DB.closeStatement(st);
		DB.closeResultSet(rs);
	}

}
