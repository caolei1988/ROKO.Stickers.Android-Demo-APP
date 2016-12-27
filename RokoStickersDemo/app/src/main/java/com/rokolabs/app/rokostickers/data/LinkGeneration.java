package com.rokolabs.app.rokostickers.data;

import com.google.gson.Gson;

/**
 * Created by mist on 07.04.16.
 */
public class LinkGeneration {
    public String apiStatusCode;
    public Data data;
    public static LinkGeneration setProperty(String st){
        LinkGeneration item = new Gson().fromJson(st, LinkGeneration.class);;

        return item;
    }
    public class Data{
        public String link;
        public String objectId;
    }
}
