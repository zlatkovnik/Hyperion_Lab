package ime.mosis.elfak.myplaces;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditMyPlaceActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_place);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button finsihedButton =(Button)findViewById(R.id.editmyplace_finished_button);
        finsihedButton.setOnClickListener(this);
        Button cancelButton =(Button)findViewById(R.id.editmyplace_cancel_button);
        cancelButton.setOnClickListener(this);


    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.editmyplace_finished_button: {
                EditText etName = findViewById(R.id.editmyplace_name_edit);
                String name = etName.getText().toString();
                EditText etDesc = findViewById(R.id.editmyplace_desc_edit);
                String desc = etDesc.getText().toString();
                MyPlace place = new MyPlace(name, desc);
                MyPlacesData.getInstance().addNewPlace(place);
                setResult(Activity.RESULT_OK);
                finish();
                break;
            }
            case R.id.editmyplace_cancel_button: {
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_my_place, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.show_map_item)
        {
            Toast.makeText(this, "Show Map!", Toast.LENGTH_SHORT).show();
        }else if(id==R.id.my_places_list_item){
            Intent i=new Intent(this, MyPlacesList.class);
            startActivity(i);
        }else if(id==R.id.about_item){
            Intent i=new Intent(this,About.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}