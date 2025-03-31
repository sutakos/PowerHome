package iut.dam.powerhome.entities;

import java.util.Date;

public class Booking {

    public int id;
    public int order;
    public Date bookedAt;
    public Appliance appliance;
    public TimeSlot timeSlot;

    public Booking() {
    }

    public Booking(int id, int order, Date bookedAt) {
        this.id = id;
        this.order = order;
        this.bookedAt = bookedAt;
    }

    public Booking(Appliance appliance, TimeSlot slot) {
        this.appliance = appliance;
        this.timeSlot=slot;
    }

    public Appliance getAppliance() {
        return appliance;
    }
}
