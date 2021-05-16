package ime.mosis.elfak.myplaces;

import java.util.ArrayList;

public class MyPlacesData {

    private ArrayList<MyPlace> myPlaces;
    private static class SingletonHolder{
        public  static final MyPlacesData instance= new MyPlacesData();
    }

    private MyPlacesData() {

        myPlaces= new ArrayList<MyPlace>();
        myPlaces.add(new MyPlace("Place A", "Opis A", "1", "1"));
        myPlaces.add(new MyPlace("Place B", "Opis B", "2", "1"));
        myPlaces.add(new MyPlace("Place C", "Opis C", "3", "1"));
        myPlaces.add(new MyPlace("Place D", "Opis D", "4", "1"));
        myPlaces.add(new MyPlace("Place E", "Opis #", "5", "1"));
    }

    public static MyPlacesData getInstance(){
        return SingletonHolder.instance;
    }

    public ArrayList<MyPlace> getMyPlaces() {
        return myPlaces;
    }

    public void addNewPlace(MyPlace place)
    {
        myPlaces.add(place);
    }
    public MyPlace getPlace(int index)
    {
        return myPlaces.get(index);
    }

    public void deletePlace(int index)
    {
        myPlaces.remove(index);
    }

}
