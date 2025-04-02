package iut.dam.powerhome.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Switch;
import com.google.android.material.switchmaterial.SwitchMaterial;
import android.content.SharedPreferences;

import com.google.android.material.switchmaterial.SwitchMaterial;

import iut.dam.powerhome.MainActivity;
import iut.dam.powerhome.R;

public class ParametresFragment extends Fragment {
    private SwitchMaterial swt;
    private SharedPreferences sharedPrefs;

    public ParametresFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Paramètres");
        View rootView=inflater.inflate(R.layout.fragment_parametres, container, false);
        swt = rootView.findViewById(R.id.darkmode);
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_settings", MODE_PRIVATE);
        swt.setChecked(prefs.getBoolean("dark_mode", false));

        // Gestion du changement de thème
        swt.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ((MainActivity)requireActivity()).toggleDarkMode(isChecked);
        });

        return rootView;
    }

}