package it.k4ppaj.droidinfo.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import it.k4ppaj.droidinfo.R;
import it.k4ppaj.droidinfo.adapter.SimpleAdapter;
import it.k4ppaj.droidinfo.helper.SoCHelper;

public class SoCFragment extends Fragment {

    private Activity activity;
    private Context context;

    private String CLICKONITEM = "CLICKONITEM";

    private String GPU_VENDOR = "GPU_VENDOR";
    private String GPU_RENDERER = "GPU_RENDERER";

    private String USE_DEFAULT_INFORMATION = "USE_DEFAULT_INFORMATION";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_soc, container, false);

        SharedPreferences sharedPreferences = activity.getSharedPreferences("DroidInfo", Context.MODE_PRIVATE);

        ListView listView = layoutView.findViewById(R.id.listViewSoC);

        String[] stringInformation = new String[] {
                getString(R.string.CPUModel),
                getString(R.string.CPUCores),
                getString(R.string.CPUFreq),
                getString(R.string.CPUGovernor),
                getString(R.string.BogoMIPS),
                getString(R.string.GPUVendor),
                getString(R.string.GPURenderer),
                getString(R.string.OpenGLVersion)
        };
        String[] stringValues;

        if (!sharedPreferences.getBoolean(USE_DEFAULT_INFORMATION, false)) {
            stringValues = new String[] {
                    SoCHelper.getCPUModel(),
                    SoCHelper.getCPUCores(),
                    SoCHelper.getCPUFreq(),
                    SoCHelper.getCPUGovernor(0),
                    SoCHelper.getBogoMIPS(),
                    sharedPreferences.getString(GPU_VENDOR, getString(R.string.Unknown)),
                    sharedPreferences.getString(GPU_RENDERER, getString(R.string.Unknown)),
                    SoCHelper.getOpenGLVersion(context)
            };
        } else {
            stringValues = new String[] {
                    "Qualcomm® Snapdragon™ 835",
                    "8 cores",
                    "1.9 - 2.35 Ghz",
                    "interactive",
                    getString(R.string.Unknown),
                    "Qualcomm®",
                    "Adreno 540",
                    getString(R.string.Unknown)
            };
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(activity, stringInformation, stringValues);
        listView.setAdapter(simpleAdapter);

        if (sharedPreferences.getBoolean(CLICKONITEM, false)) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 4:
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(R.string.BogoMIPSInfoTitle);
                            builder.setMessage(R.string.BogoMIPSInfoMessage);
                            builder.setNegativeButton(R.string.Close, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                    }
                }
            });
        }

        return layoutView;
    }
}
