package iut.dam.powerhome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class LoginActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;

    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        EditText mail = findViewById(R.id.email);
        EditText mdp = findViewById(R.id.mdp);
        Button loginbtn = findViewById(R.id.loginbtn);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("mail", mail.getText().toString());
                bundle.putString("mdp", mdp.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);

                String urlString = "http://10.125.132.73/powerhome_server/login.php?email=" + mail.getText() + "&password=" + mdp.getText();

//                Ion.with(this) //Ion pour requete réseau
//                        .load(urlString)  //charge l'url construite au dessus
//                        .asString() //la reponse attendue est sous forme de chaine de caractere
//                        .withResponse()   //inclut la reponse HTTP dans l'objet Response
//                        .setCallback(new FutureCallback<Response<String>>() {  //définit un callback qui sera exécuté quand la requete sera terminée
//                            @Override
//                            public void onCompleted(Exception e, Response<String> response) {  //quand la reponse est recue
//                                String json= response.getResult(); //recup sous forme de JSON
//                                pDialog.dismiss();
//
//
//                                if(json == null)
//                                    Log.d(TAG, "No response from the server!!!");
//                                else {
//                                    Toast.makeText(MainActivity.this, json, Toast.LENGTH_SHORT).show(); //afichage du contenu de la reponse
//                                    //Habitat.getFromJson(json);
//                                }
//                            }
//
//                        });
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_login);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}