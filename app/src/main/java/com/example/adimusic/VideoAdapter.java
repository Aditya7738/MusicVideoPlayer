package com.example.adimusic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.BaseRequestOptions;
//import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    ArrayList<VideoModel> videoModelArrayList;
    Context context;
    VideoClickInterface videoClickInterface;

    VideoModel videoModel;
    Uri uri;

    public VideoAdapter(ArrayList<VideoModel> videoModelArrayList, Context context, VideoClickInterface videoClickInterface, Uri uri) {
        this.videoModelArrayList = videoModelArrayList;
        this.context = context;
        this.videoClickInterface = videoClickInterface;
        this.uri = uri;
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_rv_item, parent, false);
        return new ViewHolder(view);
    }

    //setting data to each view
    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
         videoModel = videoModelArrayList.get(position);

        //VideoModel videoModel = videoModelArrayList.get(holder.getAdapterPosition());
        holder.thumbnailIV.setImageBitmap(videoModel.getThumbnail());

        //RequestOptions requestOptions = new RequestOptions();
        /*BaseRequestOptions<BaseRequestOptions> baseRequestOptions = new BaseRequestOptions<BaseRequestOptions>() {
            @NonNull
            @Override
            public BaseRequestOptions placeholder(int resourceId) {
                return super.placeholder(resourceId);
            }
        };*/
        /*Glide.with(context)
                .asBitmap()
                .load(videoModelArrayList.get(position).getUri()) //or use get(holder.getAdapterPosition()).uri;
                //.apply(requestOptions.placeholder(R.mipmap.ic_launcher_round).centerCrop()) //default
                .into(holder.thumbnailIV);*/


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoClickInterface.onVideoClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView thumbnailIV;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailIV = itemView.findViewById(R.id.thumimg);

        }
    }

    public interface VideoClickInterface{
        void onVideoClick(int position);
    }
}
