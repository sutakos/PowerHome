package iut.dam.powerhome.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private TextView nbTotalWattage, selectedDateText;
    private Button reserveButton;
    private TimeSlotAdapter timeSlotAdapter;
    private List<TimeSlot> timeSlots = new ArrayList<>();
    private TimeSlot selectedTimeSlot;
    private Date selectedDate;
    private List<Appliance> appliances;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mon_habitat, container, false);
        getActivity().setTitle("Mon habitat");
        // Initialisation des vues
        calendarView = view.findViewById(R.id.calendarView);
        timeSlotsRecyclerView = view.findViewById(R.id.timeSlotsRecyclerView);
        nbTotalWattage = view.findViewById(R.id.nbTotalWattage);
        selectedDateText = view.findViewById(R.id.selectedDateText);
        reserveButton = view.findViewById(R.id.reserveButton);

        // Configuration du RecyclerView
        timeSlotAdapter = new TimeSlotAdapter(getContext(),appliances);
        timeSlotsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        timeSlotsRecyclerView.setAdapter(timeSlotAdapter);

        // Calcul de la consommation totale
        calculateTotalConsumption();

        // Gestion du calendrier
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            selectedDate = calendar.getTime();
            updateSelectedDateText();
            loadTimeSlotsForDate(selectedDate);
        });

        // Bouton de réservation
        reserveButton.setOnClickListener(v -> reserveTimeSlot());

        return view;
    }

    private void calculateTotalConsumption() {
        int totalWatt = 0;
        if (getArguments() != null) {
            appliances = getArguments().getParcelableArrayList("appliances");
            if (appliances != null) {
                for (Appliance app : appliances) {
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

    private void loadTimeSlotsForDate(Date date) {
        // Simulation de chargement de créneaux
        List<TimeSlot> slots = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        // Génération de créneaux factices (remplacer par appel API)
        for (int i = 0; i < 6; i++) {
            cal.set(Calendar.HOUR_OF_DAY, 8 + (i * 2));
            cal.set(Calendar.MINUTE, 0);
            Date start = cal.getTime();

            cal.set(Calendar.HOUR_OF_DAY, 10 + (i * 2));
            Date end = cal.getTime();

            slots.add(new TimeSlot(start, end, 2000));
        }

        timeSlotsRecyclerView.setVisibility(View.VISIBLE);
    }

    private void onTimeSlotSelected(TimeSlot timeSlot) {
        selectedTimeSlot = timeSlot;
        reserveButton.setEnabled(true);
        Toast.makeText(getContext(), "Créneau sélectionné: " + timeSlot.getFormattedTime(), Toast.LENGTH_SHORT).show();
    }

    private void reserveTimeSlot() {
        if (selectedTimeSlot != null && selectedDate != null) {
            // Implémenter la logique de réservation ici
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.FRENCH);
            String message = String.format(Locale.FRENCH,
                    "Réservation confirmée pour %s - %s",
                    timeFormat.format(selectedTimeSlot.getBegin()),
                    timeFormat.format(selectedTimeSlot.getEnd()));

            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            reserveButton.setEnabled(false);
        }
    }
}