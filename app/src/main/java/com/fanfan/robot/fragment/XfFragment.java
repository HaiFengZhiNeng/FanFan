package com.fanfan.robot.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fanfan.novel.utils.DialogUtils;
import com.fanfan.robot.R;
import com.fanfan.robot.app.RobotInfo;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by android on 2018/1/19.
 */

public class XfFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener{

    Unbinder unbinder;
    @BindView(R.id.tv_line_talker)
    TextView tvLineTalker;
    @BindView(R.id.line_talker_layout)
    RelativeLayout lineTalkerLayout;
    @BindView(R.id.tv_local_talker)
    TextView tvLocalTalker;
    @BindView(R.id.local_talker_layout)
    RelativeLayout localTalkerLayout;
    @BindView(R.id.tv_line_hear)
    TextView tvLineHear;
    @BindView(R.id.line_hear_layout)
    RelativeLayout lineHearLayout;
    @BindView(R.id.line_speed_bar)
    SeekBar lineSpeedBar;
    @BindView(R.id.tv_line_speed)
    TextView tvLineSpeed;

    public static XfFragment newInstance() {
        XfFragment xfFragment = new XfFragment();
        Bundle bundle = new Bundle();
        xfFragment.setArguments(bundle);
        return xfFragment;
    }

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
        View view = inflater.inflate(R.layout.dialog_xf, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int lineTalkerIndex = valueForArray(R.array.line_talker, RobotInfo.getInstance().getTtsLineTalker());
        if (lineTalkerIndex > -1)
            tvLineTalker.setText(resArray(R.array.line_talker_show)[lineTalkerIndex]);

        int lineHearIndex = valueForArray(R.array.line_iat_language, RobotInfo.getInstance().getIatLineLanguage());
        if (lineHearIndex > -1) {
            tvLineHear.setText(resArray(R.array.line_iat_language_show)[lineHearIndex]);
        }

        int localTalkerIndex = valueForArray(R.array.local_talker, RobotInfo.getInstance().getTtsLocalTalker());
        if (localTalkerIndex > -1)
            tvLocalTalker.setText(resArray(R.array.local_talker_show)[localTalkerIndex]);

        tvLineSpeed.setText(String.valueOf(RobotInfo.getInstance().getLineSpeed()));
        lineSpeedBar.setProgress(RobotInfo.getInstance().getLineSpeed());
        lineSpeedBar.setOnSeekBarChangeListener(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.line_talker_layout, R.id.local_talker_layout, R.id.line_hear_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.line_hear_layout:
                DialogUtils.showLongListDialog(getContext(), "选择在线监听语言", R.array.line_iat_language_show, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        tvLineHear.setText(resArray(R.array.line_iat_language_show)[position]);
                        RobotInfo.getInstance().setIatLineLanguage(resArray(R.array.line_iat_language)[position]);
                    }
                });
                break;
            case R.id.line_talker_layout:
                DialogUtils.showLongListDialog(getContext(), "选择在线发言人", R.array.line_talker_show,
                        new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, final int position, CharSequence text) {
                                if (position > 1 && position < 5) {//2,3,4时，纯英文 2, 3, 4, 20, 21, 22, 23, 24
                                    tvLineTalker.setText(resArray(R.array.line_talker_show)[position]);
                                    RobotInfo.getInstance().setTtsLineTalker(resArray(R.array.line_talker)[position]);
                                    RobotInfo.getInstance().setQueryLanage(true);
                                } else if (position > 10 && position < 20) {
                                    tvLineTalker.setText(resArray(R.array.line_talker_show)[position]);
                                    RobotInfo.getInstance().setTtsLineTalker(resArray(R.array.line_talker)[position]);
                                    RobotInfo.getInstance().setQueryLanage(false);
                                } else {
                                    DialogUtils.showMultiChoiceDisabledItems(getContext(), "选择中英文", R.array.line_tts_language_show,
                                            new MaterialDialog.ListCallbackSingleChoice() {
                                                @Override
                                                public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                                    tvLineTalker.setText(resArray(R.array.line_talker_show)[position]);
                                                    RobotInfo.getInstance().setTtsLineTalker(resArray(R.array.line_talker)[position]);
                                                    if (which == 1) {
                                                        RobotInfo.getInstance().setQueryLanage(true);
                                                    } else {
                                                        RobotInfo.getInstance().setQueryLanage(false);
                                                    }
                                                    return false;
                                                }
                                            });
                                }
                            }
                        }, 20, 21, 22, 23, 24);
                break;
            case R.id.local_talker_layout:
                DialogUtils.showLongListDialog(getContext(), "选择离线发言人", R.array.local_talker_show, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
                        tvLocalTalker.setText(resArray(R.array.local_talker_show)[position]);
                        RobotInfo.getInstance().setTtsLocalTalker(resArray(R.array.local_talker)[position]);
                    }
                });
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == lineSpeedBar) {
            RobotInfo.getInstance().setLineSpeed(progress);
            tvLineSpeed.setText(String.valueOf(RobotInfo.getInstance().getLineSpeed()));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
