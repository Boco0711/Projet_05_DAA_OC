package com.cleanup.todoc.sylvain.leprince.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import com.cleanup.todoc.sylvain.leprince.model.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM task_table")
    LiveData<List<Task>> getAllTasks();

    @Insert
    void insertTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Query("DELETE FROM task_table")
    void deleteAllTasks();
}
