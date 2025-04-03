package iut.dam.powerhome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;


import java.util.ArrayList;

import iut.dam.powerhome.entities.Appliance;
import iut.dam.powerhome.fragments.HabitatsFragment;
import iut.dam.powerhome.fragments.MesRequetesFragment;
import iut.dam.powerhome.fragments.MonHabitatFragment;
import iut.dam.powerhome.fragments.ParametresFragment;
import iut.dam.powerhome.fragments.SeDeconnecterFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawerDL;
    ActionBarDrawerToggle toggle;
    FragmentManager fm;
    NavigationView navNV;
    MonHabitatFragment monHabitat = new MonHabitatFragment();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applyTheme();
        setContentView(R.layout.activity_main);

        // Initialisation Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialisation des vues
        drawerDL = findViewById(R.id.drawer);
        navNV = findViewById(R.id.nav_view);
        fm = getSupportFragmentManager();

        // Configuration du DrawerToggle
        toggle = new ActionBarDrawerToggle(
                this,
                drawerDL,
                toolbar,
                R.string.open,
                R.string.close
        );
        drawerDL.addDrawerListener(toggle);
        toggle.syncState();

        // Configuration ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Configuration du header
        View headerView = navNV.getHeaderView(0);
        TextView mail_name = headerView.findViewById(R.id.user_name);
        if (getIntent() != null && getIntent().getExtras() != null) {
            String email = getIntent().getExtras().getString("mail");
            if (email != null) mail_name.setText(email);
        }

        // Arguments du fragment
        if (getIntent() != null && getIntent().getParcelableArrayListExtra("appliances") != null) {
            Bundle app = new Bundle();
            app.putParcelableArrayList("appliances", getIntent().getParcelableArrayListExtra("appliances"));
            monHabitat.setArguments(app);
        }

        // Navigation
        navNV.setNavigationItemSelectedListener(this);

        // Chargement initial du fragment
        if (savedInstanceState == null) {
            navNV.setCheckedItem(R.id.habitat);
            loadFragment(new HabitatsFragment());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return toggle.onOptionsItemSelected(item);
    }

    private void loadFragment(Fragment fragment) {
        fm.beginTransaction()
                .replace(R.id.contentFL, fragment)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.monhabitat) {
            loadFragment(monHabitat);
        } else if (id == R.id.habitat) {
            loadFragment(new HabitatsFragment());
        } else if (id == R.id.requete) {
            loadFragment(new MesRequetesFragment());
        } else if (id == R.id.setting) {
            loadFragment(new ParametresFragment());
        } else if (id == R.id.disconnect) {
            loadFragment(new SeDeconnecterFragment());
        }

        drawerDL.closeDrawer(GravityCompat.START);
        return true;
    }

    public void applyTheme() {
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        boolean darkMode = prefs.getBoolean("dark_mode", false);

        if (darkMode) {
            setTheme(R.style.Theme_PowerHome_Dark);
        } else {
            setTheme(R.style.Theme_PowerHome);
        }
    }

    private void setupNavigationView() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        boolean darkMode = prefs.getBoolean("dark_mode", false);

        if (darkMode) {
            navigationView.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_navbar));
            navigationView.setItemTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
            navigationView.setItemIconTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)));
        }
    }

    public void toggleDarkMode(boolean enable) {
        SharedPreferences.Editor editor = getSharedPreferences("app_settings", MODE_PRIVATE).edit();
        editor.putBoolean("dark_mode", enable);
        editor.apply();

        // Recréer l'activité pour appliquer le thème
        recreate();
    }


}