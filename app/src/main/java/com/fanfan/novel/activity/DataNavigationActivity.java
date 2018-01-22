package com.fanfan.novel.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fanfan.novel.adapter.NavigationDataAdapter;
import com.fanfan.novel.common.Constants;
import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.novel.common.base.simple.BaseRecyclerAdapter;
import com.fanfan.novel.db.manager.NavigationDBManager;
import com.fanfan.novel.model.NavigationBean;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.activity.ExhibitionActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;

/**
 * Created by android on 2018/1/8.
 */

public class DataNavigationActivity extends BarBaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, DataNavigationActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private NavigationDBManager mNavigationDBManager;

    private List<String> images = new ArrayList<>();

    private NavigationDataAdapter navigationDataAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_data_navigation;
    }

    @Override
    protected void initView() {
        super.initView();
        navigationDataAdapter = new NavigationDataAdapter(mContext, images);
        navigationDataAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String name = images.get(position);
                if (name.indexOf(".") > 0) {
                    name = name.substring(0, name.indexOf("."));
                }
                ExhibitionActivity.newInstance(DataNavigationActivity.this, name);
            }
        });
        navigationDataAdapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                showDeleteDialog(position);
                return false;
            }
        });
        recyclerView.setAdapter(navigationDataAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    protected void initData() {
        mNavigationDBManager = new NavigationDBManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<NavigationBean> navigationBeanList = mNavigationDBManager.loadAll();
        if (navigationBeanList != null && navigationBeanList.size() > 0) {
            isNuEmpty();

            Set<String> sets = new HashSet<>();
            for (NavigationBean bean : navigationBeanList) {

                sets.add(bean.getImgUrl());
            }
            images.clear();
            images.addAll(sets);
            navigationDataAdapter.notifyDataSetChanged();
        } else {
            isEmpty();
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
                new MaterialDialog.Builder(this)
                        .title("选择导航图")
                        .content("目前只支持此张地图")
                        .items(Constants.NAVIGATIONS)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                                ExhibitionActivity.newInstance(DataNavigationActivity.this, (String) text);
                            }
                        })
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showDeleteDialog(final int position) {

        DialogUtils.showBasicNoTitleDialog(this, "确定要删除此图下所有的数据吗", "删除",
                "取消", new DialogUtils.OnNiftyDialogListener() {
                    @Override
                    public void onClickLeft() {
                        List<NavigationBean> beans = new ArrayList<>();
                        List<NavigationBean> navigationBeanList = mNavigationDBManager.loadAll();
                        for (NavigationBean bean : navigationBeanList) {

                            if (images.get(position).equals(bean.getImgUrl())) {
                                beans.add(bean);
                            }
                        }
                        mNavigationDBManager.deleteList(beans);
                        images.remove(position);
                        navigationDataAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onClickRight() {
                    }
                });
    }
}
