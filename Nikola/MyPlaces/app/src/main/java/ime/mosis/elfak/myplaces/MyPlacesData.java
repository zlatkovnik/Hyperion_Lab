package ime.mosis.elfak.myplaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyPlacesData {

    private ArrayList<MyPlace> myPlaces;
    private HashMap<String, Integer> myPlacesIndexMapping;
    private DatabaseReference database;
    private static final String FIREBASE_CHILD = "my-places";
    private static class SingletonHolder{
        public  static final MyPlacesData instance= new MyPlacesData();
    }

    private MyPlacesData() {

        myPlaces= new ArrayList<MyPlace>();
        myPlacesIndexMapping = new HashMap<String, Integer>();
        database = FirebaseDatabase.getInstance().getReference();
        database.child(FIREBASE_CHILD).addChildEventListener(childEventListener);
        database.child(FIREBASE_CHILD).addListenerForSingleValueEvent(parentEventListener);
    }

    ValueEventListener parentEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            String key = snapshot.getKey();
            if(!myPlacesIndexMapping.containsKey(key)){
                MyPlace myPlace = snapshot.getValue(MyPlace.class);
                myPlace.key = key;
                myPlaces.add(myPlace);
                myPlacesIndexMapping.put(key, myPlaces.size() - 1);
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            String key = snapshot.getKey();
            MyPlace myPlace = snapshot.getValue(MyPlace.class);
            myPlace.key = key;
            if(myPlacesIndexMapping.containsKey(key)){
                int index = myPlacesIndexMapping.get(key);
                myPlaces.set(index, myPlace);
            } else {
                myPlaces.add(myPlace);
                myPlacesIndexMapping.put(key, myPlaces.size() - 1);
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            String key = snapshot.getKey();
            if(myPlacesIndexMapping.containsKey(key)){
                int index = myPlacesIndexMapping.get(key);
                myPlaces.remove(index);
                recreateKeyIndexMapping();
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public static MyPlacesData getInstance(){
        return SingletonHolder.instance;
    }

    public ArrayList<MyPlace> getMyPlaces() {
        return myPlaces;
    }

    public void addNewPlace(MyPlace place)
    {
        String key = database.push().getKey();
        myPlaces.add(place);
        myPlacesIndexMapping.put(key, myPlaces.size() - 1);
        database.child(FIREBASE_CHILD).child(key).setValue(place);
        place.key = key;
    }
    public MyPlace getPlace(int index)
    {
        return myPlaces.get(index);
    }

    public void deletePlace(int index)
    {
        database.child(FIREBASE_CHILD).child(myPlaces.get(index).key).removeValue();
        myPlaces.remove(index);
        recreateKeyIndexMapping();
    }

    public void updatePlace(int index, String nme, String desc, String lng, String lat){
        MyPlace myPlace = myPlaces.get(index);
        myPlace.name = nme;
        myPlace.description = desc;
        myPlace.longitude = lng;
        myPlace.latitude = lat;
        database.child(FIREBASE_CHILD).child(myPlace.key).setValue(myPlace);
    }

    private void recreateKeyIndexMapping() {
        myPlacesIndexMapping.clear();
        for(int i = 0; i < myPlaces.size(); i++){
            myPlacesIndexMapping.put(myPlaces.get(i).key, i);
        }
    }

}
