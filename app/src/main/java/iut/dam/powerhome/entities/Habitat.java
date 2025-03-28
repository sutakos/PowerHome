package iut.dam.powerhome.entities;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Habitat {

    public int id;
    public int floor;
    public double area;
    public User user;
    public String residentName;
    public List<Appliance> appliances;

    public Habitat() {
        appliances = new ArrayList<>();
    }

    public Habitat(int id, int floor, double area) {
        this.id = id;
        this.floor = floor;
        this.area = area;
        appliances = new ArrayList<>();

    }

    public Habitat(int id, String residentName, int floor, double area, List<Appliance> appliances) {
        this.id = id;
        this.residentName = residentName;
        this.floor = floor;
        this.area = area;
        this.appliances = appliances;

    }

    public static Habitat getFromJson(String json){
        Gson gson = new Gson();
        Habitat obj = gson.fromJson(json, Habitat.class);
        return obj;
    }
    public static List<Habitat> getListFromJson(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<List<Habitat>>(){}.getType();
        List<Habitat> list = gson.fromJson(json, type);
        return list;
    }

    public String getResidentName() {
        return residentName;
    }

    public int getFloor() {
        return floor;
    }

    public int nbEquipement() {
        return appliances.size();
    }

    public List getAppliances() {
        return appliances;
    }

    public Appliance getOneAppliance(int i){
        return appliances.get(i);
    }
}
