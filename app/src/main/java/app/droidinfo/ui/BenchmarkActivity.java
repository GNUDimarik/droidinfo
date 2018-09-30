package app.droidinfo.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import app.droidinfo.R;
import app.droidinfo.adapter.BenchmarkAdapter;

public class BenchmarkActivity extends AppCompatActivity {

    private boolean IS_COMPLETED = false;

    private String[] stringInformation;
    private String[] stringValues;
    private String score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benckmark);
        Toolbar toolbar = findViewById(R.id.toolbarBenchmark);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BenchmarkActivity.this, MainActivity.class));
                finish();
            }
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.fabBenchmark);
        Spinner spinner = findViewById(R.id.spinnerBenchmark);
        Button buttonRunBenchmark = findViewById(R.id.buttonRunBenckmark);
        final ListView listView = findViewById(R.id.listViewBenchmarkResult);

        stringInformation = new String[] {
                getString(R.string.Score),
                getString(R.string.TimeTaken)
        };

        String[] stringChoices = new String[] {
                getString(R.string.CPUSHA1),
                getString(R.string.GPUOpenGL)
        };
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, stringChoices) {
            @Override
            public boolean isEnabled(int position) {
                return position == 0;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        @NonNull ViewGroup parent) {
                View mView = super.getDropDownView(position, convertView, parent);
                TextView mTextView = (TextView) mView;
                if (position == 0) {
                    mTextView.setTextColor(Color.WHITE);
                } else {
                    mTextView.setTextColor(Color.GRAY);
                }
                return mView;
            }
        };

        spinner.setAdapter(spinnerAdapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!IS_COMPLETED) {
                    Snackbar.make(view, R.string.StartTestBeforeShare, Snackbar.LENGTH_SHORT)
                            .setAction("OK", null)
                            .show();
                } else {
                    Intent shareIntent = new Intent().setAction(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, "Hi, my CPU has ended the benchmark with a score of " + score + " points, hurry up try it too!");
                    startActivity(Intent.createChooser(shareIntent, "Share through..."));
                }
            }
        });

        buttonRunBenchmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long tsLong = System.nanoTime();
                for (Integer i = 0; i < 20000; i++) {
                    computeSHAHash("DroidInfo");
                }
                long timeTakenLong = System.nanoTime() - tsLong;
                int roundnumber = Math.round(timeTakenLong / 10000000);
                double timeTaken = (double) timeTakenLong / 1000000000;
                score =  String.valueOf(roundnumber);
                stringValues = new String[] {
                        score,
                        String.format(Locale.ENGLISH, "%.2f", timeTaken) + " " + getString(R.string.Second)
                };
                BenchmarkAdapter classicAdapter = new BenchmarkAdapter(BenchmarkActivity.this, stringInformation, stringValues);
                listView.setAdapter(classicAdapter);
                IS_COMPLETED = true;
            }
        });
    }

    private void computeSHAHash(String password) {
        MessageDigest mdSha1 = null;
        try {
            mdSha1 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        try {
            mdSha1.update(password.getBytes("ASCII"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] data = mdSha1.digest();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(BenchmarkActivity.this, MainActivity.class));
        finish();
        super.onBackPressed();
    }

}
