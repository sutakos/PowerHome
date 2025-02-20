package iut.dam.powerhome;

public class Appliance {
    private int id;
    private String name;

    public String getRef() {
        return ref;
    }

    private String ref;
    private int wattage;

    public Appliance(int id, String name, String ref, int wattage) {
        this.id = id;
        this.name = name;
        this.ref = ref;
        this.wattage = wattage;
    }

    public int getId() {
        return id;
    }
}
