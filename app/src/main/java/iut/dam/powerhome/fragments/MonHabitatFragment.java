package iut.dam.powerhome.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import iut.dam.powerhome.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonHabitatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonHabitatFragment extends Fragment {

    public MonHabitatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Mon Habitat");
        View rootView=inflater.inflate(R.layout.fragment_mon_habitat, container, false);
        return rootView;
    }
}