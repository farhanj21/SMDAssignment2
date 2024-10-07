package com.example.smdassignment3;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import java.util.ArrayList;

public class TaskAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Task> taskList;
    private LayoutInflater inflater;

    public TaskAdapter(Context context, ArrayList<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        TextView taskNameTextView = convertView.findViewById(R.id.task_item_text);
        CheckBox taskCheckBox = convertView.findViewById(R.id.taskCheckBox);

        Task currentTask = taskList.get(position);
        taskNameTextView.setText(currentTask.getTaskName());
        taskCheckBox.setChecked(currentTask.isCompleted());

        taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentTask.setCompleted(isChecked);
            if (isChecked) {
                ((MainActivity) context).moveTaskToCompleted(currentTask);
            } else {
                ((MainActivity) context).moveTaskToToDoList(currentTask);
            }
        });


        //click on task name (open TaskDetailsActivity)
        taskNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to TaskDetailsActivity
                Intent intent = new Intent(context, TaskDetailsActivity.class);
                intent.putExtra("taskPosition", position);
                intent.putExtra("taskName", currentTask.getTaskName());
                intent.putExtra("taskDescription", currentTask.getTaskDescription());  // Pass description
                ((MainActivity) context).startActivityForResult(intent, 2);
            }
        });


        //long-click on the task for deletion
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Task")
                        .setMessage("Are you sure you want to delete this task?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            taskList.remove(position);
                            notifyDataSetChanged();  // Refresh the list
                            Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        });

        return convertView;
    }
}
