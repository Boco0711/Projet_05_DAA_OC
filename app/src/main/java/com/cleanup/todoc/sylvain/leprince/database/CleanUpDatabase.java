package com.cleanup.todoc.sylvain.leprince.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import com.cleanup.todoc.sylvain.leprince.database.dao.ProjectDao;
import com.cleanup.todoc.sylvain.leprince.database.dao.TaskDao;
import com.cleanup.todoc.sylvain.leprince.model.Project;
import com.cleanup.todoc.sylvain.leprince.model.Task;

@Database(entities = {Task.class, Project.class}, version = 1, exportSchema = false)
public abstract class CleanUpDatabase extends RoomDatabase {

    // --- SINGLETON ---
    private static volatile CleanUpDatabase INSTANCE; // volatile = stocker dans la mémoire principale

    // --- DAO ---
    public abstract TaskDao taskDao();

    public abstract ProjectDao projectDao();

    // --- INSTANCE ---
    public static synchronized CleanUpDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    CleanUpDatabase.class, "cleanup_database")
                    .addCallback(roomCallback)
                    .build();
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        /**
         * Called when the database is created for the first time. This is called after all the
         * tables are created.
         *
         * @param db The database.
         */
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(INSTANCE).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        //private TaskDao taskDao;                // Passer par doInBackGround pour ajouter en base de donnée a la création
        private ProjectDao projectDao;

        private PopulateDbAsyncTask(CleanUpDatabase db) {
            //taskDao = db.taskDao();
            projectDao = db.projectDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            projectDao.insertProject(new Project(1, "Projet Tartampion", 0xFFEADAD1));
            projectDao.insertProject(new Project(2, "Projet Lucidia", 0xFFB4CDBA));
            projectDao.insertProject(new Project(3, "Projet Circus", 0xFFA3CED2));
            return null;
        }
    }
}
