package com.example.todo;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.todo.Model.ToDoModel;
import com.example.todo.Utils.DatabaseHandler;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import androidx.core.content.ContextCompat;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    private EditText newTaskText;
    private Button newTaskSaveButton;
    private DatabaseHandler db;

    //returns AddNewTask to the place where the method is called
    //then we can access the methods in the
    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){    //checks if instance of this fragment exists in memory
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);    //define unique style
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        newTaskText = Objects.requireNonNull(getView()).findViewById(R.id.newTaskText);
        newTaskSaveButton = getView().findViewById(R.id.newTaskButton);

        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        boolean isUpdate = false;

        final Bundle bundle = getArguments();   //bundler are for passing data to fragment
        if(bundle != null){     //something was passed
            isUpdate = true;
            String task = bundle.getString("task");
            newTaskText.setText(task);
            if(task.length()>0){    //if task isnt empty
                newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.purple_500));
            }
        }

        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals("")){
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                }else{
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(getContext(), R.color.purple_500));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                String text = newTaskText.getText().toString();
                if(finalIsUpdate){      //checking whether we are trying to edit an existing task
                    db.updateTask(bundle.getInt("id"), text);   //update database
                }else{                  //we r trying to create a new task
                    ToDoModel task = new ToDoModel();
                    task.setTask(text);
                    task.setStatus(0);
                    db.insertTask(task);
                }
                dismiss();  //dimiss bottom sheet if user clicks cancel
            }
        });

    }

    //whenever dimiss is called we need to update the recycler view automatically and manually
    //by user
    @Override
    public void onDismiss(DialogInterface dialog){
        Activity activity = getActivity();  //activity of the parent
        //this is checking if dismiss is called from an activity
        if(activity instanceof DialogCloseListener){
            ((DialogCloseListener)activity).handleDialogClose(dialog);
        }
    }

}
