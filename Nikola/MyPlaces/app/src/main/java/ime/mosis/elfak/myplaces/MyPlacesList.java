package ime.mosis.elfak.myplaces;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyPlacesList extends AppCompatActivity {

    ArrayList<String> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MyPlacesList.this, EditMyPlaceActivity.class);
                startActivityForResult(i, NEW_PLACE);
            }
        });


//        places=new ArrayList<String>();
//        places.add("Tvrdjava");
//        places.add("Cair");
//        places.add("Park Svetog Save");
//        places.add("Trg Kralja Milana");
        ListView myPlacesList = (ListView) findViewById(R.id.my_places_list_item);
        myPlacesList.setAdapter(new ArrayAdapter<MyPlace>(this, android.R.layout.simple_list_item_1, MyPlacesData.getInstance().getMyPlaces()));

        myPlacesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //MyPlace place = (MyPlace)adapterView.getAdapter().getItem(i);
                //Toast.makeText(getApplicationContext(),place.getName()+"selected",Toast.LENGTH_SHORT).show();
                Bundle positionBundle = new Bundle();
                positionBundle.putInt("position", i);
                Intent intent = new Intent(MyPlacesList.this, ViewMyPlacesActivity.class);
                intent.putExtras(positionBundle);
                startActivity(intent);

            }
        });
        myPlacesList.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                MyPlace place = MyPlacesData.getInstance().getPlace(info.position);
                menu.setHeaderTitle(place.name);
                menu.add(0, 1, 1, "View place");
                menu.add(0, 2, 2, "Edit place");
                menu.add(0,3,3,"Delete place");
                menu.add(0,4,4,"Show on map");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_my_places_list, menu);
        return true;
    }

    static int NEW_PLACE = 1;
    static int EDIT_PLACE = 2;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.show_map_item) {
            Toast.makeText(this, "Show Map!", Toast.LENGTH_SHORT).show();
            Intent i= new Intent(this, MyPlacesMapsActivity.class);
            startActivity(i);
        } else if (id == R.id.new_place_item) {
            Toast.makeText(this, "New Place!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, EditMyPlaceActivity.class);
            startActivityForResult(i, NEW_PLACE);
        } else if (id == R.id.about_item) {
            Intent i = new Intent(this, About.class);
            startActivity(i);
        } else if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_PLACE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "New Place added", Toast.LENGTH_SHORT).show();
                ListView myPlacesList = (ListView) findViewById(R.id.my_places_list_item);
                myPlacesList.setAdapter(new ArrayAdapter<MyPlace>(this, android.R.layout.simple_list_item_1, MyPlacesData.getInstance().getMyPlaces()));
            }
        }
        else if(requestCode == EDIT_PLACE){
            if(resultCode == Activity.RESULT_OK){
                Toast.makeText(this, "Place edited", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Bundle positionBundle = new Bundle();
        positionBundle.putInt("position", info.position);
        Intent i = null;
        if (item.getItemId() == 1) {
            i = new Intent(this, ViewMyPlacesActivity.class);
            i.putExtras(positionBundle);
            startActivity(i);
        } else if (item.getItemId() == 2) {
            i = new Intent(this, EditMyPlaceActivity.class);
            i.putExtras(positionBundle);

            startActivity(i);
        } else if(item.getItemId()==3){
            MyPlacesData.getInstance().deletePlace(info.position);
            setList();
        } else if(item.getItemId()==4){
            i = new Intent(this, MyPlacesMapsActivity.class);
            i.putExtra("state", MyPlacesMapsActivity.CENTER_PLACE_ON_MAP);
            MyPlace place = MyPlacesData.getInstance().getPlace(info.position);
            i.putExtra("lat", place.latitude);
            i.putExtra("lon", place.longitude);
            startActivityForResult(i, 2);
        }
        return super.onContextItemSelected(item);
    }

    private void setList() {
        ListView myPlacesList=(ListView) findViewById(R.id.my_places_list_item);
        myPlacesList.setAdapter(new ArrayAdapter<MyPlace>(this, android.R.layout.simple_list_item_1,MyPlacesData.getInstance().getMyPlaces()));
    }
}