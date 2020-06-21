package com.example.todoapp.AdapterAndTheirViewHolders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.R;
import com.example.todoapp.SqliteDatabase.ModelTodo;

import java.util.ArrayList;


public class TodoAdapter extends   RecyclerView.Adapter<TodoViewHolder>{

    //region private Member Variable
    Context context;
    private ArrayList<ModelTodo>  allTasks = new ArrayList<>();
    private final ArrayList<ModelTodo> filterList;
    // endregion



    //region constructor to intialize adapter
    public TodoAdapter(Context context, ArrayList<ModelTodo>  allTasks ) {
        super();

        this.context = context;
        this.allTasks = allTasks;
        this.filterList = new ArrayList(allTasks);
    }

    //endregion

    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new TodoViewHolder(LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.item_task_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder viewHolder, final int position) {

        ((TodoViewHolder) viewHolder).clearView();
        ((TodoViewHolder) viewHolder).bindView(context,filterList.get(position), position);

    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }


    //region getter and setter
    public ArrayList getFilterList() {
        return filterList;
    }


    public ArrayList<ModelTodo> getAllTasks() {
        return allTasks;
    }

    public void setAllTasks(ArrayList<ModelTodo> allTasks) {
        this.allTasks = allTasks;
    }
    //endregion

    //region helper methods
    public void setFilterList(@Nullable ArrayList filterList) {

        this.filterList.clear();
        if ((!filterList.isEmpty())) {
            this.filterList.addAll(filterList);
        }
        notifyDataSetChanged();
    }
    //endregion
}

