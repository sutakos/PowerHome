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
import com.koushikdutta.ion.Response;

import java.util.ArrayList;
import java.util.List;

import iut.dam.powerhome.entities.Habitat;
import iut.dam.powerhome.HabitantAdapter;
import iut.dam.powerhome.R;


public class HabitatsFragment extends Fragment {
    String urlString = "http://[server]/powerhome_server/getHabitats.php";
    List<Habitat> habitats = new ArrayList<>();
    ProgressDialog pDialog;
    HabitantAdapter adapter;
    boolean isAdapterInitialized = false;
    ListView lv;

    public HabitatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Habitats");

        View rootView = inflater.inflate(R.layout.fragment_habitats, container, false);
        lv =rootView.findViewById(R.id.lsHabitant);
        getRemoteHabitats();

        return rootView;
    }

    private void initializeAdapter() {
        if (!isAdapterInitialized && !habitats.isEmpty()) {
            adapter = new HabitantAdapter(getActivity(), R.layout.item_habitat, habitats);
            lv.setAdapter(adapter);
            isAdapterInitialized = true;
        } else if (isAdapterInitialized) {
            adapter.notifyDataSetChanged();
        }
    }


    public void getRemoteHabitats() {
        pDialog = new ProgressDialog(this.getActivity());
        pDialog.setMessage("Getting list of habitats...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

        String urlString = "http://192.168.1.19/powerhome_server/getHabitats_v2.php?token=0645901e34c2fbe1c0e3761642e728a3";
        Ion.with(this.getActivity())
                .load(urlString)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        Response<String> json = result;
                        Log.d("JSON Response", json.getResult());
                        pDialog.dismiss();
                        if(result == null)
                            Log.d(TAG, "No response from the server!!!");
                        else {
                            // Traitement de result
                            Toast.makeText(HabitatsFragment.this.getActivity(), json.getResult(), Toast.LENGTH_SHORT).show();
                            habitats.clear();
                            habitats.addAll(Habitat.getListFromJson(json.getResult()));
                            initializeAdapter();
                        }
                    }
                });
    }



}
