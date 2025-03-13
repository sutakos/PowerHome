package iut.dam.powerhome;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;


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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        drawerDL = findViewById(R.id.drawer);
        navNV = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawerDL,
                        R.string.open, R.string.close);

        fm = getSupportFragmentManager();

        drawerDL.setDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        navNV.setNavigationItemSelectedListener(this);
        navNV.getMenu().performIdentifierAction(R.id.habitat, 0);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return toggle.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        if (item.getItemId() == R.id.monhabitat){
            fm.beginTransaction().replace(R.id.contentFL,
                    new MonHabitatFragment()).commit();
        } else if (item.getItemId() == R.id.habitat) {
            fm.beginTransaction().replace(R.id.contentFL,
                    new HabitatsFragment()).commit();
        }
        else if (item.getItemId() == R.id.requete) {
            fm.beginTransaction().replace(R.id.contentFL,
                    new MesRequetesFragment()).commit();
        }
        else if (item.getItemId() == R.id.setting) {
            fm.beginTransaction().replace(R.id.contentFL,
                    new ParametresFragment()).commit();
        }
        else if (item.getItemId() == R.id.disconnect) {
            fm.beginTransaction().replace(R.id.contentFL,
                    new SeDeconnecterFragment()).commit();
        }
        drawerDL.closeDrawer(GravityCompat.START);
        return true;
    }


}