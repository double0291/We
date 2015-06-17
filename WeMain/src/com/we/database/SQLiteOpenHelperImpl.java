package com.we.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.we.util.Logger;

public class SQLiteOpenHelperImpl extends SQLiteOpenHelper {
	// TODO 考虑从manifest获取
	private static final int VERSION = 1;

	private String name;

	public SQLiteOpenHelperImpl(Context context, String name) {
		super(context, name, null, VERSION);
		this.name = name;
	}

	public SQLiteOpenHelperImpl(Context context, String name, int version) {
		super(context, name, null, version);
		this.name = name;
	}

	public SQLiteOpenHelperImpl(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.name = name;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Logger.d("[DB]" + name + " onCreate;", false);
		createDatabase(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Logger.d("[DB]" + name + " onUpgrade;", false);
		upgradeDatabase(db);
	}

	protected void createDatabase(SQLiteDatabase db) {

	}

	protected void upgradeDatabase(SQLiteDatabase db) {

	}

}
