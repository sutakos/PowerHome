package iut.dam.powerhome.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import iut.dam.powerhome.R;
import iut.dam.powerhome.entities.Appliance;

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

        calendrier = rootView.findViewById(R.id.calendrier);
        btnReservee = rootView.findViewById(R.id.button_reserver);

        calendrier.setOnDateChangeListener((view, year, month, dayOfMonth)->{
            dateReservee = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
        });

        btnReservee.setOnClickListener(v -> {
            if(dateReservee != null){
                Toast.makeText(getContext(), "Vous avez réservé pour " + dateReservee, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Veuillez selectionner une date ", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}