package com.example.weather;

import java.text.DecimalFormat;

public class City { //A városok és a hozzátartozó adatokat reprezentáló osztály
    private String name; //város neve
    private String temp; //hőmérséklet
    private String temp_min; //minimum hőmérséklet
    private String temp_max; //maximum hőmérséklet
    private String wind; //szél
    private String cloud; //felhők

    public City(String name,String temp,String temp_min, String temp_max, String wind, String cloud){
        this.name=name;
        DecimalFormat df = new DecimalFormat("#");
        this.temp=df.format(celsius(temp));
        this.temp_min = df.format(celsius(temp_min));
        this.temp_max = df.format(celsius(temp_max));
        this.wind = wind;
        this.cloud = cloud;
    }

    private double celsius(String temp){ //Kelvinből Celsiusfokot csinál
        return Math.round((double) (Double.parseDouble(temp) - 273.15F));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemp() {
        return temp;
    }

    public String getTemp_min() {
        return temp_min;
    }

    public String getTemp_max() {
        return temp_max;
    }

    public String getWind() {
        return wind;
    }

    public String getCloud() {
        return cloud;
    }


}

