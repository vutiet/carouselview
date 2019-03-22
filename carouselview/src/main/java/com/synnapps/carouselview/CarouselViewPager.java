package com.synnapps.carouselview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;

import java.lang.reflect.Field;

/**
 * Created by Sayyam on 3/28/16.
 */
public class CarouselViewPager extends ViewPager {

    private ImageClickListener imageClickListener;
    private float oldX = 0, newX = 0, sens = 5;
    private boolean lockScroll;

    public void setImageClickListener(ImageClickListener imageClickListener) {
        this.imageClickListener = imageClickListener;
    }

    public CarouselViewPager(Context context) {
        super(context);
        postInitViewPager();
    }

    public CarouselViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        postInitViewPager();
    }

    private CarouselViewPagerScroller mScroller = null;

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        return !isLockScroll() || super.canScroll(v, checkV, dx, x, y);
    }

    boolean isLockScroll() {
        return lockScroll;
    }

    void setLockScroll(boolean lockScroll) {
        this.lockScroll = lockScroll;
    }

    /**
     * Override the Scroller instance with our own class so we can change the
     * duration
     */
    private void postInitViewPager() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = viewpager.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);

            mScroller = new CarouselViewPagerScroller(getContext(),
                    (Interpolator) interpolator.get(null));
            scroller.set(this, mScroller);
        } catch (Exception e) {
        }
    }

    /**
     * Set the factor by which the duration will change
     */
    public void setTransitionVelocity(int scrollFactor) {
        mScroller.setmScrollDuration(scrollFactor);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldX = ev.getX();
                break;

            case MotionEvent.ACTION_UP:
                newX = ev.getX();
                if (Math.abs(oldX - newX) < sens) {
                    if(imageClickListener != null)
                        imageClickListener.onClick(getCurrentItem());
                    return true;
                }
                oldX = 0;
                newX = 0;
                break;
        }

        return super.onTouchEvent(ev);
    }

}