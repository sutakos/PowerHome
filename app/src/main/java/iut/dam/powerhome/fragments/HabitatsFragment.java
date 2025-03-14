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

import java.util.List;

import iut.dam.powerhome.entities.Habitat;
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
        getRemoteHabitats();
        ListView lv = rootView.findViewById(R.id.lsHabitant);
        HabitantAdapter adapter = new HabitantAdapter(HabitatsFragment.this.getActivity(),R.layout.item_habitat,list);
        //lv.setAdapter(adapter);


        return rootView;
    }


    public void getRemoteHabitats() {
        pDialog = new ProgressDialog(this.getActivity());
        pDialog.setMessage("Getting list of habitats...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

        String urlString = "http://10.125.134.12/powerhome_server/getHabitats.php?token=eef0090d0b8815354a31943767b0a32f";
        Ion.with(this.getActivity())
                .load(urlString)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        String json = result.getResult();
                        Log.d("JSON Response", json);
                        pDialog.dismiss();
                        if(result == null)
                            Log.d(TAG, "No response from the server!!!");
                        else {
                            // Traitement de result
                            Toast.makeText(HabitatsFragment.this.getActivity(), json, Toast.LENGTH_SHORT).show();
                            list = (List<Habitat>) Habitat.getFromJson(result.toString());
                            //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            //startActivity(intent);
                        }
                    }
                });
    }



}
