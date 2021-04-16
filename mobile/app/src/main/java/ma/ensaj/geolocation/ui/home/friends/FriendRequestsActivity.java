package ma.ensaj.geolocation.ui.home.friends;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ma.ensaj.geolocation.R;
import ma.ensaj.geolocation.beans.FriendingState;
import ma.ensaj.geolocation.beans.User;
import ma.ensaj.geolocation.services.DataService;
import ma.ensaj.geolocation.services.RetrofitInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendRequestsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView title;
    private ImageView back_btn;
    private RecyclerView recyclerView;
    private User currentUser;
    private ListAdapter friendRequestListAdapter;
    private String imei;
    private DataService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);
        initViews();
        configureToolbar();
        SharedPreferences settings = getSharedPreferences("MyGamePreferences", Context.MODE_PRIVATE);
        imei = settings.getString("IMEI", "IMEI");

        service = RetrofitInstance.getInstance().create(DataService.class);
        Call<User> call = service.getUserByDeviceImei(imei);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                currentUser = response.body();
                displayFriendRequests(getFriendRequests(currentUser));
            }

            @Override
            public void onFailure(@NonNull Call<User> call, Throwable t) {
                Log.d("Error", t.toString());
            }
        });
    }

    private void initViews() {
        recyclerView = findViewById(R.id.friend_request_list);
    }

    private List<User> getFriendRequests(User currentUser) {
        List<User> friendRequests = new ArrayList<>();

        List<FriendingState> friendingStates = currentUser.getRequests();

        for (FriendingState friendingState: friendingStates) {
            if(friendingState.getStatus() == 1) {
                friendRequests.add(friendingState.getRequester());
            }
        }

        return friendRequests;
    }

    private void displayFriendRequests(List<User> friendRequests) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendRequestListAdapter = new ListAdapter(this, friendRequests, "friendRequests", imei);
        recyclerView.setAdapter(friendRequestListAdapter);
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
                finish();
            }
        });
    }
}