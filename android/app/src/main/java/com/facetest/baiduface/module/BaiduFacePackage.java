package com.facetest.baiduface.module;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BaiduFacePackage implements ReactPackage{

    private BaiduFaceModule baiduFaceModule;

    public BaiduFaceModule getBaiduFaceModule() {
        return baiduFaceModule;
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        baiduFaceModule = new BaiduFaceModule(reactContext);
        return Arrays.<NativeModule>asList(baiduFaceModule);
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }


}
