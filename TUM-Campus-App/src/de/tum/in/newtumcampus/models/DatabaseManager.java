package de.tum.in.newtumcampus.models;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import de.tum.in.newtumcampus.Const;

/**
 * Database singleton
 */
abstract public class DatabaseManager {

	/**
	 * Database connection
	 */
	private static SQLiteDatabase db;

	/**
	 * Constructor, open/create database, create table if necessary
	 * 
	 * <pre>
	 * @param c Context
	 * @return SQLiteDatabase Db
	 * </pre>
	 */
	public static SQLiteDatabase getDb(Context c) {
		if (db == null) {
			File f = c.getDatabasePath(Const.db);
			f.getParentFile().mkdirs();
			db = SQLiteDatabase.openDatabase(c.getDatabasePath(Const.db).toString(), null,
					SQLiteDatabase.CREATE_IF_NECESSARY);
		}
		return db;
	}
}