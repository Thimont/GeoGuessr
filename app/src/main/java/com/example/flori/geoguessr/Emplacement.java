package com.example.flori.geoguessr;

import com.google.android.gms.maps.model.LatLng;

public class Emplacement {
    private String name;
    private LatLng min;
    private LatLng max;

    Emplacement(String name, LatLng min, LatLng max) {
        this.name = name;
        this.min = min;
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getMin() {
        return min;
    }

    public void setMin(LatLng min) {
        this.min = min;
    }

    public LatLng getMax() {
        return max;
    }

    public void setMax(LatLng max) {
        this.max = max;
    }
}
