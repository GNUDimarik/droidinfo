package it.k4ppaj.droidinfo.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import it.k4ppaj.droidinfo.R;
import it.k4ppaj.droidinfo.adapter.SimpleAdapter;
import it.k4ppaj.droidinfo.helper.DeviceHelper;

public class DeviceFragment extends Fragment {

    private Activity context;

    private String USE_DEFAULT_INFORMATION = "USE_DEFAULT_INFORMATION";

    public DeviceFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_device, container, false);

        SharedPreferences sharedPreferences = context.getSharedPreferences("DroidInfo", Context.MODE_PRIVATE);

        ListView listView = layoutView.findViewById(R.id.listViewDevice);

        String[] stringInformation = new String[] {
                getString(R.string.Model),
                getString(R.string.Manufacturer),
                getString(R.string.RAM),
                getString(R.string.InternalStorage),
                getString(R.string.ExternalStorage),
                getString(R.string.RootAccess)
        };
        String[] stringValues;

        if (!sharedPreferences.getBoolean(USE_DEFAULT_INFORMATION, false)) {
            stringValues = new String[] {
                    DeviceHelper.getModel(),
                    DeviceHelper.getManufacturer(),
                    DeviceHelper.getRAM(),
                    DeviceHelper.getInternalStorage(),
                    DeviceHelper.getExternalStorage(context),
                    DeviceHelper.getRootAccess(context)
            };
        } else {
            stringValues = new String[] {
                    "Pixel 2 XL",
                    "Google LLC",
                    "4 GB",
                    "64 GB",
                    getString(R.string.NotMounted),
                    getString(R.string.Yes)
            };
        }



        SimpleAdapter simpleAdapter = new SimpleAdapter(context, stringInformation, stringValues);
        listView.setAdapter(simpleAdapter);
        return layoutView;
    }
}
