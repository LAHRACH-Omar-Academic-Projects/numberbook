package ma.ensaj.geolocation.ui.home;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ma.ensaj.geolocation.R;
import ma.ensaj.geolocation.beans.Config;
import ma.ensaj.geolocation.beans.FriendingState;
import ma.ensaj.geolocation.beans.Position;
import ma.ensaj.geolocation.beans.User;
import ma.ensaj.geolocation.services.DataService;
import ma.ensaj.geolocation.services.RetrofitInstance;
import ma.ensaj.geolocation.ui.components.SplashActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment {
    MapView mMapView;
    private GoogleMap googleMap;
    private User currentUser;
    private DataService service;
    private MaterialCardView materialCardView;
    private FloatingActionButton btn_position;
    private DrawerLayout drawerLayout;
    private BottomSheetBehavior behavior;
    private Button save;
    private RadioGroup gender_group;
    private ImageView mLeftArrow;
    private EditText rayon;
    private ImageView mRightArrow;
    private LinearLayout mBottomSheet;
    private ImageView toggler;
    private EditText search;
    private User friend;

    public MapFragment(User friend) {
        this.friend = friend;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        locationButton.setVisibility(View.GONE);

        gender_group = rootView.findViewById(R.id.gender_group);
        rayon = rootView.findViewById(R.id.rayon);
        save = rootView.findViewById(R.id.save);

        SharedPreferences settings = getActivity().getSharedPreferences("MyGamePreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config config = new Config();
                config.setId(12);
                config.setGender(getGender());
                config.setRayon(getRayon());

                Call<Boolean> call3 = service.saveConfig(config);
                call3.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                        Call<Config> call4 = service.getConfigById(12);
                        call4.enqueue(new Callback<Config>() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onResponse(@NonNull Call<Config> call, @NonNull Response<Config> response) {
                                Config config = response.body();
                                List<Float> distances = new ArrayList<>();
                                List<User> friends = new ArrayList<>();
                                int size = settings.getInt("Size", 0);
                                for(int i = 0; i < size; i++) {
                                    float distance = settings.getFloat("Distance" + i, 0);
                                    Gson gson = new Gson();
                                    String json = settings.getString("Friend" + i, "");
                                    User friend = gson.fromJson(json, User.class);
                                    distances.add(distance);
                                    friends.add(friend);
                                }

                                Log.d("test", friends.toString());
                                Log.d("test", distances.toString());




                                for(int i = 0; i < size; i++) {
                                    if(config.getRayon() > distances.get(i)) {
                                        if((config.getGender().toLowerCase().equals(friends.get(i).getSexe().toLowerCase())
                                                || config.getGender().toLowerCase().equals("tous"))) {

                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                NotificationChannel nc = new NotificationChannel("id1", "Simple Notification", NotificationManager.IMPORTANCE_DEFAULT);

                                                nc.setDescription("Description");
                                                nc.setShowBadge(true);
                                                nc.enableLights(true);
                                                nc.setLightColor(Color.BLUE);
                                                nc.enableVibration(true);
                                                nc.setVibrationPattern(new long[]{500, 1000, 500, 1000, 500});

                                                NotificationManager nm = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                                                nm.createNotificationChannel(nc);

                                                Notification notification = new Notification.Builder(getContext(),"id1")
                                                        .setSmallIcon(R.drawable.ic_launcher_background)
                                                        .setContentTitle("Ami")
                                                        .setContentText("Votre ami " + friends.get(i).getNom() + " " + friends.get(i).getPrenom() + " est proche de vous")
                                                        .build();

                                                NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                                                notificationManager.notify(i, notification);
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Config> call, Throwable t) {
                                Log.d("Error", t.toString());
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call<Boolean> call, Throwable t) {
                        Log.d("Error", t.toString());
                    }
                });
            }
        });


        mBottomSheet = rootView.findViewById(R.id.bottom_sheet);

        mLeftArrow = rootView.findViewById(R.id.bottom_sheet_right_arrow);
        mRightArrow = rootView.findViewById(R.id.bottom_sheet_right_arrow);

        initializeBottomSheet();

        btn_position = rootView.findViewById(R.id.btn_position);

        drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.activity_main_drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        toggler = rootView.findViewById(R.id.toggler);
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

        search = rootView.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        service = RetrofitInstance.getInstance().create(DataService.class);

        Gson gson = new Gson();
        String json = settings.getString("CurrentUser", "");
        currentUser = gson.fromJson(json, User.class);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(mMap -> {
            googleMap = mMap;

            if(friend != null) {
                Log.d("test", friend.toString());
                Call<Position> call2 = service.getLastPosition(friend);
                call2.enqueue(new Callback<Position>() {
                    @Override
                    public void onResponse(@NonNull Call<Position> call, @NonNull Response<Position> response) {
                        Position position1 = response.body();

                        LatLng friendPosition = new LatLng(position1.getLatitude(), position1.getLongitude());
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(friendPosition).zoom(12).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }

                    @Override
                    public void onFailure(@NonNull Call<Position> call, Throwable t) {
                        Log.d("Error", t.toString());
                    }
                });
            }

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        200);
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);

            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map));

            LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 6000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Position position = new Position();
                    position.setUser(currentUser);
                    position.setLongitude(location.getLongitude());
                    position.setLatitude(location.getLatitude());

                    btn_position.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LatLng friendPosition = new LatLng(position.getLatitude(), position.getLongitude());
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(friendPosition).zoom(18).build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
                    });

                    Call<Boolean> call = service.savePosition(position);
                    call.enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                            Log.d("Success", "position saved");
                        }

                        @Override
                        public void onFailure(@NonNull Call<Boolean> call, Throwable t) {
                            Log.d("Error", t.toString());
                        }
                    });

                    Call<List<User>> call1 = service.getAllFriends(currentUser);
                    call1.enqueue(new Callback<List<User>>() {
                        @Override
                        public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                            List<User> friends = response.body();
                            int i = 0;
                            for (User friend: friends) {
                                Gson gson = new Gson();
                                String myFriend = gson.toJson(friend);
                                prefEditor.putString("Friend" + i, myFriend);
                                prefEditor.apply();
                                i++;
                                Call<Position> call2 = service.getLastPosition(friend);
                                call2.enqueue(new Callback<Position>() {
                                    @Override
                                    public void onResponse(@NonNull Call<Position> call, @NonNull Response<Position> response) {
                                        int j = 0;
                                        Position position1 = response.body();
                                        float[] distance = new float[500];
                                        String distanceVal;

                                        Location.distanceBetween(position1.getLatitude(), position1.getLongitude(), position.getLatitude(), position.getLongitude(), distance);
                                        if(distance[0] > 1000) {
                                            distanceVal = Math.round(distance[0] / 1000) + " km";
                                        }
                                        else {
                                            distanceVal = Math.round(distance[0]) + " m";
                                        }

                                        prefEditor.putFloat("Distance" + j, distance[0]);
                                        prefEditor.apply();

                                        j++;

                                        LatLng friendPosition = new LatLng(position1.getLatitude(), position1.getLongitude());
                                        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).position(friendPosition).title(friend.getNom() + " " + friend.getPrenom()).snippet(distanceVal));
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<Position> call, Throwable t) {
                                        Log.d("Error", t.toString());
                                    }
                                });

                                prefEditor.putInt("Size", friends.size());
                                prefEditor.apply();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<List<User>> call, Throwable t) {
                            Log.d("Error", t.toString());
                        }
                    });
                }
                @Override
                public void onProviderDisabled(String provider) {
                    Log.d("GPS","disabled");
                }
                @Override
                public void onProviderEnabled(String provider) {
                    Log.d("GPS","enabled");
                }
                @Override
                public void onStatusChanged(String provider, int status,
                                            Bundle extras) {
                    Log.d("GPS","changed");
                }
            });
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    private void initializeBottomSheet() {

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (isAdded()) {
                    transitionBottomSheetBackgroundColor(slideOffset);
                    animateBottomSheetArrows(slideOffset);
                }
            }
        });
    }

    private void transitionBottomSheetBackgroundColor(float slideOffset) {
        int colorFrom = getResources().getColor(R.color.gray);
        int colorTo = getResources().getColor(R.color.white);
        Drawable drawable = getResources().getDrawable(R.drawable.behavior_background);
        drawable.setColorFilter(interpolateColor(slideOffset,
                colorFrom, colorTo), PorterDuff.Mode.SRC_ATOP);
        mBottomSheet.setBackground(drawable);
    }

    private void animateBottomSheetArrows(float slideOffset) {
        mLeftArrow.setRotation(slideOffset * -180);
        mRightArrow.setRotation(slideOffset * 180);
    }

    private int interpolateColor(float fraction, int startValue, int endValue) {
        int startA = (startValue >> 24) & 0xff;
        int startR = (startValue >> 16) & 0xff;
        int startG = (startValue >> 8) & 0xff;
        int startB = startValue & 0xff;
        int endA = (endValue >> 24) & 0xff;
        int endR = (endValue >> 16) & 0xff;
        int endG = (endValue >> 8) & 0xff;
        int endB = endValue & 0xff;
        return ((startA + (int) (fraction * (endA - startA))) << 24) |
                ((startR + (int) (fraction * (endR - startR))) << 16) |
                ((startG + (int) (fraction * (endG - startG))) << 8) |
                ((startB + (int) (fraction * (endB - startB))));
    }

    public String getGender() {
        int checkedId = gender_group.getCheckedRadioButtonId();
        if(checkedId == R.id.homme) {
            return "Homme";
        }
        else if(checkedId == R.id.femme) {
            return "Femme";
        }
        else {
            return "Tous";
        }
    }

    public int getRayon() {
        int rayonVal = 0;
        rayonVal = Integer.parseInt(String.valueOf(rayon.getText()));
        return rayonVal;
    }
}
