package com.reactnativenavigation.react;

import com.facebook.react.devsupport.interfaces.DevBundleDownloadListener;

import javax.annotation.Nullable;

public class DevBundleDownloadListenerAdapter implements DevBundleDownloadListener, NavigationDevBundleDownloadListener {
    @Override
    public void onSuccess() {
        onSuccess();
    }

    @Override
    public void onProgress(@Nullable String status, @Nullable Integer done, @Nullable Integer total) {

    }

    @Override
    public void onFailure(Exception cause) {

    }
}
