package com.example.todoapp.Events;

import com.example.todoapp.SqliteDatabase.ModelTodo;

public class DeleteTaskEvent {


    public ModelTodo getModelTodo() {
        return modelTodo;
    }

    private final ModelTodo modelTodo;
    public DeleteTaskEvent(ModelTodo modelTodo) {
        this.modelTodo = modelTodo;
    }
}