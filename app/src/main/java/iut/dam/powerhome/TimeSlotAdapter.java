package iut.dam.powerhome.adapters;

import android.annotation.SuppressLint;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private Context context;
    private List<TimeSlot> timeSlots = new ArrayList<>();
    private Date selectedDate;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.FRENCH);
    private SharedPreferences sharedPreferences;
    private List<Appliance> userAppliances;

    public TimeSlotAdapter(Context context, List<Appliance> userAppliances) {
        this.context = context;
        this.userAppliances = userAppliances;
        this.sharedPreferences = context.getSharedPreferences("TimeSlotPrefs", Context.MODE_PRIVATE);
        initTimeSlots();
    }

    private void initTimeSlots() {
        if (selectedDate == null) return;

        try {
            timeSlots.clear();

            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedDate);

            // génère les créneaux
            for (int i = 0; i < 12; i++) {
                Calendar startCal = (Calendar) cal.clone();
                startCal.set(Calendar.HOUR_OF_DAY, i * 2);
                startCal.set(Calendar.MINUTE, 0);
                Date start = startCal.getTime();

                Calendar endCal = (Calendar) cal.clone();
                endCal.set(Calendar.HOUR_OF_DAY, i * 2 + 2);
                endCal.set(Calendar.MINUTE, 0);
                Date end = endCal.getTime();

                timeSlots.add(new TimeSlot(start, end, 2000));
            }

            // charge les réservations déjà faites
            loadReservations();

            fetchReservationsForDate(selectedDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void loadReservations() {
        if (selectedDate == null) return;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);
        String dateKey = dateFormat.format(selectedDate);
        String reservationsJson = sharedPreferences.getString(dateKey, null);

        if (reservationsJson != null) {
            try {
                JSONObject reservationsData = new JSONObject(reservationsJson);
                JSONArray slotsArray = reservationsData.getJSONArray("slots");

                for (int i = 0; i < slotsArray.length(); i++) {
                    JSONObject slotJson = slotsArray.getJSONObject(i);
                    String slotKey = slotJson.getString("slot_key");

                    for (TimeSlot slot : timeSlots) {
                        if (slot.getSlotKey().equals(slotKey)) {
                            JSONArray bookingsArray = slotJson.getJSONArray("bookings");
                            List<Booking> bookings = new ArrayList<>();

                            for (int j = 0; j < bookingsArray.length(); j++) {
                                JSONObject bookingJson = bookingsArray.getJSONObject(j);
                                int applianceId = bookingJson.getInt("appliance_id");
                                Appliance appliance = findApplianceById(applianceId);

                                if (appliance != null) {
                                    Booking booking = new Booking();
                                    booking.appliance = appliance;
                                    booking.timeSlot = slot;
                                    bookings.add(booking);
                                }
                            }

                            slot.setBookings(bookings);
                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private Appliance findApplianceById(int id) {
        for (Appliance appliance : userAppliances) {
            if (appliance.getId() == id) {
                return appliance;
            }
        }
        return null;
    }

    public void setSelectedDate(Date date) {
        this.selectedDate = date;
        initTimeSlots();
        fetchReservationsForDate(selectedDate);
        notifyDataSetChanged();
    }
    private void saveReservations() {
        if (selectedDate == null || timeSlots.isEmpty()) return;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);
        String dateKey = dateFormat.format(selectedDate);

        JSONObject reservationsData = new JSONObject();
        try {
            JSONArray slotsArray = new JSONArray();

            for (TimeSlot slot : timeSlots) {
                JSONObject slotJson = new JSONObject();
                slotJson.put("slot_key", slot.getSlotKey());

                JSONArray bookingsArray = new JSONArray();
                for (Booking booking : slot.getBookings()) {
                    JSONObject bookingJson = new JSONObject();
                    bookingJson.put("appliance_id", booking.appliance.getId());
                    bookingsArray.put(bookingJson);
                }

                slotJson.put("bookings", bookingsArray);
                slotsArray.put(slotJson);
            }

            reservationsData.put("slots", slotsArray);
            sharedPreferences.edit()
                    .putString(dateKey, reservationsData.toString())
                    .apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void fetchReservationsForDate(Date date) {
        String url = "http://10.0.2.2/powerhome_server/addReservation.php";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);

        Ion.with(context)
                .load(url)
                .setBodyParameter("date", dateFormat.format(date))
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Toast.makeText(context, "Erreur réseau", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            JSONObject jsonResponse = new JSONObject(result);
                            if (jsonResponse.getString("status").equals("success")) {
                                JSONArray reservations = jsonResponse.getJSONArray("reservations");

                                for (TimeSlot slot : timeSlots) {
                                    slot.getBookings().clear();
                                }

                                for (int i = 0; i < reservations.length(); i++) {
                                    JSONObject res = reservations.getJSONObject(i);
                                    String slotKey = res.getString("slot_key"); // Modifié ici

                                    for (TimeSlot slot : timeSlots) {
                                        if (slot.getSlotKey().equals(slotKey)) {
                                            Appliance appliance = new Appliance(
                                                    res.getJSONObject("appliance").getInt("id"),
                                                    res.getJSONObject("appliance").getString("name"),
                                                    res.getJSONObject("appliance").getString("reference"),
                                                    res.getJSONObject("appliance").getInt("wattage")
                                            );

                                            Booking booking = new Booking();
                                            booking.appliance = appliance;
                                            booking.timeSlot = slot;
                                            slot.getBookings().add(booking);
                                            break;
                                        }
                                    }
                                }

                                ((Activity) context).runOnUiThread(() -> {
                                    notifyDataSetChanged();
                                    Log.d("SYNC", "Données synchronisées avec le serveur");
                                });
                            }
                        } catch (JSONException ex) {
                            Log.e("SYNC", "Erreur parsing date: " + ex.getMessage());
                            Toast.makeText(context, "Erreur de données", Toast.LENGTH_SHORT).show();
                        }
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
        int usedWattage = timeSlot.getUsedWattage(); // Récupère la consommation actuelle
        int maxWattage = timeSlot.getMaxWattage();

        String timeText = timeFormat.format(timeSlot.getBegin()) + " - " + timeFormat.format(timeSlot.getEnd())
                + "\n" + usedWattage + "/" + maxWattage + "W"; // Affiche les valeurs réelles

        holder.timeSlotButton.setText(timeText);

        double usagePercentage = timeSlot.getUsagePercentage();
        int colorResId;

        if (usagePercentage >= 100) {
            colorResId = R.color.gris;
            holder.timeSlotButton.setEnabled(false);
        } else if (usagePercentage > 70) {
            colorResId = R.color.red;
            holder.timeSlotButton.setEnabled(true);
        } else if (usagePercentage > 30) {
            colorResId = R.color.orange;
            holder.timeSlotButton.setEnabled(true);
        } else {
            colorResId = R.color.green;
            holder.timeSlotButton.setEnabled(true);
        }

        holder.timeSlotButton.setBackgroundTintList(
                ColorStateList.valueOf(ContextCompat.getColor(context, colorResId))
        );

        holder.timeSlotButton.setOnClickListener(v -> {
            if (usagePercentage < 100) {
                showReservationDialog(timeSlot,
                        timeFormat.format(timeSlot.getBegin()) + " - " + timeFormat.format(timeSlot.getEnd()));
            }
        });
    }

    private void showReservationDialog(TimeSlot timeSlot, String timeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation de réservation");

        String message = "Voulez-vous réserver le créneau " + timeText;
        if (selectedDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
            message += " le " + dateFormat.format(selectedDate);
        }
        message += "?";

        builder.setMessage(message);

        builder.setPositiveButton("Oui", (dialog, which) -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
            String email = sharedPreferences.getString("email", null);
            String password = sharedPreferences.getString("password", null);

            if (email != null && password != null) {
                fetchUserAppliances(timeSlot, email, password);
            }
        });

        builder.setNegativeButton("Non", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void fetchUserAppliances(TimeSlot timeSlot, String email, String password) {
        String url = "http://10.0.2.2/powerhome_server/addReservation.php";

        Ion.with(context)
                .load(url)
                .setBodyParameter("email", email.trim())
                .setBodyParameter("password", password.trim())
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            if (e != null || result == null) {
                                throw new Exception(e != null ? e.getMessage() : "Réponse vide");
                            }

                            JSONObject jsonResponse = new JSONObject(result);
                            Log.d("API_RESPONSE", jsonResponse.toString(2));

                            if (!jsonResponse.has("appliances")) {
                                throw new Exception("Aucun appareil trouvé dans la réponse");
                            }

                            JSONArray appliancesArray = jsonResponse.getJSONArray("appliances");
                            List<Appliance> applianceList = new ArrayList<>();

                            if (appliancesArray.length() == 0) {
                                Toast.makeText(context, "Aucun appareil disponible", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            for (int i = 0; i < appliancesArray.length(); i++) {
                                JSONObject appliance = appliancesArray.getJSONObject(i);
                                applianceList.add(new Appliance(
                                        appliance.getInt("id"),
                                        appliance.getString("name"),
                                        appliance.getString("reference"),
                                        appliance.getInt("wattage")
                                ));
                            }

                            // si tout est ok, autre boite de dialogue pour choix d'appliances
                            applianceSelectionDialog(timeSlot, applianceList);

                        } catch (JSONException ex) {
                            Log.e("JSON_ERROR", "Erreur parsing JSON: " + ex.getMessage());
                            Toast.makeText(context, "Erreur de format de données: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                        } catch (Exception ex) {
                            Log.e("API_ERROR", ex.getMessage());
                            Toast.makeText(context, "Erreur: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void applianceSelectionDialog(TimeSlot timeSlot, List<Appliance> appliances) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sélection des appareils");

        String[] applianceNames = new String[appliances.size()];
        boolean[] checkedItems = new boolean[appliances.size()];
        List<Appliance> selectedAppliances = new ArrayList<>();

        for (int i = 0; i < appliances.size(); i++) {
            Appliance appliance = appliances.get(i);
            applianceNames[i] = appliance.getName() + " (" + appliance.getWattage() + "W)";
        }

        builder.setMultiChoiceItems(applianceNames, checkedItems, (dialog, which, isChecked) -> {
            if (isChecked) {
                selectedAppliances.add(appliances.get(which));
            } else {
                selectedAppliances.remove(appliances.get(which));
            }
        });

        builder.setPositiveButton("Confirmer", (dialog, which) -> {
            if (!selectedAppliances.isEmpty()) {
                // fais la reservation
                makeReservation(timeSlot, selectedAppliances);
            } else {
                Toast.makeText(context, "Veuillez sélectionner au moins un appareil", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void makeReservation(TimeSlot timeSlot, List<Appliance> appliances) {
        int totalWattage = 0;
        for (Appliance appliance : appliances) {
            totalWattage += appliance.getWattage();
        }

        if (totalWattage > timeSlot.getAvailableWattage()) {
            Toast.makeText(context, "Capacité dépassée pour ce créneau", Toast.LENGTH_SHORT).show();
            return;
        }

        sendReservationToServer(timeSlot, appliances);
    }

    private void sendReservationToServer(TimeSlot timeSlot, List<Appliance> appliances) {
        String url = "http://10.0.2.2/powerhome_server/addReservation.php";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRENCH);
        String startTime = dateFormat.format(timeSlot.getBegin());
        String endTime = dateFormat.format(timeSlot.getEnd());
        int maxWattage = timeSlot.getMaxWattage();

        JSONArray appliancesArray = new JSONArray();
        for (Appliance appliance : appliances) {
            JSONObject appJson = new JSONObject();
            try {
                appJson.put("id", appliance.getId());
                appJson.put("wattage", appliance.getWattage());
                appliancesArray.put(appJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        Ion.with(context)
                .load(url)
                // ... paramètres ...
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Toast.makeText(context, "Erreur réseau", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            JSONObject response = new JSONObject(result);
                            if ("success".equals(response.getString("status"))) {
                                // Mise à jour locale
                                for (Appliance appliance : appliances) {
                                    Booking booking = new Booking();
                                    booking.appliance = appliance;
                                    booking.timeSlot = timeSlot;
                                    timeSlot.getBookings().add(booking); // Ajoute bien la réservation
                                }

                                saveReservations();

                                // Force le recalcul
                                int newUsedWattage = timeSlot.getUsedWattage();
                                Log.d("DEBUG", "Nouvelle utilisation: " + newUsedWattage + "W");

                                ((Activity)context).runOnUiThread(() -> {
                                    notifyDataSetChanged();
                                    Toast.makeText(context,
                                            "Réservation confirmée (" + newUsedWattage + "/" +
                                                    timeSlot.getMaxWattage() + "W)",
                                            Toast.LENGTH_SHORT).show();
                                });
                            }
                        } catch (JSONException ex) {
                            Log.e("RESERVATION", "Erreur parsing", ex);
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

    static class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        Button timeSlotButton;

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            timeSlotButton = itemView.findViewById(R.id.timeSlotButton);
        }
    }
}