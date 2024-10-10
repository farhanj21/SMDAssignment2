package com.example.smdassignment3;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TaskInputFragment extends Fragment {

    private EditText taskNameEditText;
    private EditText taskDescriptionEditText;
    private Button saveButton;
    private Button deleteButton;

    private int taskPosition = -1;
    private String taskName;
    private String taskDescription;

    private TaskInputListener callback;

    private static final String TAG = "TaskInputFragment";

    public interface TaskInputListener {
        void onSaveTask(int taskPosition, String taskName, String taskDescription);
        void onDeleteTask(int taskPosition);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (TaskInputListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement TaskInputListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");

        if (getArguments() != null) {
            taskPosition = getArguments().getInt("taskPosition", -1);
            taskName = getArguments().getString("taskName", "");
            taskDescription = getArguments().getString("taskDescription", "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_task_input, container, false);

        taskNameEditText = view.findViewById(R.id.taskNameEditText);
        taskDescriptionEditText = view.findViewById(R.id.taskDescriptionEditText);
        saveButton = view.findViewById(R.id.saveButton);
        deleteButton = view.findViewById(R.id.deleteButton);

        taskNameEditText.setText(taskName);
        taskDescriptionEditText.setText(taskDescription);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onSaveTask(taskPosition, taskNameEditText.getText().toString(), taskDescriptionEditText.getText().toString());
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onDeleteTask(taskPosition);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }
}
