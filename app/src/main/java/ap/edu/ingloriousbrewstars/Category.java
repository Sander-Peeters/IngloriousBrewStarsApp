package ap.edu.ingloriousbrewstars;

public class Category {

    private String name;
    private Integer maxBottles;
    private Boolean canBeHalfFull;

    public Category() {
        // necessary for Firebase's deserializer
    }

    public Category(String name, Integer maxBottles, Boolean canBeHalfFull) {
        this.name = name;
        this.maxBottles = maxBottles;
        this.canBeHalfFull = canBeHalfFull;
    }

    public String getName() {
        return name;
    }
    public Integer getMaxBottles() {
        return maxBottles;
    }
    public Boolean getCanBeHalfFull() {
        return canBeHalfFull;
    }

}
