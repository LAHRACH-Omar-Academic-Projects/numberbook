package ma.ensaj.geolocation.ui.authentication;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ma.ensaj.geolocation.R;
import ma.ensaj.geolocation.beans.User;
import ma.ensaj.geolocation.services.DataService;
import ma.ensaj.geolocation.services.RetrofitInstance;
import ma.ensaj.geolocation.ui.components.InternetCheck;
import ma.ensaj.geolocation.ui.components.SplashActivity;
import ma.ensaj.geolocation.ui.components.ViewDialog;
import ma.ensaj.geolocation.ui.contacts.ContactsActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneVerifyActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth firebaseAuth;
    private String mVerificationId;
    private GoogleApiClient apiClient;
    private DataService service;

    private ScrollView saisie_layout;
    private ScrollView verifier_layout;
    private EditText telephone_edit_text;
    private Button suivant_button;
    private EditText code_edit_text;
    private Button verifier_button;
    private Button renvoyer_button;
    private TextView timer_text_view;

    private String telephone;
    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);

            firebaseAuth = FirebaseAuth.getInstance();
            service = RetrofitInstance.getInstance().create(DataService.class);
            initViews();
            requestHint();
            suivant_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendVerificationCodeToUser();
                    new CountDownTimer(60000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            timer_text_view.setText("secondes restantes: " + millisUntilFinished / 1000);
                            renvoyer_button.setEnabled(false);
                            renvoyer_button.setTextColor(getResources().getColor(R.color.black));
                        }

                        public void onFinish() {
                            timer_text_view.setText("Si vous n'avez pas reçu le code, cliquez sur renvoyer. !");
                            renvoyer_button.setEnabled(true);
                            renvoyer_button.setTextColor(getResources().getColor(R.color.orange));
                        }
                    }.start();
                }
            });
            verifier_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToContactsActivity();
                }
            });
            renvoyer_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendVerificationCodeToUser();
                    new CountDownTimer(60000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            timer_text_view.setText("secondes restantes: " + millisUntilFinished / 1000);
                            renvoyer_button.setEnabled(false);
                            renvoyer_button.setTextColor(getResources().getColor(R.color.black));
                        }

                        public void onFinish() {
                            timer_text_view.setText("Si vous n'avez pas reçu le code, cliquez sur renvoyer. !");
                            renvoyer_button.setEnabled(true);
                            renvoyer_button.setTextColor(getResources().getColor(R.color.orange));
                        }
                    }.start();
                }
            });
    }

    private void initViews() {
        saisie_layout = findViewById(R.id.saisie_layout);
        telephone_edit_text = findViewById(R.id.telephone);
        suivant_button = findViewById(R.id.suivant);
        verifier_layout = findViewById(R.id.verifier_layout);
        code_edit_text = findViewById(R.id.code);
        verifier_button = findViewById(R.id.verifier);
        renvoyer_button = findViewById(R.id.renvoyer);
        timer_text_view = findViewById(R.id.timer);
    }
    private String getTelephone() {
        telephone = telephone_edit_text.getText().toString();
        return telephone;
    }
    private String getVerificationCode() {
        verificationCode = code_edit_text.getText().toString();
        return verificationCode;
    }
    private void setVerificationCode(String codeVerification) {
        this.verificationCode = codeVerification;
    }
    private void requestHint() {
        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();
        try {
            HintRequest hintRequest = new HintRequest.Builder()
                    .setPhoneNumberIdentifierSupported(true)
                    .build();
            PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                    apiClient, hintRequest);
            startIntentSenderForResult(intent.getIntentSender(),
                    2, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                telephone_edit_text.setText(credential.getId());
            }
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }
    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void sendVerificationCodeToUser() {
        saisie_layout.setVisibility(View.GONE);
        verifier_layout.setVisibility(View.VISIBLE);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(getTelephone())
                        .setTimeout(20L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        firebaseAuth.setLanguageCode(Locale.getDefault().getLanguage());
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            Log.d("TAG", "onVerificationCompleted:" + credential);
            setVerificationCode(credential.getSmsCode());
            if(getVerificationCode() != null) {
                verifyCode(getVerificationCode());
            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.w("TAG", "onVerificationFailed", e);
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                Log.w("TAG", "Invalid request", e);
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Log.w("TAG", "The SMS quota for the project has been exceeded", e);
            }
        }
        @Override
        public void onCodeSent(@NonNull String verificationId,
                               @NonNull PhoneAuthProvider.ForceResendingToken token) {
            Log.d("TAG", "onCodeSent:" + verificationId);
            mVerificationId = verificationId;
        }
    };
    private void verifyCode(String verificationCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    goToContactsActivity();
                    Log.d("TAG", "signInWithCredential:success");
                } else {
                    Log.w("TAG", "signInWithCredential:failure", task.getException());
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Log.w("TAG", "The verification code entered was invalid", task.getException());
                    }
                }
            }
        });
    }
    private void goToContactsActivity() {
        Intent intent = getIntent();
        User user = new User();
        user.setPrenom(intent.getStringArrayListExtra("User").get(0));
        user.setNom(intent.getStringArrayListExtra("User").get(1));
        user.setEmail(intent.getStringArrayListExtra("User").get(2));
        user.setImei(intent.getStringArrayListExtra("User").get(3));
        user.setDateNaissance(intent.getStringArrayListExtra("User").get(4));
        user.setSexe(intent.getStringArrayListExtra("User").get(5));
        user.setOnline(true);
        user.setResponses(new ArrayList<>());
        user.setRequests(new ArrayList<>());
        user.setTelephone(getTelephone());

        Call<User> call = service.saveUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                User currentUser = response.body();
                SharedPreferences settings = getSharedPreferences("MyGamePreferences", MODE_PRIVATE);
                SharedPreferences.Editor prefEditor = settings.edit();
                if (currentUser != null) {
                    Gson gson = new Gson();
                    String user = gson.toJson(currentUser);
                    prefEditor.putString("CurrentUser", user);
                    prefEditor.apply();

                    Intent intent = new Intent(PhoneVerifyActivity.this, ContactsActivity.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("Error", t.toString());
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }
}