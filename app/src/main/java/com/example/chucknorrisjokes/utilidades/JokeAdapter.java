package com.example.chucknorrisjokes.utilidades;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chucknorrisjokes.R;
import com.example.chucknorrisjokes.entitys.Joke;

import java.util.Collections;
import java.util.List;

public class JokeAdapter extends RecyclerView.Adapter<JokeViewHolder> {

    List<Joke> listResults = Collections.emptyList();
    Context context;

    public JokeAdapter(List<Joke> list, Context context) {
        this.listResults = list;
        this.context = context;
    }

    @NonNull
    @Override
    public JokeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View jokeView = inflater.inflate(R.layout.card_joke, parent, false);
        JokeViewHolder jokeViewHolder = new JokeViewHolder(jokeView);
        return jokeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull JokeViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
