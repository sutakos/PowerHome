package iut.dam.powerhome;

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

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    }

    public void setSelectedDate(Date date) {
        this.selectedDate = date;
        initTimeSlots();
        notifyDataSetChanged();
    }

    private void initTimeSlots() {
        if (selectedDate == null) return;

        try {
            timeSlots.clear();
            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedDate);

            // Génère les créneaux
            for (int i = 0; i < 12; i++) {
                Calendar startCal = (Calendar) cal.clone();
                startCal.set(Calendar.HOUR_OF_DAY, i * 2);
                startCal.set(Calendar.MINUTE, 0);
                Date start = startCal.getTime();

                Calendar endCal = (Calendar) cal.clone();
                endCal.set(Calendar.HOUR_OF_DAY, i * 2 + 2);
                endCal.set(Calendar.MINUTE, 0);
                Date end = endCal.getTime();

                timeSlots.add(new TimeSlot(start, end, 2500));
            }

            // Charge les réservations
            fetchReservationsForDate(selectedDate);

        } catch (Exception e) {
            Log.e("INIT_SLOTS", "Error initializing time slots", e);
        }
    }

    private void fetchReservationsForDate(Date date) {
        String url = "http://192.168.1.19/powerhome_server/getReservationsByDate.php";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);

        // Clear existing bookings
        for (TimeSlot slot : timeSlots) {
            slot.getBookings().clear();
        }

        Ion.with(context)
                .load(url)
                .setBodyParameter("date", dateFormat.format(date))
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> response) {
                        if (e != null) {
                            Toast.makeText(context, "Erreur réseau: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            JSONObject jsonResponse = new JSONObject(response.getResult());
                            if (jsonResponse.getString("status").equals("success")) {
                                JSONArray reservations = jsonResponse.getJSONArray("reservations");

                                for (int i = 0; i < reservations.length(); i++) {
                                    JSONObject res = reservations.getJSONObject(i);
                                    String slotKey = res.getString("slot_key");

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

                                saveReservations();
                                ((Activity) context).runOnUiThread(() -> notifyDataSetChanged());
                            }
                        } catch (JSONException ex) {
                            Log.e("SYNC", "Erreur parsing: " + ex.getMessage());
                        }
                    }
                });
    }

    private void saveReservations() {
        if (selectedDate == null || timeSlots.isEmpty()) return;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH);
        String dateKey = dateFormat.format(selectedDate);

        try {
            JSONObject reservationsData = new JSONObject();
            JSONArray slotsArray = new JSONArray();

            for (TimeSlot slot : timeSlots) {
                JSONObject slotJson = new JSONObject();
                slotJson.put("slot_key", slot.getSlotKey());
                slotJson.put("max_wattage", slot.getMaxWattage());

                JSONArray bookingsArray = new JSONArray();
                for (Booking booking : slot.getBookings()) {
                    JSONObject bookingJson = new JSONObject();
                    bookingJson.put("appliance_id", booking.appliance.getId());
                    bookingJson.put("appliance_name", booking.appliance.getName());
                    bookingJson.put("wattage", booking.appliance.getWattage());
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
            Log.e("SAVE", "Error saving reservations", e);
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
                                Appliance appliance = findApplianceById(bookingJson.getInt("appliance_id"));
                                if (appliance == null) {
                                    appliance = new Appliance(
                                            bookingJson.getInt("appliance_id"),
                                            bookingJson.getString("appliance_name"),
                                            "",
                                            bookingJson.getInt("wattage")
                                    );
                                }

                                Booking booking = new Booking();
                                booking.appliance = appliance;
                                booking.timeSlot = slot;
                                bookings.add(booking);
                            }

                            slot.setBookings(bookings);
                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                Log.e("LOAD", "Error loading reservations", e);
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

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_time_slot, parent, false);
        return new TimeSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeSlotViewHolder holder, int position) {
        TimeSlot timeSlot = timeSlots.get(position);

        int usedWattage = 0;
        for (Booking booking : timeSlot.getBookings()) {
            usedWattage += booking.appliance.getWattage();
        }

        String timeText = timeFormat.format(timeSlot.getBegin()) + " - " + timeFormat.format(timeSlot.getEnd())
                + "\n" + usedWattage + "/" + timeSlot.getMaxWattage() + "W";
        holder.timeSlotButton.setText(timeText);

        double usagePercentage = (usedWattage * 100.0) / timeSlot.getMaxWattage();
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
        if (userAppliances == null || userAppliances.isEmpty()) {
            Toast.makeText(context, "Aucun appareil disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] applianceNames = new String[userAppliances.size()];
        for (int i = 0; i < userAppliances.size(); i++) {
            Appliance appliance = userAppliances.get(i);
            applianceNames[i] = appliance.getName() + " (" + appliance.getWattage() + "W)";
        }

        new AlertDialog.Builder(context)
                .setTitle("Sélectionnez un appareil")
                .setItems(applianceNames, (dialog, which) -> {
                    Appliance selectedAppliance = userAppliances.get(which);
                    confirmReservation(timeSlot, selectedAppliance);
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void confirmReservation(TimeSlot timeSlot, Appliance appliance) {
        new AlertDialog.Builder(context)
                .setTitle("Confirmer la réservation")
                .setMessage("Voulez-vous réserver ce créneau pour " + appliance.getName() + "?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    List<Appliance> appliances = new ArrayList<>();
                    appliances.add(appliance);
                    makeReservation(timeSlot, appliances);
                })
                .setNegativeButton("Non", null)
                .show();
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
        String url = "http://192.168.1.19/powerhome_server/addReservation.php";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRENCH);

        JSONArray appliancesArray = new JSONArray();
        for (Appliance appliance : appliances) {
            JSONObject appJson = new JSONObject();
            try {
                appJson.put("id", appliance.getId());
                appJson.put("wattage", appliance.getWattage());
                appliancesArray.put(appJson);
            } catch (JSONException e) {
                Log.e("RESERVATION", "Error creating JSON", e);
            }
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        Ion.with(context)
                .load(url)
                .setBodyParameter("email", email)
                .setBodyParameter("slot_key", timeSlot.getSlotKey())
                .setBodyParameter("start_time", dateFormat.format(timeSlot.getBegin()))
                .setBodyParameter("end_time", dateFormat.format(timeSlot.getEnd()))
                .setBodyParameter("max_wattage", String.valueOf(timeSlot.getMaxWattage()))
                .setBodyParameter("appliances", appliancesArray.toString())
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            if (e == null) {
                                JSONObject response = new JSONObject(result);
                                if ("success".equals(response.getString("status"))) {
                                    for (Appliance appliance : appliances) {
                                        Booking booking = new Booking();
                                        booking.appliance = appliance;
                                        booking.timeSlot = timeSlot;
                                        timeSlot.getBookings().add(booking);
                                    }

                                    saveReservations();
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "Réservation enregistrée", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException ex) {
                            Log.e("RESERVATION", "Error parsing response", ex);
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