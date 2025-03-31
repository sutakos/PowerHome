package iut.dam.powerhome.entities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import iut.dam.powerhome.entities.Booking;

public class TimeSlot {
    private String slotKey; // Clé unique = date + plage horaire
    private Date begin;
    private Date end;
    private int maxWattage;
    private List<Booking> bookings = new ArrayList<>();

    public TimeSlot(Date begin, Date end, int maxWattage) {
        this.begin = begin;
        this.end = end;
        this.maxWattage = maxWattage;
        this.slotKey = generateSlotKey(begin, end);
    }


    private String generateSlotKey(Date start, Date end) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd-HHmm", Locale.FRENCH);
        return fmt.format(start) + "_" + fmt.format(end);
    }

    public String getSlotKey(){
        return slotKey;
    }

    public int getUsedWattage() {
        System.out.println("watt utilisé : " + bookings.stream().mapToInt(b -> b.appliance.getWattage()).sum());
        for(Booking b : bookings){
            System.out.println("liste de booking : " + b.appliance);
        }
        return bookings.stream().mapToInt(b -> b.appliance.getWattage()).sum();
    }
    public int getAvailableWattage() {
        return maxWattage - getUsedWattage();
    }
    public double getUsagePercentage() {
        return (double)getUsedWattage() / maxWattage * 100;
    }

    public List<Booking> getBookings() {
        return bookings;
    }
    public void setBookings(List<Booking> b){
        this.bookings = b;
    }
    public Date getBegin() {
        return begin;
    }

    public Date getEnd() {
        return end;
    }

    public int getMaxWattage() {
        return maxWattage;
    }

    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(begin) + " - " + sdf.format(end);
    }
}