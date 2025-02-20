package iut.dam.powerhome;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import iut.dam.powerhome.fragments.FirstFragment;
import iut.dam.powerhome.fragments.HabitatsFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawer = findViewById(R.id.drawer);
    NavigationView nav = findViewById(R.id.nav_view);
    FragmentManager fm = getSupportFragmentManager();
    ActionBarDrawerToggle toggle =
            new ActionBarDrawerToggle(this,drawer,R.string.open,R.string.close);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        drawer.setDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        nav.setNavigationItemSelectedListener(this);
        nav.getMenu().performIdentifierAction(R.id.nav_view,0);

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        return toggle.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_view){
            fm.beginTransaction().replace(R.id.contentFL,
                    new FirstFragment()).commit();
        } else if (item.getItemId() == R.id.nav_view) {
            fm.beginTransaction().replace(R.id.contentFL,
                    new HabitatsFragment()).commit();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}