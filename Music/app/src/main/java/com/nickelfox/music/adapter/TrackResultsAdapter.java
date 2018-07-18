package com.nickelfox.music.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Joiner;
import com.nickelfox.music.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

public class TrackResultsAdapter extends RecyclerView.Adapter<TrackResultsAdapter.ViewHolder> {

    private List<Track> mItems = new ArrayList<>();
    private Context mContext;
    private ItemSelectedListener mListener;
    private String lastPlayingId = "";
    private List<Track> selectedTracks = new ArrayList<>();
    private Boolean showSelector = false;

    public TrackResultsAdapter(Context context, ItemSelectedListener listener) {
        mContext = context;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Track item = mItems.get(position);

        holder.title.setText(item.name);

        List<String> names = new ArrayList<>();
        for (ArtistSimple i : item.artists) {
            names.add(i.name);
        }
        Joiner joiner = Joiner.on(", ");
        holder.subtitle.setText(joiner.join(names));

        Image image = item.album.images.get(0);
        if (image != null) {
            Picasso.with(mContext).load(image.url).into(holder.image);
        }

        if (showSelector) {
            holder.songSelector.setVisibility(View.VISIBLE);
            holder.play_pause.setVisibility(View.GONE);
            if (selectedTracks.contains(item)) {
                holder.songSelector.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_radio_checked));
            } else {
                holder.songSelector.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_radio_unchecked));
            }
        }
        else {
            holder.songSelector.setVisibility(View.GONE);
            holder.play_pause.setVisibility(View.VISIBLE);
            if (lastPlayingId.equals(item.id)) {
                holder.play_pause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pause));
            } else {
                holder.play_pause.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_play));
            }
        }

        holder.play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemSelected(item);
                if (!lastPlayingId.equals("")) {
                    if (mItems.get(holder.getAdapterPosition()).id.equals(lastPlayingId)) {
                        lastPlayingId = "";
                    } else {
                        lastPlayingId = mItems.get(holder.getAdapterPosition()).id;
                    }
                } else {
                    lastPlayingId = mItems.get(holder.getAdapterPosition()).id;
                }
                notifyDataSetChanged();
            }
        });

        holder.songSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTracks.contains(item)) {
                    selectedTracks.remove(item);
                } else {
                    if (selectedTracks.size() < 2)
                        selectedTracks.add(item);
                    else
                        Toast.makeText(mContext, "Cannot select more than 2 tracks", Toast.LENGTH_SHORT).show();

                }
                notifyDataSetChanged();
            }
        });

    }

    public void hideSelector() {
        showSelector = false;
        selectedTracks.clear();
        notifyDataSetChanged();
    }

    public void showSelector() {
        showSelector = true;
        lastPlayingId = "";
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void clearData() {
        mItems.clear();
        selectedTracks.clear();
    }

    public void addData(List<Track> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public List<Track> getSelectedTracks() {
        return selectedTracks;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title, subtitle;
        public CircleImageView image;
        public ImageView songSelector, play_pause;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.entity_title);
            subtitle = itemView.findViewById(R.id.entity_subtitle);
            image = itemView.findViewById(R.id.entity_image);
            play_pause = itemView.findViewById(R.id.play_pause);
            songSelector = itemView.findViewById(R.id.selector);
        }

    }

    public interface ItemSelectedListener {
        void onItemSelected(Track item);
    }
}
