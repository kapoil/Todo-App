package com.example.todoapp.AdapterAndTheirViewHolders;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.AppConstants;
import com.example.todoapp.Events.DeleteTaskEvent;
import com.example.todoapp.R;
import com.example.todoapp.SqliteDatabase.ModelTodo;

import de.greenrobot.event.EventBus;

import static com.example.todoapp.Activities.CreateTodoActivity.getFormattedString;


public class TodoViewHolder extends RecyclerView.ViewHolder {


    //region private member variable
    private TextView todoTitleView;
    private CheckBox checkBox;
    private TextView priority;
    private TextView dueDateAndTimeStamp;
    private ImageView deleteButton;
    private ImageView undoButton;
    //endregion


    //region constructor
    public TodoViewHolder(@NonNull View itemView) {
        super(itemView);
        todoTitleView = itemView.findViewById(R.id.title_textView);
        checkBox = itemView.findViewById(R.id.checkbox);
        priority = itemView.findViewById(R.id.priority);
        dueDateAndTimeStamp = itemView.findViewById(R.id.subtext);
        deleteButton = itemView.findViewById(R.id.delete_button);
        undoButton = itemView.findViewById(R.id.undo_button);
    }
    //endregion


    public void bindView(final Context context, final ModelTodo modelTodo, int position) {
        todoTitleView.setText(modelTodo.getTaskTitle());


        dueDateAndTimeStamp.setText(getFormattedString("dd MMM yyyy hh:mm a", modelTodo.getTimeStamp()) + ".");


        setUpCheckBoxUsingTodoStatus(modelTodo);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    modelTodo.setTaskStatus(AppConstants.TASK_STATUS_COMPLETED);
                    deleteButton.setVisibility(View.VISIBLE);
                    undoButton.setVisibility(View.VISIBLE);
                    checkBox.setVisibility(View.GONE);
                    todoTitleView.setPaintFlags((Paint.STRIKE_THRU_TEXT_FLAG));

                } else {
                    modelTodo.setTaskStatus(AppConstants.TASK_STATUS_NOT_COMPLETED);
                    deleteButton.setVisibility(View.GONE);
                    undoButton.setVisibility(View.GONE);
                    checkBox.setVisibility(View.VISIBLE);
                    todoTitleView.setPaintFlags(0);
                }

                ModelTodo.upsertTask(context, modelTodo);
            }
        });

        setUpPriorityAndTheirColour(modelTodo, context);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModelTodo.deletedTask(context, modelTodo);
                EventBus.getDefault().post(new DeleteTaskEvent(modelTodo));

            }
        });


        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteButton.setVisibility(View.GONE);
                undoButton.setVisibility(View.GONE);
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(false);
                todoTitleView.setPaintFlags((0));

                modelTodo.setTaskStatus(AppConstants.TASK_STATUS_NOT_COMPLETED);
                modelTodo.upsertTask(context, modelTodo);
            }
        });

    }


    //region helper methods
    private void setUpCheckBoxUsingTodoStatus(ModelTodo modelTodo) {
        if (modelTodo.getTaskStatus() != null && modelTodo.getTaskStatus().equals(AppConstants.TASK_STATUS_COMPLETED)) {
            deleteButton.setVisibility(View.VISIBLE);
            undoButton.setVisibility(View.VISIBLE);
            checkBox.setVisibility(View.GONE);
            todoTitleView.setPaintFlags((Paint.STRIKE_THRU_TEXT_FLAG));

        } else {
            checkBox.setChecked(false);
            todoTitleView.setPaintFlags(0);

        }
    }

    private void setUpPriorityAndTheirColour(ModelTodo modelTodo, Context context) {

        if (modelTodo.getPriority() != null && !modelTodo.getPriority().isEmpty()) {

            if (modelTodo.getPriority().equals(AppConstants.TASK_PRIORITY_HIGH)) {
                priority.setText(AppConstants.TASK_PRIORITY_HIGH);
                priority.setTextColor(context.getResources().getColor(R.color.red));
            } else if (modelTodo.getPriority().equals(AppConstants.TASK_PRIORITY_MEDIUM)) {
                priority.setText(AppConstants.TASK_PRIORITY_MEDIUM);
                priority.setTextColor(context.getResources().getColor(R.color.green));
            }
            if (modelTodo.getPriority().equals(AppConstants.TASK_PRIORITY_LOW)) {
                priority.setText(AppConstants.TASK_PRIORITY_LOW);
                priority.setTextColor(context.getResources().getColor(R.color.yellow));
            }
        }

    }
    //endregions


    // region clear last views
    public void clearView() {
        todoTitleView.setText("");
        priority.setText("");
        dueDateAndTimeStamp.setText("");
        deleteButton.setVisibility(View.GONE);
        undoButton.setVisibility(View.GONE);
        checkBox.setVisibility(View.VISIBLE);
    }
    // endregion

}
