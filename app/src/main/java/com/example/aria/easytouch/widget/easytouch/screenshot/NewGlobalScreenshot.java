package com.example.aria.easytouch.widget.easytouch.screenshot;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.media.MediaActionSound;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.assistivetool.booster.easytouch.R;

/**
 * Created by Aria on 2017/7/28.
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class NewGlobalScreenshot {

    private static final int SCREENSHOT_FLASH_TO_PEAK_DURATION = 130;
    private static final int SCREENSHOT_DROP_IN_DURATION = 430;
    private static final int SCREENSHOT_DROP_OUT_DELAY = 500;
    private static final int SCREENSHOT_DROP_OUT_DURATION = 430;
    private static final int SCREENSHOT_DROP_OUT_SCALE_DURATION = 370;
    private static final int SCREENSHOT_FAST_DROP_OUT_DURATION = 320;
    private static final float BACKGROUND_ALPHA = 0.5f;
    private static final float SCREENSHOT_SCALE = 1f;
    private static final float SCREENSHOT_DROP_IN_MIN_SCALE = SCREENSHOT_SCALE * 0.725f;
    private static final float SCREENSHOT_DROP_OUT_MIN_SCALE = SCREENSHOT_SCALE * 0.45f;
    private static final float SCREENSHOT_FAST_DROP_OUT_MIN_SCALE = SCREENSHOT_SCALE * 0.6f;
    private static final float SCREENSHOT_DROP_OUT_MIN_SCALE_OFFSET = 0f;

    private Context context;
    private MediaActionSound cameraSound;
    private WindowManager windowManager;
    private WindowManager.LayoutParams windowLayoutParams;
    private OnScreenshotListener onScreenshotListener;
    private Bitmap screenshotBitmap;
    private Display mDisplay;
    private DisplayMetrics mDisplayMetrics;

    private View mScreenshotLayout;
    private ImageView mBackgroundView;
    private ImageView mScreenshotView;
    private ImageView mScreenshotFlash;

    private float mBgPadding;
    private float mBgPaddingScale;

    private AnimatorSet mScreenshotAnimation;


    public NewGlobalScreenshot(Context context){
        this.context = context;

        mScreenshotLayout = LayoutInflater.from(context).inflate(R.layout.global_screenshot, null);
        mBackgroundView = (ImageView) mScreenshotLayout.findViewById(R.id.global_screenshot_background);
        mScreenshotView = (ImageView) mScreenshotLayout.findViewById(R.id.global_screenshot);
        mScreenshotFlash = (ImageView) mScreenshotLayout.findViewById(R.id.global_screenshot_flash);
        mScreenshotLayout.setFocusable(true);
        mScreenshotLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Intercept and ignore all touch events
                return true;
            }
        });

        windowLayoutParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        windowLayoutParams.x = 0;
        windowLayoutParams.y = 0;
        windowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        windowLayoutParams.format = PixelFormat.TRANSLUCENT;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        mDisplay = windowManager.getDefaultDisplay();
        mDisplayMetrics = new DisplayMetrics();
        mDisplay.getMetrics(mDisplayMetrics);

        // Scale has to account for both sides of the bg
        mBgPadding = (float) context.getResources().getDimensionPixelSize(R.dimen.global_screenshot_bg_padding);
        mBgPaddingScale = mBgPadding / mDisplayMetrics.widthPixels;

         cameraSound = new MediaActionSound();
        cameraSound.load(MediaActionSound.SHUTTER_CLICK);
    }

    public void takeScreenshot(Bitmap bitmap,OnScreenshotListener onScreenshotListener,boolean statusBarVisible,boolean navBarVisible){
        this.onScreenshotListener = onScreenshotListener;
        screenshotBitmap = bitmap;
        this.onScreenshotListener = onScreenshotListener;
        if (onScreenshotListener != null)
            onScreenshotListener.startScreenshot();
        if (screenshotBitmap == null){
            onScreenshotListener.endScreenshot(false);
            return;
        }

        screenshotBitmap.setHasAlpha(false);
        screenshotBitmap.prepareToDraw();

        startAnimation(mDisplayMetrics.widthPixels,mDisplayMetrics.heightPixels,statusBarVisible,navBarVisible);

    }

    private void startAnimation(int w,int h,boolean statusBarVisible,boolean navBarVisible){
        mScreenshotView.setImageBitmap(screenshotBitmap);
        mScreenshotLayout.requestFocus();

        // Setup the animation with the screenshot just taken
        if (mScreenshotAnimation != null) {
            mScreenshotAnimation.end();
            mScreenshotAnimation.removeAllListeners();
        }

        windowManager.addView(mScreenshotLayout, windowLayoutParams);
        ValueAnimator screenshotDropInAnim = createScreenshotDropInAnimation();
        ValueAnimator screenshotFadeOutAnim = createScreenshotDropOutAnimation(w, h,
                statusBarVisible, navBarVisible);
        mScreenshotAnimation = new AnimatorSet();
        mScreenshotAnimation.playSequentially(screenshotDropInAnim, screenshotFadeOutAnim);
        mScreenshotAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                onScreenshotListener.endScreenshot(true);
                windowManager.removeView(mScreenshotLayout);
                screenshotBitmap = null;
                mScreenshotView.setImageBitmap(null);
            }
        });
        mScreenshotLayout.post(new Runnable() {
            @Override
            public void run() {
                // Play the shutter sound to notify that we've taken a screenshot
                cameraSound.play(MediaActionSound.SHUTTER_CLICK);

                mScreenshotView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                mScreenshotView.buildLayer();
                mScreenshotAnimation.start();
            }
        });
    }

    public interface OnScreenshotListener{
        void startScreenshot();
        void endScreenshot(boolean success);
    }

    private ValueAnimator createScreenshotDropInAnimation() {
        final float flashPeakDurationPct = ((float) (SCREENSHOT_FLASH_TO_PEAK_DURATION)
                / SCREENSHOT_DROP_IN_DURATION);
        final float flashDurationPct = 2f * flashPeakDurationPct;
        final Interpolator flashAlphaInterpolator = new Interpolator() {
            @Override
            public float getInterpolation(float x) {
                // Flash the flash view in and out quickly
                if (x <= flashDurationPct) {
                    return (float) Math.sin(Math.PI * (x / flashDurationPct));
                }
                return 0;
            }
        };
        final Interpolator scaleInterpolator = new Interpolator() {
            @Override
            public float getInterpolation(float x) {
                // We start scaling when the flash is at it's peak
                if (x < flashPeakDurationPct) {
                    return 0;
                }
                return (x - flashDurationPct) / (1f - flashDurationPct);
            }
        };
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(SCREENSHOT_DROP_IN_DURATION);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mBackgroundView.setAlpha(0f);
                mBackgroundView.setVisibility(View.VISIBLE);
                mScreenshotView.setAlpha(0f);
                mScreenshotView.setTranslationX(0f);
                mScreenshotView.setTranslationY(0f);
                mScreenshotView.setScaleX(SCREENSHOT_SCALE + mBgPaddingScale);
                mScreenshotView.setScaleY(SCREENSHOT_SCALE + mBgPaddingScale);
                mScreenshotView.setVisibility(View.VISIBLE);
                mScreenshotFlash.setAlpha(0f);
                mScreenshotFlash.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mScreenshotFlash.setVisibility(View.GONE);

//        mScreenshotView.setScaleX(SCREENSHOT_SCALE);
//        mScreenshotView.setScaleY(SCREENSHOT_SCALE);
            }
        });
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float t = (Float) animation.getAnimatedValue();
                float scaleT = (SCREENSHOT_SCALE + mBgPaddingScale)
                        - scaleInterpolator.getInterpolation(t)
                        * (SCREENSHOT_SCALE - SCREENSHOT_DROP_IN_MIN_SCALE);
                mBackgroundView.setAlpha(scaleInterpolator.getInterpolation(t) * BACKGROUND_ALPHA);
                mScreenshotView.setAlpha(t);
                mScreenshotView.setScaleX(scaleT);
                mScreenshotView.setScaleY(scaleT);
                mScreenshotFlash.setAlpha(flashAlphaInterpolator.getInterpolation(t));
            }
        });
        return anim;
    }

    private ValueAnimator createScreenshotDropOutAnimation(int w, int h, boolean statusBarVisible,
                                                           boolean navBarVisible) {
        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setStartDelay(SCREENSHOT_DROP_OUT_DELAY);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBackgroundView.setVisibility(View.GONE);
                mScreenshotView.setVisibility(View.GONE);
                mScreenshotView.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        });

        if (!statusBarVisible || !navBarVisible) {
            // There is no status bar/nav bar, so just fade the screenshot away in place
            anim.setDuration(SCREENSHOT_FAST_DROP_OUT_DURATION);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float t = (Float) animation.getAnimatedValue();
                    float scaleT = (SCREENSHOT_DROP_IN_MIN_SCALE + mBgPaddingScale)
                            - t * (SCREENSHOT_DROP_IN_MIN_SCALE - SCREENSHOT_FAST_DROP_OUT_MIN_SCALE);
                    mBackgroundView.setAlpha((1f - t) * BACKGROUND_ALPHA);
                    mScreenshotView.setAlpha(1f - t);
                    mScreenshotView.setScaleX(scaleT);
                    mScreenshotView.setScaleY(scaleT);
                }
            });
        } else {
            // In the case where there is a status bar, animate to the origin of the bar (top-left)
            final float scaleDurationPct = (float) SCREENSHOT_DROP_OUT_SCALE_DURATION
                    / SCREENSHOT_DROP_OUT_DURATION;
            final Interpolator scaleInterpolator = new Interpolator() {
                @Override
                public float getInterpolation(float x) {
                    if (x < scaleDurationPct) {
                        // Decelerate, and scale the input accordingly
                        return (float) (1f - Math.pow(1f - (x / scaleDurationPct), 2f));
                    }
                    return 1f;
                }
            };

            // Determine the bounds of how to scale
            float halfScreenWidth = (w - 2f * mBgPadding) / 2f;
            float halfScreenHeight = (h - 2f * mBgPadding) / 2f;
            final float offsetPct = SCREENSHOT_DROP_OUT_MIN_SCALE_OFFSET;
            final PointF finalPos = new PointF(
                    -halfScreenWidth + (SCREENSHOT_DROP_OUT_MIN_SCALE + offsetPct) * halfScreenWidth,
                    -halfScreenHeight + (SCREENSHOT_DROP_OUT_MIN_SCALE + offsetPct) * halfScreenHeight);

            // Animate the screenshot to the status bar
            anim.setDuration(SCREENSHOT_DROP_OUT_DURATION);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float t = (Float) animation.getAnimatedValue();
                    float scaleT = (SCREENSHOT_DROP_IN_MIN_SCALE + mBgPaddingScale)
                            - scaleInterpolator.getInterpolation(t)
                            * (SCREENSHOT_DROP_IN_MIN_SCALE - SCREENSHOT_DROP_OUT_MIN_SCALE);
                    mBackgroundView.setAlpha((1f - t) * BACKGROUND_ALPHA);
                    mScreenshotView.setAlpha(1f - scaleInterpolator.getInterpolation(t));
                    mScreenshotView.setScaleX(scaleT);
                    mScreenshotView.setScaleY(scaleT);
                    mScreenshotView.setTranslationX(t * finalPos.x);
                    mScreenshotView.setTranslationY(t * finalPos.y);
                }
            });
        }
        return anim;
    }
}
