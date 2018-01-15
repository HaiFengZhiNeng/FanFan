package com.fanfan.novel.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.fanfan.robot.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 2018/1/6.
 */

public class LocalLexicon {

    private Context mContext;

    public LocalLexicon(Context context) {
        mContext = context;
    }

    public String updateLocalLexiconContents() {
        StringBuilder sb = new StringBuilder();
        List<String> words = getLocalStrings();
        for (String anArrStandard : words) {
            sb.append(anArrStandard).append("\n");
        }

        String[] arrVoiceQuestion = FucUtil.resArray(mContext, R.array.local_voice_question);
        for (String anArrVoiceQuestion : arrVoiceQuestion) {
            sb.append(anArrVoiceQuestion).append("\n");
        }

        String[] arrVideoQuestion = FucUtil.resArray(mContext, R.array.local_video_question);
        for (String anArrVideoQuestion : arrVideoQuestion) {
            sb.append(anArrVideoQuestion).append("\n");
        }
        String[] arrNavigationQuestion = FucUtil.resArray(mContext, R.array.local_navigation);
        for (String anArrNavigationQuestion : arrNavigationQuestion) {
            sb.append(anArrNavigationQuestion).append("\n");
        }
        return sb.toString();
    }

    @NonNull
    public List<String> getLocalStrings() {
        List<String> words = new ArrayList<>();
        words.add(mContext.getResources().getString(R.string.Video));

        words.add(mContext.getResources().getString(R.string.Problem));
        words.add(mContext.getResources().getString(R.string.MultiMedia));
        words.add(mContext.getResources().getString(R.string.Seting_up));
        words.add(mContext.getResources().getString(R.string.Public_num));
        words.add(mContext.getResources().getString(R.string.Navigation));
        words.add(mContext.getResources().getString(R.string.Face));
        words.add(mContext.getResources().getString(R.string.Map));
        words.add(mContext.getResources().getString(R.string.Logout));
        words.add(mContext.getResources().getString(R.string.StopListener));
        words.add(mContext.getResources().getString(R.string.Back));
        words.add(mContext.getResources().getString(R.string.Forward));
        words.add(mContext.getResources().getString(R.string.Backoff));
        words.add(mContext.getResources().getString(R.string.Turnleft));
        words.add(mContext.getResources().getString(R.string.Turnright));
        words.add(mContext.getResources().getString(R.string.Artificial));
        words.add(mContext.getResources().getString(R.string.Face_check_in));
        words.add(mContext.getResources().getString(R.string.Instagram));
        words.add(mContext.getResources().getString(R.string.Witness_contrast));
        words.add(mContext.getResources().getString(R.string.Face_lifting_area));
        words.add(mContext.getResources().getString(R.string.Next));
        words.add(mContext.getResources().getString(R.string.Lase));
        return words;
    }

}
