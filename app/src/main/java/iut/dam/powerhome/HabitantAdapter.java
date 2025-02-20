package iut.dam.powerhome;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class HabitantAdapter extends ArrayAdapter<Habitant> {
    Activity activity;
    int itemResourceId;
    List<Habitant> items;

    public HabitantAdapter(Activity activity, int itemResourceId, List<Habitant> items) {
        super(activity, itemResourceId, items);
        this.activity = activity;
        this.itemResourceId = itemResourceId;
        this.items = items;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        View layout = convertView;
        if (convertView == null){
            LayoutInflater inflater = activity.getLayoutInflater();
            layout = inflater.inflate(itemResourceId, parent, false);
        }

        TextView residentName = (TextView) layout.findViewById(R.id.residentName);
        ImageView icone1 = (ImageView) layout.findViewById(R.id.icone1);
        ImageView icone2 = (ImageView) layout.findViewById(R.id.icone2);
        ImageView icone3 = (ImageView) layout.findViewById(R.id.icone3);
        ImageView icone4 = (ImageView) layout.findViewById(R.id.icone4);
        ImageView[] icon = new ImageView[]{icone1,icone2,icone3,icone4};

        TextView etage = (TextView) layout.findViewById(R.id.nbEtage);
        TextView nbEquip = (TextView) layout.findViewById(R.id.nbEquipements);

        residentName.setText(items.get(position).getResidentName());
        etage.setText(String.valueOf(items.get(position).getFloor()));
        nbEquip.setText(String.valueOf(items.get(position).nbEquipement()));

        for(int i=0;i<items.get(position).getAppliances().size();i++){
            if(items.get(position).getApp(i).getRef() == "ic_machine_a_laver")
                icon[i].setImageResource(R.drawable.ic_machine_a_laver);
            if(items.get(position).getApp(i).getRef() == "ic_aspirateur")
                icon[i].setImageResource(R.drawable.ic_aspirateur);
            if(items.get(position).getApp(i).getRef() == "ic_climatiseur")
                icon[i].setImageResource(R.drawable.ic_climatiseur);
            if(items.get(position).getApp(i).getRef() == "ic_fer_a_repasser")
                icon[i].setImageResource(R.drawable.ic_fer_a_repasser);
        }
        return layout;
    }
}
