package ma.ensaj.geolocation.ui.home.friends;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ma.ensaj.geolocation.R;
import ma.ensaj.geolocation.beans.FriendingState;
import ma.ensaj.geolocation.beans.User;
import ma.ensaj.geolocation.services.DataService;
import ma.ensaj.geolocation.services.RetrofitInstance;
import ma.ensaj.geolocation.ui.contacts.ContactInfo;
import ma.ensaj.geolocation.ui.contacts.ContactsActivity;
import ma.ensaj.geolocation.ui.contacts.ContactsListAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsFragment extends Fragment {
    private User currentUser;
    private View root;
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private Button requests_btn;
    private Button friends_btn;
    private Button friend_suggetions_btn;
    private DataService service;
    private String imei;
    private DrawerLayout drawerLayout;
    private ImageView toggler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_friends, container, false);
        initViews();
        service = RetrofitInstance.getInstance().create(DataService.class);

        drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.activity_main_drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        toggler = root.findViewById(R.id.toggler);
        toggler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        SharedPreferences settings = getActivity().getSharedPreferences("MyGamePreferences", Context.MODE_PRIVATE);
        imei = settings.getString("IMEI", "IMEI");

        Call<User> call = service.getUserByDeviceImei(imei);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                currentUser = response.body();
                displayFriends(getFriends(currentUser));
            }

            @Override
            public void onFailure(@NonNull Call<User> call, Throwable t) {
                Log.d("Error", t.toString());
            }
        });

        requests_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FriendRequestsActivity.class);
                intent.putExtra("title", "Les demandes");
                startActivity(intent);
            }
        });
        friends_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyFriendsActivity.class);
                intent.putExtra("title", "Tous les amis");
                startActivity(intent);
            }
        });
        friend_suggetions_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FriendSuggestionsActivity.class);
                intent.putExtra("title", "Suggestion d'amis");
                startActivity(intent);
            }
        });
        return root;
    }

    private void initViews() {
        recyclerView = root.findViewById(R.id.listFriends);
        requests_btn = root.findViewById(R.id.requests_btn);
        friends_btn = root.findViewById(R.id.friends_btn);
        friend_suggetions_btn = root.findViewById(R.id.friend_suggestions_btn);
    }

    private List<User> getFriends(User currentUser) {
        List<User> friends = new ArrayList<>();

        List<FriendingState> friendingStates = currentUser.getResponses();
        for (FriendingState friendingState: friendingStates) {
            if(friendingState.getStatus() == 1) {
                friends.add(friendingState.getResponder());
            }
        }

        return friends;
    }

    private void displayFriends(List<User> friends) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listAdapter = new ListAdapter(getContext(), friends, "friendsWaiting", imei);
        recyclerView.setAdapter(listAdapter);
    }
}