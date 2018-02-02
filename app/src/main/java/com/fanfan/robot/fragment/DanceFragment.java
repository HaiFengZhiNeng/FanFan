package com.fanfan.robot.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfan.novel.activity.AddNavigationActivity;
import com.fanfan.novel.activity.DanceActivity;
import com.fanfan.novel.activity.DataNavigationActivity;
import com.fanfan.novel.common.base.BaseFragment;
import com.fanfan.novel.common.base.simple.BaseRecyclerAdapter;
import com.fanfan.novel.model.NavigationBean;
import com.fanfan.novel.presenter.SerialPresenter;
import com.fanfan.novel.service.cache.MusicCache;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.activity.DanceAddActivity;
import com.fanfan.robot.adapter.LocalDanceAdapter;
import com.fanfan.robot.adapter.LocalMusicAdapter;
import com.fanfan.robot.db.DanceDBManager;
import com.fanfan.robot.model.Dance;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by android on 2018/1/10.
 */

public class DanceFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_empty)
    TextView tvEmpty;


    public static DanceFragment newInstance() {
        return new DanceFragment();
    }

    private LocalDanceAdapter mAdapter;
    private List<Dance> dances;

    private DanceDBManager mDanceDBManager;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dence;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mAdapter = new LocalDanceAdapter(getActivity(), dances);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                playDance(dances.get(position));
            }
        });
        mAdapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                showNeutralNotitleDialog(position);
                return false;
            }
        });
    }

    private void playDance(Dance dance) {
        DanceActivity.newInstance(getActivity(), dance.getId());
    }

    @Override
    protected void initData() {
        mDanceDBManager = new DanceDBManager();
        dances = mDanceDBManager.loadAll();
        if (dances != null && dances.size() > 0) {
            tvEmpty.setVisibility(View.GONE);
            mAdapter.refreshData(dances);
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void setListener(View view) {

    }

    public void add() {
        DanceAddActivity.newInstance(getActivity(), this, DanceAddActivity.ADD_DANCE_REQUESTCODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DanceAddActivity.ADD_DANCE_REQUESTCODE) {
            if (resultCode == DanceAddActivity.ADD_DANCE_RESULTCODE) {
                dances = mDanceDBManager.loadAll();
                if (dances != null && dances.size() > 0) {
                    mAdapter.refreshData(dances);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                }
            }
        }
    }

    private void showNeutralNotitleDialog(final int position) {
        DialogUtils.showNeutralNotitleDialog(getActivity(), "选择您要执行的操作", "删除此舞蹈",
                "修改此舞蹈", "开始跳舞", new DialogUtils.OnNeutralDialogListener() {
                    @Override
                    public void neutralText() {
                        if (mDanceDBManager.delete(dances.get(position))) {
                            mAdapter.removeItem(dances.get(position));
                            dances.remove(position);
                        }
                    }

                    @Override
                    public void negativeText() {
                        Dance dance = dances.get(position);
                        DanceAddActivity.newInstance(getActivity(), DanceFragment.this, dance.getId(), DanceAddActivity.ADD_DANCE_REQUESTCODE);
                    }

                    @Override
                    public void positiveText() {
                        playDance(dances.get(position));
                    }
                });
    }
}
