package com.example.chucknorrisjokes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chucknorrisjokes.entitys.Joke;
import com.example.chucknorrisjokes.utilidades.ConexionSQLiteHelper;
import com.example.chucknorrisjokes.utilidades.JokeAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ConexionSQLiteHelper conn;
    private RecyclerView recyclerJokes;
    private JokeAdapter jokeAdapter;
    private List<Joke> listJokes;
    private NestedScrollView nestedSV;
    private ProgressBar pbLoading;
    private int requestCount;
    public static ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerJokes = findViewById(R.id.recyclerJokes);
        nestedSV = findViewById(R.id.nestedSV);
        pbLoading = findViewById(R.id.pbLoading);
        conn = new ConexionSQLiteHelper(this,
                "chucknorris",
                null,
                1);

        listJokes = new ArrayList<>();
        requestCount = 0;
        getDataFromEndpoint();

        RequestQueue queueImages = Volley.newRequestQueue(this);
        imageLoader = new ImageLoader(queueImages, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(10);
            @Nullable
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });

        nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    pbLoading.setVisibility(View.VISIBLE);
                    getDataFromEndpoint();
                    //getDataFromDatabase();
                }
            }
        });

    }

    private void getDataFromEndpoint() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.chucknorris.io/jokes/random?category=dev";
        List<Joke> endpointResults = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    requestCount++;
                    endpointResults.add(new Joke(new String[] {response.getString("icon_url"), response.getString("value")}));
                    if(requestCount == 10) {
                        requestCount = 0;
                        listJokes.addAll(endpointResults);
                        llenarRecycler(listJokes);
                        insertDataOnDatabase(endpointResults);
                    }
                } catch (JSONException e) {
                    requestCount++;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ChuckNorris","No call");
            }
        });

        for(int cont=0;cont<10;cont++) {
            queue.add(jsonObjectRequest);
        }
    }
    
    private void getDataFromDatabase() {
        SQLiteDatabase db = conn.getReadableDatabase();
        List<Joke> databaseResults = new ArrayList<>();
        Cursor cursor = ConexionSQLiteHelper.retrieveJokes(db);
        while (cursor.moveToNext()){
            databaseResults.add(new Joke(cursor));
        }
        llenarRecycler(databaseResults);
    }

    private void insertDataOnDatabase(List<Joke> newJokes) {
        SQLiteDatabase db = conn.getWritableDatabase();
        for (Joke element:
             newJokes) {
            element.setValue(element.getValue().replace("'","''"));
            ConexionSQLiteHelper.insertJoke(db,element);
        }
    }

    private void llenarRecycler(List<Joke> jokesToShow){
        jokeAdapter = new JokeAdapter(jokesToShow);
        recyclerJokes.setAdapter(jokeAdapter);
        recyclerJokes.setLayoutManager(new LinearLayoutManager(this));
    }
}