package com.cleanup.todoc.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "project_table")
public class Project {

    @PrimaryKey()
    private long id;

    private String name;

    private int color;

    public Project(long id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    // ----- GETTER ---------
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }
    //-----------------------

    @Override
    @NonNull
    public String toString(){
        return name;
    }
}
