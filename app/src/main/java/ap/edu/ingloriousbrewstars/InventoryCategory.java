package ap.edu.ingloriousbrewstars;

/**
 * Created by Media Markt on 9/01/2016.
 */
public class InventoryCategory {

    private String catName;
    private String extraBoxes;
    private String flessen;

    public InventoryCategory(String catName, String extraBoxes, String flessen){
        this.catName = catName;
        this.extraBoxes = extraBoxes;
        this.flessen = flessen;
    }

    public InventoryCategory(){

    }

    public String getCatName() {
        return catName;
    }

    public String getExtraBoxes() {
        return extraBoxes;
    }

    public String getFlessen() {
        return flessen;
    }

}