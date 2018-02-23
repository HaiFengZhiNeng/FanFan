package com.fanfan.novel.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fanfan.novel.adapter.SiteDataAdapter;
import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.novel.common.base.simple.BaseRecyclerAdapter;
import com.fanfan.novel.db.manager.SiteDBManager;
import com.fanfan.novel.model.SiteBean;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.robot.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by android on 2018/2/23.
 */

public class DataSiteActivity extends BarBaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, DataSiteActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private SiteDBManager mSiteDBManager;

    private List<SiteBean> siteBeanList = new ArrayList<>();

    private SiteDataAdapter siteDataAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_data_site;
    }

    @Override
    protected void initView() {
        super.initView();
        siteDataAdapter = new SiteDataAdapter(mContext, siteBeanList);
        siteDataAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });
        siteDataAdapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {

                showNeutralNotitleDialog(position);

                return false;
            }
        });
        recyclerView.setAdapter(siteDataAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void initData() {
        mSiteDBManager = new SiteDBManager();

        siteBeanList = mSiteDBManager.loadAll();
        if (siteBeanList != null && siteBeanList.size() > 0) {
            siteDataAdapter.refreshData(siteBeanList);
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
                AddSiteActivity.newInstance(this, AddSiteActivity.ADD_SITE_REQUESTCODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddSiteActivity.ADD_SITE_REQUESTCODE) {
            if (resultCode == RESULT_OK) {
                siteBeanList = mSiteDBManager.loadAll();
                if (siteBeanList != null && siteBeanList.size() > 0) {
                    siteDataAdapter.refreshData(siteBeanList);
                }
            }
        }
    }

    private void showNeutralNotitleDialog(final int position) {
        DialogUtils.showNeutralNotitleDialog(this, "选择您要执行的操作", "删除所有",
                "删除此条", "修改此条", new DialogUtils.OnNeutralDialogListener() {
                    @Override
                    public void neutralText() {
                        if (mSiteDBManager.deleteAll()) {
                            siteDataAdapter.clear();
                            siteBeanList.clear();
                        }
                    }

                    @Override
                    public void negativeText() {
                        if (mSiteDBManager.delete(siteBeanList.get(position))) {
                            siteDataAdapter.removeItem(siteBeanList.get(position));
                            siteBeanList.remove(position);
                        }
                    }

                    @Override
                    public void positiveText() {
                        SiteBean siteBean = siteBeanList.get(position);
                        AddSiteActivity.newInstance(DataSiteActivity.this, siteBean.getId(), AddSiteActivity.ADD_SITE_REQUESTCODE);
                    }
                });
    }
}
