package com.example.flori.geoguessr;

import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements
        GoogleMap.OnMarkerDragListener,
        StreetViewPanorama.OnStreetViewPanoramaChangeListener,
        GoogleMap.OnMapClickListener,
        OnMapReadyCallback, CheckStatusStreetViewTask.AsyncResponse {

    private static final String MARKER_POSITION_KEY = "MarkerPosition";

    private GoogleMap mMap;

    ArrayList<Emplacement> emplacements_faciles = new ArrayList<>();
    private static LatLng RANDOMLOC = null;
    Random random = new Random();

    // George St, Sydney
//    private static final LatLng SYDNEY = new LatLng(48.881756, 2.317638);

    private StreetViewPanorama mStreetViewPanorama;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        int newInt;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                newInt= -1;
            } else {
                newInt= extras.getInt("LEVEL");
            }
        } else {
            newInt= (int) savedInstanceState.getSerializable("LEVEL");
        }

        if (newInt==1) {
            emplacements_faciles.add(new Emplacement("PARIS", new LatLng(48.842484, 2.308891), new LatLng(48.875257, 2.373030)));
            emplacements_faciles.add(new Emplacement("SHIBUYA", new LatLng(35.655178, 139.693350), new LatLng(35.661698, 139.706911)));
            emplacements_faciles.add(new Emplacement("BROOKLYN", new LatLng(40.621877, -73.993299), new LatLng(40.671253, -73.901947)));
            emplacements_faciles.add(new Emplacement("LOSANGELES", new LatLng(34.020006, -118.317714), new LatLng(34.062834, -118.251309)));
            emplacements_faciles.add(new Emplacement("CAIRE", new LatLng(30.032373, 31.236823), new LatLng(30.049777, 31.267779)));
            emplacements_faciles.add(new Emplacement("RIO", new LatLng(-22.885595, -43.372476), new LatLng(-22.860194, -43.342335)));
            emplacements_faciles.add(new Emplacement("MOSCOU", new LatLng(55.725238, 37.531937), new LatLng(55.783830, 37.692568)));
            emplacements_faciles.add(new Emplacement("BERLIN", new LatLng(52.506065, 13.362684), new LatLng(52.534836, 13.426134)));
            emplacements_faciles.add(new Emplacement("TORONTO", new LatLng(43.651314, -79.526931), new LatLng(43.816441, -79.273541)));
            emplacements_faciles.add(new Emplacement("PEKIN", new LatLng(39.877378, 116.351716), new LatLng(39.933757, 116.430739)));
        }

        int index = random.nextInt(emplacements_faciles.size());
        Emplacement emplacement = emplacements_faciles.get(index);

        LatLng MIN_COORD = emplacement.getMin();
        LatLng MAX_COORD = emplacement.getMax();

        double randomLat = 0.0000;
        double randomLng = 0.0000;

        String status = "KO";

        while (!status.equals("OK")) {
            randomLat = MIN_COORD.latitude + (MAX_COORD.latitude - MIN_COORD.latitude) * random.nextDouble();
            randomLng = MAX_COORD.longitude + (MAX_COORD.longitude - MIN_COORD.longitude) * random.nextDouble();
            try {
                status = new CheckStatusStreetViewTask(this).execute(new URL("https://maps.googleapis.com/maps/api/streetview/metadata?size=600x300&location="+randomLat+","+randomLng+"&fov=90&heading=235&pitch=106&key=AIzaSyB0jWScXMng8iCBifr61j7ZIa1Kd-9r3hQ")).get();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        RANDOMLOC = new LatLng(randomLat, randomLng);

        final LatLng markerPosition;
        if (savedInstanceState == null) {
            markerPosition = RANDOMLOC;
        } else {
            markerPosition = savedInstanceState.getParcelable(MARKER_POSITION_KEY);
        }

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                        mStreetViewPanorama = panorama;
                        mStreetViewPanorama.setOnStreetViewPanoramaChangeListener(
                                MapsActivity.this);
                        // Only need to set the position once as the streetview fragment will maintain
                        // its state.
                        if (savedInstanceState == null) {
                            mStreetViewPanorama.setPosition(RANDOMLOC);
                        }
                    }
                });

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putParcelable(MARKER_POSITION_KEY, mMarker.getPosition());
    }

    @Override
    public void onStreetViewPanoramaChange(StreetViewPanoramaLocation location) {
        /*if (location != null) {
            mMarker.setPosition(location.position);
        }*/
        if (location != null && location.links != null) {
            // location is present
        } else {
            // location not available
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        mStreetViewPanorama.setPosition(marker.getPosition(), 150);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));
        Location correctLocation = new Location(LocationManager.GPS_PROVIDER);
        Location proposedLocation = new Location(LocationManager.GPS_PROVIDER);

        correctLocation.setLatitude(RANDOMLOC.latitude);
        correctLocation.setLongitude(RANDOMLOC.longitude);

        proposedLocation.setLatitude(latLng.latitude);
        proposedLocation.setLongitude(latLng.longitude);

        float distance = correctLocation.distanceTo(proposedLocation)/1000;

        Toast toast = Toast.makeText(getApplicationContext(), Float.toString(distance) + "km", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void processFinish(String output) {

    }
}
