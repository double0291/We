package com.we.database;

import com.we.util.Logger;

import java.lang.reflect.Field;

public abstract class Entity implements Cloneable {
	public static final int NEW = 1000;
	public static final int MANAGED = 1001;
	public static final int DETACHED = 1002;
	public static final int REMOVED = 1003;

	int status = NEW;
	long _id = -1;

	public long getId() {
		return _id;
	}

	public int getStatus() {
		return status;
	}

	public void setId(long id) {
		this._id = id;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTableName() {
		return getClass().getSimpleName();
	}

	/**
	 * 在从DB读取完成之后调用
	 */
	protected void postRead() {

	}

	/**
	 * 从DB写之前调用
	 */
	protected void preWrite() {

	}

	/**
	 * 从DB写之后调用
	 */
	protected void postWrite() {

	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Entity entity = null;
		try {
			entity = this.getClass().newInstance();

			if (entity != null) {
				Field[] fields = this.getClass().getFields();

				for (Field field : fields) {
					if (!field.isAccessible()) {
						field.setAccessible(true);
					}
					field.set(entity, field.get(this));
				}

				entity.status = NEW;
				entity.postRead();
			}
		} catch (Exception e) {
			Logger.i("Entity.clone() Exception: " + e, false);
			entity = null;
		}
		return entity;
	}
}
