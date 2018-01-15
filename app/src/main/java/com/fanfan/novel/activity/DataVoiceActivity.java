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

import com.fanfan.novel.adapter.VoiceDataAdapter;
import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.novel.common.base.simple.BaseRecyclerAdapter;
import com.fanfan.novel.db.manager.VoiceDBManager;
import com.fanfan.novel.model.VoiceBean;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.robot.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by android on 2018/1/8.
 */

public class DataVoiceActivity extends BarBaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, DataVoiceActivity.class);
        context.startActivity(intent);
    }

    private VoiceDBManager mVoiceDBManager;

    private List<VoiceBean> voiceBeanList = new ArrayList<>();

    private VoiceDataAdapter voiceDataAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_data_voice;
    }

    @Override
    protected void initView() {
        super.initView();
        voiceDataAdapter = new VoiceDataAdapter(mContext, voiceBeanList);
        voiceDataAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
        voiceDataAdapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {

                showNeutralNotitleDialog(position);

                return false;
            }
        });
        recyclerView.setAdapter(voiceDataAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void initData() {
        mVoiceDBManager = new VoiceDBManager();

        voiceBeanList = mVoiceDBManager.loadAll();
        if (voiceBeanList != null && voiceBeanList.size() > 0) {
            voiceDataAdapter.refreshData(voiceBeanList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.add_white, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                AddVoiceActivity.newInstance(this, AddVoiceActivity.ADD_VOICE_REQUESTCODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddVoiceActivity.ADD_VOICE_REQUESTCODE) {
            if (resultCode == RESULT_OK) {
                voiceBeanList = mVoiceDBManager.loadAll();
                if (voiceBeanList != null && voiceBeanList.size() > 0) {
                    voiceDataAdapter.refreshData(voiceBeanList);
                }
            }
        }
    }

    private void showNeutralNotitleDialog(final int position) {
        DialogUtils.showNeutralNotitleDialog(this, "选择您要执行的操作", "删除所有",
                "删除此条", "修改此条", new DialogUtils.OnNeutralDialogListener() {
                    @Override
                    public void neutralText() {
                        if (mVoiceDBManager.deleteAll()) {
                            voiceDataAdapter.clear();
                            voiceBeanList.clear();
                        }
                    }

                    @Override
                    public void negativeText() {
                        if (mVoiceDBManager.delete(voiceBeanList.get(position))) {
                            voiceDataAdapter.removeItem(voiceBeanList.get(position));
                            voiceBeanList.remove(position);
                        }
                    }

                    @Override
                    public void positiveText() {
                        VoiceBean voiceBean = voiceBeanList.get(position);
                        AddVoiceActivity.newInstance(DataVoiceActivity.this, voiceBean.getId(), AddVoiceActivity.ADD_VOICE_REQUESTCODE);
                    }
                });
    }
}
