package com.cleanup.todoc.sylvain.leprince.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import com.cleanup.todoc.sylvain.leprince.model.Project;
import com.cleanup.todoc.sylvain.leprince.repositories.ProjectRepository;
import java.util.List;

public class ProjectViewModel extends AndroidViewModel {
    private ProjectRepository projectRepository;
    private LiveData<List<Project>> allProjects;


    public ProjectViewModel(@NonNull Application application) {
        super(application);
        projectRepository = new ProjectRepository(application);
        allProjects = projectRepository.getAllProjects();
    }

    public LiveData<List<Project>> getAllProjects() {
        return allProjects;
    }

    public LiveData<Project> getProjectById(long id) {
        return projectRepository.getProject(id);
    }
}
