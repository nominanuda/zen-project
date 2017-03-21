package com.nominanuda.jdbc;

import static java.lang.System.currentTimeMillis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.junit.Test;

import com.nominanuda.zen.obj.Obj;

public class JdbcWrapTest {

	@Test
	public void test() throws Exception {
		Connection c = HsqlTestUtils.connect("testDb");
		HsqlTestUtils.executeSql(c, getClass().getResourceAsStream("testDb.ddl.sql"));
		String insertSql = "INSERT INTO test(obj, usr, desc, tstamp,flag) VALUES(?,?,?,?,?)";

		PreparedStatement ins = c.prepareStatement(insertSql);
		ins.setString(1, "OBJ");
		ins.setInt(2, 1001);
		ins.setString(3, "A LONG DESC");
		ins.setLong(4, currentTimeMillis());
		ins.setBoolean(5, false);
		ins.executeUpdate();

		PreparedStatement q = c.prepareStatement("SELECT * FROM test");
		ResultSet rs = q.executeQuery();
		ResultSetMetaData meta =  rs.getMetaData();
		while(rs.next()) {
			Obj o = wrapRow(rs, meta);
			System.err.println(o.toString());//(rs.getObject("obj"));
		}

	}

	private Obj wrapRow(ResultSet rs, ResultSetMetaData meta, String...cols) throws SQLException {
		Obj o = Obj.make();
		int ncol = meta.getColumnCount();
		for(int i = 1; i <= ncol; i++) {
			String cn = meta.getColumnName(i);
			if(i != 3)
			o.put(cn, rs.getObject(i));
		}
		return o;
	}

}
