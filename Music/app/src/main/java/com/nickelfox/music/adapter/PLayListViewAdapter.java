package com.nickelfox.music.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.nickelfox.music.R;


import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTracksInformation;

public class PLayListViewAdapter extends RecyclerView.Adapter<PLayListViewAdapter.ViewHolder> {

    private OnClickListener mListener;

    private List<PlaylistSimple> list = new ArrayList<>();
    private Context mContext;

    public PLayListViewAdapter(Context context, List<PlaylistSimple> eventList, OnClickListener listener) {
        this.list = eventList;
        this.mContext = context;
        this.mListener = listener;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlist_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Glide.with(mContext).load(list.get(position).images.get(0).url).into(holder.imgBanner);

        holder.imgBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) ;
                mListener.onClick(list.get(position).tracks);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView imgBanner;
        ToggleButton play_pause;


        public ViewHolder(View view) {
            super(view);

            imgBanner = view.findViewById(R.id.image_playlist);


        }


    }

    public static interface OnClickListener {
        void onClick(PlaylistTracksInformation playlistTracksInformation);
    }

}
