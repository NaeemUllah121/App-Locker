package com.qsa.lock.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


//import com.squareup.leakcanary.RefWatcher;

public abstract class BaseFragment extends Fragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getOptionsMenuId() != -1) {
            setHasOptionsMenu(true);
        }

        initBefore(inflater, container, savedInstanceState);
        view = inflater.inflate(getContentViewId(), container, false);
        init(view);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater) {
        if (getOptionsMenuId() != -1) {
            inflater.inflate(getOptionsMenuId(), menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }


    protected void initBefore(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    }

    protected abstract int getContentViewId();


    protected abstract void init(View rootView);


    public View findViewById(int id) {
        return view.findViewById(id);
    }


    protected int getOptionsMenuId() {
        return -1;
    }


}


