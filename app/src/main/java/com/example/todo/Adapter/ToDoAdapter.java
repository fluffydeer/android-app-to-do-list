package com.example.todo.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.todo.AddNewTask;
import com.example.todo.MainActivity;
import com.example.todo.Model.ToDoModel;
import com.example.todo.R;
import com.example.todo.Utils.DatabaseHandler;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> todoList;
    private MainActivity activity;  //context
    private DatabaseHandler db;

    public ToDoAdapter(DatabaseHandler db, MainActivity acitivity){
        this.db = db;
        this.activity = acitivity;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder, int position){
        db.openDatabase();
        ToDoModel item = todoList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));

        //every time we check or uncheck something we need to update our db
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    db.updateStatus(item.getId(), 1);   //id of the task we r gonna update
                }else{
                    db.updateStatus(item.getId(), 0);
                }
            }
        });
    }

    public int getItemCount(){
        return todoList.size();
    }

    private boolean toBoolean(int number){
        return number!=0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        //task element which we are going to address
        CheckBox task;
        ViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
        }
    }

    public Context getContext(){
        return activity;
    }

    public void deleteItem(int position){   //it is a position from adapter
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);    //automatically updates the recycler view
    }

    public void editItem(int position){
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public void setTasks(List<ToDoModel> todoList){
        this.todoList=todoList;
        notifyDataSetChanged(); //notify RecyclerView that data changed in the list
    }
}
