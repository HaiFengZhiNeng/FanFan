package com.fanfan.robot.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fanfan.novel.common.activity.BarBaseActivity;
import com.fanfan.novel.common.base.simple.BaseRecyclerAdapter;
import com.fanfan.novel.common.enums.SpecialType;
import com.fanfan.novel.model.SerialBean;
import com.fanfan.novel.presenter.LocalSoundPresenter;
import com.fanfan.novel.presenter.SerialPresenter;
import com.fanfan.novel.presenter.SynthesizerPresenter;
import com.fanfan.novel.presenter.ipresenter.ILocalSoundPresenter;
import com.fanfan.novel.presenter.ipresenter.ISerialPresenter;
import com.fanfan.novel.service.SerialService;
import com.fanfan.novel.service.event.ReceiveEvent;
import com.fanfan.novel.service.event.ServiceToActivityEvent;
import com.fanfan.novel.service.udp.SocketManager;
import com.fanfan.novel.utils.MediaFile;
import com.fanfan.novel.utils.PPTUtil;
import com.fanfan.robot.R;
import com.fanfan.robot.adapter.PPTAdapter;
import com.fanfan.robot.adapter.PptTextAdapter;
import com.fanfan.robot.adapter.SiteAdapter;
import com.fanfan.youtu.api.face.bean.detectFace.Face;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by android on 2018/1/16.
 */

public class PPTActivity extends BarBaseActivity implements ILocalSoundPresenter.ILocalSoundView,
        ISerialPresenter.ISerialView {

    @BindView(R.id.recycler_title)
    RecyclerView recyclerTitle;
    @BindView(R.id.recycler_content)
    RecyclerView recyclerContent;

    public static void newInstance(Activity context) {
        Intent intent = new Intent(context, PPTActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private LocalSoundPresenter mSoundPresenter;
    private SerialPresenter mSerialPresenter;

    private List<File> pptFiles = new ArrayList<>();
    private List<String> contentArray;

    private int curCount;

    private PPTAdapter pptAdapter;
    private PptTextAdapter pptTextAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ppt;
    }

    @Override
    protected void initView() {
        super.initView();

        mSoundPresenter = new LocalSoundPresenter(this);
        mSoundPresenter.start();
        mSerialPresenter = new SerialPresenter(this);
        mSerialPresenter.start();

        pptAdapter = new PPTAdapter(pptFiles);
        pptAdapter.openLoadAnimation();
        pptAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                refPPT(pptFiles.get(position), position);
            }
        });

        recyclerTitle.setAdapter(pptAdapter);
        recyclerTitle.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerTitle.setItemAnimator(new DefaultItemAnimator());

        pptTextAdapter = new PptTextAdapter(contentArray);
        pptTextAdapter.openLoadAnimation();
        recyclerContent.setAdapter(pptTextAdapter);
        recyclerContent.setLayoutManager(new LinearLayoutManager(this));
        recyclerContent.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    protected void initData() {

        List<File> files = loadFile("robotResources");
        if (files != null && files.size() > 0) {
            isNuEmpty();
            pptFiles = files;
            pptAdapter.replaceData(pptFiles);
            pptAdapter.notifyClick(0);
        } else {
            isEmpty();
        }
    }

    private List<File> loadFile(String dirName) {//Music
        List<File> pptFiles = new ArrayList<>();
        String dirPath = Environment.getExternalStorageDirectory() + File.separator + dirName;
        File dirFile = new File(dirPath);
        if (!dirFile.exists() || dirFile.isFile()) {
            dirFile.mkdirs();
            return null;
        }
        File[] files = dirFile.listFiles();
        if (files.length == 0) {
            return null;
        }
        for (File file : files) {
            if (MediaFile.isPPTFileType(file.getAbsolutePath())) {
                pptFiles.add(file);
            }
        }
        return pptFiles;
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        mSoundPresenter.startRecognizerListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSoundPresenter.buildTts();
        mSoundPresenter.buildIat();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSoundPresenter.stopTts();
        mSoundPresenter.stopRecognizerListener();
        mSoundPresenter.stopHandler();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSoundPresenter.finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(ServiceToActivityEvent event) {
        if (event.isOk()) {
            SerialBean serialBean = event.getBean();
            mSerialPresenter.onDataReceiverd(serialBean);
        } else {
            Print.e("ReceiveEvent error");
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onResultEvent(ReceiveEvent event) {
        if (event.isOk()) {
            DatagramPacket packet = event.getBean();
            if (!SocketManager.getInstance().isGetTcpIp) {
                SocketManager.getInstance().setUdpIp(packet.getAddress().getHostAddress(), packet.getPort());
            }
            String recvStr = new String(packet.getData(), 0, packet.getLength());
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, recvStr);
            Print.e(recvStr);
        } else {
            Print.e("ReceiveEvent error");
        }
    }

    private void refPPT(File itemData, int position) {
        pptAdapter.notifyClick(position);

        contentArray = PPTUtil.readPPT(itemData.getAbsolutePath());
        if (contentArray != null && contentArray.size() > 0) {
            pptTextAdapter.replaceData(contentArray);
            curCount = 0;
            recyclerContent.scrollToPosition(curCount);
            addSpeakAnswer(contentArray.get(curCount));
        }
    }

    private void addSpeakAnswer(String messageContent) {
        if (messageContent.length() > 0) {
            mSoundPresenter.doAnswer(messageContent);
            speakingAddAction(messageContent.length());
        } else {
            onCompleted();
        }
    }

    private void addSpeakAnswer(int res) {
        mSoundPresenter.doAnswer(getResources().getString(res));
    }

    private void speakingAddAction(int length) {
        if (length <= 13) {
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A50C8001AA");
        } else if (length > 13 && length <= 40) {
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A50C8003AA");
        } else {
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A50C8021AA");
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showMsg(String msg) {
        showToast(msg);
    }

    @Override
    public void showMsg(int msg) {
        showToast(msg);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void spakeMove(SpecialType type, String result) {
        mSoundPresenter.onCompleted();
        switch (type) {
            case Forward:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038002AA");
                break;
            case Backoff:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038008AA");
                break;
            case Turnleft:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038004AA");
                break;
            case Turnright:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038006AA");
                break;
        }
    }

    @Override
    public void openMap() {
        addSpeakAnswer(R.string.open_map);
    }

    @Override
    public void stopListener() {
        mSoundPresenter.stopTts();
        mSoundPresenter.stopRecognizerListener();
        mSoundPresenter.stopHandler();
    }

    @Override
    public void back() {
        finish();
    }

    @Override
    public void artificial() {
        addSpeakAnswer(R.string.open_artificial);
    }

    @Override
    public void face(SpecialType type, String result) {
        addSpeakAnswer(R.string.open_face);
    }

    @Override
    public void control(SpecialType type, String result) {
        addSpeakAnswer(R.string.open_control);
    }

    @Override
    public void refLocalPage(String result) {
        addSpeakAnswer(R.string.open_local);
    }

    @Override
    public void onCompleted() {
        if (curCount < contentArray.size() - 1) {
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A50C80E3AA");
            curCount++;
            Print.e("curCount : " + curCount);
            recyclerContent.scrollToPosition(curCount);
            addSpeakAnswer(contentArray.get(curCount));
        } else if (curCount == contentArray.size() - 1) {
            curCount++;
            addSpeakAnswer("本次阅读完成");
        }
    }

    @Override
    public void stopAll() {
        super.stopAll();
        mSoundPresenter.stopTts();
        mSoundPresenter.stopRecognizerListener();
        mSoundPresenter.stopHandler();
    }

    @Override
    public void onMoveStop() {

    }
}
