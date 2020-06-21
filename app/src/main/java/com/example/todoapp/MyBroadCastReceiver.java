package com.example.todoapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.todoapp.SqliteDatabase.DataBaseQueries;
import com.example.todoapp.SqliteDatabase.ModelTodo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import static com.example.todoapp.Activities.CreateTodoActivity.getFormattedString;


public class MyBroadCastReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        try {


            String modelTodoUUId = (String) intent.getStringExtra("modelTodo");

            ModelTodo modelTodo = DataBaseQueries.getTaskWithId(context, UUID.fromString(modelTodoUUId));

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            int notificationId = 1;
            String channelId = "channel-01";
            String channelName = "Channel Name";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Todo Alarm :- " + modelTodo.getTaskTitle())
                    .setContentText(modelTodo.getPriority() + " priority ." + getFormattedString("dd MMM yyyy hh:mm a", modelTodo.getTimeStamp()) + ".");

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(intent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            mBuilder.setContentIntent(resultPendingIntent);

            notificationManager.notify(notificationId, mBuilder.build());

        } catch (Exception e){

        }
    }

}
