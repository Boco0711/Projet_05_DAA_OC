package com.cleanup.todoc.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cleanup.todoc.R;
import com.cleanup.todoc.base.BaseActivity;
import com.cleanup.todoc.model.Project;
import com.cleanup.todoc.model.Task;
import com.cleanup.todoc.viewmodel.ProjectViewModel;
import com.cleanup.todoc.todolist.TaskAdapter;
import com.cleanup.todoc.viewmodel.TaskViewModel;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import butterknife.BindView;

import static java.lang.String.valueOf;

public class MainActivity extends BaseActivity implements TaskAdapter.DeleteTaskListener {
    @BindView(R.id.list_tasks) RecyclerView listTasks;
    @BindView(R.id.lbl_no_task) TextView noTaskTextView;
    @BindView(R.id.fab_add_task) FloatingActionButton addTaskButton;

    private TaskViewModel taskViewModel;
    private TaskAdapter adapter;
    private List<Task> tasks;
    private Spinner dialogSpinner = null;
    private SortMethod sortMethod = SortMethod.NONE;
    public AlertDialog dialog = null;
    private EditText dialogEditText = null;
    private Project[] projects;

    @Override
    public int getLayoutContentViewID() { return R.layout.activity_main; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureRecyclerView();
        this.configureViewModel();
        addTaskButton.setOnClickListener(view -> showAddTaskDialog());
    }

    // -------------------
    // DATA
    // -------------------

    private void configureViewModel(){
        ProjectViewModel projectViewModel = ViewModelProviders.of(this).get(ProjectViewModel.class);
        projectViewModel.getAllProjects().observe(this, newProjects -> {
            projects = Objects.requireNonNull(newProjects).toArray(new Project[newProjects.size()]);
            adapter.setProjects(newProjects);
        });
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        taskViewModel.getAllTasks().observe(this, newTasks -> {
            tasks = newTasks;
            adapter.setTasks(newTasks);
            updateTasks();
        });

    }

    private void createTask(Task task){
        this.taskViewModel.insertTask(task);
    }


    // -------------------
    // UI
    // -------------------

    private void configureRecyclerView(){
        this.listTasks.setLayoutManager(new LinearLayoutManager(this));
        this.adapter = new TaskAdapter(this);
        this.listTasks.setAdapter(this.adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.filter_alphabetical) {
            sortMethod = SortMethod.ALPHABETICAL;
        } else if (id == R.id.filter_alphabetical_inverted) {
            sortMethod = SortMethod.ALPHABETICAL_INVERTED;
        } else if (id == R.id.filter_oldest_first) {
            sortMethod = SortMethod.OLD_FIRST;
        } else if (id == R.id.filter_recent_first) {
            sortMethod = SortMethod.RECENT_FIRST;
        }

        updateTasks();

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when a task needs to be deleted.
     *
     * @param task the task that needs to be deleted
     */
    @Override
    public void onDeleteTask(Task task) {
        taskViewModel.deleteTask(task);
        updateTasks();
    }


    private enum SortMethod {
        ALPHABETICAL,
        ALPHABETICAL_INVERTED,
        RECENT_FIRST,
        OLD_FIRST,
        NONE
    }

    private void updateTasks() {
        if (tasks.size() == 0) {
            noTaskTextView.setVisibility(View.VISIBLE);
            listTasks.setVisibility(View.GONE);
        } else {
            noTaskTextView.setVisibility(View.GONE);
            listTasks.setVisibility(View.VISIBLE);
            switch (sortMethod) {
                case ALPHABETICAL:
                    Collections.sort(tasks, new Task.TaskAZComparator());
                    break;
                case ALPHABETICAL_INVERTED:
                    Collections.sort(tasks, new Task.TaskZAComparator());
                    break;
                case RECENT_FIRST:
                    Collections.sort(tasks, new Task.TaskRecentComparator());
                    break;
                case OLD_FIRST:
                    Collections.sort(tasks, new Task.TaskOldComparator());
                    break;
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void onPositiveButtonClick(DialogInterface dialogInterface) {
        // If dialog is open
        if (dialogEditText != null && dialogSpinner != null) {
            // Get the name of the task
            String taskName = dialogEditText.getText().toString();

            // Get the selected project to be associated to the task
            Project taskProject = null;
            if (dialogSpinner.getSelectedItem() instanceof Project) {
                taskProject = (Project) dialogSpinner.getSelectedItem();
            }

            // If a name has not been set
            if (taskName.trim().isEmpty()) {
                dialogEditText.setError(getString(R.string.empty_task_name));
            }
            // If both project and name of the task have been set
            else if (taskProject != null) {
                Task task = new Task(
                        taskProject.getId(),
                        taskName,
                        new Date().getTime()

                );
                Toast.makeText(this, valueOf(taskProject.getId()), Toast.LENGTH_SHORT).show();

                createTask(task);
                dialogInterface.dismiss();
            }
            // If name has been set, but project has not been set (this should never occur)
            else{
                dialogInterface.dismiss();
            }
        }
        // If dialog is aloready closed
        else {
            dialogInterface.dismiss();
        }
    }

    private void showAddTaskDialog() {
        final AlertDialog dialog = getAddTaskDialog();

        dialog.show();

        dialogEditText = dialog.findViewById(R.id.txt_task_name);
        dialogSpinner = dialog.findViewById(R.id.project_spinner);

        populateDialogSpinner();
    }

    private AlertDialog getAddTaskDialog() {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this, R.style.Dialog);

        alertBuilder.setTitle(R.string.add_task);
        alertBuilder.setView(R.layout.dialog_add_task);
        alertBuilder.setPositiveButton(R.string.add, null);
        alertBuilder.setOnDismissListener(dialogInterface -> {
            dialogEditText = null;
            dialogSpinner = null;
            dialog = null;
        });

        dialog = alertBuilder.create();

        // This instead of listener to positive button in order to avoid automatic dismiss
        dialog.setOnShowListener(dialogInterface -> {

            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> onPositiveButtonClick(dialog));
        });

        return dialog;
    }

    private void populateDialogSpinner() {
        ArrayAdapter<Project> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, projects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (dialogSpinner != null) {
            dialogSpinner.setAdapter(adapter);
        }
    }

    /*public void getSelectedProject(View v) {
        Project project = (Project) dialogSpinner.getSelectedItem();
    }*/
}