package ma.ensaj.geolocation.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ma.ensaj.geolocation.R;
import ma.ensaj.geolocation.beans.User;
import ma.ensaj.geolocation.ui.home.friends.FriendsFragment;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Fragment mapFragment;
    private Fragment friendsFragment;


    private static final int MAPS_FRAGMENT = 0;
    private static final int FRIENDS_FRAGMENT = 1;

    private NavigationView navigationView;

    public static final int MULTIPLE_PERMISSIONS = 10;
    String[] permissions= new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        checkPermissions();

        configureNavigationView();

        showFirstFragment();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item1:
                this.showFragment(MAPS_FRAGMENT);
                break;
            case R.id.item2:
                this.showFragment(FRIENDS_FRAGMENT);
                break;
            default:
                break;
        }
        return true;
    }

    private void showFragment(int fragmentIdentifier) {
        switch (fragmentIdentifier) {
            case MAPS_FRAGMENT:
                this.showMapsFragment();
                break;
            case FRIENDS_FRAGMENT:
                this.showFriendsFragment();
                break;
            default:
                break;
        }
    }

    private void showFirstFragment() {
        Fragment visibleFragment = getSupportFragmentManager().findFragmentById(R.id.activity_main_frame_layout);
        if (visibleFragment == null) {
            this.showFragment(MAPS_FRAGMENT);
            this.navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    private void showMapsFragment() {
        Intent intent = getIntent();
        boolean toMap = intent.getBooleanExtra("toMap", false);
        if(toMap) {
            User user = new User();
            user.setId(intent.getIntExtra("FriendId", 0));
            user.setNom(intent.getStringExtra("FriendNom"));
            user.setPrenom(intent.getStringExtra("FriendPrenom"));
            if (this.mapFragment == null) this.mapFragment = new MapFragment(user);
        }
        else {
            if (this.mapFragment == null) this.mapFragment = new MapFragment(null);
        }
        this.startTransactionFragment(this.mapFragment);
    }

    private void showFriendsFragment() {
        if (this.friendsFragment == null) this.friendsFragment = new FriendsFragment();
        this.startTransactionFragment(this.friendsFragment);
    }

    private void startTransactionFragment(Fragment fragment) {
        if (!fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_frame_layout, fragment).commit();
        }
    }

    private void configureNavigationView() {
        this.navigationView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(),p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permissions granted.
            } else {
                // no permissions granted.
            }
        }
    }

}