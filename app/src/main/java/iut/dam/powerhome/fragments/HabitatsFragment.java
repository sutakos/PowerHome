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

import java.util.List;

import entities.Habitat;
import iut.dam.powerhome.HabitantAdapter;
import iut.dam.powerhome.R;


public class HabitatsFragment extends Fragment {
    String urlString = "http://[server]/powerhome_server/getHabitats.php";
    List<Habitat> list;
    ProgressDialog pDialog;
    public HabitatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Habitats");

        View rootView = inflater.inflate(R.layout.fragment_habitats, container, false);

        ListView lv = rootView.findViewById(R.id.lsHabitant);
        HabitantAdapter adapter = new HabitantAdapter(HabitatsFragment.this.getActivity(),R.layout.item_habitat,list);
        lv.setAdapter(adapter);


        return rootView;
    }


    public void getRemoteHabitats() {
        pDialog = new ProgressDialog(this.getActivity());
        pDialog.setMessage("Getting list of habitats...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

        String urlString = "http://10.125.132.73/powerhome_server/getHabitats.php";
        Ion.with(this.getActivity())
                .load(urlString)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        pDialog.dismiss();
                        if(result == null)
                            Log.d(TAG, "No response from the server!!!");
                        else {
                            // Traitement de result
                            Toast.makeText(HabitatsFragment.this.getActivity(), result, Toast.LENGTH_SHORT).show();
                            list = (List<Habitat>) Habitat.getFromJson(result);

                        }
                    }
                });
    }



}
