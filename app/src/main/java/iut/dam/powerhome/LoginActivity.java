package iut.dam.powerhome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;

import iut.dam.powerhome.databinding.ActivityLoginBinding;
import iut.dam.powerhome.entities.Appliance;

public class LoginActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    EditText mail;
    EditText mdp;

    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mail = findViewById(R.id.email);
        mdp = findViewById(R.id.mdp);
        Button loginbtn = findViewById(R.id.loginbtn);
//        Button registerbtn = findViewById(R.id.btn_register);
//
//        registerbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
//                startActivity(intent);
//            }
//        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = mail.getText().toString();
                String password = mdp.getText().toString();

                if(email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                attemptLogin(email, password);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_login);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


    private void attemptLogin(String email, String password) {
        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("Connexion en cours...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

        String urlString = "http://10.0.2.2/powerhome_server/login.php?email="
                + email + "&password=" + password;
        Ion.with(LoginActivity.this)
                .load(urlString) // Méthode GET par défaut
                .asJsonObject()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        Log.d("RESULT",result.toString());
                        pDialog.dismiss();
                        if(e != null) {
                            Toast.makeText(LoginActivity.this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                            Log.e("LOGIN_ERROR", e.getMessage());
                            return;
                        }

                        if(result == null) {
                            Toast.makeText(LoginActivity.this, "Aucune réponse du serveur", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JsonObject response = result.getResult();
                        if(response != null && response.has("token")) {
                            // Connexion réussie
                            String token = response.get("token").getAsString();
                            String expiredAt = response.get("expired_at").getAsString();
                            Log.d("RESPONSE",response.toString());

                            // Récupération des appliances
                            JsonArray appliancesArray = null;
                            if(response.has("appliances")) {
                                appliancesArray = response.getAsJsonArray("appliances");
                            }

                            ArrayList<Appliance> appliancesList = new ArrayList<>();
                            if(appliancesArray != null) {
                                for(JsonElement element : appliancesArray) {
                                    JsonObject applianceObj = element.getAsJsonObject();
                                    Appliance appliance = new Appliance(
                                            applianceObj.get("id").getAsInt(),
                                            applianceObj.get("name").getAsString(),
                                            applianceObj.get("reference").getAsString(),
                                            applianceObj.get("wattage").getAsInt()
                                    );
                                    appliancesList.add(appliance);
                                }
                            }





                            // Stocker le token et la date d'expiration pour les requêtes futures
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putParcelableArrayListExtra("appliances", appliancesList);
                            intent.putExtra("token", token);
                            intent.putExtra("expired_at", expiredAt);
                            Bundle bundle = new Bundle();
                            bundle.putString("mail", mail.getText().toString());
                            bundle.putString("mdp", mdp.getText().toString());
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else {
                            // Échec de la connexion
                            String errorMessage = "Échec de la connexion";
                            if(response != null && response.has("message")) {
                                errorMessage = response.get("message").getAsString();
                            }
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}