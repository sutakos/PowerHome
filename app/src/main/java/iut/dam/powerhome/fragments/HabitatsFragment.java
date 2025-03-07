package iut.dam.powerhome.fragments;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.os.Bundle;


import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import iut.dam.powerhome.Appliance;
import iut.dam.powerhome.Habitant;
import iut.dam.powerhome.HabitantAdapter;
import iut.dam.powerhome.HabitatActivity;
import iut.dam.powerhome.MainActivity;
import iut.dam.powerhome.R;


public class HabitatsFragment extends Fragment {
    String urlString = "http://[server]/powerhome_server/getHabitats.php";
    ProgressDialog pDialog;
    public HabitatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Habitats");

        View rootView = inflater.inflate(R.layout.fragment_habitats, container, false);

        List<Habitant> h = getHabitants();
        ListView lv = rootView.findViewById(R.id.lsHabitant);
        HabitantAdapter adapter = new HabitantAdapter(HabitatsFragment.this.getActivity(),R.layout.item_habitat,h);
        lv.setAdapter(adapter);


        return rootView;
    }

    private static List<Habitant> getHabitants() {
        Appliance aspi = new Appliance(1,"Aspirateur","ic_aspirateur",100);
        Appliance maLav = new Appliance(2,"Machine à laver","ic_machine_a_laver",100);
        Appliance clim = new Appliance(3,"Climatiseur","ic_climatiseur",100);
        Appliance fer = new Appliance(4,"Fer à repasser","ic_fer_a_repasser",100);
        Appliance[] all = {aspi, maLav,clim,fer};
        Appliance[] lav = {maLav};
        Appliance[] ferAspi = {aspi,fer};
        Appliance[] lavFerAspi = {aspi, maLav,fer};
        Appliance[] asp = {aspi};
        List<Habitant> h = new ArrayList<>();
        h.add(new Habitant(1,"Gaëtan Leclair",1,25,all));
        h.add(new Habitant(2,"Cédric Boudet",1,25, lav));
        h.add(new Habitant(3,"Gaylord Thibodeaux",2,25, ferAspi));
        h.add(new Habitant(4,"Adam Jacquinot",3,25, lavFerAspi));
        h.add(new Habitant(5,"Abel Fresnel",3,25, asp));
        return h;
    }

    public void getRemoteHabitats() {
        pDialog = new ProgressDialog(this.getActivity());
        pDialog.setMessage("Getting list of habitats...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

        String urlString = "http://[server]/powerhome_server/getHabitats.php";
        Ion.with(this.getActivity())
                .load(urlString)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        pDialog.dismiss();
                        if(result == null)
                            Log.d(TAG, "No response from the server!!!");
                        else {
                            // Traitement de result
                            Toast.makeText(HabitatsFragment.this.getActivity(), result, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



}
