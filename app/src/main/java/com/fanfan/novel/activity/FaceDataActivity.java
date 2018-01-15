package com.fanfan.novel.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fanfan.novel.adapter.UserInfoAdapter;
import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.novel.common.base.simple.BaseRecyclerAdapter;
import com.fanfan.novel.db.manager.FaceAuthDBManager;
import com.fanfan.novel.model.FaceAuth;
import com.fanfan.novel.ui.manager.FullyLinearLayoutManager;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.robot.R;
import com.fanfan.youtu.Youtucode;
import com.fanfan.youtu.api.face.bean.Delperson;
import com.fanfan.youtu.api.face.bean.FacePersonid;
import com.fanfan.youtu.api.face.bean.GetInfo;
import com.fanfan.youtu.api.face.event.DelPersonEvent;
import com.fanfan.youtu.api.face.event.FacePersonidEvent;
import com.fanfan.youtu.api.face.event.GetInfoEvent;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by android on 2018/1/6.
 */

public class FaceDataActivity extends BarBaseActivity {

    @BindView(R.id.recycler_face)
    RecyclerView recyclerFace;

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, FaceDataActivity.class);
        context.startActivity(intent);
    }


    private UserInfoAdapter userInfoAdapter;
    private List<FaceAuth> faceAuths = new ArrayList<>();

    private Youtucode youtucode;

    private FaceAuthDBManager mFaceAuthDBManager;

    private MaterialDialog materialDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_face_data;
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initData() {
        setAdapter();
        youtucode = Youtucode.getSingleInstance();
        youtucode.getPersonids();

        mFaceAuthDBManager = new FaceAuthDBManager();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void setAdapter() {
        userInfoAdapter = new UserInfoAdapter(this, faceAuths);
        recyclerFace.setAdapter(userInfoAdapter);

        recyclerFace.setLayoutManager(new FullyLinearLayoutManager(this));
        recyclerFace.setLayoutManager(new LinearLayoutManager(this));
        recyclerFace.setItemAnimator(new DefaultItemAnimator());
        recyclerFace.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        userInfoAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FaceAuth faceAuth = faceAuths.get(position);
                if (faceAuth.getAuthId() != null) {
                    PersonInfoDetailActivity.navToPersonInfoDetail(FaceDataActivity.this, faceAuth.getPersonId(),
                            PersonInfoDetailActivity.PERSONINFO_DETAIL);
                } else {
                    showProgressDialog();
                    youtucode.getInfo(faceAuth.getPersonId());
                }
            }
        });
        userInfoAdapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position) {
                String personId = faceAuths.get(position).getPersonId();
                showDialog(personId);
                return true;
            }
        });
    }

    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(FacePersonidEvent event) {
        if (event.isOk()) {
            FacePersonid facePersonid = event.getBean();
            Print.e(facePersonid);
            if (facePersonid.getErrorcode() == 0) {
                List<String> personIds = facePersonid.getPerson_ids();
                for (String personId : personIds) {
                    FaceAuth faceAuth = mFaceAuthDBManager.queryByPersonId(personId);
                    if (faceAuth == null) {
                        faceAuth = new FaceAuth();
                        faceAuth.setPersonId(personId);
                    }
                    userInfoAdapter.addData(faceAuth);
                }
            } else {
                onError(facePersonid.getErrorcode(), facePersonid.getErrormsg());
            }
        } else {
            onError(event);
        }
    }

    private void showDialog(final String personId) {
        DialogUtils.showBasicNoTitleDialog(this, "确定要删除此人脸信息吗？", "取消", "确定",
                new DialogUtils.OnNiftyDialogListener() {
                    @Override
                    public void onClickLeft() {
                    }

                    @Override
                    public void onClickRight() {
                        youtucode.delPerson(personId);
                    }
                });
    }

    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(DelPersonEvent event) {
        if (event.isOk()) {
            Delperson delperson = event.getBean();
            Print.e(delperson);
            if (delperson.getErrorcode() == 0) {
                String personId = delperson.getPerson_id();
                showToast("删除 ：" + delperson.getDeleted() + " 张人脸");
                FaceAuth faceAuth = new FaceAuth();
                faceAuth.setPersonId(personId);
                int position = faceAuths.indexOf(faceAuth);
                faceAuths.remove(position);
                userInfoAdapter.removeItem(faceAuth);
            } else {
                onError(delperson.getErrorcode(), delperson.getErrormsg());
            }
        } else {
            onError(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(GetInfoEvent event) {
        dismissProgressDialog();
        if (event.isOk()) {
            GetInfo getInfo = event.getBean();
            Print.e(getInfo);
            if (getInfo.getErrorcode() == 0) {
                String person_id = getInfo.getPerson_id();

                FaceAuth faceAuth = new FaceAuth();
                faceAuth.setPersonId(person_id);
                int position = faceAuths.indexOf(faceAuth);
                faceAuth = faceAuths.get(position);

                faceAuth.setSaveTime(System.currentTimeMillis());
                faceAuth.setAuthId(getInfo.getPerson_name());
                faceAuth.setFaceCount(getInfo.getFace_ids().size());

                if (mFaceAuthDBManager.queryByPersonId(person_id) == null) {
                    mFaceAuthDBManager.insert(faceAuth);
                }
                userInfoAdapter.notifyDataSetChanged();
            } else {
                onError(getInfo.getErrorcode(), getInfo.getErrormsg());
            }
        } else {
            onError(event);
        }
    }

    private void showProgressDialog() {
        if (materialDialog == null) {
            materialDialog = new MaterialDialog.Builder(this)
                    .title("请稍等...")
                    .content("正在获取中...")
                    .progress(true, 0)
                    .progressIndeterminateStyle(false)
                    .build();
        }
        materialDialog.show();
    }

    private void dismissProgressDialog() {
        if (materialDialog != null && materialDialog.isShowing()) {
            materialDialog.dismiss();
            materialDialog = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PersonInfoDetailActivity.PERSONINFO_DETAIL) {
            if (resultCode == RESULT_OK) {
                youtucode.getPersonids();
            }
        }
    }
}
