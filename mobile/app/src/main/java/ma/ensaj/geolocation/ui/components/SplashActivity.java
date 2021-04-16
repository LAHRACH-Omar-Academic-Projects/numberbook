package ma.ensaj.geolocation.ui.components;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import ma.ensaj.geolocation.R;
import ma.ensaj.geolocation.beans.User;
import ma.ensaj.geolocation.services.DataService;
import ma.ensaj.geolocation.services.RetrofitInstance;
import ma.ensaj.geolocation.ui.authentication.AuthActivity;
import ma.ensaj.geolocation.ui.contacts.ContactsActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    private DataService service;
    private String imei = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
            SplashActivity.this.finish();
            System.exit(0);
        }

        requestPhoneStatePermission();
    }

    public String getDeviceImei() {
        return imei;
    }

    public void setDeviceImei() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = tm.getDeviceId();
    }


    public void requestPhoneStatePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_PHONE_STATE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Autorisation de lecture l'état du téléphone");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Veuillez activer la lecture de l'état du téléphone.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_PHONE_STATE}
                                    , PERMISSIONS_REQUEST_READ_PHONE_STATE);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_PHONE_STATE},
                            PERMISSIONS_REQUEST_READ_PHONE_STATE);
                }
            } else {
                setDeviceImei();
                next();
            }
        } else {
            setDeviceImei();
            next();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setDeviceImei();
                next();
            } else {
                requestPhoneStatePermission();
            }
        }
    }

    private void next() {


        service = RetrofitInstance.getInstance().create(DataService.class);
        Call<User> call = service.getUserByDeviceImei(getDeviceImei());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                User currentUser = response.body();
                SharedPreferences settings = getSharedPreferences("MyGamePreferences", MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = settings.edit();
                prefEditor.putString("IMEI", getDeviceImei());
                if (currentUser != null) {
                    Gson gson = new Gson();
                    String user = gson.toJson(currentUser);
                    prefEditor.putString("CurrentUser", user);
                    prefEditor.apply();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (currentUser.isOnline()) {
                                goToContactsActivity();
                            } else {
                                currentUser.setOnline(true);
                                Call<User> call = service.updateUser(currentUser);
                                call.enqueue(new Callback<User>() {
                                    @Override
                                    public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                                        if (response.body().isOnline()) {
                                            goToContactsActivity();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<User> call, Throwable t) {
                                        Log.d("Error", t.toString());
                                    }
                                });
                            }
                        }
                    }, 1500);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            prefEditor.apply();
                            goToAuthActivity();
                        }
                    }, 1500);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, Throwable t) {
                Log.d("Error", t.toString());
            }
        });
    }

    private void goToAuthActivity() {
        Intent mainIntent = new Intent(SplashActivity.this, AuthActivity.class);
        SplashActivity.this.startActivity(mainIntent);
        SplashActivity.this.finish();
    }
    private void goToContactsActivity() {
        Intent intent = new Intent(SplashActivity.this, ContactsActivity.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        SplashActivity.this.finish();
        System.exit(0);
    }
}