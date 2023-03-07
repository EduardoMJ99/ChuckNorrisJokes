package com.example.chucknorrisjokes.utilidades;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.chucknorrisjokes.entitys.Joke;

public class ConexionSQLiteHelper extends SQLiteOpenHelper {

    public ConexionSQLiteHelper(@Nullable Context context,
                                @Nullable String name,
                                @Nullable SQLiteDatabase.CursorFactory factory,
                                int version){
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(createJokesTable());
    }

    private String createJokesTable() {
        return "CREATE TABLE joke ("+
                "id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "icon_url TEXT, "+
                "value TEXT)";
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS joke");
        onCreate(sqLiteDatabase);
    }

    public static Cursor retrieveJokes(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT * from joke", null);
        return cursor;
    }

    public static void insertJoke(SQLiteDatabase db, Joke joke) {
        String insert = "INSERT INTO joke ("+
                "icon_url, value) values ('"+
                joke.getUrlIcon()+"', '"+
                joke.getValue()+"')";
        db.execSQL(insert);
    }
}
