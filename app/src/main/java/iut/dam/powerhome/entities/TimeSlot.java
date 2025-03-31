package iut.dam.powerhome.entities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimeSlot {

    private String slotKey;
    private int id;
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

    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.FRENCH);
        return sdf.format(begin) + " - " + sdf.format(end);
    }

    public int getUsedWattage() {
        int total = 0;
        for (Booking booking : bookings) {
            total += booking.getAppliance().getWattage();
        }
        return total;
    }

    public int getAvailableWattage() {
        return maxWattage - getUsedWattage();
    }

    public double getUsagePercentage() {
        if (maxWattage == 0) return 0;
        return ((double) getUsedWattage() / maxWattage) * 100;
    }

    // Getters et Setters
    public int getId() { return id; }
    public Date getBegin() { return begin; }
    public Date getEnd() { return end; }
    public int getMaxWattage() { return maxWattage; }
    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public String getSlotKey() {
        return slotKey;
    }
}