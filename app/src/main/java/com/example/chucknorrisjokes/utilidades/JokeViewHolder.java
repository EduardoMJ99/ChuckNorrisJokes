package com.example.chucknorrisjokes.utilidades;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chucknorrisjokes.R;

public class JokeViewHolder extends RecyclerView.ViewHolder {
    ImageView imgIcon;
    TextView txtValue;
    View view;

    public JokeViewHolder(@NonNull View itemView) {
        super(itemView);
        imgIcon = itemView.findViewById(R.id.imgIcon);
        txtValue = itemView.findViewById(R.id.txtValue);
        view = itemView;
    }

    public TextView getTxtValue() {
        return txtValue;
    }

    public ImageView getImgIcon() {
        return imgIcon;
    }
}
