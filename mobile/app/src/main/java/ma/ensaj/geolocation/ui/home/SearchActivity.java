package ma.ensaj.geolocation.ui.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ma.ensaj.geolocation.R;
import ma.ensaj.geolocation.beans.User;
import ma.ensaj.geolocation.services.DataService;
import ma.ensaj.geolocation.services.RetrofitInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements PositionsAdapter.ItemClickListener  {
    private ImageView back;
    private EditText search_edit;
    private String keyword = "";
    private DataService service;
    private User currentUser;
    private RecyclerView list;
    private PositionsAdapter positionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        service = RetrofitInstance.getInstance().create(DataService.class);

        SharedPreferences settings = getSharedPreferences("MyGamePreferences", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = settings.getString("CurrentUser", "");
        currentUser = gson.fromJson(json, User.class);

        list = findViewById(R.id.positions_list);

        back = findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        search_edit = findViewById(R.id.search);
        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                keyword = s.toString().toLowerCase();
                Call<List<User>> call1 = service.getAllFriends(currentUser);
                call1.enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                        List<User> friends = response.body();
                        List<User> showList = new ArrayList<>();

                        for (int i = 0; i < friends.size(); i++) {
                            String username = friends.get(i).getNom() + " " + friends.get(i).getPrenom();
                            if(keyword.length() != 0 && username.contains(keyword)) {
                                showList.add(friends.get(i));
                            }
                        }

                        if (keyword.length() == 0) {
                            friends.clear();
                        }

                        list.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                        positionsAdapter = new PositionsAdapter(SearchActivity.this, showList);
                        positionsAdapter.setClickListener(SearchActivity.this::onItemClick);
                        list.setAdapter(positionsAdapter);
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<User>> call, Throwable t) {
                        Log.d("Error", t.toString());
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    @Override
    public void onItemClick(View view, int position) {
        User friend = positionsAdapter.getItem(position);

        Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
        intent.putExtra("toMap", true);
        intent.putExtra("FriendId", friend.getId());
        intent.putExtra("FriendNom", friend.getNom());
        intent.putExtra("FriendPrenom", friend.getPrenom());
        startActivity(intent);
    }
}