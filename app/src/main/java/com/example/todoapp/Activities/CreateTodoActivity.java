package com.example.todoapp.Activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.todoapp.AppConstants;
import com.example.todoapp.MyBroadCastReceiver;
import com.example.todoapp.R;
import com.example.todoapp.SqliteDatabase.DataBaseQueries;
import com.example.todoapp.SqliteDatabase.ModelTodo;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class CreateTodoActivity extends AppCompatActivity {

    public static final String mypreference = "mypref";
    public static final String TODO_MODEL = "todoModel";


    // region member variables
    private EditText taskTitle;
    private String priority;
    private Long dateAndTime;
    private TextView dateAndTimeView;
    private Spinner dropdown;
    SharedPreferences sharedpreferences;
//endregion

    //region ActivityLifecycle methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_fragment_layout);

        //region find view by ids
        taskTitle = findViewById(R.id.edit_text_edit_mode);
        ImageView speechToText = findViewById(R.id.image_button_speech_to_text);
        dateAndTimeView = findViewById(R.id.text_view_date);
        Button createTaskButton = findViewById(R.id.create_button);
        dropdown = findViewById(R.id.text_view_select);
        //endregion

        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        speechToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSpeechToTextWindow();
            }
        });


        createTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = taskTitle.getText().toString();

                if (title.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.cannot_create_task_with_out_title, Toast.LENGTH_SHORT).show();
                    return;
                }

                ModelTodo modelTodo = new ModelTodo();
                modelTodo.setTaskTitle(title);
                modelTodo.setPriority(priority);
                if (dateAndTime != null && dateAndTime >= System.currentTimeMillis()) {
                    modelTodo.setTimeStamp(dateAndTime);
                } else {
                    Toast.makeText(getApplicationContext(),"due date cannot be less than current time",Toast.LENGTH_SHORT).show();
                    return;
                }
                modelTodo.setCreatedOn(System.currentTimeMillis());
                modelTodo.setTaskId(UUID.randomUUID());
                ModelTodo.upsertTask(getApplicationContext(), modelTodo);
                Intent intent = new Intent(getApplicationContext(), AllTodoActivity.class);
                intent.putExtra("task", modelTodo);

                saveInSharedPrefAndShowNotification(modelTodo);

                setResult(1, intent);
                finish();

            }
        });


        setUpSpinnerDropDown();

        dateAndTimeView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                selectDateAndTime();
            }
        });
    }

    private void saveInSharedPrefAndShowNotification(ModelTodo currentModelTodo) {
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(TODO_MODEL, currentModelTodo.getTaskId().toString());
            editor.commit();

             showNotification(currentModelTodo);

    }

    private void showNotification(ModelTodo currentModelTodo) {

        Intent intent = new Intent(this, MyBroadCastReceiver.class);
        intent.putExtra("modelTodo",currentModelTodo.getTaskId().toString());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, currentModelTodo.getTimeStamp(), pendingIntent);

    }

    //region helper methods
    private void setUpSpinnerDropDown() {

        final String[] items = new String[]{AppConstants.TASK_PRIORITY_HIGH, AppConstants.TASK_PRIORITY_MEDIUM, AppConstants.TASK_PRIORITY_LOW};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                priority = items[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void selectDateAndTime() {

        final Calendar calender = Calendar.getInstance();

        int mYear = calender.get(Calendar.YEAR);
        int mMonth = calender.get(Calendar.MONTH);
        int mDay = calender.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateTodoActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        //Setting date on calender
                        calender.set(Calendar.YEAR, year);
                        calender.set(Calendar.MONTH, monthOfYear);
                        calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                        // Get Current Time
                        int mHour = calender.get(Calendar.HOUR_OF_DAY);
                        int mMinute = calender.get(Calendar.MINUTE);

                        // Launch Time Picker Dialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateTodoActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {

                                        //Setting time on calender
                                        calender.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        calender.set(Calendar.MINUTE, minute);

                                        dateAndTime = (calender.getTimeInMillis());
                                        dateAndTimeView.setText(getFormattedString("dd MMM yyyy hh:mm a", calender.getTimeInMillis()));

                                    }
                                }, mHour, mMinute, false);
                        timePickerDialog.setCancelable(false);
                        timePickerDialog.show();

                    }


                }, mYear, mMonth, mDay);

        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
    }


    public static String getFormattedString(String format, Long currentTime) {
        String resultTime = "";
        format = format;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
            resultTime = sdf.format(currentTime);
        } catch (Exception ex) {

            ex.printStackTrace();
        }
        return resultTime;
    }

    public void openSpeechToTextWindow() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Recording...");
        try {
            startActivityForResult(intent, 101);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), R.string.language_not_supported, Toast.LENGTH_SHORT).show();
        }
    }
//endregion


    //region onActivityResult
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 101) {
            if (intent != null) {
                ArrayList<String> result = (ArrayList<String>) intent.getSerializableExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && !result.isEmpty()) {
                    taskTitle.setText(result.get(0));
                }
            }
        }
    }


    //endregion
}
