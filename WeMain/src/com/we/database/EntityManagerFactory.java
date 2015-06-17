package com.we.database;

import android.database.sqlite.SQLiteOpenHelper;

import com.we.base.BaseApplication;

public class EntityManagerFactory {
    private static final String CLOSE_EXCEPTION_MSG = "The EntityManagerFactory has been already " +
            "closed";

    private final SQLiteOpenHelper dbHelper;
    private boolean isClosed;

    public EntityManagerFactory(String dbName) {
        dbHelper = new SQLiteOpenHelperImpl(BaseApplication.mApp, dbName);
    }

    public EntityManager createEntityManager() {
        if (isClosed) {
            throw new IllegalStateException(CLOSE_EXCEPTION_MSG);
        }
        EntityManager em = new EntityManager(dbHelper);
        isClosed = false;
        return em;
    }

    public void close() {
        if (isClosed) {
            throw new IllegalStateException(CLOSE_EXCEPTION_MSG);
        }
        isClosed = true;
        dbHelper.close();
    }

    public boolean isOpen() {
		return !isClosed;
	}
}
