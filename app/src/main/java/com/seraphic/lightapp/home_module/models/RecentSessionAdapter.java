package com.seraphic.lightapp.home_module.models;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.seraphic.lightapp.R;

import org.w3c.dom.Text;

import java.util.List;

public class RecentSessionAdapter extends RecyclerView.Adapter<RecentSessionAdapter.MyViewHOlder> {
    List<SessionGetter> mList;
    Context mContext;

    public RecentSessionAdapter(Context mContext, List<SessionGetter> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public MyViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.healing_session_items, parent, false);

        return new MyViewHOlder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHOlder holder, int position) {
        SessionGetter ses = mList.get(position);
        Glide.with(mContext).load( ses.sessionThumbNail).centerCrop().placeholder(R.mipmap.placeholder1).into(holder.ivThumbnail);

//        Glide.with(mContext).load("http://ec2-3-21-237-151.us-east-2.compute.amazonaws.com:3000/profileImage_1587615583108_dummyImg.jpg").centerCrop().placeholder(R.mipmap.placeholder1).into(holder.ivThumbnail);
        holder.tvFileName.setText( ses.sessionName);


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHOlder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvFileName;

        public MyViewHOlder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvFileName = itemView.findViewById(R.id.tvFileName);

        }
    }
}
