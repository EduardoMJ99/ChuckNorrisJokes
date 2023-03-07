package com.example.chucknorrisjokes.utilidades;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.example.chucknorrisjokes.MainActivity;
import com.example.chucknorrisjokes.R;
import com.example.chucknorrisjokes.entitys.Joke;

import java.util.Collections;
import java.util.List;

public class JokeAdapter extends RecyclerView.Adapter<JokeViewHolder> {

    List<Joke> listResults = Collections.emptyList();

    public JokeAdapter(List<Joke> list) {
        this.listResults = list;
    }

    @NonNull
    @Override
    public JokeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View jokeView =
               LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_joke, viewGroup, false);
        return new JokeViewHolder(jokeView);
    }

    @Override
    public void onBindViewHolder(@NonNull JokeViewHolder holder, int position) {
        holder.getTxtValue().setText(listResults.get(position).getValue());
        MainActivity.imageLoader.get(listResults.get(position).getUrlIcon(),
                ImageLoader.getImageListener(holder.getImgIcon(),
                        R.drawable.loading,
                        R.drawable.chuck_norris));
    }

    @Override
    public int getItemCount() {
        return listResults.size();
    }
}
