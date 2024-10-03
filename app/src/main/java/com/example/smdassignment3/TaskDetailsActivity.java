package com.example.smdassignment3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailsActivity extends AppCompatActivity {

    private EditText taskNameEditText;
    private EditText taskDescriptionEditText;
    private Button saveButton;
    private Button deleteButton;
    private int taskPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        taskNameEditText = findViewById(R.id.taskNameEditText);
        taskDescriptionEditText = findViewById(R.id.taskDescriptionEditText);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Get task details from intent
        Intent intent = getIntent();
        if (intent != null) {
            taskPosition = intent.getIntExtra("taskPosition", -1);
            String taskName = intent.getStringExtra("taskName");
            String taskDescription = intent.getStringExtra("taskDescription");

            taskNameEditText.setText(taskName);
            taskDescriptionEditText.setText(taskDescription);  // Display the description
        }



    // Save button click listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass updated task name and description back to MainActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("taskPosition", taskPosition);
                resultIntent.putExtra("updatedTaskName", taskNameEditText.getText().toString());
                resultIntent.putExtra("updatedTaskDescription", taskDescriptionEditText.getText().toString());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        // Delete button click listener
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("deleteTask", true);
                resultIntent.putExtra("taskPosition", taskPosition);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
