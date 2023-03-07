package com.example.chucknorrisjokes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.chucknorrisjokes.entitys.Joke;
import com.example.chucknorrisjokes.utilidades.JokeAdapter;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerJokes;
    JokeAdapter jokeAdapter;
    List<Joke> listJokes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerJokes = findViewById(R.id.recyclerJokes);
    }

    private List<Joke> getDataFromEndpoint() {
        return null;
    }

    private void llenarRecycler(){
        jokeAdapter = new JokeAdapter(listJokes,getApplicationContext());
        recyclerJokes.setAdapter(jokeAdapter);
        recyclerJokes.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }
}