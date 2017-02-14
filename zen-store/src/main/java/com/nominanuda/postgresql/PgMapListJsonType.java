package com.nominanuda.postgresql;

import static com.nominanuda.dataobject.DataStructHelper.Z;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

public class PgMapListJsonType implements UserType {

	@Override
	public int[] sqlTypes() {
		return new int[] { Types.JAVA_OBJECT };
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<Map> returnedClass() {
		return Map.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		if(x == null || y == null) {
			return false;
		}
		return x.equals(y);//TODO id equality
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
			throws HibernateException, SQLException {
		final String cellContent = rs.getString(names[0]);
		if (cellContent == null) {
			return null;
		}
		try {
			return Z.toMapsAndLists(Z.parseObject(cellContent));
		} catch (final Exception ex) {
			throw new RuntimeException("Failed to convert String to Invoice: " + ex.getMessage(), ex);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
			throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, Types.OTHER);
			return;
		}
		try {
			st.setObject(index, Z.fromMapsAndCollections((Map<String,Object>)value).toString(), Types.OTHER);
		} catch (final Exception ex) {
			throw new RuntimeException("Failed to convert Invoice to String: " + ex.getMessage(), ex);
		}
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return
		Z.toMapsAndLists(
			Z.clone(
				Z.fromMapsAndCollections((Map<String,Object>)value)));
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		throw new HibernateException("unimplemented");
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		throw new HibernateException("unimplemented");
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		throw new HibernateException("unimplemented");
	}

}