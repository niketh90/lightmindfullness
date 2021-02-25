package com.seraphic.lightapp.home_module.models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.seraphic.lightapp.R;
import com.seraphic.lightapp.home_module.views.MediaPlayFragment;
import com.seraphic.lightapp.utilities.RecyclerItemClickListener;

import java.util.List;

public class RecentListCategoryAdapter extends RecyclerView.Adapter<RecentListCategoryAdapter.MyViewHOlder>  {
    List<SessionCategories> mList;
    Context mContext;
    OnItemClickListner onItemClickListner;

    public RecentListCategoryAdapter(Context mContext, List<SessionCategories> mList,OnItemClickListner onItemClickListner) {
        this.mContext = mContext;
        this.mList = mList;
        this.onItemClickListner=onItemClickListner;
    }

    @NonNull
    @Override
    public MyViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.home_cate_items, parent, false);

        return new MyViewHOlder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHOlder holder, int position) {
        SessionCategories ses = mList.get(position);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
        holder.rvRecents.setLayoutManager(lm);
        holder.tvCatename.setText(ses.categoryName);
        RecentSessionAdapter recentSessionAdapter = new RecentSessionAdapter(mContext, ses.sessions);
        holder.rvRecents.setAdapter(recentSessionAdapter);
        holder.rvRecents.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                onItemClickListner.onClickSession(pos);
                Intent n = new Intent(mContext, MediaPlayFragment.class);
                n.putExtra("session", mList.get(position).sessions.get(pos));
                n.putExtra("dailySession",false);

                mContext.startActivity(n);
            }
        }));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class MyViewHOlder extends RecyclerView.ViewHolder {
        RecyclerView rvRecents;
        TextView tvCatename;

        public MyViewHOlder(@NonNull View itemView) {
            super(itemView);
            tvCatename = itemView.findViewById(R.id.tvCaetName);
            rvRecents = itemView.findViewById(R.id.rvRecentList);

        }
    }
}
