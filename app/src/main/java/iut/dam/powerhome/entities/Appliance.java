package iut.dam.powerhome.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Appliance implements Parcelable {

    public int id;
    public String name;
    public String reference;
    public int wattage;
    public List<Booking> bookings;

    public Appliance() {
        bookings = new ArrayList<>();
    }

    public Appliance(int id, String name, String reference, int wattage) {
        this.id = id;
        this.name = name;
        this.reference = reference;
        this.wattage = wattage;
        this.bookings = new ArrayList<>();
    }

    // Constructeur pour Parcelable
    protected Appliance(Parcel in) {
        id = in.readInt();
        name = in.readString();
        reference = in.readString();
        wattage = in.readInt();
    }

    public static final Creator<Appliance> CREATOR = new Creator<Appliance>() {
        @Override
        public Appliance createFromParcel(Parcel in) {
            return new Appliance(in);
        }

        @Override
        public Appliance[] newArray(int size) {
            return new Appliance[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(reference);
        dest.writeInt(wattage);
    }

    // Getters
    public String getReference() {
        return reference;
    }

    public String getName() {
        return name;
    }
}