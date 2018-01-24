package com.fanfan.robot.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfan.novel.adapter.VideoDataAdapter;
import com.fanfan.novel.adapter.VoiceDataAdapter;
import com.fanfan.novel.common.Constants;
import com.fanfan.novel.common.base.simple.BaseRecyclerAdapter;
import com.fanfan.novel.common.instance.SpeakIat;
import com.fanfan.novel.db.manager.NavigationDBManager;
import com.fanfan.novel.db.manager.VideoDBManager;
import com.fanfan.novel.db.manager.VoiceDBManager;
import com.fanfan.novel.model.VideoBean;
import com.fanfan.novel.model.VoiceBean;
import com.fanfan.novel.utils.BitmapUtils;
import com.fanfan.novel.utils.FileUtil;
import com.fanfan.novel.utils.LocalLexicon;
import com.fanfan.novel.utils.MediaFile;
import com.fanfan.robot.R;
import com.iflytek.cloud.SpeechRecognizer;
import com.seabreeze.log.Print;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by android on 2018/1/19.
 */

public class ImportFragment extends DialogFragment implements LocalLexicon.RobotLexiconListener{

    Unbinder unbinder;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.voice_view)
    RecyclerView voiceView;
    @BindView(R.id.video_view)
    RecyclerView videoView;
    @BindView(R.id.tv_import)
    TextView tvImport;

    public static ImportFragment newInstance() {
        ImportFragment importFragment = new ImportFragment();
        Bundle bundle = new Bundle();
        importFragment.setArguments(bundle);
        return importFragment;
    }

    private List<File> videoFiles = new ArrayList<>();
    private List<File> imageFiles = new ArrayList<>();

    private String[] action;
    private String[] actionOrder;

    private String[] expression;
    private String[] expressionData;

    private String[] localVoiceQuestion;
    private String[] localVoiceAnswer;

    private List<VoiceBean> voiceBeanList = new ArrayList<>();
    private List<VideoBean> videoBeanList = new ArrayList<>();

    private VideoDataAdapter videoDataAdapter;
    private VoiceDataAdapter voiceDataAdapter;

    private VideoDBManager mVideoDBManager;
    private VoiceDBManager mVoiceDBManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Dialog dialog = getDialog();
        if (dialog != null) {
            //添加动画
            dialog.getWindow().setWindowAnimations(R.style.dialogSlideAnim);
        }
        View view = inflater.inflate(R.layout.dialog_import, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadFile("Music");

        action = resArray(R.array.action);
        actionOrder = resArray(R.array.action_order);

        expression = resArray(R.array.expression);
        expressionData = resArray(R.array.expression_data);

        localVoiceQuestion = resArray(R.array.local_voice_question);
        localVoiceAnswer = resArray(R.array.local_voice_answer);

        loadVoice();

        loadVideo();

        mVideoDBManager = new VideoDBManager();
        mVoiceDBManager = new VoiceDBManager();

        setAdapter();
    }

    private void loadVoice() {
        for (int i = 0; i < localVoiceQuestion.length; i++) {
            VoiceBean voiceBean = new VoiceBean();

            int actionIndex = new Random().nextInt(action.length);
            int expressionIndex = new Random().nextInt(expression.length);

            voiceBean.setSaveTime(System.currentTimeMillis());
            voiceBean.setShowTitle(localVoiceQuestion[i]);
            voiceBean.setVoiceAnswer(localVoiceAnswer[i]);
            voiceBean.setExpression(expression[expressionIndex]);
            voiceBean.setExpressionData(expressionData[expressionIndex]);
            voiceBean.setAction(action[actionIndex]);
            voiceBean.setActionData(actionOrder[actionIndex]);

            if (imageFiles.size() > 0) {
                int imageIndex = new Random().nextInt(imageFiles.size());
                voiceBean.setImgUrl(imageFiles.get(imageIndex).getAbsolutePath());
            }
            voiceBeanList.add(voiceBean);
        }
    }

    private void loadVideo() {
        if (videoFiles == null || videoFiles.size() == 0) {
            return;
        }
        for (File file : videoFiles) {
            VideoBean videoBean = new VideoBean();

            String name = file.getName().substring(0, file.getName().indexOf("."));
            String showTitle = name.replace(" ", "").replace("-", "");
            long size = FileUtil.getFileSize(file);
            Bitmap bitmap = FileUtil.getVideoThumb(file.getAbsolutePath());
            String savePath = Constants.PROJECT_PATH + "video" + File.separator + name + ".jpg";
            BitmapUtils.saveBitmapToFile(bitmap, "video", name + ".jpg");

            videoBean.setSize(size);
            videoBean.setVideoName(name);
            videoBean.setVideoUrl(file.getAbsolutePath());
            videoBean.setVideoImage(savePath);
            videoBean.setShowTitle(showTitle);
            videoBean.setSaveTime(System.currentTimeMillis());
            videoBeanList.add(videoBean);
        }

    }

    private void setAdapter() {
        videoDataAdapter = new VideoDataAdapter(getActivity(), videoBeanList);
        videoView.setAdapter(videoDataAdapter);
        videoView.setLayoutManager(new LinearLayoutManager(getActivity()));
        videoView.setItemAnimator(new DefaultItemAnimator());
        videoView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        voiceDataAdapter = new VoiceDataAdapter(getActivity(), voiceBeanList);
        voiceView.setAdapter(voiceDataAdapter);
        voiceView.setLayoutManager(new LinearLayoutManager(getActivity()));
        voiceView.setItemAnimator(new DefaultItemAnimator());
        voiceView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_back, R.id.tv_import})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                dismiss();
                break;
            case R.id.tv_import:
                mVideoDBManager.deleteAll();
                mVideoDBManager.insertList(videoBeanList);
                mVoiceDBManager.deleteAll();
                mVoiceDBManager.insertList(voiceBeanList);

                LocalLexicon.getInstance().init(getActivity()).setListener(this).updateContents();
                break;
        }
    }

    @NonNull
    private String[] resArray(int resId) {
        return getResources().getStringArray(resId);
    }


    private int valueForArray(int resId, String compare) {
        String[] arrays = resArray(resId);
        return Arrays.asList(arrays).indexOf(compare);
    }

    private void loadFile(String dirName) {//Music

        String dirPath = Environment.getExternalStorageDirectory() + File.separator + dirName;
        File dirFile = new File(dirPath);
        if (!dirFile.exists() || dirFile.isFile()) {
            return;
        }
        File[] files = dirFile.listFiles();
        if (files.length == 0) {
            return;
        }
        for (File file : files) {
            if (MediaFile.isVideoFileType(file.getAbsolutePath())) {
                videoFiles.add(file);
            }
            if (MediaFile.isImageFileType(file.getAbsolutePath())) {
                imageFiles.add(file);
            }
        }
    }

    @Override
    public void onLexiconSuccess() {
        dismiss();
    }

    @Override
    public void onLexiconError(String error) {

    }
}
