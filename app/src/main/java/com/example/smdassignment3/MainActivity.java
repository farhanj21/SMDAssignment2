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

        loadTasks();

        toDoTaskAdapter = new TaskAdapter(this, toDoTaskList);
        completedTaskAdapter = new TaskAdapter(this, completedTaskList);

        toDoListView.setAdapter(toDoTaskAdapter);
        completedTasksListView.setAdapter(completedTaskAdapter);

        // FAB to open TaskInputActivity for to add new task
        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TaskInputActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // long click listener for deleting tasks in the To-Do list
        toDoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteConfirmationDialog(position, true);
                return true;
            }
        });

    }
    private void showDeleteConfirmationDialog(final int position, final boolean isToDoTask) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (isToDoTask) {
                            toDoTaskList.remove(position);
                            toDoTaskAdapter.notifyDataSetChanged();
                        } else {
                            completedTaskList.remove(position);
                            completedTaskAdapter.notifyDataSetChanged();
                        }
                        saveTasks();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1) {  // Handle result from TaskInputActivity (new task)
                String taskName = data.getStringExtra("taskName");
                String taskDescription = data.getStringExtra("taskDescription");

                if (taskName != null && !taskName.trim().isEmpty()) {
                    Task newTask = new Task(taskName, taskDescription, false);
                    toDoTaskList.add(newTask);
                    toDoTaskAdapter.notifyDataSetChanged();
                    saveTasks();
                }
            } else if (requestCode == 2) {  // Handle result from TaskDetailsActivity (edit or delete)
                int taskPosition = data.getIntExtra("taskPosition", -1);

                if (data.getBooleanExtra("deleteTask", false)) {
                    if (taskPosition != -1) {
                        Task task = getTaskFromLists(taskPosition);
                        toDoTaskList.remove(task);
                        completedTaskList.remove(task);
                        toDoTaskAdapter.notifyDataSetChanged();
                        completedTaskAdapter.notifyDataSetChanged();
                        saveTasks();
                    }
                } else {
                    String updatedTaskName = data.getStringExtra("updatedTaskName");
                    String updatedTaskDescription = data.getStringExtra("updatedTaskDescription");

                    if (taskPosition != -1 && updatedTaskName != null && updatedTaskDescription != null) {
                        Task task = getTaskFromLists(taskPosition);
                        if (task != null) {
                            task.setTaskName(updatedTaskName);
                            task.setTaskDescription(updatedTaskDescription);
                            toDoTaskAdapter.notifyDataSetChanged();
                            completedTaskAdapter.notifyDataSetChanged();
                            saveTasks();
                        }
                    }
                }
            }
        }
    }

    // Move task to Completed list when checkbox is clicked
    public void moveTaskToCompleted(Task task) {
        toDoTaskList.remove(task);
        task.setCompleted(true);
        completedTaskList.add(task);
        toDoTaskAdapter.notifyDataSetChanged();
        completedTaskAdapter.notifyDataSetChanged();
        saveTasks();
    }

    // Move task back to To-Do list when checkbox is unchecked
    public void moveTaskToToDoList(Task task) {
        completedTaskList.remove(task);
        task.setCompleted(false);
        toDoTaskList.add(task);
        toDoTaskAdapter.notifyDataSetChanged();
        completedTaskAdapter.notifyDataSetChanged();
        saveTasks();
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
        String toDoJson = gson.toJson(toDoTaskList);
        String completedJson = gson.toJson(completedTaskList);
        editor.putString("to_do_task_list", toDoJson);
        editor.putString("completed_task_list", completedJson);
        editor.apply();
    }

    // Load tasks from SharedPreferences
    private void loadTasks() {
        String toDoJson = sharedPreferences.getString("to_do_task_list", null);
        String completedJson = sharedPreferences.getString("completed_task_list", null);
        Type type = new TypeToken<ArrayList<Task>>() {}.getType();

        toDoTaskList = gson.fromJson(toDoJson, type);
        completedTaskList = gson.fromJson(completedJson, type);

        if (toDoTaskList == null) {
            toDoTaskList = new ArrayList<>();
        }
        if (completedTaskList == null) {
            completedTaskList = new ArrayList<>();
        }
    }

    // Helper method to retrieve a task from either the To-Do or Completed lists
    private Task getTaskFromLists(int taskPosition) {
        if (taskPosition < toDoTaskList.size()) {
            return toDoTaskList.get(taskPosition);
        } else if (taskPosition - toDoTaskList.size() < completedTaskList.size()) {
            return completedTaskList.get(taskPosition - toDoTaskList.size());
        }
        return null;
    }
}
