package com.example.todoapp.SqliteDatabase;

import android.content.Context;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.UUID;


@DatabaseTable(tableName = "task")
public class ModelTodo implements Serializable {


    public static final String TASK_ID = "taskId";
    public static final String CREATED_ON = "createdOn";


    @DatabaseField(id = true)
    private UUID taskId;

    @DatabaseField
    private  String taskTitle ;
    @DatabaseField
    private  long timeStamp;
    @DatabaseField
    private  String taskStatus;
    @DatabaseField
    private  String priority;
    @DatabaseField
    private  String repeat;

    @DatabaseField
    private Long createdOn;



    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }



    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public static void upsertTask(Context context , ModelTodo modelTodo) {

        if ((modelTodo == null)) {
            return;
        }
        try {
            DataBaseQueries.getTaskDao(context).createOrUpdate(modelTodo);
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    public static void deletedTask(Context context , ModelTodo modelTodo) {
        if ((modelTodo == null)) {
            return;
        }
        try {
            //When model like is present in the database.Only then we will delete it from the database and send event
            ModelTodo modelTodoPresentInDb = DataBaseQueries.getTaskWithId(context, modelTodo.getTaskId());

            if ((modelTodoPresentInDb != null)) {
                DataBaseQueries.getTaskDao(context).delete(modelTodo);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }
}
