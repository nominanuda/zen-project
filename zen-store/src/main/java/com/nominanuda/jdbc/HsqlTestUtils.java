package com.nominanuda.jdbc;

import static com.nominanuda.io.IOHelper.IO;

import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HsqlTestUtils {
	public static Connection connect(String dbName) throws SQLException, IOException {
		Connection c = DriverManager.getConnection("jdbc:hsqldb:mem:"+dbName, "sa", "");
		return c;
	}

	public static void executeSql(Connection c, InputStream is) throws IOException, SQLException {
		String stmts = IO.readAndCloseUtf8(is);
		CallableStatement ddlStmt = c.prepareCall(stmts);
		ddlStmt.execute();
	}
}
