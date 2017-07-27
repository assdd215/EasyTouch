package com.example.aria.easytouch.widget.easytouch.screenshot;

/**
 * Created by Aria on 2017/7/24.
 */

public interface ScreenShotUtil {
    void setOnScreenshotEventListener(OnScreenshotEventListener onScreenshotEventListener);
    void startScreenshot();
    boolean isSupportScreenshot();
}
