package com.qsa.lock.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.qsa.lock.R;
import com.qsa.lock.activities.setting.ThemeHomeActivity;
import com.qsa.lock.model.Theme;

import java.util.List;

public class ThemeRecyclerAdapter extends RecyclerView.Adapter<ThemeRecyclerAdapter.ThemeViewHolder> {

    List<Theme> themes;
    Context context;

    public ThemeRecyclerAdapter(List<Theme> themes, Context context) {
        this.themes = themes;
        this.context = context;
    }

    @NonNull
    @Override
    public ThemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_change_theme, parent, false);
        return new ThemeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeViewHolder holder, int position) {
        Theme theme=themes.get(position);
        holder.imageView.setImageResource(theme.getBackGround());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ThemeHomeActivity.class);
                intent.putExtra("theme",new Gson().toJson(theme));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return themes.size();
    }

    public class ThemeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ThemeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.theme_id);
        }
    }
}
