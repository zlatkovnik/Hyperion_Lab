package ime.mosis.elfak.myplaces;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEvent;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class MyPlacesMapsActivity extends AppCompatActivity {
    MapView map = null;
    IMapController mapController = null;
    static int NEW_PLACE=1;
    static int EDIT_PLACE=2;

    static final int PERMISSION_ACCESS_FINE_LOCATION = 1;


    public static final int SHOW_MAP = 0;
    public static final int CENTER_PLACE_ON_MAP = 1;
    public static final int SELECT_COORDINATES = 2;

    private int state = 0;
    private boolean selCoorsEnabled = false;
    private GeoPoint placeLoc;

    private ItemizedIconOverlay<OverlayItem> myPlacesOverlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            Intent mapIntent = getIntent();
            Bundle mapBundle = mapIntent.getExtras();
            if(mapBundle != null){
                state = mapBundle.getInt("state");
                if(state == CENTER_PLACE_ON_MAP){
                    String placeLat = mapBundle.getString("lat");
                    String placeLon = mapBundle.getString("lon");
                    placeLoc = new GeoPoint(Double.parseDouble(placeLat), Double.parseDouble((placeLon)));
                }
            }
        } catch(Exception e){
            Log.d("Error", "Error reading state");
        }
        setContentView(R.layout.activity_my_places_maps);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        if(state != SELECT_COORDINATES) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MyPlacesMapsActivity.this, EditMyPlaceActivity.class);
                    startActivityForResult(i, NEW_PLACE);
                }
            });
        } else {
            ViewGroup layout = (ViewGroup)fab.getParent();
            if(layout != null){
                layout.removeView(fab);
            }
        }

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, PERMISSION_ACCESS_FINE_LOCATION);
        } else {
//            setMyLocationOverlay();
//            setOnMapClickOverlay();
            setupMap();
        }

        mapController = map.getController();
        if(mapController != null){
            mapController.setZoom(15.0);
            GeoPoint startPoint = new GeoPoint(43.3209, 21.8958);
            mapController.setCenter(startPoint);
        }
    }


    private void setMyLocationOverlay(){
        MyLocationNewOverlay myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), map);
        myLocationOverlay.enableMyLocation();
        map.getOverlays().add(myLocationOverlay);
        mapController = map.getController();
        if(mapController != null){
            mapController.setZoom(15.0);
            myLocationOverlay.enableFollowLocation();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            case PERMISSION_ACCESS_FINE_LOCATION: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    setMyLocationOverlay();
//                    setOnMapClickOverlay();
                    setupMap();
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(state == SELECT_COORDINATES && !selCoorsEnabled){
            menu.add(0, 1, 1, "SelectCoordinates");
            menu.add(0, 2, 2, "Cancel");
        }
        getMenuInflater().inflate(R.menu.menu_my_places_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(state == SELECT_COORDINATES && !selCoorsEnabled){
            if(id == 1){
                selCoorsEnabled = true;
                Toast.makeText(this, "Select Coordinates", Toast.LENGTH_SHORT).show();
            } else if(id == 2){
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        } else {
            if(id==R.id.new_place_item) {
                Toast.makeText(this, "New Place!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, EditMyPlaceActivity.class);
                startActivityForResult(i, NEW_PLACE);
            } else if(id==R.id.about_item){
                Intent i=new Intent(this, About.class);
                startActivity(i);
            } else if (id == android.R.id.home){
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void onResume(){
        super.onResume();
        map.onResume();
    }

    public void onPause(){
        super.onPause();
        map.onPause();
    }

    private void setOnMapClickOverlay(){
        MapEventsReceiver mReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                if(state == SELECT_COORDINATES && selCoorsEnabled){
                    String lon=Double.toString(p.getLongitude());
                    String lat=Double.toString(p.getLatitude());
                    Intent locationIntent = new Intent();
                    locationIntent.putExtra("lon",lon);
                    locationIntent.putExtra("lat",lat);
                    setResult(Activity.RESULT_OK, locationIntent);
                    finish();
                }
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        MapEventsOverlay OverlayEvents= new MapEventsOverlay(mReceiver);
        map.getOverlays().add(OverlayEvents);
    }

    private void setCenterPlaceOnMap(){
        mapController = map.getController();
        if(mapController != null){
            mapController.setZoom(15.0);
            mapController.animateTo(placeLoc);
        }
    }

    private void setupMap(){
        switch(state){
            case SHOW_MAP:
                setMyLocationOverlay();
                break;
            case SELECT_COORDINATES:
                mapController = map.getController();
                if(mapController != null){
                    mapController.setZoom(15.0);
                    mapController.setCenter(new GeoPoint(43.3209, 21.8958));
                }
                setOnMapClickOverlay();
                break;
            case CENTER_PLACE_ON_MAP:
            default:
                setCenterPlaceOnMap();
                break;
        }
        showMyPlaces();
    }

    private void showMyPlaces(){
        if(myPlacesOverlay != null){
            this.map.getOverlays().remove(myPlacesOverlay);
        }
        final ArrayList<OverlayItem> items = new ArrayList<>();
        for(int i = 0; i < MyPlacesData.getInstance().getMyPlaces().size(); i++){
            MyPlace myPlace = MyPlacesData.getInstance().getMyPlaces().get(i);
            OverlayItem item = new OverlayItem(myPlace.name, myPlace.description,
                    new GeoPoint(Double.parseDouble(myPlace.latitude), Double.parseDouble(myPlace.longitude)));
            item.setMarker(this.getResources().getDrawable(R.drawable.pin));
            items.add(item);
        }
        myPlacesOverlay = new ItemizedIconOverlay<>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(int index, OverlayItem item) {
                        Intent i = new Intent(MyPlacesMapsActivity.this, ViewMyPlacesActivity.class);
                        i.putExtra("position", index);
                        startActivity(i);
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(int index, OverlayItem item) {
                        Intent i = new Intent(MyPlacesMapsActivity.this, EditMyPlaceActivity.class);
                        i.putExtra("position", index);
                        startActivityForResult(i, EDIT_PLACE);
                        return true;
                    }
                }, getApplicationContext());
        map.getOverlays().add(myPlacesOverlay);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
//            setList();
        }
    }
}















