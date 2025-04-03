package iut.dam.powerhome.fragments;

import static androidx.core.graphics.drawable.DrawableCompat.applyTheme;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import iut.dam.powerhome.R;
import iut.dam.powerhome.TimeSlotAdapter;
import iut.dam.powerhome.entities.Appliance;
import iut.dam.powerhome.entities.TimeSlot;

public class MonHabitatFragment extends Fragment {

    private CalendarView calendarView;
    private RecyclerView timeSlotsRecyclerView;
    private TextView nbTotalWattage;
    private TimeSlotAdapter timeSlotAdapter;
    private Date selectedDate;
    private List<Appliance> userAppliances = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mon_habitat, container, false);
        getActivity().setTitle("Mon habitat");
        // Initialisation des vues
        calendarView = view.findViewById(R.id.calendarView);
        nbTotalWattage = view.findViewById(R.id.nbTotalWattage);
        timeSlotsRecyclerView = view.findViewById(R.id.timeSlotsRecyclerView);
        timeSlotsRecyclerView.setVisibility(View.VISIBLE); // Rend visible dès le départ

        // Récupération des appareils
        if (getArguments() != null) {
            userAppliances = getArguments().getParcelableArrayList("appliances");
        }
        calculateTotalConsumption();
        // Initialisation de l'adaptateur
        timeSlotAdapter = new TimeSlotAdapter(getContext(), userAppliances);
        timeSlotsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        timeSlotsRecyclerView.setAdapter(timeSlotAdapter);

        // Configuration du calendrier
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            selectedDate = calendar.getTime();
            updateCalendar();
            setupTimeSlots();
            updateTimeSlotsForSelectedDate();

        });

        // Initialisation avec la date actuelle
        selectedDate = new Date();
        calendarView.setDate(selectedDate.getTime());
        updateCalendar();
        setupTimeSlots();
        updateTimeSlotsForSelectedDate();

        return view;
    }



    private void calculateTotalConsumption() {
        int totalWatt = 0;
            if (userAppliances != null) {
                for (Appliance app : userAppliances) {
                    totalWatt += app.getWattage();
                }
            }
        nbTotalWattage.setText(String.format(Locale.getDefault(), "%dW", totalWatt));
    }

    private void updateTimeSlotsForSelectedDate() {
        if (timeSlotAdapter != null && selectedDate != null) {
            timeSlotAdapter.setSelectedDate(selectedDate);
            timeSlotsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setupTimeSlots() {
        timeSlotAdapter = new TimeSlotAdapter(getContext(), userAppliances); // Passer la liste des appareils
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        timeSlotsRecyclerView.setLayoutManager(layoutManager);
        timeSlotsRecyclerView.setAdapter(timeSlotAdapter);
        timeSlotsRecyclerView.setVisibility(View.GONE);
    }

    private void updateCalendar() {
        timeSlotsRecyclerView.setVisibility(View.GONE);
    }













/*
    private void calculateTotalConsumption() {
        int totalWatt = 0;
        if (getArguments() != null) {
            userAppliances = getArguments().getParcelableArrayList("appliances");
            if (userAppliances != null) {
                for (Appliance app : userAppliances) {
                    totalWatt += app.getWattage();
                }
            }
        }
        nbTotalWattage.setText(String.format(Locale.getDefault(), "%dW", totalWatt));
    }

    private void updateSelectedDateText() {
        if (selectedDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRENCH);
            selectedDateText.setText(sdf.format(selectedDate));
        }
    }

    private void showApplianceSelectionDialog() {
        if (selectedTimeSlot == null || appliances == null) return;

        boolean[] checkedItems = new boolean[appliances.size()];
        List<Appliance> selectedAppliances = new ArrayList<>();
        String[] applianceNames = new String[appliances.size()];

        for (int i = 0; i < appliances.size(); i++) {
            applianceNames[i] = appliances.get(i).getName() + " (" + appliances.get(i).getWattage() + "W)";
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Sélectionnez les appareils")
                .setMultiChoiceItems(applianceNames, checkedItems, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        selectedAppliances.add(appliances.get(which));
                    } else {
                        selectedAppliances.remove(appliances.get(which));
                    }
                })
                .setPositiveButton("Confirmer", (dialog, which) -> {
                    if (!selectedAppliances.isEmpty()) {
                        timeSlotAdapter.makeReservation(selectedTimeSlot, selectedAppliances);
                    } else {
                        Toast.makeText(getContext(), "Veuillez sélectionner au moins un appareil", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    public void onTimeSlotSelected(TimeSlot timeSlot) {
        this.selectedTimeSlot = timeSlot;
        reserveButton.setEnabled(true);
        Toast.makeText(getContext(), "Créneau sélectionné: " + timeSlot.getFormattedTime(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReservationConfirmed() {
        reserveButton.setEnabled(false);
        selectedTimeSlot = null;
    }
    */

}