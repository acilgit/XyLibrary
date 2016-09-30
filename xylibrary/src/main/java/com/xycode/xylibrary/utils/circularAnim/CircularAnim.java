package com.xycode.xylibrary.utils.circularAnim;

/**
 * Created by XY on 2016-09-30.
 */


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class CircularAnim {
    public static final long PERFECT_MILLS = 618L;
    public static final int MINI_RADIUS = 0;
    private static Long sPerfectMills;
    private static Long sFullActivityPerfectMills;
    private static Integer sColorOrImageRes;

    public CircularAnim() {
    }

    private static long getPerfectMills() {
        return sPerfectMills != null?sPerfectMills.longValue():618L;
    }

    private static long getFullActivityMills() {
        return sFullActivityPerfectMills != null?sFullActivityPerfectMills.longValue():618L;
    }

    private static int getColorOrImageRes() {
        return sColorOrImageRes != null?sColorOrImageRes.intValue():17170443;
    }

    public static CircularAnim.VisibleBuilder show(View animView) {
        return new CircularAnim.VisibleBuilder(animView, true);
    }

    public static CircularAnim.VisibleBuilder hide(View animView) {
        return new CircularAnim.VisibleBuilder(animView, false);
    }

    public static CircularAnim.FullActivityBuilder fullActivity(Activity activity, View triggerView) {
        return new CircularAnim.FullActivityBuilder(activity, triggerView);
    }

    public static void init(long perfectMills, long fullActivityPerfectMills, int colorOrImageRes) {
        sPerfectMills = Long.valueOf(perfectMills);
        sFullActivityPerfectMills = Long.valueOf(fullActivityPerfectMills);
        sColorOrImageRes = Integer.valueOf(colorOrImageRes);
    }

    @SuppressLint({"NewApi"})
    public static class FullActivityBuilder {
        private Activity mActivity;
        private View mTriggerView;
        private float mStartRadius = 0.0F;
        private int mColorOrImageRes = CircularAnim.getColorOrImageRes();
        private Long mDurationMills;
        private CircularAnim.OnAnimationEndListener mOnAnimationEndListener;
        private int mEnterAnim = 17432576;
        private int mExitAnim = 17432577;

        public FullActivityBuilder(Activity activity, View triggerView) {
            this.mActivity = activity;
            this.mTriggerView = triggerView;
        }

        public CircularAnim.FullActivityBuilder startRadius(float startRadius) {
            this.mStartRadius = startRadius;
            return this;
        }

        public CircularAnim.FullActivityBuilder colorOrImageRes(int colorOrImageRes) {
            this.mColorOrImageRes = colorOrImageRes;
            return this;
        }

        public CircularAnim.FullActivityBuilder duration(long durationMills) {
            this.mDurationMills = Long.valueOf(durationMills);
            return this;
        }

        public CircularAnim.FullActivityBuilder overridePendingTransition(int enterAnim, int exitAnim) {
            this.mEnterAnim = enterAnim;
            this.mExitAnim = exitAnim;
            return this;
        }

        public void go(CircularAnim.OnAnimationEndListener onAnimationEndListener) {
            this.mOnAnimationEndListener = onAnimationEndListener;
            if(VERSION.SDK_INT < 21) {
                this.doOnEnd();
            } else {
                int[] location = new int[2];
                this.mTriggerView.getLocationInWindow(location);
                final int cx = location[0] + this.mTriggerView.getWidth() / 2;
                final int cy = location[1] + this.mTriggerView.getHeight() / 2;
                final ImageView view = new ImageView(this.mActivity);
                view.setScaleType(ScaleType.CENTER_CROP);
                view.setImageResource(this.mColorOrImageRes);
                final ViewGroup decorView = (ViewGroup)this.mActivity.getWindow().getDecorView();
                int w = decorView.getWidth();
                int h = decorView.getHeight();
                decorView.addView(view, w, h);
                int maxW = Math.max(cx, w - cx);
                int maxH = Math.max(cy, h - cy);
                final int finalRadius = (int)Math.sqrt((double)(maxW * maxW + maxH * maxH)) + 1;

                try {
                    Animator e = ViewAnimationUtils.createCircularReveal(view, cx, cy, this.mStartRadius, (float)finalRadius);
                    int maxRadius = (int)Math.sqrt((double)(w * w + h * h)) + 1;
                    if(this.mDurationMills == null) {
                        double finalDuration = 1.0D * (double)finalRadius / (double)maxRadius;
                        this.mDurationMills = Long.valueOf((long)((double) CircularAnim.getFullActivityMills() * Math.sqrt(finalDuration)));
                    }

                    final long finalDuration1 = this.mDurationMills.longValue();
                    e.setDuration((long)((double)finalDuration1 * 0.9D));
                    e.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            CircularAnim.FullActivityBuilder.this.doOnEnd();
                            CircularAnim.FullActivityBuilder.this.mActivity.overridePendingTransition(CircularAnim.FullActivityBuilder.this.mEnterAnim, CircularAnim.FullActivityBuilder.this.mExitAnim);
                            CircularAnim.FullActivityBuilder.this.mTriggerView.postDelayed(new Runnable() {
                                public void run() {
                                    if(!CircularAnim.FullActivityBuilder.this.mActivity.isFinishing()) {
                                        try {
                                            Animator e = ViewAnimationUtils.createCircularReveal(view, cx, cy, (float)finalRadius, CircularAnim.FullActivityBuilder.this.mStartRadius);
                                            e.setDuration(finalDuration1);
                                            e.addListener(new AnimatorListenerAdapter() {
                                                public void onAnimationEnd(Animator animation) {
                                                    super.onAnimationEnd(animation);

                                                    try {
                                                        decorView.removeView(view);
                                                    } catch (Exception var3) {
                                                        var3.printStackTrace();
                                                    }

                                                }
                                            });
                                            e.start();
                                        } catch (Exception var4) {
                                            var4.printStackTrace();

                                            try {
                                                decorView.removeView(view);
                                            } catch (Exception var3) {
                                                var3.printStackTrace();
                                            }
                                        }

                                    }
                                }
                            }, 1000L);
                        }
                    });
                    e.start();
                } catch (Exception var16) {
                    var16.printStackTrace();
                    this.doOnEnd();
                }

            }
        }

        private void doOnEnd() {
            this.mOnAnimationEndListener.onAnimationEnd();
        }
    }

    @SuppressLint({"NewApi"})
    public static class VisibleBuilder {
        private View mAnimView;
        private View mTriggerView;
        private Float mStartRadius;
        private Float mEndRadius;
        private long mDurationMills = CircularAnim.getPerfectMills();
        private boolean isShow;
        private CircularAnim.OnAnimationEndListener mOnAnimationEndListener;

        public VisibleBuilder(View animView, boolean isShow) {
            this.mAnimView = animView;
            this.isShow = isShow;
            if(isShow) {
                this.mStartRadius = Float.valueOf(0.0F);
            } else {
                this.mEndRadius = Float.valueOf(0.0F);
            }

        }

        public CircularAnim.VisibleBuilder triggerView(View triggerView) {
            this.mTriggerView = triggerView;
            return this;
        }

        public CircularAnim.VisibleBuilder startRadius(float startRadius) {
            this.mStartRadius = Float.valueOf(startRadius);
            return this;
        }

        public CircularAnim.VisibleBuilder endRadius(float endRadius) {
            this.mEndRadius = Float.valueOf(endRadius);
            return this;
        }

        public CircularAnim.VisibleBuilder duration(long durationMills) {
            this.mDurationMills = durationMills;
            return this;
        }

        /** @deprecated */
        @Deprecated
        public CircularAnim.VisibleBuilder onAnimationEndListener(CircularAnim.OnAnimationEndListener onAnimationEndListener) {
            this.mOnAnimationEndListener = onAnimationEndListener;
            return this;
        }

        public void go() {
            this.go((CircularAnim.OnAnimationEndListener)null);
        }

        public void go(CircularAnim.OnAnimationEndListener onAnimationEndListener) {
            this.mOnAnimationEndListener = onAnimationEndListener;
            if(VERSION.SDK_INT < 21) {
                this.doOnEnd();
            } else {
                int rippleCX;
                int rippleCY;
                int maxRadius;
                int h;
                if(this.mTriggerView != null) {
                    int[] e = new int[2];
                    this.mTriggerView.getLocationInWindow(e);
                    h = e[0] + this.mTriggerView.getWidth() / 2;
                    int tvCY = e[1] + this.mTriggerView.getHeight() / 2;
                    int[] avLocation = new int[2];
                    this.mAnimView.getLocationInWindow(avLocation);
                    int avLX = avLocation[0];
                    int avTY = avLocation[1];
                    int triggerX = Math.max(avLX, h);
                    triggerX = Math.min(triggerX, avLX + this.mAnimView.getWidth());
                    int triggerY = Math.max(avTY, tvCY);
                    triggerY = Math.min(triggerY, avTY + this.mAnimView.getHeight());
                    int avW = this.mAnimView.getWidth();
                    int avH = this.mAnimView.getHeight();
                    rippleCX = triggerX - avLX;
                    rippleCY = triggerY - avTY;
                    int maxW = Math.max(rippleCX, avW - rippleCX);
                    int maxH = Math.max(rippleCY, avH - rippleCY);
                    maxRadius = (int)Math.sqrt((double)(maxW * maxW + maxH * maxH)) + 1;
                } else {
                    rippleCX = (this.mAnimView.getLeft() + this.mAnimView.getRight()) / 2;
                    rippleCY = (this.mAnimView.getTop() + this.mAnimView.getBottom()) / 2;
                    int e1 = this.mAnimView.getWidth();
                    h = this.mAnimView.getHeight();
                    maxRadius = (int)Math.sqrt((double)(e1 * e1 + h * h)) + 1;
                }

                if(this.isShow && this.mEndRadius == null) {
                    this.mEndRadius = Float.valueOf((float)maxRadius + 0.0F);
                } else if(!this.isShow && this.mStartRadius == null) {
                    this.mStartRadius = Float.valueOf((float)maxRadius + 0.0F);
                }

                try {
                    Animator e2 = ViewAnimationUtils.createCircularReveal(this.mAnimView, rippleCX, rippleCY, this.mStartRadius.floatValue(), this.mEndRadius.floatValue());
                    this.mAnimView.setVisibility(0);
                    e2.setDuration(this.mDurationMills);
                    e2.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            CircularAnim.VisibleBuilder.this.doOnEnd();
                        }
                    });
                    e2.start();
                } catch (Exception var17) {
                    var17.printStackTrace();
                    this.doOnEnd();
                }

            }
        }

        private void doOnEnd() {
            if(this.isShow) {
                this.mAnimView.setVisibility(0);
            } else {
                this.mAnimView.setVisibility(4);
            }

            if(this.mOnAnimationEndListener != null) {
                this.mOnAnimationEndListener.onAnimationEnd();
            }

        }
    }

    public interface OnAnimationEndListener {
        void onAnimationEnd();
    }
}

