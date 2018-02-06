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
import com.fanfan.novel.adapter.NavigationAdapter;
import com.fanfan.novel.adapter.NavigationDataAdapter;
import com.fanfan.novel.common.Constants;
import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.novel.common.base.simple.BaseRecyclerAdapter;
import com.fanfan.novel.db.manager.NavigationDBManager;
import com.fanfan.novel.model.NavigationBean;
import com.fanfan.novel.model.VoiceBean;
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

    private List<NavigationBean> navigationBeanList;

    private NavigationAdapter navigationAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_data_navigation;
    }

    @Override
    protected void initView() {
        super.initView();
        navigationAdapter = new NavigationAdapter(mContext, navigationBeanList);
        navigationAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                navigationBeanList.get(position);
            }
        });
        navigationAdapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                showNeutralNotitleDialog(position);
                return false;
            }
        });
        recyclerView.setAdapter(navigationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    protected void initData() {
        mNavigationDBManager = new NavigationDBManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationBeanList = mNavigationDBManager.loadAll();
        if (navigationBeanList != null && navigationBeanList.size() > 0) {
            isNuEmpty();
            navigationAdapter.refreshData(navigationBeanList);
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
//                new MaterialDialog.Builder(this)
//                        .title("选择导航图")
//                        .content("目前只支持此张地图")
//                        .items(Constants.NAVIGATIONS)
//                        .itemsCallback(new MaterialDialog.ListCallback() {
//                            @Override
//                            public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
//                                ExhibitionActivity.newInstance(DataNavigationActivity.this, (String) text);
//                            }
//                        })
//                        .show();
                AddNavigationActivity.newInstance(DataNavigationActivity.this, AddNavigationActivity.ADD_NAVIGATION_REQUESTCODE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddVoiceActivity.ADD_VOICE_REQUESTCODE) {
            if (resultCode == RESULT_OK) {
                navigationBeanList = mNavigationDBManager.loadAll();
                if (navigationBeanList != null && navigationBeanList.size() > 0) {
                    navigationAdapter.refreshData(navigationBeanList);
                }
            }
        }
    }

    private void showNeutralNotitleDialog(final int position) {
        DialogUtils.showNeutralNotitleDialog(this, "选择您要执行的操作", "删除所有",
                "删除此条", "修改此条", new DialogUtils.OnNeutralDialogListener() {
                    @Override
                    public void neutralText() {
                        if (mNavigationDBManager.deleteAll()) {
                            navigationAdapter.clear();
                            navigationBeanList.clear();
                        }
                    }

                    @Override
                    public void negativeText() {
                        if (mNavigationDBManager.delete(navigationBeanList.get(position))) {
                            navigationAdapter.removeItem(navigationBeanList.get(position));
                            navigationBeanList.remove(position);
                        }
                    }

                    @Override
                    public void positiveText() {
                        NavigationBean navigationBean = navigationBeanList.get(position);
                        AddNavigationActivity.newInstance(DataNavigationActivity.this, navigationBean.getId(), AddNavigationActivity.ADD_NAVIGATION_REQUESTCODE);
                    }
                });
    }
}
