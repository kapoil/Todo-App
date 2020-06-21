package com.example.todoapp.Activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todoapp.AdapterAndTheirViewHolders.TodoAdapter;
import com.example.todoapp.AppConstants;
import com.example.todoapp.Events.DeleteTaskEvent;
import com.example.todoapp.R;
import com.example.todoapp.SqliteDatabase.DataBaseQueries;
import com.example.todoapp.SqliteDatabase.ModelTodo;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import java.sql.SQLException;
import java.util.ArrayList;



import de.greenrobot.event.EventBus;


public class AllTodoActivity extends AppCompatActivity implements MaterialSearchView.OnQueryTextListener,
        MaterialSearchView.SearchViewListener {

    //region private Member variables
    private MenuItem searchItem, todoItem, sortByItem;
    private MaterialSearchView mMaterialSearchView;
    private RecyclerView mTodosRecyclerView;
    private ArrayList<ModelTodo> mAllTasks = new ArrayList<>();
    private TodoAdapter mTodosRecyclerViewAdapter;
    private ArrayList<ModelTodo> search_result = new ArrayList<>();
    //endregion

    //region ActivityLifeCycle methods
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        mTodosRecyclerView = findViewById(R.id.recycler_view);
        mMaterialSearchView = findViewById(R.id.material_search_view);

        mMaterialSearchView.setOnQueryTextListener(this);


        EventBus.getDefault().register(this);

        try {
            ArrayList arrayList = (ArrayList<ModelTodo>) DataBaseQueries.getAllTasks(this);
            mAllTasks.addAll(arrayList);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setUpRecyclerView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        searchItem = menu.findItem(R.id.search);
        todoItem = menu.findItem(R.id.todo);
        sortByItem = menu.findItem(R.id.sort);


        mMaterialSearchView.setMenuItem(searchItem);
        mMaterialSearchView.setVisibility(View.VISIBLE);


        Drawable drawableSearch = searchItem.getIcon();
        if (drawableSearch != null) {
            drawableSearch.mutate();
            drawableSearch.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        }

        Drawable drawableTodo = todoItem.getIcon();
        if (drawableTodo != null) {
            drawableTodo.mutate();
            drawableTodo.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        }

        Drawable drawableJoinGroup = sortByItem.getIcon();
        if (drawableJoinGroup != null) {
            drawableJoinGroup.mutate();
            drawableJoinGroup.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null) {
                ModelTodo modelTodo = (ModelTodo) data.getSerializableExtra("task");
                mTodosRecyclerViewAdapter.getAllTasks().add(0,modelTodo);
                mTodosRecyclerViewAdapter.getFilterList().add(0, modelTodo);
                mTodosRecyclerViewAdapter.notifyDataSetChanged();
            }

        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        mMaterialSearchView.setOnSearchViewListener(this);
        mMaterialSearchView.setOnQueryTextListener(this);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.todo:
                AddTodo();
                break;
            case R.id.sortPriority:
                sortByPriority();
                break;
            case R.id.sortTimeCreation:
                sortByTimeCreation();
                break;
        }
        return true;

    }

    //endregion

    // region helper methods

    public ArrayList<ModelTodo> getAllTaskFromDb(){
        ArrayList<ModelTodo> mAllTasks = new ArrayList<>();

        try {
            ArrayList arrayList = (ArrayList<ModelTodo>) DataBaseQueries.getAllTasks(this);
            mAllTasks.addAll(arrayList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mAllTasks;
    }

    private void setUpRecyclerView() {
        mTodosRecyclerViewAdapter = new TodoAdapter(this, mAllTasks);
        mTodosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTodosRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.HORIZONTAL));
        mTodosRecyclerView.setAdapter(mTodosRecyclerViewAdapter);

    }

    public void onEventMainThread(DeleteTaskEvent eventdeleteTask) {
        ModelTodo modelTodo = eventdeleteTask.getModelTodo();
        mTodosRecyclerViewAdapter.getFilterList().remove(modelTodo);
        mTodosRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void sortByTimeCreation() {
        try {
            ArrayList<ModelTodo> allTasks = (ArrayList) DataBaseQueries.getAllTasks(this);

            mTodosRecyclerViewAdapter.getFilterList().clear();
            mTodosRecyclerViewAdapter.setFilterList(allTasks);
            mTodosRecyclerViewAdapter.notifyDataSetChanged();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void sortByPriority() {
        ArrayList<ModelTodo> highPriorityList = new ArrayList<>();
        ArrayList<ModelTodo> mediumPriorityList = new ArrayList<>();
        ArrayList<ModelTodo> lowPriorityList = new ArrayList<>();
        ArrayList<ModelTodo> allTasks = new ArrayList<>();

        for (int i = 0; i < mTodosRecyclerViewAdapter.getAllTasks().size(); i++) {
            ModelTodo modelTodo = mTodosRecyclerViewAdapter.getAllTasks().get(i);
            if (modelTodo.getPriority() != null && modelTodo.getPriority().equals(AppConstants.TASK_PRIORITY_HIGH)) {
                highPriorityList.add(modelTodo);
            } else if (modelTodo.getPriority() != null && modelTodo.getPriority().equals(AppConstants.TASK_PRIORITY_MEDIUM)) {
                mediumPriorityList.add(modelTodo);
            } else {
                lowPriorityList.add(modelTodo);
            }
        }

        allTasks.addAll(highPriorityList);
        allTasks.addAll(mediumPriorityList);
        allTasks.addAll(lowPriorityList);

        mTodosRecyclerViewAdapter.getFilterList().clear();
        mTodosRecyclerViewAdapter.setFilterList(allTasks);
        mTodosRecyclerViewAdapter.notifyDataSetChanged();


    }

    private void AddTodo() {
        Intent intent = new Intent(this, CreateTodoActivity.class);
        startActivityForResult(intent, 1);
    }


    //endregion

    // region materialSearchView Override methods
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ArrayList<ModelTodo> modelTodos = mTodosRecyclerViewAdapter.getAllTasks();
        if ((search_result == null)) {
            search_result = new ArrayList<>();
        } else {
            search_result.clear();
        }
        if (modelTodos != null && modelTodos.size() > 0) {
            for (int i = 0; i < modelTodos.size(); i++) {
                ModelTodo modelTodo = modelTodos.get(i);
                String title = modelTodo.getTaskTitle();
                if (title != null) {
                    String textQuery = title.toLowerCase();
                    if (textQuery.contains(newText)) {
                        search_result.add(modelTodo);
                    }

                }
            }

            mTodosRecyclerViewAdapter.setFilterList(search_result);
        }


        return false;
    }

    @Override
    public void onSearchViewShown() {

    }

    @Override
    public void onSearchViewClosed() {

    }
    //endregion


}
