package com.banzhi.sample;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.banzhi.permission.AndPermisstion;
import com.banzhi.permission.PermissionCallback;

import java.util.List;


public class PermissionFragment extends Fragment {


    public PermissionFragment() {
    }


    public static PermissionFragment newInstance() {
        PermissionFragment fragment = new PermissionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_permission, container, false);
        view.findViewById(R.id.btn_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });
        return view;
    }


    private void request() {
        AndPermisstion.getInstance()
                .newBuilder()
                .permissions(Manifest.permission.CALL_PHONE,Manifest.permission.ACCESS_FINE_LOCATION)
                .request(new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        Toast.makeText(getActivity(), "授权成功!", Toast.LENGTH_SHORT).show();
                        Log.e("MainActivity", "onGranted");
                    }

                    @Override
                    public void onDenied(List<String> list) {
                        Toast.makeText(getActivity(), "授权失败!", Toast.LENGTH_SHORT).show();
                        Log.e("MainActivity", "onDenied: ******>" + list);
                    }
                });
    }

}
