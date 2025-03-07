package iut.dam.powerhome.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import iut.dam.powerhome.R;

public class ParametresFragment extends Fragment {

    public ParametresFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Paramètres");
        View rootView=inflater.inflate(R.layout.fragment_parametres, container, false);
        return rootView;}
}