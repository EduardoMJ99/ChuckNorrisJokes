package com.example.chucknorrisjokes.entitys;

import android.database.Cursor;

public class Joke {

    private String urlIcon;
    private String value;

    public Joke(Cursor cursor) {
        this.urlIcon = cursor.getString(1);
        this.value = cursor.getString(2);
    }

    public Joke(String[] values) {
        this.urlIcon = values[0];
        this.value = values[1];
    }

    public String getUrlIcon() {
        return urlIcon;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
