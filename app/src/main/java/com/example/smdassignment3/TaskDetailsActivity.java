package com.example.smdassignment3;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class TaskDetailsActivity extends AppCompatActivity implements TaskInputFragment.TaskInputListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        // Get task details from intent
        Intent intent = getIntent();
        int taskPosition = intent.getIntExtra("taskPosition", -1);
        String taskName = intent.getStringExtra("taskName");
        String taskDescription = intent.getStringExtra("taskDescription");

        // Load the TaskInputFragment with task data
        if (savedInstanceState == null) {
            TaskInputFragment fragment = new TaskInputFragment();
            Bundle args = new Bundle();
            args.putInt("taskPosition", taskPosition);
            args.putString("taskName", taskName);
            args.putString("taskDescription", taskDescription);
            fragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
        }
    }

    @Override
    public void onSaveTask(int taskPosition, String taskName, String taskDescription) {
        // Pass the updated task data back to MainActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("taskPosition", taskPosition);
        resultIntent.putExtra("updatedTaskName", taskName);
        resultIntent.putExtra("updatedTaskDescription", taskDescription);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onDeleteTask(int taskPosition) {
        // Pass the delete action back to MainActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("deleteTask", true);
        resultIntent.putExtra("taskPosition", taskPosition);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
