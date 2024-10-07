package com.example.smdassignment3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class TaskInputActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_input);

        EditText taskNameInput = findViewById(R.id.taskName);
        EditText taskDescriptionInput = findViewById(R.id.taskDescription);
        Button btnSaveTask = findViewById(R.id.btnSaveTask);

        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName = taskNameInput.getText().toString().trim();
                String taskDescription = taskDescriptionInput.getText().toString().trim();

                if (!taskName.isEmpty()) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("taskName", taskName);
                    resultIntent.putExtra("taskDescription", taskDescription);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    taskNameInput.setError("Task name cannot be empty!");
                }
            }
        });
    }
}
