package iut.dam.powerhome;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import iut.dam.powerhome.entities.Habitat;

public class HabitatAdapter extends ArrayAdapter<Habitat> {
    Activity activity;
    int itemResourceId;
    List<Habitat> items;

    public HabitatAdapter(Activity activity, int itemResourceId, List<Habitat> items) {
        super(activity, itemResourceId, items);
        this.activity = activity;
        this.itemResourceId = itemResourceId;
        this.items = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // Création d'une nouvelle vue
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(itemResourceId, parent, false);

            holder = new ViewHolder();
            holder.residentName = convertView.findViewById(R.id.residentName);
            holder.icone1 = convertView.findViewById(R.id.icone1);
            holder.icone2 = convertView.findViewById(R.id.icone2);
            holder.icone3 = convertView.findViewById(R.id.icone3);
            holder.icone4 = convertView.findViewById(R.id.icone4);
            holder.etage = convertView.findViewById(R.id.nbEtage);
            holder.nbEquip = convertView.findViewById(R.id.nbEquipements);

            convertView.setTag(holder);
        } else {
            // Réutilisation de la vue existante
            holder = (ViewHolder) convertView.getTag();
        }

        // Réinitialisation des icônes
        holder.icone1.setImageResource(0); // ou android.R.color.transparent
        holder.icone2.setImageResource(0);
        holder.icone3.setImageResource(0);
        holder.icone4.setImageResource(0);

        // Remplissage des données
        Habitat habitat = items.get(position);
        holder.residentName.setText(habitat.getResidentName());
        holder.etage.setText(String.valueOf(habitat.getFloor()));
        holder.nbEquip.setText(String.valueOf(habitat.nbEquipement())+" ");

        // Affichage des appareils
        for (int i = 0; i < habitat.getAppliances().size() && i < 4; i++) {
            String applianceName = habitat.getOneAppliance(i).getName();
            ImageView currentIcon = getIconView(holder, i);

            switch (applianceName) {
                case "Machine a laver":
                    currentIcon.setImageResource(R.drawable.ic_machine_a_laver);
                    break;
                case "Aspirateur":
                    currentIcon.setImageResource(R.drawable.ic_aspirateur);
                    break;
                case "Climatiseur":
                    currentIcon.setImageResource(R.drawable.ic_climatiseur);
                    break;
                case "Fer a repasser":
                    currentIcon.setImageResource(R.drawable.ic_fer_a_repasser);
                    break;
            }
        }

        return convertView;
    }

    // Classe ViewHolder pour le pattern d'optimisation
    private static class ViewHolder {
        TextView residentName;
        ImageView icone1, icone2, icone3, icone4;
        TextView etage;
        TextView nbEquip;
    }

    // Méthode utilitaire pour obtenir la bonne ImageView
    private ImageView getIconView(ViewHolder holder, int index) {
        switch (index) {
            case 0: return holder.icone1;
            case 1: return holder.icone2;
            case 2: return holder.icone3;
            case 3: return holder.icone4;
            default: return null;
        }
    }
}
