package ma.ensaj.geolocation.ui.components;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ma.ensaj.geolocation.R;
import ma.ensaj.geolocation.ui.authentication.AuthActivity;
import ma.ensaj.geolocation.ui.authentication.PhoneVerifyActivity;
import ma.ensaj.geolocation.ui.home.HomeActivity;

public class ViewDialog extends AppCompatActivity {
    private TextView text_view_title_dialog;
    private TextView text_view_description_dialog;
    private Button btn_dialog;
    private LinearLayout parent;
    private ImageView tower;

    private String title_dialog;
    private String description_dialog;
    private String context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_dialog);

        Intent intent = getIntent();
        title_dialog = intent.getStringExtra("title");
        description_dialog = intent.getStringExtra("description");
        context = intent.getStringExtra("context");
        show();
    }

    public void show() {
        parent = findViewById(R.id.dialog_layout);
        tower = findViewById(R.id.tower);
        text_view_title_dialog = (TextView) findViewById(R.id.title_dialog);
        text_view_title_dialog.setText(title_dialog);

        text_view_description_dialog = (TextView) findViewById(R.id.description_dialog);
        text_view_description_dialog.setText(description_dialog);

        btn_dialog = (Button) findViewById(R.id.btn_dialog);
        btn_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new InternetCheck(internet -> {
                    if (internet) {
                        Class dest = SplashActivity.class;
                        switch (context) {
                            case "splashActivity":
                                break;
                            case "authActivity":
                                dest = AuthActivity.class;
                                break;
                            case "phoneVerifyActivity":
                                dest = PhoneVerifyActivity.class;
                                break;
                            case "homeActivity":
                                dest = HomeActivity.class;
                                break;
                            default:
                                Log.d("Error", "No sash activity for the context " + context);
                                break;
                        }
                        Intent intent = new Intent(getApplicationContext(), dest);
                        startActivity(intent);
                    }
                    else {
                        TranslateAnimation shake = new TranslateAnimation(0, 0, 0, 20);
                        shake.setDuration(500);
                        shake.setInterpolator(new CycleInterpolator(2));
                        tower.startAnimation(shake);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(getIntent());
    }

    public Animation success() {
        Animation an = new RotateAnimation(360.0f, 0.0f, 0, 0);

        // Set the animation's parameters
        an.setDuration(300);               // duration in ms
        an.setRepeatCount(0);                // -1 = infinite repeated
        an.setRepeatMode(Animation.REVERSE); // reverses each repeat
        an.setFillAfter(true);
        return an;
    }
}