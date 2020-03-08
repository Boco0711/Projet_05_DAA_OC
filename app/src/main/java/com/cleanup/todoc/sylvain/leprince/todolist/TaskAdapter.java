package com.cleanup.todoc.sylvain.leprince.todolist;

import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.cleanup.todoc.sylvain.leprince.R;
import com.cleanup.todoc.sylvain.leprince.model.Project;
import com.cleanup.todoc.sylvain.leprince.model.Task;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {
    private List<Task> tasks = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();
    private DeleteTaskListener deleteTaskListener;

    public TaskAdapter(DeleteTaskListener deleteTaskListener) {
        this.deleteTaskListener = deleteTaskListener;
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);

        return new TaskHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TaskHolder holder, int position) {
        Task currentTask = tasks.get(position);
        Project projectRelated = getProjectById(currentTask.getProjectId());
        holder.taskName.setText(currentTask.getName());
        if (projectRelated != null) {
            holder.projectName.setText(projectRelated.getName());
            holder.projectImage.setSupportImageTintList(ColorStateList.valueOf(projectRelated.getColor()));
        }
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteTaskListener.onDeleteTask(currentTask);
                System.out.println("Le bouton est cliqué ");
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> tasks){
        this.tasks = tasks;
        this.notifyDataSetChanged();
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    private Project getProjectById(long id) {
        for (Project project : projects) {
            if (project.getId() == id)
                return project;
        }
        return null;
    }

    public interface DeleteTaskListener {
        /**
         * Called when a task needs to be deleted.
         *
         * @param task the task that needs to be deleted
         */
        void onDeleteTask(Task task);
    }

    class TaskHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.lbl_task_name)
        TextView taskName;
        @BindView(R.id.lbl_project_name)
        TextView projectName;
        @BindView(R.id.img_project)
        AppCompatImageView projectImage;
        @BindView(R.id.img_delete)
        AppCompatImageView deleteButton;

        TaskHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}