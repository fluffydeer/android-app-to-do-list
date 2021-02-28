package com.example.todo.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todo.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

//reading, deleting and updating database
//default SQL database
public class DatabaseHandler extends SQLiteOpenHelper {
    //setting constants tjhat will be used everywhere
    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASK + " TEXT, " + STATUS + " INTEGER)";

    private SQLiteDatabase db;  //reference for DB

    public DatabaseHandler(Context context){
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_TODO_TABLE);    //executes raw query
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //Drop the older table
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        //create table again
        onCreate(db);
    }

    //we call this from the main activity
    public void openDatabase(){
        db = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task){
        ContentValues cv = new ContentValues(); //some data type which we can insert into db easily
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0);  //initial task will be unchecked
        db.insert(TODO_TABLE, null, cv);    //null - we want to insert the whole row
    }

    public List<ToDoModel> getAllTasks(){
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        //transactions are used so the block of code runs at the same time
        //safer data manipulation
        db.beginTransaction();
        try{
            //returns all rows from db based on no conditions
            cur = db.query(TODO_TABLE, null, null, null, null, null, null, null);
            if(cur != null){
                if(cur.moveToFirst()){
                    do{
                        ToDoModel task = new ToDoModel();
                        task.setId(cur.getInt(cur.getColumnIndex(ID))); //we get id of the cursor
                        task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                        task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                        taskList.add(task);
                    }while(cur.moveToNext());   //runs while cursor isnt empty
                }
            }
        }
        finally{
            db.endTransaction();
            cur.close();
        }
        return taskList;
    }

    //we get this from the recyclerView
    public void updateStatus(int id, int status){
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID+"= ?", new String[] {String.valueOf(id)});
    }

    public void updateTask(int id, String task){
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void deleteTask(int id){
        db.delete(TODO_TABLE, ID + "= ?" , new String[] {String.valueOf(id)});
    }


}
