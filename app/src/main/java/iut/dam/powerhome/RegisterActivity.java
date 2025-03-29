package iut.dam.powerhome;

import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import iut.dam.powerhome.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        Spinner sp = (Spinner) findViewById(R.id.phone_spinner);
        String[] phonePre = new String[]{"+33",
        "+44",
        "+34",
        "+15",
        "+93",
        "+54",
        "+44",
        "+55"};
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,phonePre);
        sp.setAdapter(adapter);

        EditText editTextEmail = findViewById(R.id.email_register);
        String inputTextE = editTextEmail.getText().toString().trim();
        EditText editTextPass = findViewById(R.id.password_register);
        String inputTextP = editTextPass.getText().toString().trim();
        Button btn_register = findViewById(R.id.button_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputTextE.isEmpty() || inputTextP.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Veuillez saisir les champs", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

    }


}