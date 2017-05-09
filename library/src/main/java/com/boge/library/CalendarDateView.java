package com.boge.library;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.boge.library.CalendarFactory.getMonthOfDayList;

/**
 * @author boge
 * @version 1.0
 * @date 2017/4/14
 */

public class CalendarDateView extends ViewPager {

    HashMap<Integer, CalendarView> views = new HashMap<>();
    private LinkedList<CalendarView> cache = new LinkedList();
    private CalendarView.OnItemClickListener onItemClickListener;

    private OnCaledarChangeListener onCaledarChangeListener;

    private final int[] dateArr= CalendarUtil.getYMD(new Date());

    private int index = 0;

    private int[] date;

    private List<String> stars = new ArrayList<String>();

    public CalendarDateView(Context context) {
        super(context);
    }

    public CalendarDateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public void initData() {
        index = Integer.MAX_VALUE/2;
        setCurrentItem(Integer.MAX_VALUE/2, false);
        getAdapter().notifyDataSetChanged();

    }

    public void setStars(List<String> stars) {
        this.stars = stars;
        getAdapter().notifyDataSetChanged();
    }

    public void setOnItemClickListener(CalendarView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnCaledarChangeListener(OnCaledarChangeListener onCaledarChangeListener) {
        this.onCaledarChangeListener = onCaledarChangeListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int calendarHeight = 0;
        if (getAdapter() != null) {
            CalendarView view = (CalendarView) getChildAt(0);
            if (view != null) {
                calendarHeight = view.getMeasuredHeight();
            }
        }
        setMeasuredDimension(widthMeasureSpec, MeasureSpec.makeMeasureSpec(calendarHeight, MeasureSpec.EXACTLY));
    }

    private void init() {

        setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return Integer.MAX_VALUE;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, final int position) {
                if(views.get(position) != null){
                    return views.get(position);
                }

                CalendarView view;
//                if (!cache.isEmpty()) {
//                    view = cache.removeFirst();
//                } else {
                    view = new CalendarView(container.getContext());
//                }
                view.setOnItemClickListener(onItemClickListener);

                view.setData(getMonthOfDayList(dateArr[0],dateArr[1]+position-Integer.MAX_VALUE/2),position==Integer.MAX_VALUE/2);
                if(stars.size()>0){
                    view.setStars(stars);
                }
                container.addView(view);
                views.put(position, view);

                return view;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
//                cache.addLast((CalendarView) object);
                views.remove(position);
            }
        });

        addOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                index = position;
                if(position == Integer.MAX_VALUE/2){
                    date = new int[]{dateArr[0], dateArr[1] + position - Integer.MAX_VALUE / 2, dateArr[2]};
                }else {
                    date = new int[]{dateArr[0], dateArr[1] + position - Integer.MAX_VALUE / 2, 1};
                }

                if(onCaledarChangeListener != null){
                    onCaledarChangeListener.onCaledarChangeListener(CalendarUtil.getyear(date[0], date[1])
                        ,CalendarUtil.getMothOfMonth(date[0], date[1]), date[2]);
                }
            }
        });
    }

    public int getPosition(){
        return index;
    }

    public interface OnCaledarChangeListener{
        void onCaledarChangeListener(int year, int mouth, int day);
    }

}
