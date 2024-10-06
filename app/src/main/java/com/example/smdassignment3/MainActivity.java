package com.example.smdassignment3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Two lists: To-Do and Completed Tasks
    private ArrayList<Task> toDoTaskList;
    private ArrayList<Task> completedTaskList;
    private TaskAdapter toDoTaskAdapter;
    private TaskAdapter completedTaskAdapter;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gson = new Gson();
        sharedPreferences = getSharedPreferences("tasks_pref", Context.MODE_PRIVATE);

        ListView toDoListView = findViewById(R.id.toDoListView);
        ListView completedTasksListView = findViewById(R.id.completedTasksListView);
        FloatingActionButton fabAddTask = findViewById(R.id.fabAddTask);

        // Load tasks from SharedPreferences
        loadTasks();

        // Initialize adapters for both lists
        toDoTaskAdapter = new TaskAdapter(this, toDoTaskList);
        completedTaskAdapter = new TaskAdapter(this, completedTaskList);

        // Set adapters for ListViews
        toDoListView.setAdapter(toDoTaskAdapter);
        completedTasksListView.setAdapter(completedTaskAdapter);

        // OnClickListener for FAB to open TaskInputActivity for result (to add new task)
        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TaskInputActivity.class);
                startActivityForResult(intent, 1);  // Open TaskInputActivity for result (requestCode 1)
            }
        });

        // Add long click listener for deleting tasks in the To-Do list
        toDoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteConfirmationDialog(position, true);
                return true;
            }
        });

        // Add long click listener for deleting tasks in the Completed tasks list
        completedTasksListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteConfirmationDialog(position, false);
                return true;
            }
        });
    }

    // Show a confirmation dialog before deleting a task
    private void showDeleteConfirmationDialog(final int position, final boolean isToDoTask) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (isToDoTask) {
                            // Remove the task from To-Do List
                            toDoTaskList.remove(position);
                            toDoTaskAdapter.notifyDataSetChanged();
                        } else {
                            // Remove the task from Completed List
                            completedTaskList.remove(position);
                            completedTaskAdapter.notifyDataSetChanged();
                        }
                        saveTasks();  // Save updated task list
                    }
                })
                .setNegativeButton("No", null)  // Do nothing on "No"
                .show();
    }

    // Handle results from TaskInputActivity and TaskDetailsActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1) {  // Handle result from TaskInputActivity (new task)
                String taskName = data.getStringExtra("taskName");
                String taskDescription = data.getStringExtra("taskDescription");  // Retrieve the description

                if (taskName != null && !taskName.trim().isEmpty()) {
                    // Add the new task to the To-Do List (incomplete)
                    Task newTask = new Task(taskName, taskDescription, false);  // False means not completed
                    toDoTaskList.add(newTask);
                    toDoTaskAdapter.notifyDataSetChanged();  // Refresh To-Do List
                    saveTasks();  // Save tasks after adding a new one
                }
            } else if (requestCode == 2) {  // Handle result from TaskDetailsActivity (edit or delete)
                int taskPosition = data.getIntExtra("taskPosition", -1);

                if (data.getBooleanExtra("deleteTask", false)) {
                    // Delete the task
                    if (taskPosition != -1) {
                        Task task = getTaskFromLists(taskPosition);  // Fetch task from either list
                        toDoTaskList.remove(task);
                        completedTaskList.remove(task);
                        toDoTaskAdapter.notifyDataSetChanged();
                        completedTaskAdapter.notifyDataSetChanged();  // Refresh both lists
                        saveTasks();  // Save tasks after deletion
                    }
                } else {
                    // Update the task
                    String updatedTaskName = data.getStringExtra("updatedTaskName");
                    String updatedTaskDescription = data.getStringExtra("updatedTaskDescription");

                    if (taskPosition != -1 && updatedTaskName != null && updatedTaskDescription != null) {
                        Task task = getTaskFromLists(taskPosition);
                        if (task != null) {
                            task.setTaskName(updatedTaskName);
                            task.setTaskDescription(updatedTaskDescription);
                            toDoTaskAdapter.notifyDataSetChanged();
                            completedTaskAdapter.notifyDataSetChanged();  // Refresh both lists
                            saveTasks();  // Save tasks after editing
                        }
                    }
                }
            }
        }
    }

    // Move task to Completed list when checkbox is clicked
    public void moveTaskToCompleted(Task task) {
        toDoTaskList.remove(task);
        task.setCompleted(true);  // Mark task as completed
        completedTaskList.add(task);
        toDoTaskAdapter.notifyDataSetChanged();
        completedTaskAdapter.notifyDataSetChanged();
        saveTasks();  // Save tasks after moving
    }

    // Move task back to To-Do list when checkbox is unchecked
    public void moveTaskToToDoList(Task task) {
        completedTaskList.remove(task);
        task.setCompleted(false);  // Mark task as incomplete
        toDoTaskList.add(task);
        toDoTaskAdapter.notifyDataSetChanged();
        completedTaskAdapter.notifyDataSetChanged();
        saveTasks();  // Save tasks after moving
    }

    // Save tasks to SharedPreferences when the activity is paused
    @Override
    protected void onPause() {
        super.onPause();
        saveTasks();
    }

    // Convert the task lists to JSON and save in SharedPreferences
    private void saveTasks() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String toDoJson = gson.toJson(toDoTaskList);  // Convert To-Do list to JSON
        String completedJson = gson.toJson(completedTaskList);  // Convert Completed list to JSON
        editor.putString("to_do_task_list", toDoJson);
        editor.putString("completed_task_list", completedJson);
        editor.apply();  // Save both lists in SharedPreferences
    }

    // Load tasks from SharedPreferences
    private void loadTasks() {
        String toDoJson = sharedPreferences.getString("to_do_task_list", null);
        String completedJson = sharedPreferences.getString("completed_task_list", null);
        Type type = new TypeToken<ArrayList<Task>>() {}.getType();

        toDoTaskList = gson.fromJson(toDoJson, type);
        completedTaskList = gson.fromJson(completedJson, type);

        if (toDoTaskList == null) {
            toDoTaskList = new ArrayList<>();  // If no tasks were saved, initialize the list
        }
        if (completedTaskList == null) {
            completedTaskList = new ArrayList<>();  // If no tasks were saved, initialize the list
        }
    }

    // Helper method to retrieve a task from either the To-Do or Completed lists
    private Task getTaskFromLists(int taskPosition) {
        // Try to find the task in both lists
        if (taskPosition < toDoTaskList.size()) {
            return toDoTaskList.get(taskPosition);
        } else if (taskPosition - toDoTaskList.size() < completedTaskList.size()) {
            return completedTaskList.get(taskPosition - toDoTaskList.size());
        }
        return null;  // Task not found in either list
    }
}
