package app.droidinfo.ui;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.opengl.GLSurfaceView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import app.droidinfo.R;
import app.droidinfo.helper.BatteryHelper;
import app.droidinfo.helper.DisplayHelper;
import app.droidinfo.helper.SoCHelper;

public class SplashActivity extends AppCompatActivity implements GLSurfaceView.Renderer {

    private SharedPreferences sharedPreferences;
    private GLSurfaceView glSurfaceView;

    private String FONT = "FONT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Typeface typeface;

        sharedPreferences = getSharedPreferences("DroidInfo", MODE_PRIVATE);

        if (sharedPreferences.getString(FONT, "Roboto").equals("Google Sans")) {
            typeface = Typeface.createFromAsset(getAssets(), "fonts/" + "GoogleSans" + ".ttf");
        } else {
            typeface = Typeface.createFromAsset(getAssets(), "fonts/" + sharedPreferences.getString(FONT, "Roboto") + ".ttf");
        }

        TextView textViewDroidInfo = findViewById(R.id.textViewDroidInfoSplash);
        TextView textViewWelcomeTo = findViewById(R.id.textViewWelcomeToSplash);

        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));

        this.glSurfaceView = new GLSurfaceView(this);
        this.glSurfaceView.setRenderer(this);
        ((ViewGroup) textViewWelcomeTo.getParent()).addView(glSurfaceView);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryInfoReceiver, intentFilter);

        textViewDroidInfo.setTypeface(typeface);
        textViewWelcomeTo.setTypeface(typeface);

        sharedPreferences
                .edit()
                .putString("SCREEN_INCHES", DisplayHelper.getScreenSize(SplashActivity.this))
                .putString("RESOLUTION", DisplayHelper.getResolution(SplashActivity.this))
                .apply();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED){
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                /*boolean firstRun = sharedPreferences.getBoolean("FIRST_RUN", true);
                if (firstRun) {
                    startActivity(new Intent(SplashActivity.this, IntroActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }*/
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }, 2000);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            /*boolean firstRun = sharedPreferences.getBoolean("FIRST_RUN", true);
                            if (firstRun) {
                                startActivity(new Intent(SplashActivity.this, IntroActivity.class));
                                finish();
                            } else {
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            }*/
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        }
                    }, 2000);
                } else {
                    Toast.makeText(this, "Soory, you can't use app.", Toast.LENGTH_SHORT).show();
                    android.os.Process.killProcess(Process.myPid());
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        sharedPreferences
                .edit()
                .putString("GPU_VENDOR", SoCHelper.getGPUVendor(gl10))
                .putString("GPU_RENDERER", SoCHelper.getGPURenderer(gl10))
                .apply();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                glSurfaceView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

    }

    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Activity activity = (Activity) context;
            sharedPreferences
                    .edit()
                    .putString("BATTERY_HEALTH", BatteryHelper.getHealth(intent, activity))
                    .putString("BATTERY_PERCENTAGE", BatteryHelper.getPercentage(intent, activity))
                    .putString("BATTERY_PLUGGED_SOURCE", BatteryHelper.getPluggedSource(intent, activity))
                    .putString("BATTERY_STATUS", BatteryHelper.getStatus(intent, activity))
                    .putString("BATTERY_TECHNOLOGY", BatteryHelper.getTechnology(intent, activity))
                    .putString("BATTERY_TEMPERATURE", BatteryHelper.getTemperature(intent, activity))
                    .putString("BATTERY_VOLTAGE", BatteryHelper.getVoltage(intent, activity))
                    .putString("BATTERY_CAPACITY", BatteryHelper.getCapacity(context))
                    .apply();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(batteryInfoReceiver);
    }
}