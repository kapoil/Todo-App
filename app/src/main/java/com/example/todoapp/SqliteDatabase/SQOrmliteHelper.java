package com.example.todoapp.SqliteDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.todoapp.AppConstants;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.UUID;

public class SQOrmliteHelper  extends com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = AppConstants.DB_NAME_JAVA;
    public static final int DATABASE_VERSION = 1;
    private Dao<ModelTodo, UUID> daoTask;



    public SQOrmliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static SQOrmliteHelper getInstance(Context context) {

        return OpenHelperManager.getHelper(context, SQOrmliteHelper.class);
    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
         createTable(ModelTodo.class);

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    void createTable(Class mClass) {
        try {
            TableUtils.createTable(connectionSource, mClass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<ModelTodo, UUID> getTaskDao() {
        if (daoTask == null) {
            try {
                daoTask = getDao(ModelTodo.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoTask;
    }
}

