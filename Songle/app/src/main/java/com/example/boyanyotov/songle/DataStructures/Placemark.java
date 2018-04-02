package com.example.boyanyotov.songle.DataStructures;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Boyan Yotov on 10/31/2017.
 */

public class Placemark {
    static private String TAG = Placemark.class.getSimpleName();
    private String name;
    private String description;
    private String styleUrl;
    private LatLng coordinates;

    public Placemark(String name, String description, String styleUrl, LatLng coordinates) {
        this.name = name;
        this.description = description;
        this.styleUrl = styleUrl;
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStyleUrl() {
        return styleUrl;
    }

    public void setStyleUrl(String styleUrl) {
        this.styleUrl = styleUrl;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }
}