package iut.dam.powerhome.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Locale;

import iut.dam.powerhome.R;
import iut.dam.powerhome.entities.Appliance;
import iut.dam.powerhome.entities.TimeSlot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonHabitatFragment} factory method to
 * create an instance of this fragment.
 */
public class MonHabitatFragment extends Fragment {

    private ArrayList<Appliance> appliances;
    private CalendarView calendrier;
    private String dateReservee;
    private Button btnReservee;
    private Appliance selectedAppliance;
    private TimeSlot selectedTimeSlot;
    private String url = "http://192.168.1.30/api/timeslots";

    public MonHabitatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Mon Habitat");
        View rootView = inflater.inflate(R.layout.fragment_mon_habitat, container, false);

        int totalWatt = 0;

        TextView wattage = rootView.findViewById(R.id.nbTotalWattage);

        if (getArguments() != null) {

            appliances = getArguments().getParcelableArrayList("appliances");

            for (Appliance app : appliances) {

                totalWatt += app.wattage;

            }

        }

        wattage.setText(String.valueOf(totalWatt) + "W");

        selectedAppliance = getActivity().getIntent().getParcelableExtra("appliance");

        calendrier = rootView.findViewById(R.id.calendrier);
        btnReservee = rootView.findViewById(R.id.button_reserver);

        calendrier.setOnDateChangeListener((view, year, month, dayOfMonth)->{
            dateReservee = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
        });

        btnReservee.setOnClickListener(v -> {
            if(dateReservee != null || selectedAppliance != null){
                reserverCreneau();
            } else {
                Toast.makeText(getContext(), "Sélectionnez une date ", Toast.LENGTH_SHORT).show();
                return;
            }
        });

        return rootView;
    }

    private void reserverCreneau() {
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("timeSlotId", selectedTimeSlot.id);
        jsonBody.addProperty("applianceId", selectedAppliance.id);
        jsonBody.addProperty("date", dateReservee);


        Ion.with(this)
                .load(url + "/reserver")
                .setJsonObjectBody(jsonBody)
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Erreur réseau : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        if (result != null && result.has("success") && result.get("success").getAsBoolean()) {
                            Toast.makeText(getContext(), "Réservation confirmée !", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = result.has("message") ? result.get("message").getAsString() : "Erreur inconnue";
                            Toast.makeText(getContext(), "Erreur : " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
    }
}