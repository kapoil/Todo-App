package com.example.todoapp.Events;

import com.example.todoapp.SqliteDatabase.ModelTodo;

public class UpdateTaskEvent {

    public ModelTodo getModelTodo() {
        return modelTodo;
    }

    private final ModelTodo modelTodo;

    public UpdateTaskEvent(ModelTodo modelTodo) {
        this.modelTodo = modelTodo;
    }
}
