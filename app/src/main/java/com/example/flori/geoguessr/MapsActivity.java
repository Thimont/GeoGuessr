package com.example.flori.geoguessr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements
        GoogleMap.OnMarkerDragListener,
        StreetViewPanorama.OnStreetViewPanoramaChangeListener,
        GoogleMap.OnMapClickListener,
        OnMapReadyCallback, CheckStatusStreetViewTask.AsyncResponse {

    private static final String MARKER_POSITION_KEY = "MarkerPosition";

    private GoogleMap mMap;
    private static LatLng RANDOMLOC = null;
    private static float EARTHICIRC = 20037.5f; //moitie de la circonference de la Terre
    private static int newInt = -1;
    Random random = new Random();

    private int count = 0;
    private float score=0;

    private StreetViewPanorama mStreetViewPanorama;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

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

        //mise a jour des coordonnees
        RANDOMLOC = this.getRANDOMLOC(newInt);

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
    }

    @Override
    public void onStreetViewPanoramaChange(StreetViewPanoramaLocation location) {
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
        //PLACEMENT DU MARQUEUR PAR LE JOUEUR
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));
        Location correctLocation = new Location(LocationManager.GPS_PROVIDER);
        Location proposedLocation = new Location(LocationManager.GPS_PROVIDER);

        correctLocation.setLatitude(RANDOMLOC.latitude);
        correctLocation.setLongitude(RANDOMLOC.longitude);

        proposedLocation.setLatitude(latLng.latitude);
        proposedLocation.setLongitude(latLng.longitude);

        //Trait entre la proposition et la vraie localisation
        Polyline polyline = mMap.addPolyline(new PolylineOptions()
                .add(latLng, RANDOMLOC)
                .width(5)
                .color(Color.RED)
        );

        System.out.println("Correcte : " + correctLocation.toString());
        System.out.println("Proposee : " + proposedLocation.toString());

        //CALCUL DE DISTANCE AU POINT REEL
        final float distance = correctLocation.distanceTo(proposedLocation)/1000;

        //CALCUL DU SCORE
        float tempScore = (1-distance/EARTHICIRC)*100;

        score += tempScore;

        if (count < 4) {
            AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
            alertDialog.setTitle("Reponse");
            alertDialog.setMessage("Score : " + tempScore);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            count ++;
                                RANDOMLOC = MapsActivity.this.getRANDOMLOC(newInt);
                                mStreetViewPanorama.setPosition(RANDOMLOC);
                        }
                    });
            alertDialog.show();
        } else if (count == 4) { //affichage de la boite de dialogue recapitulative apres la derniere question
            AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
            alertDialog.setTitle("Reponse");
            alertDialog.setMessage("Score : " + tempScore);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
                            alertDialog.setTitle("Fin de partie");
                            alertDialog.setMessage("Score final : " + score + "/500");
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Score score = new Score(MapsActivity.this.getScore(), String.valueOf(newInt), Calendar.getInstance().getTime());
                                            score.save();
                                            finish();
                                        }
                                    });
                            alertDialog.show();
                        }
                    });
            alertDialog.show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Listener pour permettre de cliquer
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void processFinish(String output) {

    }

    public LatLng getRANDOMLOC(int newInt) {
        ArrayList<Emplacement> emplacements = new ArrayList<>();
        Emplacement emplacement = new Emplacement();

        if (newInt==1) { //NIVEAU FACILE
            emplacements.add(new Emplacement("PARIS", new LatLng(48.842484, 2.308891), new LatLng(48.875257, 2.373030)));
            emplacements.add(new Emplacement("SHIBUYA", new LatLng(35.655178, 139.693350), new LatLng(35.661698, 139.706911)));
            emplacements.add(new Emplacement("BROOKLYN", new LatLng(40.621877, -73.993299), new LatLng(40.671253, -73.901947)));
            emplacements.add(new Emplacement("LOSANGELES", new LatLng(34.020006, -118.317714), new LatLng(34.062834, -118.251309)));
            emplacements.add(new Emplacement("CAIRE", new LatLng(30.032373, 31.236823), new LatLng(30.049777, 31.267779)));
            emplacements.add(new Emplacement("RIO", new LatLng(-22.885595, -43.372476), new LatLng(-22.860194, -43.342335)));
            emplacements.add(new Emplacement("MOSCOU", new LatLng(55.725238, 37.531937), new LatLng(55.783830, 37.692568)));
            emplacements.add(new Emplacement("BERLIN", new LatLng(52.506065, 13.362684), new LatLng(52.534836, 13.426134)));
            emplacements.add(new Emplacement("TORONTO", new LatLng(43.651314, -79.526931), new LatLng(43.816441, -79.273541)));
            emplacements.add(new Emplacement("PEKIN", new LatLng(39.877378, 116.351716), new LatLng(39.933757, 116.430739)));
        } else if (newInt == 2) { //NIVEAU MOYEN
            emplacements.add(new Emplacement("ALPES", new LatLng(45.870546, 6.623008), new LatLng(45.948327, 6.742666)));
            emplacements.add(new Emplacement("JOHANNESBURG", new LatLng(-26.246161, 27.993520), new LatLng(-26.165798, 28.090744)));
            emplacements.add(new Emplacement("MEXICO", new LatLng(19.384504, -99.198311), new LatLng(19.467857, -99.077998)));
            emplacements.add(new Emplacement("SYDNEY", new LatLng(-33.870925, 151.194790), new LatLng(-33.831131, 151.240622)));
            emplacements.add(new Emplacement("MONREAL", new LatLng(45.438776, -73.915208), new LatLng(45.604267, -73.436514)));
            emplacements.add(new Emplacement("ATHENES", new LatLng(37.963584, 23.709136), new LatLng(37.982146, 23.733872)));
            emplacements.add(new Emplacement("STOCKHOLM", new LatLng(59.304380, 17.987801), new LatLng(59.348203, 18.106435)));
            emplacements.add(new Emplacement("SAN JUAN", new LatLng(18.377492, -66.118215), new LatLng(18.439622, -66.046915)));
            emplacements.add(new Emplacement("BORDEAUX", new LatLng(44.830102, -0.592451), new LatLng(44.844982, -0.571400)));
            emplacements.add(new Emplacement("TOULOUSE", new LatLng(43.594421, 1.427528), new LatLng(43.611680, 1.457513)));
        } else if (newInt == 3) { //NIVEAU DIFFICILE
            emplacements.add(new Emplacement("CHRISTCHURCH", new LatLng(-43.555253, 172.593983), new LatLng(-43.520499, 172.644802)));
            emplacements.add(new Emplacement("PHNOM PENH", new LatLng(11.536526, 104.892962), new LatLng(11.564325, 104.927802)));
            emplacements.add(new Emplacement("CHAMPAGNE", new LatLng(48.388639, 2.788194), new LatLng(48.400690, 2.805100)));
            emplacements.add(new Emplacement("ANAKARA", new LatLng(39.919730, 32.833710), new LatLng(39.949423, 32.878804)));
            emplacements.add(new Emplacement("JERUSALEM", new LatLng(31.761976, 35.207480), new LatLng(31.771024, 35.221041)));
            emplacements.add(new Emplacement("SEOUL", new LatLng(37.561436, 126.979750), new LatLng(37.589187, 127.012506)));
            emplacements.add(new Emplacement("TROYES", new LatLng(48.289847, 4.066956), new LatLng(48.302222, 4.082279)));
            emplacements.add(new Emplacement("MADERE", new LatLng(32.643931, -16.925938), new LatLng(32.660770, -16.910331)));
            emplacements.add(new Emplacement("LAOS", new LatLng(17.964338, 102.601165), new LatLng(17.987968, 102.625624)));
            emplacements.add(new Emplacement("DUBAI", new LatLng(25.184580, 55.260648), new LatLng(25.200156, 55.279565)));
        }
        //RECUPERATION DE LA ZONE
        int index = random.nextInt(emplacements.size());
        emplacement = emplacements.get(index);

        LatLng MIN_COORD = emplacement.getMin();
        LatLng MAX_COORD = emplacement.getMax();

        double randomLat = 0.0000;
        double randomLng = 0.0000;

        String status = "KO";

        //TIRAGE DES COORDONNEES
        while (!status.equals("OK")) {
            randomLat = MIN_COORD.latitude + (MAX_COORD.latitude - MIN_COORD.latitude) * random.nextDouble();
            randomLng = MAX_COORD.longitude + (MAX_COORD.longitude - MIN_COORD.longitude) * random.nextDouble();
            try {
                // UTILISATION D'UNE CLASSE ASYNCHRONE POUR FAIRE LA VERIFICATION D'UNE STREET VIEW SUR LES COORDONNEES
                status = new CheckStatusStreetViewTask(this).execute(new URL("https://maps.googleapis.com/maps/api/streetview/metadata?size=600x300&location="+randomLat+","+randomLng+"&fov=90&heading=235&pitch=106&key=AIzaSyB0jWScXMng8iCBifr61j7ZIa1Kd-9r3hQ")).get();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return new LatLng(randomLat, randomLng);
    }

    public float getScore() {
        return score;
    }
}
