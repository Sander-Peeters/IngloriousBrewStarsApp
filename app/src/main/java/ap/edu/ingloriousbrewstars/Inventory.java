package ap.edu.ingloriousbrewstars;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Created by Apo on 11/12/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Inventory {

    private String name;
    private String storage;
    private String totaalAantalExtraBoxes;
    private String totaalAantalFlessen;


    public  Inventory(){}

    public Inventory(String name, String storage, String boxes, String flessen, InventoryCategory products) {
        this.name = name;
        this.storage = storage;
        this.totaalAantalExtraBoxes = boxes;
        this.totaalAantalFlessen = flessen;

    }


    public String getName(){
        return name;
    }

    public String getStorage(){
        return storage;
    }

    public String getTotaalAantalExtraBoxes() {
        return totaalAantalExtraBoxes;
    }

    public String getTotaalAantalFlessen() {
        return totaalAantalFlessen;
    }


    @Override
    public String toString() {
        return  name + " " +   storage;
    }
}