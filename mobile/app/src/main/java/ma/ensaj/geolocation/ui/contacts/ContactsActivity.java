package ma.ensaj.geolocation.ui.contacts;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ma.ensaj.geolocation.R;
import ma.ensaj.geolocation.beans.FriendingState;
import ma.ensaj.geolocation.beans.User;
import ma.ensaj.geolocation.services.DataService;
import ma.ensaj.geolocation.services.RetrofitInstance;
import ma.ensaj.geolocation.ui.components.InternetCheck;
import ma.ensaj.geolocation.ui.components.SplashActivity;
import ma.ensaj.geolocation.ui.components.ViewDialog;
import ma.ensaj.geolocation.ui.home.HomeActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsActivity extends AppCompatActivity {
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    ContactsListAdapter dataAdapter = null;
    ProgressBar loader;
    RecyclerView recyclerView;
    List<ContactInfo> contactsInfoList;
    String imei;
    Button next_btn;
    MaterialCardView cardView;

    private DataService service;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        SharedPreferences settings = getSharedPreferences("MyGamePreferences", MODE_PRIVATE);
        imei = settings.getString("IMEI", "IMEI");

        Gson gson = new Gson();
        String json = settings.getString("CurrentUser", "");
        currentUser = gson.fromJson(json, User.class);


            service = RetrofitInstance.getInstance().create(DataService.class);
            recyclerView = findViewById(R.id.lstContacts);
            cardView = findViewById(R.id.card);
            loader = findViewById(R.id.loader);

            requestContactPermission();

            next_btn = findViewById(R.id.next_btn);
            next_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToHomeActivity();
                }
            });

    }

    private void getContacts(){
        String contactId;
        String displayName;
        String phoneNumber;
        contactsInfoList = new ArrayList<>();
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    ContactInfo contactsInfo = new ContactInfo();
                    contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                    contactsInfo.setContactId(contactId);

                    Cursor phoneCursor = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null);

                    if (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if(phoneNumber.length() == 10) {
                            StringBuilder sb = new StringBuilder(phoneNumber);
                            phoneNumber = "+212" + sb.deleteCharAt(0).toString();
                        }
                        contactsInfo.setPhoneNumber(phoneNumber);
                    }
                    phoneCursor.close();
                    if(contactsInfoList.isEmpty()) {
                        contactsInfoList.add(contactsInfo);
                    }
                    else {
                        boolean alreadyExistInTheList = false;
                        for(ContactInfo contact: contactsInfoList) {
                            if (contact.getPhoneNumber().equals(contactsInfo.getPhoneNumber())) {
                                alreadyExistInTheList = true;
                                break;
                            }
                        }
                        if(!alreadyExistInTheList) {
                            contactsInfoList.add(contactsInfo);
                        }
                    }
                }
            }
        }
        cursor.close();
    }

    private void filterAndDisplayContacts() {
        getContacts();
        List<ContactInfo> contacts = new ArrayList<>();
        for (ContactInfo contactInfo: contactsInfoList) {
            Call<User> call = service.getUserByTelephone(contactInfo.getPhoneNumber());
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                    User contactOwner = response.body();
                    boolean isUsingTheApp = contactOwner != null;
                    if(isUsingTheApp) {
                        List<Integer> currentUserFriends = new ArrayList<>();
                        if(!currentUser.getResponses().isEmpty()) {
                            for (FriendingState friendingState : currentUser.getResponses()) {
                                currentUserFriends.add(friendingState.getResponder().getId());
                            }
                        }
                        List<Integer> currentUserFriendRequests = new ArrayList<>();
                        if(!currentUser.getRequests().isEmpty()) {
                            for (FriendingState friendingState : currentUser.getRequests()) {
                                currentUserFriendRequests.add(friendingState.getRequester().getId());
                            }
                        }
                        if(!currentUserFriends.contains(contactOwner.getId()) && !currentUserFriendRequests.contains(contactOwner.getId())) {
                            contactInfo.setDisplayName(contactOwner.getNom() + " " + contactOwner.getPrenom());
                            contacts.add(contactInfo);
                        }
                    }
                    if(contactsInfoList.indexOf(contactInfo) == contactsInfoList.size()-1) {
                        loader.setVisibility(View.GONE);
                        displayContacts(contacts);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.d("Error", t.toString());
                }
            });
        }
    }

    private void displayContacts(List<ContactInfo> contacts) {
        recyclerView.setLayoutManager(new LinearLayoutManager(ContactsActivity.this));
        dataAdapter = new ContactsListAdapter(ContactsActivity.this, contacts, imei);
        recyclerView.setAdapter(dataAdapter);
    }

    public void requestContactPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_CONTACTS)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Autorisation de la lecture des contacts");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setMessage("Veuillez activer l'accès aux contacts.");
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(
                                new String[]
                                        {android.Manifest.permission.READ_CONTACTS}
                                , PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
                });
                builder.show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_CONTACTS},
                        PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        } else {
            filterAndDisplayContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                filterAndDisplayContacts();
            } else {
                requestContactPermission();
            }
        }
    }

    private void goToHomeActivity() {
        Transition transition = new Fade();
        transition.setDuration(400);
        transition.addTarget(cardView);

        TransitionManager.beginDelayedTransition(cardView, transition);
        cardView.setVisibility(View.GONE);
        Intent intent = new Intent(this, HomeActivity.class);
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