package com.dreamland.database;

import com.dreamland.database.annotation.DefaultZero;
import com.dreamland.database.annotation.NoColumn;
import com.dreamland.database.annotation.Unique;
import com.dreamland.database.annotation.UniqueConstraints;
import com.dreamland.util.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableBuilder {
	public static final String PRIMARY_KEY = "_id";

	public static final Map<Class<?>, String> TYPES = new HashMap<Class<?>, String>();
	// 缓存创建表的SQL语句，首次登陆会加快
	private static final Map<String, String> createTableCache = new HashMap<String, String>();

	static {
		TYPES.put(byte.class, "INTEGER");
		TYPES.put(boolean.class, "INTEGER");
		TYPES.put(short.class, "INTEGER");
		TYPES.put(int.class, "INTEGER");
		TYPES.put(long.class, "INTEGER");
		TYPES.put(String.class, "TEXT");
		TYPES.put(byte[].class, "BLOB");
		TYPES.put(float.class, "REAL");
		TYPES.put(double.class, "REAL");
	}

	/**
	 * 根据entity获取建表SQL语句
	 *
	 * @param entity
	 * @return
	 */
	public static String createSQLStatement(Entity entity) {
		String tableName = entity.getTableName();

		// 缓存创建表的SQL语句，首次登陆会加快
		if (createTableCache.containsKey(tableName)) {
			String sql = createTableCache.get(tableName);
			Logger.d("TableBuilder.createSQLStatement(), sql from cache: " + sql, false);
			return sql;
		}

		StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
		sb.append(tableName);
		sb.append(" (" + PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT");

		List<Field> validFieldList = getValidField(entity);

		for (Field field : validFieldList) {
			String name = field.getName();
			Class<?> c = field.getType();
			// 写入数据库的数据类型
			String type = TYPES.get(c);
			if (type != null) {
				sb.append(", ");
				sb.append(name + " " + type);
				if (field.isAnnotationPresent(Unique.class)) {
					sb.append(" UNIQUE");
				} else if (field.isAnnotationPresent(DefaultZero.class)) {
					sb.append(" default " + 0);
				}
			}
		}

		Class<? extends Entity> clazz = entity.getClass();
		if (clazz.isAnnotationPresent(UniqueConstraints.class)) {
			UniqueConstraints constrains = (UniqueConstraints) clazz.getAnnotation(UniqueConstraints.class);
			String columnName = constrains.columnNames();
			sb.append(", UNIQUE (" + columnName + ") ");
			String clause = constrains.clause().toString();
			sb.append(" ON CONFLICT " + clause);
		}
		sb.append(')');

		String sql = sb.toString();

		createTableCache.put(tableName, sql);
		Logger.d("TableBuilder.createSQLStatement(), sql: " + sql, false);
		return sql;
	}

	/**
	 * 获取类非static和非noColumn的field
	 *
	 * @param entity
	 * @return
	 */
	public static List<Field> getValidField(Entity entity) {
		Class<? extends Entity> clazz = entity.getClass();

		Field[] fields = clazz.getFields();

		List<Field> validFieldList = new ArrayList<Field>(fields.length);

		for (Field field : fields) {
			// 跳过static和noColumn修饰的字段
			if (!Modifier.isStatic(field.getModifiers()) && !field.isAnnotationPresent(NoColumn.class)) {
				validFieldList.add(field);
			}
		}
		return validFieldList;
	}

	public static String dropSQLStatement(String tableName) {
		return "DROP TABLE IF EXISTS " + tableName;
	}
}
