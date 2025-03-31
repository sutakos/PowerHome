package iut.dam.powerhome.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import iut.dam.powerhome.R;
import iut.dam.powerhome.entities.Appliance;
import iut.dam.powerhome.entities.Booking;
import iut.dam.powerhome.entities.TimeSlot;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder> {

    public interface OnTimeSlotClickListener {
        void onTimeSlotClick(TimeSlot timeSlot);
    }

    private static final String BASE_URL = "http://192.168.1.19/powerhome_server/";

    private Context context;
    private List<TimeSlot> timeSlots;
    private OnTimeSlotClickListener listener;
    private SharedPreferences sharedPreferences;
    private Date selectedDate;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.FRENCH);
    private SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRENCH);

    public TimeSlotAdapter(Context context, OnTimeSlotClickListener listener) {
        this.context = context;
        this.timeSlots = new ArrayList<>();
        this.listener = listener;
        this.sharedPreferences = context.getSharedPreferences("TimeSlotPrefs", Context.MODE_PRIVATE);
    }

    public void setSelectedDate(Date date) {
        this.selectedDate = date;
        fetchTimeSlotsForDate(date);
    }

    private void fetchTimeSlotsForDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);
        String formattedDate = dateFormat.format(date);

        Ion.with(context)
                .load(BASE_URL + "getTimeSlots.php")
                .setBodyParameter("date", formattedDate)
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (e != null) {
                        showError("Erreur réseau: " + e.getMessage());
                        return;
                    }

                    try {
                        if (result.get("status").getAsString().equals("success")) {
                            List<TimeSlot> slots = new ArrayList<>();
                            JsonArray slotsArray = result.get("time_slots").getAsJsonArray();

                            for (int i = 0; i < slotsArray.size(); i++) {
                                JsonObject slotJson = slotsArray.get(i).getAsJsonObject();

                                TimeSlot slot = new TimeSlot(
                                        slotJson.get("id").getAsInt(),
                                        dbDateFormat.parse(slotJson.get("begin").getAsString()),
                                        dbDateFormat.parse(slotJson.get("end").getAsString()),
                                        slotJson.get("max_wattage").getAsInt()
                                );
                                slots.add(slot);
                            }

                            this.timeSlots = slots;
                            fetchReservationsForDate(date);
                        } else {
                            showError(result.get("message").getAsString());
                        }
                    } catch (Exception ex) {
                        showError("Erreur de traitement des données");
                        Log.e("TimeSlotAdapter", "Parsing error", ex);
                    }
                });
    }

    private void fetchReservationsForDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);

        Ion.with(context)
                .load(BASE_URL + "getReservationsByDate.php")
                .setBodyParameter("date", dateFormat.format(date))
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (e != null) {
                        Log.e("TimeSlotAdapter", "Network error", e);
                        return;
                    }

                    try {
                        if (result.get("status").getAsString().equals("success")) {
                            JsonArray reservations = result.get("reservations").getAsJsonArray();

                            for (TimeSlot slot : timeSlots) {
                                slot.getBookings().clear();
                            }

                            for (int i = 0; i < reservations.size(); i++) {
                                JsonObject res = reservations.get(i).getAsJsonObject();
                                int slotId = res.get("time_slot_id").getAsInt();

                                for (TimeSlot slot : timeSlots) {
                                    if (slot.getId() == slotId) {
                                        JsonObject applianceJson = res.get("appliance").getAsJsonObject();
                                        Appliance appliance = new Appliance(
                                                applianceJson.get("id").getAsInt(),
                                                applianceJson.get("name").getAsString(),
                                                applianceJson.get("reference").getAsString(),
                                                applianceJson.get("wattage").getAsInt()
                                        );
                                        slot.getBookings().add(new Booking(appliance, slot));
                                        break;
                                    }
                                }
                            }
                            notifyDataSetChanged();
                        }
                    } catch (Exception ex) {
                        Log.e("TimeSlotAdapter", "Reservation parsing error", ex);
                    }
                });
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        TimeSlot timeSlot = timeSlots.get(position);

        String buttonText = String.format(Locale.getDefault(),
                "%s\n%d/%dW",
                timeSlot.getFormattedTime(),
                timeSlot.getUsedWattage(),
                timeSlot.getMaxWattage());

        holder.timeSlotButton.setText(buttonText);

        int colorRes = getAvailabilityColor(timeSlot);
        holder.timeSlotButton.setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
        );

        holder.timeSlotButton.setEnabled(timeSlot.getUsagePercentage() < 100);
        holder.timeSlotButton.setOnClickListener(v -> {
            if (timeSlot.getUsagePercentage() < 100) {
                if (listener != null) {
                    listener.onTimeSlotClick(timeSlot);
                } else {
                    showReservationDialog(timeSlot);
                }
            }
        });
    }

    private int getAvailabilityColor(TimeSlot timeSlot) {
        double usagePercentage = timeSlot.getUsagePercentage();
        if (usagePercentage >= 100) return R.color.red;
        if (usagePercentage > 70) return R.color.orange;
        return R.color.green;
    }

    private void showReservationDialog(TimeSlot timeSlot) {
        new AlertDialog.Builder(context)
                .setTitle("Confirmer la réservation")
                .setMessage("Réserver le créneau " + timeSlot.getFormattedTime() + "?")
                .setPositiveButton("Oui", (dialog, which) -> checkUserAndProceed(timeSlot))
                .setNegativeButton("Non", null)
                .show();
    }

    private void checkUserAndProceed(TimeSlot timeSlot) {
        SharedPreferences prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String email = prefs.getString("email", null);
        String password = prefs.getString("password", null);

        if (email != null && password != null) {
            fetchUserAppliances(timeSlot, email, password);
        } else {
            showError("Veuillez vous connecter");
        }
    }

    private void fetchUserAppliances(TimeSlot timeSlot, String email, String password) {
        Ion.with(context)
                .load(BASE_URL + "getUserAppliances.php")
                .setBodyParameter("email", email)
                .setBodyParameter("password", password)
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (e != null || result == null) {
                        showError("Erreur de connexion");
                        return;
                    }

                    try {
                        if (result.get("status").getAsString().equals("success")) {
                            List<Appliance> appliances = parseAppliances(result.get("appliances").getAsJsonArray());
                            showApplianceSelectionDialog(timeSlot, appliances);
                        } else {
                            showError(result.get("message").getAsString());
                        }
                    } catch (Exception ex) {
                        showError("Erreur de traitement");
                        Log.e("TimeSlotAdapter", "Appliance parsing error", ex);
                    }
                });
    }

    private List<Appliance> parseAppliances(JsonArray appliancesArray) {
        List<Appliance> appliances = new ArrayList<>();
        for (int i = 0; i < appliancesArray.size(); i++) {
            JsonObject app = appliancesArray.get(i).getAsJsonObject();
            appliances.add(new Appliance(
                    app.get("id").getAsInt(),
                    app.get("name").getAsString(),
                    app.get("reference").getAsString(),
                    app.get("wattage").getAsInt()
            ));
        }
        return appliances;
    }

    private void showApplianceSelectionDialog(TimeSlot timeSlot, List<Appliance> appliances) {
        if (appliances.isEmpty()) {
            showError("Aucun appareil disponible");
            return;
        }

        boolean[] checkedItems = new boolean[appliances.size()];
        List<Appliance> selectedAppliances = new ArrayList<>();
        String[] applianceNames = new String[appliances.size()];

        for (int i = 0; i < appliances.size(); i++) {
            applianceNames[i] = appliances.get(i).getName() + " (" + appliances.get(i).getWattage() + "W)";
        }

        new AlertDialog.Builder(context)
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
                        makeReservation(timeSlot, selectedAppliances);
                    } else {
                        showError("Veuillez sélectionner au moins un appareil");
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void makeReservation(TimeSlot timeSlot, List<Appliance> appliances) {
        int totalWattage = 0;
        for (Appliance appliance : appliances) {
            totalWattage += appliance.getWattage();
        }

        if (totalWattage > timeSlot.getAvailableWattage()) {
            showError("Capacité dépassée pour ce créneau");
            return;
        }

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("time_slot_id", timeSlot.getId());

        JsonArray appliancesArray = new JsonArray();
        for (Appliance appliance : appliances) {
            JsonObject appJson = new JsonObject();
            appJson.addProperty("id", appliance.getId());
            appJson.addProperty("wattage", appliance.getWattage());
            appliancesArray.add(appJson);
        }
        jsonBody.add("appliances", appliancesArray);

        SharedPreferences prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String email = prefs.getString("email", "");

        Ion.with(context)
                .load(BASE_URL + "addReservation.php")
                .setJsonObjectBody(jsonBody)
                .asJsonObject()
                .setCallback((e, result) -> {
                    if (e != null) {
                        showError("Erreur réseau");
                        return;
                    }

                    try {
                        if (result.get("status").getAsString().equals("success")) {
                            for (Appliance appliance : appliances) {
                                timeSlot.getBookings().add(new Booking(appliance, timeSlot));
                            }
                            notifyDataSetChanged();
                            Toast.makeText(context, "Réservation confirmée", Toast.LENGTH_SHORT).show();
                        } else {
                            showError(result.get("message").getAsString());
                        }
                    } catch (Exception ex) {
                        showError("Erreur de traitement");
                        Log.e("TimeSlotAdapter", "Reservation error", ex);
                    }
                });
    }

    private void showError(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    public void updateTimeSlots(List<TimeSlot> newTimeSlots) {
        this.timeSlots = newTimeSlots;
        notifyDataSetChanged();
    }

    static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        Button timeSlotButton;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            timeSlotButton = itemView.findViewById(R.id.timeSlotButton);
        }
    }
}