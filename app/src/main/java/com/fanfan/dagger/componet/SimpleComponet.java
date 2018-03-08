package com.fanfan.dagger.componet;

import com.fanfan.dagger.module.SimpleModule;
import com.fanfan.robot.activity.AuthenticationActivity;

import dagger.Component;

/**
 * Created by Administrator on 2018/3/8/008.
 */

@Component(modules = {SimpleModule.class})
public interface SimpleComponet {

    void inject(AuthenticationActivity activity);

}
