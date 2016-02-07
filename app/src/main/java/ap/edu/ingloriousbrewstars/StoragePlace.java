package ap.edu.ingloriousbrewstars;

/**
 * Created by Sander Peeters on 11/26/2015.
 */
public class StoragePlace {


    private String name;
    private String address;
    private String city;
    private String zip;


    public StoragePlace(String name, String address, String city, String zip) {

        this.name = name;
        this.address = address;
        this.city = city;
        this.zip = zip;
    }

    public StoragePlace(){

    }
    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getZIP() {
        return zip;
    }
}
