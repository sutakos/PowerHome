package iut.dam.powerhome.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import com.google.android.material.switchmaterial.SwitchMaterial;
import android.content.SharedPreferences;

import com.google.android.material.switchmaterial.SwitchMaterial;

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
        sharedPrefs = requireActivity().getSharedPreferences("app_settings", 0);

        // Configurer le switch selon le thème actuel
        boolean isDarkMode = sharedPrefs.getBoolean("dark_mode", false);
        swt.setChecked(isDarkMode);

        // Gestion du changement de thème
        swt.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Sauvegarder la préférence
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();

            // Appliquer le thème
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            // Redémarrer l'activité pour appliquer le thème
            requireActivity().recreate();
        });

        return rootView;
    }
}