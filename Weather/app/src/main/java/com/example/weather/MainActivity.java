package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView; //Az adott város időjáráshoz tartozó adatokat egy recyclerViewba helyeztem.
    private List<City> cities; // Az adott város időjáráshoz tartozó adatokat egy osztályba tettem (City) és ebből készítek egy listát
    //Ebbe a listába fogom majd a lekérdezés után hozzáadni az adott várost
    //Ezt a listát fogom majd az adapternek átadni

    private MainActivity main;
    private Context context;

    private EditText edt; //Ez a keresőhöz tartozó EditText

    private ProductAdapter adapter; //Ez az adapterem amelyik majd összeköti a cities listát a recycleview-val

    private ProgressBar progressBar; //Amíg lenem kérdezte az összes várost, addig egy progressbar jelez.
    private int t=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recy); //az activity_main layoutból meg keresem és deklarálom a recycleViewt
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //A LinearLayout mondja meg hogy hogyan jelenen meg majd az adatunk(lineárisan egymás alá)

        progressBar = findViewById(R.id.progb);

        cities = new ArrayList<>();

        main = this;
        context=getApplicationContext();

        edt = findViewById(R.id.edt);
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setFilter(s.toString());
            }
        });  //figyeli, ha bármi történik az EditTextbe, pl beleírok, kitörlök akkor meghívódnak ezek a függvények

        try {
            getWeater(); //Ezzel a függvényel fogjuk lekérni a weboldalról a városok adatait.
        } catch (JSONException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setFilter(String txt){ //ez a függvény egy Stringet vár bemenetre. Megkapja az EditText szövegét és megnézi hogy a cities listában valamelyik város neve tartalmazza e.
        //ha igen akkor hozzáadja egy listához.
        //majd a végén átállítja az adapternek a listáját. Vagyis mostmár csak azok a városok fognak megjelenni, amelyik tartalmazza a beírt szöveget.
        List<City> filterCities=new LinkedList<>();
        for( City city : cities){
            if(city.getName().toLowerCase().contains(txt.toLowerCase())){
                filterCities.add(city);
            }
        }
        adapter.setList(filterCities);

    }

    private List<String> cityAdder(){ //Ez a függvény vissza tér egy listával amiben van 30 nagyobb város String változóval
        final List<String> cities2 = new LinkedList<>();
        cities2.add("Budapest");
        cities2.add("London");
        cities2.add("Bukarest");
        cities2.add("Wien");
        cities2.add("Barcelona");
        cities2.add("Berlin");
        cities2.add("Amsterdam");
        cities2.add("Dublin");
        cities2.add("Hamburg");
        cities2.add("Helsinki");
        cities2.add("Istanbul");
        cities2.add("Katowice");
        cities2.add("Kiev");
        cities2.add("Kazan");
        cities2.add("Lisbon");
        cities2.add("Liverpool");
        cities2.add("Madrid");
        cities2.add("Manchester");
        cities2.add("Milan");
        cities2.add("Naples");
        cities2.add("Odessa");
        cities2.add("Paris");
        cities2.add("Porto");
        cities2.add("Prague");
        cities2.add("Rotterdam");
        cities2.add("Sofia");
        cities2.add("Samara");
        cities2.add("Valencia");
        cities2.add("Warsaw");
        cities2.add("Ufa");
        return cities2;
    }

    private void getWeater() throws JSONException, InterruptedException {


        final List<String> cities2 = cityAdder();

        //A következőkben elindítok két szálat.
        //Az elsőben(thread1) a szál elindul, majd meg áll. Addig fog várni amíg mind a 30 város adatait le nem kértük.
            //Ha ez megtörtént utána kap egy notify()-t és a szál fut tovább.
            //Majd létrehozok egy adaptert a cities listával, amiben bennek vannak a már leíkért adatok és végül beállítom a recycleviewnak az adatpert.

        Thread thread1 = new Thread() {
            public void run() {
                synchronized (cities) {
                    try {
                        cities.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    adapter = new ProductAdapter(main, cities);  //Az adapterre azért van szükségünk, hogy összekössük az adatunkat(városok időjárásainak részléteit) a recycleview-val
                    main.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }
            }
        };
        thread1.start();

        final int p = 3; //progresbar időegysége

        //A másodikban(thread2) a száll elindítja mind a 30 városhoz a kívánt lekérdezést.
            //Majd ha ez megtörtént notify()-t küld a thread1-nek.

        Thread thread2 = new Thread() {
            public void run() {
                for (int i = 0; i < cities2.size(); i++) {
                    String url = "http://api.openweathermap.org/data/2.5/weather?q="; //Kell egy URL amiről majd megakarja kapni az adatokat
                    url += cities2.get(i); //melyik várost kérdezze le
                    url += "&appid=9fa7576719e619eafafa48618af939a7"; //appid key amit a honlapról kaptam
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest //Ez egy olyan Post/Get folyamat ahol egy JSONObjectet várok és fogok visszakapni
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) { //A szerver válasza
                                    try {
                                        JSONObject main_o = response.getJSONObject("main"); //A visszakapott JSONObject-ből pedig kiszedem a strukturált adatokat, városnév,hőmérséklet,szél,felhők
                                        JSONObject wind_o = response.getJSONObject("wind");
                                        JSONObject clouds_o = response.getJSONObject("clouds");
                                        String temp = String.valueOf(main_o.getDouble("temp"));
                                        String temp_min = String.valueOf(main_o.getDouble("temp_min"));
                                        String temp_max = String.valueOf(main_o.getDouble("temp_max"));
                                        String wind = String.valueOf(wind_o.getDouble("speed"));
                                        String clouds = String.valueOf(clouds_o.getDouble("all"));
                                        String city = response.getString("name");

                                        synchronized (cities){
                                            cities.add(new City(city, temp, temp_min, temp_max, wind, clouds)); //hozzáadom a listához az új várost és a hozzátartozó adatokat
                                            main.runOnUiThread(new Runnable() { //Külön a UIThreaden frissítem a progressbart
                                                @Override
                                                public void run() {
                                                    t+=p;
                                                    progressBar.setProgress(t);
                                                }
                                            });
                                            if(cities.size()==cities2.size()){ //Hogyha az összes város és a hozzátartozó adatok lejöttek akkor notify()-t küldök az első szállnak, hogy a megjelenítés elinduljon
                                                progressBar.setVisibility(View.GONE); //progresbar eltűnik
                                                edt.setVisibility(View.VISIBLE);//edittext és a recycleview jelenjen meg
                                                recyclerView.setVisibility(View.VISIBLE);
                                                cities.notify();
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });
                    RequestQueue queue = Volley.newRequestQueue(context); //A RequestQueue a lekérdezéshez és a válaszok feldolgozásához külön szálakat használ
                    queue.add(jsonObjectRequest); //A JSONObjectet belerakjuk ebbe a queueba.

                }
            }
        };
        thread2.start();

    }



}
