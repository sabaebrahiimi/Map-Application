package com.example.mapapp.database;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;

import com.example.mapapp.MainActivity;
import com.example.mapapp.ui.bookmark.BookmarkItem;
import com.example.mapapp.ui.map.Location;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseManager implements Runnable {

    private int task;
    private HashMap<BookmarkItem, String> id = new HashMap<>();
    private BookmarkItem location;
    private DatabaseHelper db;
    private static DatabaseManager dataBaseManager = null;

    private DatabaseManager(Context context) {
        this.db = new DatabaseHelper(context);
        this.location = new BookmarkItem("", 0.0 , 0.0);
    }


    public static synchronized DatabaseManager getInstance(Context context) {
        if (dataBaseManager == null) {
            dataBaseManager = new DatabaseManager(context);
        }
        return dataBaseManager;
    }

    @Override
    public void run() {
        switch (task) {
            case 1:
                readFromDb();
                break;
            case 2:
                insertToDb(location);
                break;
            case 3:
                deleteFromDb(location);
                break;
        }
    }

    private void deleteFromDb(BookmarkItem location) {
        db.deleteData(this.id.get(location));
        this.id.remove(location);
        MainActivity.locations.remove(location);
    }

    private void insertToDb(BookmarkItem location) {
        long id = db.insertData(location.getName(), String.valueOf(location.getLatitude()), String.valueOf(location.getLongtitude()));
        this.id.put(location, String.valueOf(id));
        MainActivity.locations.add(location);
    }

    private void readFromDb() {
        Cursor res = db.getAllData();
        if (res.getCount() == 0) {
            return;
        }
        while (res.moveToNext()) {
            if (!this.id.containsValue(String.valueOf(res.getInt(0)))) {
                BookmarkItem location = new BookmarkItem(res.getString(1), Double.parseDouble(res.getString(2)), Double.parseDouble(res.getString(3)));
                this.id.put(location, String.valueOf(res.getInt(0)));
                MainActivity.locations.add(location);
            }
        }
    }

    public void setLocation(BookmarkItem location) {
        this.location = location;
    }

    public void setTask(int task) {
        this.task = task;
    }
}