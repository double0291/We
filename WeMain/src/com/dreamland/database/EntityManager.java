package com.dreamland.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dreamland.util.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class EntityManager {
	private static final String CLOSE_EXCEPTION_MSG = "The EntityManager has been already closed";

	private SQLiteOpenHelper dbHelper;
	private SQLiteDatabase db; // getWritableDatabase的时候用到
	private EntityTransaction transaction;
	private boolean isClosed;

	// 缓存创建表的表名
	private static final HashSet<String> createTableCache = new HashSet<String>();

	EntityManager(SQLiteOpenHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	/**
	 * 删除表
	 *
	 * @param tableName
	 * @return
	 */
	public boolean drop(String tableName) {
		if (isClosed) {
			throw new IllegalStateException(CLOSE_EXCEPTION_MSG);
		}
		if (db == null) {
			db = dbHelper.getWritableDatabase();
		}
		try {
			db.execSQL("DROP TABLE IF EXISTS " + tableName);
			createTableCache.remove(tableName);
			return true;
		} catch (Exception e) {
			Logger.e("EntityManager.drop() error, " + e, false);
			return false;
		}
	}

	/**
	 * 删除表
	 *
	 * @param clazz
	 * @return
	 */
	public boolean drop(Class<? extends Entity> clazz) {
		if (isClosed) {
			throw new IllegalStateException(CLOSE_EXCEPTION_MSG);
		}
		if (db == null) {
			db = dbHelper.getWritableDatabase();
		}
		try {
			String tableName = clazz.newInstance().getTableName();
			db.execSQL("DROP TABLE IF EXISTS " + tableName);
			createTableCache.remove(tableName);
			return true;
		} catch (Exception e) {
			Logger.e("EntityManager.drop() error, " + e, false);
			return false;
		}
	}

	public boolean insertOrReplace(Entity entity, boolean isReplace) {
		if (isClosed) {
			throw new IllegalStateException(CLOSE_EXCEPTION_MSG);
		}
		if (db == null) {
			db = dbHelper.getWritableDatabase();
		}
		if (entity.status == Entity.NEW) {
			String table = entity.getTableName();
			entity.preWrite();
			try {
				ContentValues cv = createContentValue(entity);
				long id = -1;
				if (isReplace)
					id = db.replace(table, null, cv);
				else
					id = db.insert(table, null, cv);

				if (id == -1) {
					// 插入失败，可能是没有建表
					boolean isCreated = createTable(entity);
					if (isCreated) {
						if (isReplace)
							id = db.replace(table, null, cv);
						else
							id = db.insert(table, null, cv);
					}
				}

				// 这个时候还没插成功的话。。。
				if (id != -1) {
					entity._id = id;
					entity.status = Entity.MANAGED;
					entity.postWrite();
					return true;
				}

			} catch (Exception e) {
				Logger.e("EntityManager.insert() error, " + e, false);
			}
		}
		return false;
	}

	public boolean update(Entity entity) {
		if (isClosed) {
			throw new IllegalStateException(CLOSE_EXCEPTION_MSG);
		}
		if (db == null) {
			db = dbHelper.getWritableDatabase();
		}

		if (entity.status == Entity.MANAGED || entity.status == Entity.DETACHED) {
			try {
				entity.preWrite();
				String table = entity.getTableName();
				ContentValues cv = createContentValue(entity);
				int result = db.update(table, cv, TableBuilder.PRIMARY_KEY + "=?",
						new String[] { String.valueOf(entity._id) });
				if (result > 0) {
					entity.postWrite();
					return true;
				}
			} catch (Exception e) {
				Logger.e("EntityManager.update() error, " + e, false);
			}
		}
		return false;
	}

	public boolean delete(Entity entity) {
		if (isClosed) {
			throw new IllegalStateException(CLOSE_EXCEPTION_MSG);
		}
		if (db == null) {
			db = dbHelper.getWritableDatabase();
		}
		if (entity.status == Entity.MANAGED) {
			try {
				entity.preWrite();
				String table = entity.getTableName();
				int result = db.delete(table, TableBuilder.PRIMARY_KEY + "=?",
						new String[] { String.valueOf(entity._id) });
				if (result > 0) {
					entity.status = Entity.REMOVED;
					entity.postWrite();
					return true;
				}
			} catch (Exception e) {
				Logger.e("EntityManager.delete() error, " + e, false);
			}
		}
		return false;
	}

	public List<? extends Entity> query(Class<? extends Entity> clazz) {
		return query(false, clazz, null, null, null, null, null, null, null);
	}

	/**
	 * 全条件query，可以根据需求重载
	 *
	 * @return
	 */
	public List<? extends Entity> query(boolean distinct, Class<? extends Entity> clazz, String[] columns,
			String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
		if (isClosed) {
			throw new IllegalStateException(CLOSE_EXCEPTION_MSG);
		}
		List<? extends Entity> list = null;
		Cursor cursor = null;
		try {
			String table = clazz.newInstance().getTableName();
			cursor = query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
			list = cursor2List(cursor, clazz);
			return list;
		} catch (Exception e) {
			Logger.e("EntityManager.query() error, " + e, false);
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	private Cursor query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs,
			String groupBy, String having, String orderBy, String limit) {
		if (isClosed) {
			throw new IllegalStateException(CLOSE_EXCEPTION_MSG);
		}
		try {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			return db.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
		} catch (Exception e) {
			Logger.e("EntityManager.query() error, " + e, false);
			return null;
		}
	}

	public boolean execSQL(String sql) {
		if (isClosed) {
			throw new IllegalStateException(CLOSE_EXCEPTION_MSG);
		}
		try {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			db.execSQL(sql);
			return true;
		} catch (Exception e) {
			Logger.e("EntityManager.execSQL() error, " + e, false);
			return false;
		}
	}

	public EntityTransaction getTransaction() {
		if (isClosed) {
			throw new IllegalStateException(CLOSE_EXCEPTION_MSG);
		}
		if (transaction == null) {
			transaction = new EntityTransaction(dbHelper);
		}
		return transaction;
	}

	private boolean createTable(Entity entity) {
		String tableName = entity.getTableName();
		// 不是强制建表，先查缓存，看是否创建过
		if (createTableCache.contains(tableName))
			return true;

		try {
			db.execSQL(TableBuilder.createSQLStatement(entity));
			createTableCache.add(tableName);
			return true;
		} catch (Exception e) {
			Logger.e("EntityManager.createTable() error, " + e, false);
			return false;
		}
	}

	public boolean isOpen() {
		return !isClosed;
	}

	public void close() {
		if (isClosed) {
			throw new IllegalStateException(CLOSE_EXCEPTION_MSG);
		}
		dbHelper = null;
		isClosed = true;
	}

	private Entity cursor2Entity(Cursor cursor, Class<? extends Entity> clazz) {
		if (cursor == null)
			return null;

		if (cursor.isBeforeFirst()) {
			cursor.moveToFirst();
		}

		// 获取ID
		long id = -1;
		try {
			int idIndex = -1;
			if ((idIndex = cursor.getColumnIndex(TableBuilder.PRIMARY_KEY)) >= 0) {
				id = cursor.getLong(idIndex);
			}
		} catch (Exception e) {

		}
		// 构造Entity
		Entity entity = null;
		try {
			entity = clazz.newInstance();
			if (entity != null) {
				entity._id = id;

				List<Field> fields = TableBuilder.getValidField(entity);
				for (Field field : fields) {
					Class<?> type = field.getType();
					String columnName = field.getName();
					int columnIndex = cursor.getColumnIndex(columnName);
					if (columnIndex != -1) {
						if (!field.isAccessible()) {
							field.setAccessible(true);
						}

						/*
						 * 根据统计各类型引用次数排序，稍微提高一下性能
						 */
						if (type == long.class) {
							field.set(entity, cursor.getLong(columnIndex));
						} else if (type == int.class) {
							field.set(entity, cursor.getInt(columnIndex));
						} else if (type == String.class) {
							field.set(entity, cursor.getString(columnIndex));
						} else if (type == byte.class) {
							field.set(entity, (byte) cursor.getShort(columnIndex));
						} else if (type == byte[].class) {
							field.set(entity, cursor.getBlob(columnIndex));
						} else if (type == short.class) {
							field.set(entity, cursor.getShort(columnIndex));
						} else if (type == boolean.class) {
							field.set(entity, cursor.getInt(columnIndex) != 0);
						} else if (type == float.class) {
							field.set(entity, cursor.getFloat(columnIndex));
						} else if (type == double.class) {
							field.set(entity, cursor.getDouble(columnIndex));
						}
					}
				}

				if (id != -1)
					entity.status = Entity.MANAGED;
				else
					entity.status = Entity.DETACHED;

				entity.postRead();
			}
		} catch (Exception e) {
			Logger.e("EntityManager.cursor2Entity() error, " + e, false);
		}
		return entity;
	}

	private List<? extends Entity> cursor2List(Cursor cursor, Class<? extends Entity> clazz) {
		if (cursor == null || !cursor.moveToFirst())
			return null;

		List<Entity> list = null;
		try {
			int size = cursor.getCount();
			list = new ArrayList<Entity>(size);
			do {
				list.add(cursor2Entity(cursor, clazz));
			} while (cursor.moveToNext());
		} catch (Exception e) {
			Logger.e("EntityManager.cursor2List() error, " + e, false);
		}
		return list;
	}

	private ContentValues createContentValue(Entity entity) throws IllegalArgumentException, IllegalAccessException {
		ContentValues cv = new ContentValues();
		List<Field> fields = TableBuilder.getValidField(entity);
		for (Field field : fields) {
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			String name = field.getName();
			Object value = field.get(entity);
			if (value instanceof Long) {
				cv.put(name, (Long) value);
			} else if (value instanceof Integer) {
				cv.put(name, (Integer) value);
			} else if (value instanceof String) {
				cv.put(name, (String) value);
			} else if (value instanceof Byte) {
				cv.put(name, (Byte) value);
			} else if (value instanceof byte[]) {
				cv.put(name, (byte[]) value);
			} else if (value instanceof Short) {
				cv.put(name, (Short) value);
			} else if (value instanceof Boolean) {
				cv.put(name, (Boolean) value);
			} else if (value instanceof Float) {
				cv.put(name, (Float) value);
			} else if (value instanceof Double) {
				cv.put(name, (Double) value);
			}
		}
		return cv;
	}
}
