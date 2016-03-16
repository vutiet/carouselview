package com.synnapps.carouselview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Sayyam on 11/25/15.
 */
public class CarouselView extends FrameLayout {

    private final int DEFAULT_SLIDE_INTERVAL = 3500;

    private int mPageCount;
    private int slideInterval = DEFAULT_SLIDE_INTERVAL;
    private int mPosition = 1;

    private ViewPager containerViewPager;
    private CirclePageIndicator mIndicator;
    private ImageListener mImageListener;

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

            //Retrieve styles attributes
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CarouselView, defStyleAttr, 0);
            try {
                slideInterval = a.getInt(R.styleable.CarouselView_slideInterval, DEFAULT_SLIDE_INTERVAL);
                mPosition = a.getInt(R.styleable.CarouselView_indicatorPosition, 1);
                mIndicator.setCentered(true);
                mIndicator.setOrientation(a.getInt(R.styleable.CarouselView_android_orientation, LinearLayout.HORIZONTAL));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mIndicator.setBackground(a.getDrawable(R.styleable.CarouselView_android_background));
                } else {
                    mIndicator.setBackgroundDrawable(a.getDrawable(R.styleable.CarouselView_android_background));
                }
                mIndicator.setFillColor(a.getColor(R.styleable.CarouselView_indicatorFillColor, 0));
                mIndicator.setPageColor(a.getColor(R.styleable.CarouselView_indicatorPageColor, 0));
                mIndicator.setRadius(a.getFloat(R.styleable.CarouselView_indicatorRadius, 0));
                mIndicator.setSnap(a.getBoolean(R.styleable.CarouselView_indicatorSnap, false));
                mIndicator.setRadius(a.getFloat(R.styleable.CarouselView_indicatorRadius, 0));
                mIndicator.setStrokeWidth(a.getFloat(R.styleable.CarouselView_indicatorStrokeWidth, 0));
                mIndicator.setStrokeColor(a.getColor(R.styleable.CarouselView_indicatorStrokeColor, 0));
            } finally {
                a.recycle();
            }
        }
    }

    public void setData() {
        CarouselPagerAdapter carouselPagerAdapter = new CarouselPagerAdapter(getContext());
        containerViewPager.setAdapter(carouselPagerAdapter);

        mIndicator.setViewPager(containerViewPager);

        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new SwipeTask(), slideInterval, slideInterval);
    }

    private class CarouselPagerAdapter extends PagerAdapter {
        private Context mContext;

        public CarouselPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));  //setting image position
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);


            //Picasso.with(mContext).load(imagesURL[position]).placeholder(IMAGE_PLACE_HOLDER).fit().centerCrop().into(imageView);    //setting placeholder image resource and image from URL

            mImageListener.setImageForPosition(position, imageView);

            collection.addView(imageView);
            return imageView;
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

    public int getPageCount() {
        return mPageCount;
    }

    public void setPageCount(int mPageCount) {
        this.mPageCount = mPageCount;
        setData();
    }
}