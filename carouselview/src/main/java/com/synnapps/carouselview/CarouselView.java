package com.synnapps.carouselview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sayyam on 11/25/15.
 */
public class CarouselView extends RelativeLayout {

    public static final int POSITION_LEFT = 0;
    public static final int POSITION_RIGHT = 1;
    public static final int POSITION_CENTER = 2;

    private final int DEFAULT_SLIDE_INTERVAL = 3500;

    private int mPageCount;
    private int slideInterval = DEFAULT_SLIDE_INTERVAL;
    private int mPosition = 1;

    private ViewPager containerViewPager;
    private CirclePageIndicator mIndicator;
    private ViewListener mViewListener = null;
    private ImageListener mImageListener = null;

    private Timer swipeTimer;
    private SwipeTask swipeTask;

    private boolean autoPlay;
    private boolean disableAutoPlayOnUserInteraction;
    private boolean userInitiatedScroll;
    private int previousState;

    public CarouselView(Context context) {
        super(context);
    }

    public CarouselView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0, 0);
    }

    public CarouselView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CarouselView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (isInEditMode()) {
            return;
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.view_carousel, this, true);
            containerViewPager = (ViewPager) view.findViewById(R.id.containerViewPager);
            mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

            containerViewPager.addOnPageChangeListener(carouselOnPageChangeListener);

            //Retrieve styles attributes
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CarouselView, defStyleAttr, 0);
            try {
                setSlideInterval(a.getInt(R.styleable.CarouselView_slideInterval, DEFAULT_SLIDE_INTERVAL));
                setOrientation(a.getInt(R.styleable.CarouselView_indicatorOrientation, LinearLayout.HORIZONTAL));
                setPosition(a.getInt(R.styleable.CarouselView_indicatorPosition, 1));
                int fillColor = a.getColor(R.styleable.CarouselView_fillColor, 0);
                setAutoPlay(a.getBoolean(R.styleable.CarouselView_autoPlay, true));
                setDisableAutoPlayOnUserInteraction(a.getBoolean(R.styleable.CarouselView_disableAutoPlayOnUserInteraction, false));

                if (fillColor != 0) {
                    setFillColor(fillColor);
                }
                int pageColor = a.getColor(R.styleable.CarouselView_pageColor, 0);
                if (pageColor != 0) {
                    setPageColor(pageColor);
                }
                float radius = a.getDimensionPixelSize(R.styleable.CarouselView_radius, 0);
                if (radius != 0) {
                    setRadius(radius);
                }
                setSnap(a.getBoolean(R.styleable.CarouselView_snap, getResources().getBoolean(R.bool.default_circle_indicator_snap)));
                int strokeColor = a.getColor(R.styleable.CarouselView_strokeColor, 0);
                if (strokeColor != 0) {
                    setStrokeColor(strokeColor);
                }
                float strokeWidth = a.getDimensionPixelSize(R.styleable.CarouselView_strokeWidth, 0);
                if (strokeWidth != 0) {
                    setStrokeWidth(strokeWidth);
                }
            } finally {
                a.recycle();
            }
        }
    }

    public int getSlideInterval() {
        return slideInterval;
    }

    /**
     * Set interval for one slide in milliseconds.
     *
     * @param slideInterval milliseconds
     */
    public void setSlideInterval(int slideInterval) {
        this.slideInterval = slideInterval;
    }

    /**
     * Set interval for one slide in milliseconds.
     *
     * @param slideInterval milliseconds
     */
    public void reSetSlideInterval(int slideInterval) {
        this.slideInterval = slideInterval;

        if (null != containerViewPager) {
            playCarousel();
        }
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    private void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public boolean isDisableAutoPlayOnUserInteraction() {
        return disableAutoPlayOnUserInteraction;
    }

    private void setDisableAutoPlayOnUserInteraction(boolean disableAutoPlayOnUserInteraction) {
        this.disableAutoPlayOnUserInteraction = disableAutoPlayOnUserInteraction;
    }

    public void setData() {
        CarouselPagerAdapter carouselPagerAdapter = new CarouselPagerAdapter(getContext());
        containerViewPager.setAdapter(carouselPagerAdapter);
        mIndicator.setViewPager(containerViewPager);

        playCarousel();
    }

    private void stopScrollTimer() {

        if (null != swipeTimer) {
            swipeTimer.cancel();
        }

        if (null != swipeTask) {
            swipeTask.cancel();
        }
    }


    private void resetScrollTimer() {

        stopScrollTimer();

        swipeTask = new SwipeTask();
        swipeTimer = new Timer();

    }

    /**
     * Starts auto scrolling if
     */
    public void playCarousel() {

        resetScrollTimer();

        if (autoPlay && slideInterval > 0 && containerViewPager.getAdapter() != null && containerViewPager.getAdapter().getCount() > 1) {

            swipeTimer.schedule(swipeTask, slideInterval, slideInterval);
        }
    }

    /**
     * Stops auto scrolling.
     */
    public void pauseCarousel() {

        resetScrollTimer();

    }


    private class CarouselPagerAdapter extends PagerAdapter {
        private Context mContext;

        public CarouselPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {

            Object objectToReturn;

            //Either let user set image to ImageView
            if (mImageListener != null) {

                ImageView imageView = new ImageView(mContext);
                imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));  //setting image position
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                objectToReturn = imageView;
                mImageListener.setImageForPosition(position, imageView);

                collection.addView(imageView);

                //Or let user add his own ViewGroup
            } else if (mViewListener != null) {

                View view = mViewListener.setViewForPosition(position);

                if (null != view) {
                    objectToReturn = view;
                    collection.addView(view);
                } else {
                    throw new RuntimeException("View can not be null for position " + position);
                }

            } else {
                throw new RuntimeException("View must set " + ImageListener.class.getSimpleName() + " or " + ViewListener.class.getSimpleName() + ".");
            }

            return objectToReturn;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return getPageCount();
        }
    }

    ViewPager.OnPageChangeListener carouselOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            //Programmatic scroll
            userInitiatedScroll = false;

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

            //User initiated scroll

            if (previousState == ViewPager.SCROLL_STATE_DRAGGING
                    && state == ViewPager.SCROLL_STATE_SETTLING) {
                userInitiatedScroll = true;

                if (disableAutoPlayOnUserInteraction) {
                    pauseCarousel();
                } else {
                    playCarousel();
                }

            } else if (previousState == ViewPager.SCROLL_STATE_SETTLING
                    && state == ViewPager.SCROLL_STATE_IDLE) {
                userInitiatedScroll = false;
            }

            previousState = state;

        }
    };

    private class SwipeTask extends TimerTask {
        public void run() {
            containerViewPager.post(new Runnable() {
                public void run() {
                    containerViewPager.setCurrentItem((containerViewPager.getCurrentItem() + 1) % getPageCount(), true);
                }
            });
        }
    }

    public void setImageListener(ImageListener mImageListener) {
        this.mImageListener = mImageListener;
    }

    public void setViewListener(ViewListener mViewListener) {
        this.mViewListener = mViewListener;
    }

    public int getPageCount() {
        return mPageCount;
    }

    public void setPageCount(int mPageCount) {
        this.mPageCount = mPageCount;
        setData();
    }

    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        containerViewPager.addOnPageChangeListener(listener);
    }

    public void setCurrentItem(int item) {
        containerViewPager.setCurrentItem(item);
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mIndicator.getLayoutParams();
        switch (mPosition) {
            case POSITION_LEFT:
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                break;
            case POSITION_RIGHT:
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                break;
            case POSITION_CENTER:
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                break;
            default:
                throw new IllegalArgumentException("Position must be LEFT, RIGHT or CENTER.");
        }
    }


    public int getOrientation() {
        return mIndicator.getOrientation();
    }

    public int getFillColor() {
        return mIndicator.getFillColor();
    }

    public int getStrokeColor() {
        return mIndicator.getStrokeColor();
    }

    public void setSnap(boolean snap) {
        mIndicator.setSnap(snap);
    }

    public void setRadius(float radius) {
        mIndicator.setRadius(radius);
    }

    public float getStrokeWidth() {
        return mIndicator.getStrokeWidth();
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
    }

    public Drawable getIndicatorBackground() {
        return mIndicator.getBackground();
    }

    public void setFillColor(int fillColor) {
        mIndicator.setFillColor(fillColor);
    }

    public int getPageColor() {
        return mIndicator.getPageColor();
    }

    public void setOrientation(int orientation) {
        mIndicator.setOrientation(orientation);
    }

    public boolean isSnap() {
        return mIndicator.isSnap();
    }

    public void setStrokeColor(int strokeColor) {
        mIndicator.setStrokeColor(strokeColor);
    }

    public float getRadius() {
        return mIndicator.getRadius();
    }

    public void setPageColor(int pageColor) {
        mIndicator.setPageColor(pageColor);
    }

    public void setStrokeWidth(float strokeWidth) {
        mIndicator.setStrokeWidth(strokeWidth);
    }
}