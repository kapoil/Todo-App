package com.example.todoapp.SqliteDatabase;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class DataBaseQueries {

    public static Dao<ModelTodo, UUID> getTaskDao(Context context) {
        return SQOrmliteHelper.getInstance(context).getTaskDao();
    }

    public static List<ModelTodo> getAllTasks(Context context) throws SQLException {
      return   getTaskDao(context).queryBuilder().orderBy(ModelTodo.CREATED_ON, false).query();
    }

    public static ModelTodo getTaskWithId(Context context , UUID taskId) throws SQLException {

        Where<ModelTodo, UUID> where = getTaskDao(context).queryBuilder().where();

        Where<ModelTodo, UUID> query = where.eq(ModelTodo.TASK_ID, taskId);

        return query.queryForFirst();
    }

}
