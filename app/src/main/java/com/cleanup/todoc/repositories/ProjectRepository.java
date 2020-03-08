package com.cleanup.todoc.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import com.cleanup.todoc.database.CleanUpDatabase;
import com.cleanup.todoc.database.dao.ProjectDao;
import com.cleanup.todoc.model.Project;
import java.util.List;

public class ProjectRepository {
    private ProjectDao projectDao;
    private LiveData<List<Project>> allProjects;

    public ProjectRepository(Application application) {
        CleanUpDatabase database = CleanUpDatabase.getInstance(application);
        projectDao = database.projectDao();
        allProjects = projectDao.getProjects();
    }

    public LiveData<Project> getProject(long projectId) {
        return this.projectDao.getProject(projectId);
    }

    public LiveData<List<Project>> getAllProjects() {
        return allProjects;
    }

     /*
      Possibilité d'ajouter des projet par la suite conservé ( mis en commentaire car non utilisé actuellement
      Il faudra juste remettre le ProjectDao projectDao en attribut privé de la classe ProjectRepository
     */

    /*public void insertProject(Project project) {
        new InsertProjectAsyncTask(projectDao).execute(project);
    }

    private static class InsertProjectAsyncTask extends AsyncTask<Project, Void, Void> {
        private ProjectDao projectDao;

        private InsertProjectAsyncTask(ProjectDao projectDao) {
            this.projectDao = projectDao;
        }

        @Override
        protected Void doInBackground(Project... projects) {
            projectDao.insertProject(projects[0]);
            return null;
        }
    }*/


}
