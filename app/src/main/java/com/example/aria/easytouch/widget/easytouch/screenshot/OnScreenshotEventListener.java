package com.example.aria.easytouch.widget.easytouch.screenshot;

import android.media.Image;

/**
 * Created by Aria on 2017/7/24.
 */

public interface OnScreenshotEventListener {
    void beforeScreenshot();
    void onImageCaptured(Image image);
    void afterScreenshot();
    void onPostImageSaved(boolean succeed);
}
