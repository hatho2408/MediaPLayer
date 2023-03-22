package com.app1.musicmediaplayer;

import static com.app1.musicmediaplayer.MainActivity.albums;
import static com.app1.musicmediaplayer.MainActivity.musicFiles;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class AlbumFragment extends Fragment {
    RecyclerView recyclerView;
    MusicAdapter musicAdapter;
    AlbumAdapter albumAdapter;

    public AlbumFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView=view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        if(!(albums.size()<1))
        {
            albumAdapter = new AlbumAdapter(getContext(),albums);
            recyclerView.setAdapter(albumAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        }
        return view;
    }
}