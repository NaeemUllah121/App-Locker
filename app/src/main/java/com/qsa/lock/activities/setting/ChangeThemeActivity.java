package com.qsa.lock.activities.setting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.qsa.lock.R;
import com.qsa.lock.adapters.ThemeRecyclerAdapter;
import com.qsa.lock.model.Theme;

import java.util.ArrayList;

public class ChangeThemeActivity extends AppCompatActivity {

    ArrayList<Theme>themeList=new ArrayList<>();
    RecyclerView recyclerView;
    ThemeRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chnage_theme);
        recyclerView = findViewById(R.id.recyclerView_id);

     //   getSupportActionBar().setTitle(" Select Theme ");
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ThemeRecyclerAdapter(themeArrayList(),this );
        recyclerView.setAdapter(adapter);

    }

    public ArrayList<Theme> themeArrayList() {
        Theme obj1=new Theme();
        obj1.setBackGround(R.drawable.blluecolor);
        themeList.add(obj1);

        Theme obj2=new Theme();
        obj2.setBackGround(R.drawable.pickcolor);
        themeList.add(obj2);

        Theme obj3=new Theme();
        obj3.setBackGround(R.drawable.wal);
        themeList.add(obj3);

        Theme obj4=new Theme();
        obj4.setBackGround(R.drawable.walllllll);
        themeList.add(obj4);

        return themeList;
    }
}