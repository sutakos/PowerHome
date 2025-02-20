package iut.dam.powerhome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Habitant {
    private int id;
    private String residentName;
    private int floor;
    private double area;
    List<Appliance> appliances = new ArrayList<>();
    public Habitant(int id,String residentName,int floor,double area,Appliance[] app){
        this.id=id;
        this.residentName=residentName;
        this.floor=floor;
        this.area=area;
        appliances.addAll(Arrays.asList(app));
    }


    public int getId() {
        return id;
    }

    public String getResidentName() {
        return residentName;
    }

    public int getFloor() {
        return floor;
    }

    public double getArea() {
        return area;
    }

    public List<Appliance> getAppliances() {
        return appliances;
    }
    public Appliance getApp(int i){
        if(i>appliances.size())
            return null;
        return appliances.get(i);
    }
    public String nbEquipement(){
        return appliances.size()+" équipements";
    }
}
