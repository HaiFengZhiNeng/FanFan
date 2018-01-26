package com.fanfan.robot.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fanfan.novel.activity.AddNavigationActivity;
import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.novel.common.glide.GlideRoundTransform;
import com.fanfan.novel.service.PlayService;
import com.fanfan.novel.service.cache.MusicCache;
import com.fanfan.novel.service.music.OnPlayerEventListener;
import com.fanfan.novel.utils.music.MusicUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.adapter.FragmentAdapter;
import com.fanfan.robot.fragment.DanceFragment;
import com.fanfan.robot.fragment.PlayFragment;
import com.fanfan.robot.fragment.SongFragment;
import com.fanfan.robot.model.Music;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by android on 2018/1/6.
 */

public class MultimediaActivity extends BarBaseActivity implements OnPlayerEventListener, ViewPager.OnPageChangeListener {

    @BindView(R.id.tv_local_music)
    TextView tvLocalMusic;
    @BindView(R.id.tv_local_dance)
    TextView tvLocalDance;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.v_music_select)
    View vMusicSelect;
    @BindView(R.id.v_dance_select)
    View vDanceSelect;

    private boolean isPlayFragmentShow = false;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, MultimediaActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private SongFragment songFragment;
    private DanceFragment danceFragment;

    private PlayFragment mPlayFragment;

    private Menu mMenu;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_multimedia;
    }

    @Override
    protected void initView() {
        super.initView();

        songFragment = SongFragment.newInstance();
        danceFragment = DanceFragment.newInstance();

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(songFragment);
        adapter.addFragment(danceFragment);
        mViewPager.setAdapter(adapter);

        tvLocalMusic.setSelected(true);
        vMusicSelect.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initData() {
        PlayService playService = MusicCache.get().getPlayService();
        if (playService == null) {
            throw new NullPointerException("play service is null");
        }
        playService.setOnPlayEventListener(this);
    }

    @Override
    protected void setListener() {
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        mMenu = menu;
        getMenuInflater().inflate(R.menu.add_white, menu);
        mMenu.findItem(R.id.add).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                if (danceFragment != null && danceFragment.isAdded()) {
                    danceFragment.add();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                songFragment.onRestoreInstanceState(savedInstanceState);
            }
        });
    }

    @OnClick({R.id.tv_local_music, R.id.tv_local_dance})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_local_music:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tv_local_dance:
                mViewPager.setCurrentItem(1);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            tvLocalMusic.setSelected(true);
            vMusicSelect.setVisibility(View.VISIBLE);
            tvLocalDance.setSelected(false);
            vDanceSelect.setVisibility(View.GONE);
            mMenu.findItem(R.id.add).setVisible(false);
        } else {
            if (songFragment != null && songFragment.isAdded()) {
                songFragment.stopMusic();
            }
            tvLocalMusic.setSelected(false);
            vMusicSelect.setVisibility(View.GONE);
            tvLocalDance.setSelected(true);
            vDanceSelect.setVisibility(View.VISIBLE);
            mMenu.findItem(R.id.add).setVisible(true);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void showPlayingFragment() {
        if (isPlayFragmentShow) {
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_slide_up, 0);
        if (mPlayFragment == null) {
            mPlayFragment = new PlayFragment();
            ft.replace(android.R.id.content, mPlayFragment);
        } else {
            ft.show(mPlayFragment);
        }
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = true;
    }

    private void hidePlayingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(0, R.anim.fragment_slide_down);
        ft.hide(mPlayFragment);
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = false;
    }

    @Override
    public void onBackPressed() {
        if (mPlayFragment != null && isPlayFragmentShow) {
            hidePlayingFragment();
            return;
        }
        if (songFragment != null && songFragment.isAdded()) {
            songFragment.stopMusic();
        }
        super.onBackPressed();
    }

    @Override
    public void onChange(Music music) {
        if (songFragment != null && songFragment.isAdded()) {
            songFragment.onChange(music);
        }
        if (mPlayFragment != null && mPlayFragment.isAdded()) {
            mPlayFragment.onChange(music);
        }
    }

    @Override
    public void onPlayerStart() {
        if (songFragment != null && songFragment.isAdded()) {
            songFragment.onPlayerStart();
        }
        if (mPlayFragment != null && mPlayFragment.isAdded()) {
            mPlayFragment.onPlayerStart();
        }
    }

    @Override
    public void onPlayerPause() {
        if (songFragment != null && songFragment.isAdded()) {
            songFragment.onPlayerPause();
        }
        if (mPlayFragment != null && mPlayFragment.isAdded()) {
            mPlayFragment.onPlayerPause();
        }
    }

    @Override
    public void onPublish(int progress) {
        if (songFragment != null && songFragment.isAdded()) {
            songFragment.onPublish(progress);
        }
        if (mPlayFragment != null && mPlayFragment.isAdded()) {
            mPlayFragment.onPublish(progress);
        }
    }

    @Override
    public void onBufferingUpdate(int percent) {
        if (songFragment != null && songFragment.isAdded()) {
            songFragment.onBufferingUpdate(percent);
        }
        if (mPlayFragment != null && mPlayFragment.isAdded()) {
            mPlayFragment.onBufferingUpdate(percent);
        }
    }

    @Override
    public void onTimer(long remain) {
        if (songFragment != null && songFragment.isAdded()) {
            songFragment.onTimer(remain);
        }
        if (mPlayFragment != null && mPlayFragment.isAdded()) {
            mPlayFragment.onTimer(remain);
        }
    }

    @Override
    public void onMusicListUpdate() {
        if (songFragment != null && songFragment.isAdded()) {
            songFragment.onMusicListUpdate();
        }
        if (mPlayFragment != null && mPlayFragment.isAdded()) {
            mPlayFragment.onMusicListUpdate();
        }
    }

}
