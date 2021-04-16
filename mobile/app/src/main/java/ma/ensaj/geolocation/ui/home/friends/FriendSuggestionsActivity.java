package ma.ensaj.geolocation.ui.home.friends;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ma.ensaj.geolocation.R;
import ma.ensaj.geolocation.beans.FriendingState;
import ma.ensaj.geolocation.beans.User;
import ma.ensaj.geolocation.services.DataService;
import ma.ensaj.geolocation.services.RetrofitInstance;
import ma.ensaj.geolocation.ui.contacts.ContactInfo;
import ma.ensaj.geolocation.ui.contacts.ContactsListAdapter;
import ma.ensaj.geolocation.ui.home.HomeActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendSuggestionsActivity extends AppCompatActivity {
    private User currentUser;
    private RecyclerView recyclerView;
    ContactsListAdapter dataAdapter = null;
    ProgressBar loader;
    private List<ContactInfo> contactsInfoList;
    private String imei;
    private DataService service;
    private ImageView back_btn;
    private Toolbar toolbar;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_suggestions);
        configureToolbar();
        initViews();
        service = RetrofitInstance.getInstance().create(DataService.class);

        SharedPreferences settings = getSharedPreferences("MyGamePreferences", Context.MODE_PRIVATE);
        imei = settings.getString("IMEI", "IMEI");

        displayData();
    }

    private void displayData() {
        Call<User> call = service.getUserByDeviceImei(imei);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                currentUser = response.body();
                filterAndDisplayContacts(currentUser, contactsInfoList);
            }

            @Override
            public void onFailure(@NonNull Call<User> call, Throwable t) {
                Log.d("Error", t.toString());
            }
        });
    }


    private void initViews() {
        recyclerView = findViewById(R.id.friend_suggestions_list);
        back_btn = findViewById(R.id.back_btn);
        loader = findViewById(R.id.loader);
    }

    private void getContacts(){
        String contactId;
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

    private void filterAndDisplayContacts(User currentUser, List<ContactInfo> contactsInfoList) {
        Log.d("test", String.valueOf(contactsInfoList.toString() == null));
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
        recyclerView.setLayoutManager(new LinearLayoutManager(FriendSuggestionsActivity.this));
        dataAdapter = new ContactsListAdapter(FriendSuggestionsActivity.this, contacts, imei);
        recyclerView.setAdapter(dataAdapter);
    }

    public void configureToolbar() {
        Intent intent = getIntent();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String titleText = intent.getStringExtra("title");
        title = (TextView) findViewById(R.id.title);
        title.setText(titleText);

        setCloseAction();
    }

    public void setCloseAction() {
        back_btn = (ImageView) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendSuggestionsActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}