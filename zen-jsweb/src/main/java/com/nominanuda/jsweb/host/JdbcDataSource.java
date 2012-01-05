package com.nominanuda.jsweb.host;

import java.io.Serializable;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import com.nominanuda.dataobject.DataArray;
import com.nominanuda.dataobject.DataObject;
import com.nominanuda.dataobject.DataObjectImpl;
import com.nominanuda.dataobject.DataStructHelper;
import com.nominanuda.lang.DateTimeHelper;
import com.nominanuda.rhino.DataStructScriptableConvertor;
import com.nominanuda.rhino.host.JavaJsHostObject;


public class JdbcDataSource extends ScriptableObject implements JavaJsHostObject {
	private static final long serialVersionUID = -2787369864052968821L;
	private static transient final RowMapper<DataObject> rowMapper = new DataObjectRowMapper();
	private static transient final DataStructScriptableConvertor scriptableConvertor = new DataStructScriptableConvertor();
	
	private static final int ORACLE_CURSOR = -10;
	private final DataObject dataSourceConfig;
	private transient DataSource dataSource;
	private transient Map<String, PreparedStatementCreatorFactory> pscMap = new HashMap<String, PreparedStatementCreatorFactory>();

	@Override
	public String getClassName() {
		return "JdbcDataSource";
	}

	public JdbcDataSource() {
		dataSourceConfig = null;
	}

	public JdbcDataSource(DataObject state) {
		dataSourceConfig = state;
	}

	public static Scriptable jsConstructor(Context cx, Object[] args, Function ctorObj, boolean inNewExpr) {
		DataObject state = (DataObject)scriptableConvertor.fromScriptable((Scriptable)args[0]);
		return new JdbcDataSource(state);
	}
	private DataSource getDataSource() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if(dataSource == null) {
			dataSource = createDataSource();
		}
		return dataSource;
	}

	private DataSource createDataSource() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		@SuppressWarnings("unchecked")
		Class<DataSource> dsClass = (Class<DataSource>)Class.forName(
				dataSourceConfig.getString("dataSourceClass"));
		DataSource ds = dsClass.newInstance();
		BeanWrapperImpl beanWrapper = new BeanWrapperImpl(ds);
		for(String k : dataSourceConfig.getKeys()) {
			if(! "dataSourceClass".equals(k)) {
				beanWrapper.setPropertyValue(k, dataSourceConfig.get(k));
			}
		}
		return ds;
	}
static DataStructHelper dataStruct = new DataStructHelper();
	public static DataArray jsFunction_invokeCallable(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
		DataObject conf = (DataObject)scriptableConvertor.fromScriptable((Scriptable)args[0]);
		List<JdbcParam> params = toParams(conf.getArray("args"));
		SimpleJdbcCall call = ((JdbcDataSource)thisObj).createCallable(conf);
		normalizeArgs(args);
		Map<String, Object> m = call.execute(buildSqlParameterSource(params, Arrays.copyOfRange(args, 1, args.length)));
		@SuppressWarnings("unchecked")
		List<? super Object> res = (List<? super Object>)m.get("CUR_RS");
		DataArray ta = dataStruct.fromMapsAndCollections(res);
		return ta;
	}
	
	public static int jsFunction_update(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
		JdbcTemplate  jdbcTemplate  =  new  JdbcTemplate(((JdbcDataSource)thisObj).getDataSource());
		return jdbcTemplate.update(extractStatement(args), extractStatementArgs(args));
	}

	public static DataArray jsFunction_query(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception {
		JdbcTemplate  jdbcTemplate  =  new  JdbcTemplate(((JdbcDataSource)thisObj).getDataSource()); 
		List<?> result = jdbcTemplate.query(extractStatement(args), extractStatementArgs(args), rowMapper);
		DataArray ta = dataStruct.fromMapsAndCollections(result);
		return ta;
	}
	
	public static DataArray jsFunction_invokeStatementForQuery(Context cx, Scriptable thisObj, final Object[] args, Function funObj) throws Exception {
		JdbcDataSource jdbcDataSource = (JdbcDataSource)thisObj;
		JdbcTemplate  jdbcTemplate  =  new  JdbcTemplate(jdbcDataSource.getDataSource()); 
		PreparedStatementCreator newPreparedStatementCreator = jdbcDataSource.getPreparedStatementCreator(args);
		List<?> result = jdbcTemplate.query(newPreparedStatementCreator, rowMapper);
		DataArray ta = dataStruct.fromMapsAndCollections(result);
		return ta;
	}
	
	public static int jsFunction_invokeStatementForUpdate(Context cx, Scriptable thisObj, final Object[] args, Function funObj) throws Exception {
		JdbcDataSource jdbcDataSource = (JdbcDataSource)thisObj;
		JdbcTemplate  jdbcTemplate  =  new  JdbcTemplate(jdbcDataSource.getDataSource()); 
		PreparedStatementCreator newPreparedStatementCreator = jdbcDataSource.getPreparedStatementCreator(args);
		return jdbcTemplate.update(newPreparedStatementCreator);
	}
	
	private PreparedStatementCreator getPreparedStatementCreator(Object[] args) {
		PreparedStatementCreatorFactory psc = getPreparedStatementCreatorFactory(args);
		PreparedStatementCreator newPreparedStatementCreator = psc.newPreparedStatementCreator(extractStatementArgs(args));
		return newPreparedStatementCreator;
	}
	
	private PreparedStatementCreatorFactory getPreparedStatementCreatorFactory(Object[] args){
		DataObject conf = (DataObject)scriptableConvertor.fromScriptable((Scriptable)args[0]);
		SqlParameter[] declared = getDeclareParameters(toParams(conf.getArray("args")));
		String sql = conf.getString("sql");
		if(pscMap.containsKey(sql)) {
			return pscMap.get(sql);
		} else {
			return new PreparedStatementCreatorFactory(sql, Arrays.asList(declared));
		}
	}
	

	private static String extractStatement(Object[] args) {
		return (String) args[0];
	}
	
	private static Object[] extractStatementArgs(Object[] args) {
		Object[] argsResult = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : null;
		if(argsResult != null) { normalizeArgs(argsResult); }
		return argsResult;
	}
	

	private static void normalizeArgs(Object[] args) {
		for (int i = 0; i < args.length; i++) {
			Object object = args[i];
			if(object instanceof java.util.Date || object.getClass().getName().equals("org.mozilla.javascript.NativeDate")){
				Object v = Context.jsToJava(args[i], java.util.Date.class);
				args[i] = new java.sql.Date(((java.util.Date)v).getTime());
			}
		}
	}
	
	

	private SimpleJdbcCall createCallable(DataObject conf) throws Exception {
		DataSource ds = getDataSource();
		List<JdbcParam> params = toParams(conf.getArray("args"));
		JdbcTemplate tpl = new JdbcTemplate(ds);
		String fullName = conf.exists("pkg")
			? conf.getString("pkg") +"."+conf.getStrict("name")
			: (String)conf.getStrict("name");
		SimpleJdbcCall call = new SimpleJdbcCall(tpl)
			.withoutProcedureColumnMetaDataAccess()
			.withProcedureName(fullName)
			.useInParameterNames(getUseInParameterNames(params))
			.declareParameters(getDeclareParameters(params));
		if(conf.exists("catalog")) {
			call.withCatalogName(conf.getString("catalog"));
		}
		return call;
	}
	
	private static List<JdbcParam> toParams(DataArray args) {
		int len = args.getLength();
		List<JdbcParam> res = new LinkedList<JdbcParam>();
		for(int i = 0; i < len; i++) {
			DataArray arg = args.getArray(i);
			res.add(JdbcParam.p(arg.getString(0),
				JdbcParamDirection.valueOf(arg.getString(1)),
				toSqlType(arg.getString(2))));
		}
		return res;
	}
	private static int toSqlType(String s) {
		if("INTEGER".equals(s)) {
			return Types.INTEGER;
		} else if("DATE".equals(s)) {
			return Types.DATE;
		} else if("VARCHAR".equals(s)){
			return Types.VARCHAR;
		} else if("CURSOR".equals(s)) {
			return ORACLE_CURSOR;
		} else {
			throw new IllegalArgumentException(s+" sql type: not found");
		}
	}
	private static SqlParameterSource buildSqlParameterSource(List<JdbcParam> params, Object...args) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		Iterator<JdbcParam> pIter = params.iterator();
		for(int i = 0; i < args.length; i++) {
			JdbcParam p = pIter.next();
			Object val = args[i];
			while(p.direction != JdbcParamDirection.IN) {
				p = pIter.next();
			}
			paramSource.addValue(p.name, val);
		}
		return paramSource;
	}
	private SqlParameter[] getDeclareParameters(List<JdbcParam> params) {
		SqlParameter[] res = new SqlParameter[params.size()];
		for(int i = 0; i < params.size(); i++) {
			JdbcParam p = params.get(i);
			res[i] = p.direction == JdbcParamDirection.IN
				? new SqlParameter(p.name, p.sqlType)
				: new SqlOutParameter(p.name, p.sqlType, rowMapper);
		}
		return res;
	}
	private String[] getUseInParameterNames(List<JdbcParam> params) {
		String[] res = new String[params.size()];
		for(int i = 0; i < params.size(); i++) {
			res[i] = params.get(i).name;
		}
		return res;
	}
	
	
	public String getJsScript() {
		return "JdbcDataSource.prototype.createCallable = function(conf,ddss) {" +
				"var ds = ddss; " +
				"  return { " +
				"    execute: function() { " +
				"      var args = Array.prototype.slice.call(arguments); " +
				"      args.unshift(conf); " +
				"      return ds.invokeCallable.apply(ds, args); " +
				"    } " +
				"  }; " +
				"}; " +

				"JdbcDataSource.prototype.createStatementForQuery = function(conf) { " +
				"  var ds = this;" +
				"  return { " + 
				"    execute: function() { " +
				"      var args = Array.prototype.slice.call(arguments); " +
				"      args.unshift(conf); " +
				"      return ds.invokeStatementForQuery.apply(ds, args); " +
				"    } " +
				"  }; " +
				"};" +
		
				"JdbcDataSource.prototype.createStatementForUpdate = function(conf) { " +
				"  var ds = this;" +
				"  return { " + 
				"    execute: function() { " +
				"      var args = Array.prototype.slice.call(arguments); " +
				"      args.unshift(conf); " +
				"      return ds.invokeStatementForUpdate.apply(ds, args); " +
				"    } " +
				"  }; " +
				"};";
	}
	
	public enum JdbcParamDirection {
		IN, OUT, INOUT
	}
	
	public static class JdbcParam {
		public final String name;
		public final JdbcDataSource.JdbcParamDirection direction;
		public final int sqlType;
	
		public JdbcParam(String name, JdbcDataSource.JdbcParamDirection direction, int sqlType) {
			this.name = name;
			this.direction = direction;
			this.sqlType = sqlType;
		}
		
		public static JdbcParam p(String name, JdbcDataSource.JdbcParamDirection direction, int sqlType) {
			return new JdbcParam(name, direction, sqlType);
		}
	}
	
	public static class DataObjectRowMapper implements RowMapper<DataObject>, Serializable {
		private static final long serialVersionUID = 4753675867L;
		private final DateTimeHelper dateTime = new DateTimeHelper();
	
		public DataObject mapRow(ResultSet rs, int rowNum) throws SQLException {
			DataObjectImpl o = new DataObjectImpl();
			ResultSetMetaData rsMetaData = rs.getMetaData();
			int numberOfColumns = rsMetaData.getColumnCount();
			for (int i = 1; i < numberOfColumns + 1; i++) {
				String name = rsMetaData.getColumnName(i);
				//Object jdbcVal = rs.getObject(name);
				Object tval = convertToNative(rs, name);
				o.put(name, tval);
			}
			return o;
		}
	
		protected Object convertToNative(ResultSet rs, String name) throws SQLException {
			Date ts = null;
			try {
				ts = rs.getDate(name);
			} catch (Exception e) {}
			if(ts != null) {
				Time tm = null;
				try {
					tm = rs.getTime(name);
				} catch (Exception e) {}
				if(tm != null) {
					DateTime dt = new DateTime(ts.getTime());
					dt = dt.plusMillis((int)tm.getTime());
					return dateTime.toISO8601UtcSecs(dt.getMillis());//TODO millis ??
				}
			}
			Object jdbcVal = rs.getObject(name);
			return convertToNative(jdbcVal);
		}
	
		protected Object convertToNative(Object jdbcVal) {
			if(jdbcVal == null) {
				return null;
			} else if(jdbcVal instanceof java.sql.Timestamp) {
				java.sql.Timestamp ts = (java.sql.Timestamp)jdbcVal;
				long utcEpochMillis = ts.getTime();
				return dateTime.toISO8601UtcSecs(utcEpochMillis);
			} else if(jdbcVal instanceof java.sql.Date) {
				java.sql.Date sqlDt = (java.sql.Date)jdbcVal;
				long utcEpochMillis = sqlDt.getTime();
				return dateTime.toISO8601UtcSecs(utcEpochMillis);
			} else {
				return jdbcVal;
			}
		}
	}

}


