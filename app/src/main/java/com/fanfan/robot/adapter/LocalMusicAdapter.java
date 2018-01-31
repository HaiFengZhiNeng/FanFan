package com.fanfan.robot.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fanfan.novel.common.base.simple.BaseRecyclerViewHolder;
import com.fanfan.novel.common.base.simple.SimpleAdapter;
import com.fanfan.novel.common.glide.GlideRoundTransform;
import com.fanfan.novel.service.PlayService;
import com.fanfan.novel.utils.FucUtil;
import com.fanfan.novel.utils.music.MusicUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.model.Music;
import com.seabreeze.log.Print;

import java.util.List;

/**
 * Created by android on 2018/1/10.
 */

public class LocalMusicAdapter extends SimpleAdapter<Music> {

    private int mPlayingPosition;

    public LocalMusicAdapter(Context context, List<Music> musicList) {
        super(context, R.layout.view_holder_music, musicList);
    }

    @Override
    protected void convert(BaseRecyclerViewHolder viewHolder, Music music, int pos) {
        View vPlaying = viewHolder.getView(R.id.v_playing);
        if (pos == mPlayingPosition) {
            vPlaying.setVisibility(View.VISIBLE);
        } else {
            vPlaying.setVisibility(View.INVISIBLE);
        }

        ImageView ivCover = viewHolder.getImageView(R.id.iv_cover);
        RequestOptions options = new RequestOptions()
                .error(R.mipmap.default_cover)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .transform(new GlideRoundTransform());
        Glide.with(context)
                .load(music.getAlbumId() == -1 ? MusicUtils.getMediaDataAlbumPic(music.getPath()) : MusicUtils.getMediaStoreAlbumCoverUri(music.getAlbumId()))
                .apply(options)
                .into(ivCover);


        viewHolder.getTextView(R.id.tv_title).setText(music.getTitle());

        String artist = FucUtil.getArtistAndAlbum(music.getArtist(), music.getAlbum());
        viewHolder.getTextView(R.id.tv_artist).setText(artist);

        ImageView ivMore = viewHolder.getImageView(R.id.iv_more);


        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void updatePlayingPosition(PlayService playService) {
        int forPos = mPlayingPosition;
        if (playService.getPlayingMusic() != null) {
            mPlayingPosition = playService.getPlayingPosition();
        } else {
            mPlayingPosition = -1;
        }
        if(mPlayingPosition != -1) {
            notifyItemChanged(forPos);
            notifyItemChanged(mPlayingPosition);
        }
    }

    public int getPlayingPosition() {
        return mPlayingPosition;
    }
}
