package ime.mosis.elfak.myplaces;

public class MyPlace {
    private String name;
    private String description;
    private String longitude;
    private String latitude;
    private int ID;

    public MyPlace(String name) {
        this.name = name;
        this.description="";
    }

    public MyPlace(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLongitude() {return longitude;}
    public String getLatitude() {return latitude;}

    public void setLongitude(String longitude)
    { this.longitude=longitude;}

    public void setLatitude(String latitude)
    {
        this.latitude=latitude;
    }

    public int getID() {
        return ID;
    }
    public void setID(int ID){
        this.ID=ID;
    }

    @Override
    public String toString() {
        return name ;
    }
}
