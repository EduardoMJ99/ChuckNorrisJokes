package com.example.chucknorrisjokes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import com.example.chucknorrisjokes.utilidades.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ConexionSQLiteHelper conn;
    private RecyclerView recyclerJokes;
    private JokeAdapter jokeAdapter;
    private List<Joke> listJokes;
    private NestedScrollView nestedSV;
    private ProgressBar pbLoading;
    private Button btnConnectToInternet;
    private int requestCount;
    private String urlToRequest;
    private String dbName;
    public static ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialSetup();

        recyclerJokes = findViewById(R.id.recyclerJokes);
        nestedSV = findViewById(R.id.nestedSV);
        pbLoading = findViewById(R.id.pbLoading);
        btnConnectToInternet = findViewById(R.id.btnConnectoToInternet);
        conn = new ConexionSQLiteHelper(this,
                dbName,
                null,
                1);

        listJokes = new ArrayList<>();

        /**
         * Bloque de codigo encargado de cargar las imagenes del endpoint y guardarlas en memoria
         * cache. Este metodo es llamado durante la creacion del RecyclerView.
         */
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

        /**
         * Bloque de codigo encargado de detectar cuando el usuario ha llegado al final del
         * RecyclerView para cargar mas datos ya sea de internet o de la base de datos.
         */
        nestedSV.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    getData();
                }
            }
        });

        /**
         * Bloque de codigo encargado de abrir la configuracion de conexion del telefono.
         */
        btnConnectToInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        });

        getData();
    }

    /**
     * Metodo encargado de hacer llamadas GET al endpoint definido en config.properties.
     * Se hara un total de n llamadas al mismo endpoint, n definido en config.properties (10
     * actualmente).
     */
    private void getDataFromEndpoint() {
        RequestQueue queue = Volley.newRequestQueue(this);
        List<Joke> endpointResults = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlToRequest, null, new Response.Listener<JSONObject>() {
            /**
             * Si la respuesta GET fue exitosa, este metodo se encarga de recibir la respuesta y
             * obtener los valores "icon_url" y "value" del json para despues crear un objecto de
             * tipo Joke que se encargara de almacenar estos valores.
             * Una vez se hayan realizado todas las llamadas al endpoint (10), se reinicia el contador
             * para nuevos requests y se agregan los resultados a una lista global (listJokes) y se
             * envia esta informacion al recycler y a la base de datos.
             *
             * @param response  la respuesta al llamado del endpoint en formato JSONObject
             */
            @Override
            public void onResponse(JSONObject response) {
                try {
                    requestCount--;
                    endpointResults.add(new Joke(new String[] {response.getString("icon_url"), response.getString("value")}));
                    if(requestCount == 0) {
                        requestCount = 10;
                        listJokes.addAll(endpointResults);
                        llenarRecycler(listJokes);
                        insertDataOnDatabase(endpointResults);
                    }
                } catch (JSONException e) {
                    requestCount--;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("ChuckNorris","No call");
            }
        });

        for(int cont=0;cont<requestCount;cont++) {
            queue.add(jsonObjectRequest);
        }
    }

    /**
     * Metodo encargado de consultar la informacion de la base de datos y enviarla al Recycler.
     * Si el resultado del llamado a la base de datos es vacio, significa que no existen datos
     * en la base de datos y se muestra el boton para ir a la configuracion de red.
     */
    private void getDataFromDatabase() {
        SQLiteDatabase db = conn.getReadableDatabase();
        List<Joke> databaseResults = new ArrayList<>();
        Cursor cursor = ConexionSQLiteHelper.retrieveJokes(db);
        while (cursor.moveToNext()){
            databaseResults.add(new Joke(cursor));
        }
        if(databaseResults.isEmpty()) {
            Toast.makeText(this,"No hay bromas para mostrar.",Toast.LENGTH_LONG).show();
            btnConnectToInternet.setVisibility(View.VISIBLE);
        }
        listJokes.addAll(databaseResults);
        llenarRecycler(listJokes);
        db.close();
    }

    /**
     * Metodo encargado de insertar los datos a la base de datos.
     *
     * @param newJokes  lista de objetos Joke para insertar a la base de datos.
     */
    private void insertDataOnDatabase(List<Joke> newJokes) {
        SQLiteDatabase db = conn.getWritableDatabase();
        for (Joke element:
             newJokes) {
            element.setValue(element.getValue().replace("'","''"));
            ConexionSQLiteHelper.insertJoke(db,element);
        }
        db.close();
    }

    /**
     * Metodo encargado de enviar la lista de datos al Recycler para ser renderizados.
     *
     * @param jokesToShow   lista de objetos Joke para mostrar en el Recycler.
     */
    private void llenarRecycler(List<Joke> jokesToShow){
        jokeAdapter = new JokeAdapter(jokesToShow);
        recyclerJokes.setAdapter(jokeAdapter);
        recyclerJokes.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Funcion encargada de verificar una conexcion a una red WiFi.
     *
     * @return  el booleano de la comparacion.
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    /**
     * Funcion encargada de verificar si existe conexion a Internet.
     *
     * @return el booleano de la comparacion.
     */
    private boolean internetIsConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Metodo encargado de obtener los datos ya sea de internet (endpoint) o de forma local
     * (base de datos) detectando si existe o no conexion a Internet.
     */
    private void getData() {
        if(isNetworkConnected() || internetIsConnected()) {
            getDataFromEndpoint();
            pbLoading.setVisibility(View.VISIBLE);
            btnConnectToInternet.setVisibility(View.GONE);
        } else {
            if(listJokes.isEmpty()) {
                btnConnectToInternet.setVisibility(View.GONE);
                getDataFromDatabase();
            }
            pbLoading.setVisibility(View.GONE);
        }
    }

    /**
     * Metodo encargado de obtener las propiedades del archivo config.properties dentro de assets.
     */
    public void initialSetup() {
        try {
            urlToRequest = Util.getProperty("urlToRequest", this);
            dbName = Util.getProperty("dbName", this);
            requestCount = Integer.parseInt(Util.getProperty("requestCount", this));
        } catch (FileNotFoundException e) {
            System.out.println("Error: properties file not found.");
        } catch (IOException e) {
            System.out.println("Error: unable to read properties file.");
        }
    }
}