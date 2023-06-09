package com.app1.musicmediaplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.MyHolder> {
    private Context mContext;
    static ArrayList<MusicFiles>albumFiles;

    View view;
    public AlbumDetailsAdapter(Context mContext,ArrayList<MusicFiles>albumFiles)
    {
        this.mContext=mContext;
        this.albumFiles=albumFiles;

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.music_items,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.album_name.setText(albumFiles.get(holder.getAdapterPosition()).getTitle());

        byte[]  image ;
        try {
             image = getAlbum(albumFiles.get(holder.getAdapterPosition()).getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (image != null) {
            Glide.with(mContext).asBitmap().load(image).into(holder.album_image);
        } else {
            Glide.with(mContext).load(R.drawable.exc).into(holder.album_image);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(mContext,PlayerActivity.class);
                intent.putExtra("sender","albumDetails");
                intent.putExtra("position",position);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder
    {
        ImageView album_image;
        TextView album_name;
        public MyHolder(View itemView)
        {
            super(itemView);
            album_image=itemView.findViewById((R.id.music_img));
            album_name=itemView.findViewById((R.id.music_file_name));
        }
    }
    private  byte[]getAlbum(String uri) throws IOException {
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art=retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}
