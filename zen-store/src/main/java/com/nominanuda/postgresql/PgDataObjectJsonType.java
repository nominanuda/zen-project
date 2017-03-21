package com.nominanuda.postgresql;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import com.nominanuda.zen.obj.Obj;
import com.nominanuda.zen.stereotype.Value;

public class PgDataObjectJsonType implements UserType {

	@Override
	public int[] sqlTypes() {
		return new int[] { Types.JAVA_OBJECT };
	}

	@Override
	public Class<Obj> returnedClass() {
		return Obj.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return Value.nullSafeEquals(x, y);
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
			return Obj.parse(cellContent);
		} catch (final Exception ex) {
			throw new RuntimeException("Failed to convert String to Invoice: " + ex.getMessage(), ex);
		}
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
			throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, Types.OTHER);
			return;
		}
		try {
			st.setObject(index, value.toString(), Types.OTHER);
		} catch (final Exception ex) {
			throw new RuntimeException("Failed to convert Invoice to String: " + ex.getMessage(), ex);
		}
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return ((Obj)value).copy();
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