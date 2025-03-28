package iut.dam.powerhome.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import iut.dam.powerhome.R;
import iut.dam.powerhome.entities.Appliance;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class MonHabitatFragment extends Fragment {

    public MonHabitatFragment() {
        // Required empty public constructor
    }
    private ArrayList<Appliance> appliances;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Mon Habitat");
        View rootView=inflater.inflate(R.layout.fragment_mon_habitat, container, false);
        int totalWatt=0;

        TextView wattage = rootView.findViewById(R.id.nbTotalWattage);
        if (getArguments() != null) {
            appliances = getArguments().getParcelableArrayList("appliances");
            for (Appliance app : appliances) {
                totalWatt += app.wattage;
            }
        }
        wattage.setText(String.valueOf(totalWatt)+"W");



        return rootView;
    }
}