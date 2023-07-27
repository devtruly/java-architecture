package repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import config.Database;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;

public abstract class AbstractRepository<T> {
	protected Map<String, String> fieldMap = new HashMap<>();
	protected Map<String, Object> fieldValue = new HashMap<>();
	protected ResultSet rs;
	protected Field[] fields;
	private static final String toUderRegex = "([a-z])([A-Z]+)";
	private static final String toUderReplacement = "$1_$2";
	//private final String toCamelReplacement = "\U$1";
	protected Class<T> vo;
	protected String tableName;
	protected String indexName;
	protected Database db;
	
	
	protected final String insertSql = "Insert Into %s (%s) values (%s)";
	protected final String updateSql = "Update %s Set %s Where %s";
	protected final String deleteSql = "Delete From %s Where %s = %s";
	protected final String selectSql = "select %s From %s %s";
	
	public AbstractRepository(Class<T> vo) {
		db = new Database();
		this.vo = vo;
		
		fields = this.vo.getDeclaredFields();
		try {
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].getName().equals("TABLENAME")) {
					tableName = (String) fields[i].get(vo);
				}
				else if (fields[i].getName().equals("INDEXNAME")) {
					indexName = (String) fields[i].get(vo);
				}
				else {
					fieldMap.put(fields[i].getName(), convertCamelToSnake(fields[i].getName()));
				}
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param vo : VO Object
	 * @return int : insert result value
	 */
	public int save(T vo) {
		fields = this.vo.getDeclaredFields();
		fieldValue = new HashMap<>();
		int result = 0;
		try {
			for (int i = 0; i < fields.length; i++) {
				if (!fields[i].getName().equals("TABLENAME") && !fields[i].getName().equals("INDEXNAME")) {
					fieldValue.put(fieldMap.get(fields[i].getName()), getValue(fields[i].getName(), vo));
				}
			}
			
			Map<String, String> insertValues = setOneValues(fieldValue);
			result = db.update(String.format(insertSql, tableName, insertValues.get("fields"), insertValues.get("values")));
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * @param vo : VO Object
	 * @return int : update result value
	 */
	public int update(T vo) {
		int result = 0;
		Map<String, Object> whereMap = new HashMap<>();
		fields = this.vo.getDeclaredFields();
		fieldValue = new HashMap<>();
		try {
			//System.out.println(getValue(indexName, vo));
			
			whereMap.put(convertCamelToSnake(indexName), (Object) getValue(indexName, vo));
			for (int i = 0; i < fields.length; i++) {
				if (!fields[i].getName().equals("TABLENAME") && !fields[i].getName().equals("INDEXNAME") && !fields[i].getName().equals("boardNo")) {
					fieldValue.put(fieldMap.get(fields[i].getName()), getValue(fields[i].getName(), vo));
				}
			}
			
			String insertValues = setTwoValues(fieldValue);
			
			result = db.update(String.format(updateSql, tableName, insertValues, setTwoValues(whereMap)));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * @param no : delete target index no, VO class INDEXNAME field search no
	 * @return int : delete result value
	 */
	public int deleteById(int no) {
		int result = 0;
		
		try {
			result = db.update(String.format(deleteSql, tableName, convertCamelToSnake(indexName), no));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * @param select target index no, VO class INDEXNAME field search no
	 * @return List<T> select result Object
	 */
	public List<T> selectFindById(int no) {
		List<T> returnData = new ArrayList<>();
		try {
			for (int i = 0; i < fields.length; i++) {
				if (!fields[i].getName().equals("TABLENAME") && !fields[i].getName().equals("INDEXNAME")) {
					fieldValue.put(fieldMap.get(fields[i].getName()), 1);
				}
			}
			
			Map<String, String> insertValues = setOneValues(fieldValue);
			rs = db.select(String.format("Select %s From %s Where %s = %d", insertValues.get("fields"), tableName, convertCamelToSnake(indexName), no));
			while (rs.next()) {
				returnData.add(setValue(rs));
			}
		}
		catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return returnData;
	}
	
	/**
	 * @return List<T> select result Object
	 */
	public List<T> selectAll() {
		List<T> returnData = new ArrayList<>();
		try {
			for (int i = 0; i < fields.length; i++) {
				if (!fields[i].getName().equals("TABLENAME") && !fields[i].getName().equals("INDEXNAME")) {
					fieldValue.put(fieldMap.get(fields[i].getName()), 1);
				}
			}
			
			Map<String, String> insertValues = setOneValues(fieldValue);
			
			rs = db.selectAll(String.format("Select %s From %s", insertValues.get("fields"), tableName));
			while (rs.next()) {
				returnData.add(setValue(rs));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		catch (InstantiationException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return returnData;
	}
	
	/**
	 * @param delete query
	 * @return int : delete result no;
	 */
	public int deleteByQuery(String query) {
		return updateByQuery(query);
	}
	
	/**
	 * @param insert query
	 * @return int : insert result no;
	 */
	public int insertByQuery(String query) {
		return updateByQuery(query);
	}
	
	/**
	 * @param update query
	 * @return int : update result no;
	 */
	public int updateByQuery(String query) {
		int result = 0;
		
		try {
			result = db.update(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * @param select query
	 * @return List<T> select result Object
	 */
	public List<T> selectByQuery(String query) {
		List<T> returnData = new ArrayList<>();
		
		try {
			rs = db.select(query);
			while (rs.next()) {
				returnData.add(setValue(rs));
			}
		} catch (NullPointerException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | InstantiationException | NoSuchFieldException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return returnData;
	}
	
	public Object getValue(String fieldName, T vo) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = this.vo.getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
		
		return method.invoke(vo);
	}
	
	public T setValue(ResultSet rs) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException, InstantiationException, NoSuchFieldException {
		ResultSetMetaData rsmd = rs.getMetaData();
		List<String> columnList = new ArrayList<>();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			columnList.add(rsmd.getColumnName(i));
		}
		
		T vo = this.vo.newInstance();
		
		for (Method m : this.vo.getMethods()) {
			if (m.getName().indexOf("set") > -1) {
				String fieldName = m.getName().replace("set", "");
				String camelCase = (fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1));
				String underScore = convertCamelToSnake(camelCase);
				Class<?>[] params = m.getParameterTypes();
				if (params[0].equals(String.class) && columnList.indexOf(underScore) > -1) {
					m.invoke(vo, rs.getString(underScore));
				}
				else if ((params[0].equals(int.class) || params[0].equals(Integer.class)) && columnList.indexOf(underScore) > -1) {
					m.invoke(vo, rs.getInt(underScore));
				}
				else if (params[0].equals(Long.class) && columnList.indexOf(underScore) > -1) {
					m.invoke(vo, rs.getLong(underScore));
				}
				else if ((params[0].equals(Date.class) || params[0].equals(java.sql.Date.class)) && columnList.indexOf(underScore) > -1) {
					m.invoke(vo, rs.getTimestamp(underScore));
				}
			}
		}

		return vo;
	}
	
	public Map<String, String> setOneValues(Map<String, Object> data) {
		Map<String, String> returnData = new HashMap<>();
		
		Set<String> keys = data.keySet();
		List<String> arrayKeys = new ArrayList<>();
		List<String> arrayValues = new ArrayList<>();
		for (String key: keys) {
			if (data.get(key) != null) {
				arrayKeys.add(key);
				if (data.get(key) instanceof String || data.get(key) instanceof Date || data.get(key) instanceof LocalDateTime) {
					arrayValues.add(String.format("'%s'", data.get(key)));
				}
				else {
					arrayValues.add(String.format("%s", data.get(key)));
				}
			}
		}
		
		returnData.put("fields", String.join(", ", arrayKeys));
		returnData.put("values", String.join(", ", arrayValues));
		return returnData;
	}
	
	public String setTwoValues(Map<String, Object> data) {
		String returnData = null;
		
		Set<String> keys = data.keySet();
		List<String> arrayValues = new ArrayList<>();
		for (String key: keys) {
			if (data.get(key) != null) {
				if (data.get(key) instanceof String || data.get(key) instanceof Date || data.get(key) instanceof LocalDateTime) {
					arrayValues.add(String.format("%s = '%s'", key, data.get(key)));
				}
				else {
					arrayValues.add(String.format("%s = %s", key, data.get(key)));
				}
			}
		}
		
		returnData = String.join(", ", arrayValues);
		return returnData;
	}
	
	public static String convertCamelToSnake(String str) {
		return str.replaceAll(toUderRegex, toUderReplacement).toLowerCase();
	}
	
	public static String convertSnakeToCamel(String str) {

	    if (str.indexOf('_') < 0 && Character.isLowerCase(str.charAt(0))) {
	        return str;
	    }
		
	    StringBuilder result = new StringBuilder();
	    boolean nextUpper = false;
	    int len = str.length();

	    for (int i = 0; i < len; i++) {
		    char currentChar = str.charAt(i);	
	        if (currentChar == '_') {
	            nextUpper = true;
		    } else {	
	            if (nextUpper) {
	                result.append(Character.toUpperCase(currentChar));
	                nextUpper = false;
	            } else {
	                result.append(Character.toLowerCase(currentChar));
	            }
	        }
	    }
	    return result.toString();
	}
}
