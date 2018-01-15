package com.fanfan.novel.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fanfan.novel.adapter.VideoDataAdapter;
import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.novel.common.base.simple.BaseRecyclerAdapter;
import com.fanfan.novel.db.manager.VideoDBManager;
import com.fanfan.novel.model.VideoBean;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.robot.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by android on 2018/1/6.
 */

public class DataVideoActivity extends BarBaseActivity {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, DataVideoActivity.class);
        context.startActivity(intent);
    }

    private VideoDBManager mVideoDBManager;

    private List<VideoBean> videoBeanList = new ArrayList<>();

    private VideoDataAdapter videoDataAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_data_video;
    }

    @Override
    protected void initView() {
        super.initView();
        videoDataAdapter = new VideoDataAdapter(mContext, videoBeanList);
        videoDataAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
        videoDataAdapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {

                showNeutralNotitleDialog(position);

                return false;
            }
        });
        recyclerView.setAdapter(videoDataAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void initData() {
        mVideoDBManager = new VideoDBManager();

        videoBeanList = mVideoDBManager.loadAll();
        if (videoBeanList != null && videoBeanList.size() > 0) {
            videoDataAdapter.refreshData(videoBeanList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.add_black, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                AddVideoActivity.newInstance(this, AddVideoActivity.ADD_VIDEO_REQUESTCODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddVideoActivity.ADD_VIDEO_REQUESTCODE) {
            if (resultCode == RESULT_OK) {
                videoBeanList = mVideoDBManager.loadAll();
                if (videoBeanList != null && videoBeanList.size() > 0) {
                    videoDataAdapter.refreshData(videoBeanList);
                }
            }
        }
    }

    private void showNeutralNotitleDialog(final int position) {
        DialogUtils.showNeutralNotitleDialog(this, "选择您要执行的操作", "删除所有",
                "删除此条", "修改此条", new DialogUtils.OnNeutralDialogListener() {
                    @Override
                    public void neutralText() {
                        if (mVideoDBManager.deleteAll()) {
                            videoDataAdapter.clear();
                            videoBeanList.clear();
                        }
                    }

                    @Override
                    public void negativeText() {
                        if (mVideoDBManager.delete(videoBeanList.get(position))) {
                            videoDataAdapter.removeItem(videoBeanList.get(position));
                            videoBeanList.remove(position);
                        }
                    }

                    @Override
                    public void positiveText() {
                        VideoBean videoBean = videoBeanList.get(position);
                        AddVideoActivity.newInstance(DataVideoActivity.this, videoBean.getId(), AddVideoActivity.ADD_VIDEO_REQUESTCODE);
                    }
                });
    }
}
