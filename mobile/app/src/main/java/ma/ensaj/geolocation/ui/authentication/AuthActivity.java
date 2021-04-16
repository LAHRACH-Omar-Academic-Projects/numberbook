package ma.ensaj.geolocation.ui.authentication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ma.ensaj.geolocation.R;
import ma.ensaj.geolocation.ui.components.InternetCheck;
import ma.ensaj.geolocation.ui.components.SplashActivity;
import ma.ensaj.geolocation.ui.components.ViewDialog;

public class AuthActivity extends AppCompatActivity {
    private DatePickerDialog datePickerDialog;

    private EditText prenom_edit_text;
    private EditText nom_edit_text;
    private EditText email_edit_text;
    private EditText dateNaissance_edit_text;
    private Spinner sexe_spinner;
    private Button suivant_button;

    private String imei;
    private String prenom;
    private String nom;
    private String email;
    private String dateNaissance;
    private String sexe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        SharedPreferences settings = getSharedPreferences("MyGamePreferences", MODE_PRIVATE);
        imei = settings.getString("IMEI", "IMEI");


        initViews();
        initializeDate();
        dateNaissance_edit_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDatePicker();
            }
        });
        suivant_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> user = new ArrayList<>();
                user.add(getPrenom());
                user.add(getNom());
                user.add(getEmail());
                user.add(imei);
                user.add(getDateNaissance());
                user.add(getSexe());
                goToPhoneVerifyAvtivity(user);
            }
        });
    }

    private void initViews() {
        prenom_edit_text = findViewById(R.id.prenom);
        nom_edit_text = findViewById(R.id.nom);
        email_edit_text = findViewById(R.id.email);
        dateNaissance_edit_text = findViewById(R.id.dateNaissance);
        sexe_spinner = findViewById(R.id.sexe);
        suivant_button = findViewById(R.id.suivant);
    }
    private String getPrenom() {
        prenom = prenom_edit_text.getText().toString();
        return prenom;
    }
    private String getNom() {
        nom = nom_edit_text.getText().toString();
        return nom;
    }
    private String getEmail() {
        email = email_edit_text.getText().toString();
        return email;
    }
    private String getDateNaissance() {
        dateNaissance = dateNaissance_edit_text.getText().toString();
        return dateNaissance;
    }
    private String getSexe() {
        sexe = (String) sexe_spinner.getSelectedItem();
        return sexe;
    }
    private void initializeDate() {
        final Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        dateNaissance_edit_text.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);
    }
    private void displayDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateNaissance_edit_text.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }
    private void goToPhoneVerifyAvtivity(List<String> user) {
        Intent intent = new Intent(AuthActivity.this, PhoneVerifyActivity.class);
        intent.putStringArrayListExtra("User", (ArrayList<String>) user);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }
}