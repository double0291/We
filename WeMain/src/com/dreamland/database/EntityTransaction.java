package com.dreamland.database;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EntityTransaction {
	private final SQLiteOpenHelper dbHelper;
	private SQLiteDatabase db;

	private static final Lock lock = new ReentrantLock();

	EntityTransaction(SQLiteOpenHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	public void begin() {
		lock.lock();
		db = dbHelper.getWritableDatabase();
		db.beginTransaction();
	}

	public void end() {
		try {
			db.endTransaction();
			db = null;
		} catch (Exception e) {

		} finally {
			lock.unlock();
		}
	}

	public void commit() {
		db.setTransactionSuccessful();
	}
}
