package iut.dam.powerhome;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class HabitatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habitat);


               List<Habitant> h = getHabitants();
//        String[] h = new String[]{"Gaëtan Leclair",
//                "Cédric Boudet",
//                "Gaylord Thibodeaux",
//                "Adam Jacquinot",
//                "Abel Fresnel"};
//        Appliance aspi = new Appliance(1,"Aspirateur","laspirateur",100);
//        List<Habitant> h = new ArrayList<>();
//        h.add(new Habitant(1,"Gaëtan Leclair",1,25,aspi));


        ListView lv = (ListView) findViewById(R.id.lsHabitant);
        HabitantAdapter adapter = new HabitantAdapter(HabitatActivity.this,R.layout.item_habitat,h);
//        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1,h);
        lv.setAdapter(adapter);

    }

    @NonNull
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
}