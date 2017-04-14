package com.boge.library;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

/**
 * Created by codbking on 2016/12/18.
 * email:codbking@gmail.com
 * github:https://github.com/codbking
 * blog:http://www.jianshu.com/users/49d47538a2dd/latest_articles
 */

public class CalendarView extends ViewGroup {

    private static final String TAG = "CalendarView";

    private int selectPostion = -1;

    private List<CalendarBean> data;
    private OnItemClickListener onItemClickListener;

    private int row = 6;
    private int column = 7;
    private int itemWidth;
    private int itemHeight;

    private boolean isToday;

    private int beforeColor = Color.BLACK;
    private int afterColor = 0xff9299a1;

    public interface OnItemClickListener {
        void onItemClick(View view, int postion, CalendarBean bean);
    }

    public CalendarView(Context context) {
        super(context);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public void setData(List<CalendarBean> data,boolean isToday) {
        this.data = data;
        this.isToday=isToday;
        setItem();
        requestLayout();
    }

    private void setItem() {

        selectPostion = -1;
        View childAt = getChildAt(0);

        View view1 = LayoutInflater.from(getContext()).inflate(R.layout.week_calendar, null);
        ViewGroup.LayoutParams params2 = new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, px(48));
        view1.setLayoutParams(params2);
        if(childAt == null || childAt != view1){
            addViewInLayout(view1, 0, view1.getLayoutParams(), true);
        }

        for (int i = 0; i < data.size(); i++) {
            CalendarBean bean = data.get(i);
            View view = getChildAt(i);

            View chidView = LayoutInflater.from(getContext()).inflate(R.layout.item_calendar, null);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(px(48), px(48));
            chidView.setLayoutParams(params);
            TextView textView = (TextView) chidView.findViewById(R.id.text);

            textView.setText("" + bean.day);
            if (bean.mothFlag != 0) {
                textView.setTextColor(0xff9299a1);
                textView.setVisibility(View.INVISIBLE);
            } else {
                textView.setTextColor(Color.BLACK);
            }
            int[]date=CalendarUtil.getYMD(new Date());
            if((bean.year > date[0]) || (bean.year == date[0] && bean.moth==date[1] && bean.day > date[2]) || (bean.year == date[0] && bean.moth>date[1]) ){
                textView.setTextColor(0xff9299a1);
            }

            if (view == null || view != chidView) {
                addViewInLayout(chidView, i+1, chidView.getLayoutParams(), true);
            }

            if(isToday&&selectPostion==-1){

                if(bean.year==date[0]&&bean.moth==date[1]&&bean.day==date[2]){
                     selectPostion=i+1;
                }
            }else {
                if (selectPostion == -1 && bean.day == 1) {
                    selectPostion = i+1;
                }
            }
            chidView.setSelected(selectPostion==i+1);

            setItemClick(chidView, i+1, bean);
        }
    }

    public void setItemClick(final View view, final int potsion, final CalendarBean bean) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectPostion != -1) {
                    getChildAt(selectPostion).setSelected(false);
                    getChildAt(potsion).setSelected(true);
                }
                selectPostion = potsion;
                if (onItemClickListener != null) {
                    int num = 0;
                    for (int i = 0 ; i < data.size() ; i++){
                        if(data.get(i).mothFlag ==0 ){
                            break;
                        }else {
                            num++;
                        }
                    }
                    onItemClickListener.onItemClick(view, potsion - num, bean);
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parentWidth = MeasureSpec.getSize(MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY));

        itemWidth = parentWidth / column;
        itemHeight = itemWidth;
        View childAt = getChildAt(0);
        LayoutParams params1 = childAt.getLayoutParams();
        int weekHeight = params1.height;
        int weekWidth = parentWidth;

        View view = getChildAt(1);
        if (view == null) {
            return;
        }
        LayoutParams params = view.getLayoutParams();
        if (params != null && params.height > 0) {
            itemHeight = params.height;
        }
        setMeasuredDimension(parentWidth, weekHeight+itemHeight * row);

        childAt.measure(MeasureSpec.makeMeasureSpec(weekWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(weekHeight, MeasureSpec.EXACTLY));
        for(int i=1;i<getChildCount();i++){
            View childView=getChildAt(i);
            childView.measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY));
        }

        Log.i(TAG, "onMeasure() called with: itemHeight = [" + itemHeight + "], itemWidth = [" + itemWidth + "]");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View view = getChildAt(0);
        view.layout(0,t,view.getMeasuredWidth(),view.getMeasuredHeight());
        for (int i = 1; i <getChildCount(); i++) {
            layoutChild(getChildAt(i), i-1, l, t, r, b, view.getMeasuredHeight());
        }
    }

    private void layoutChild(View view, int postion, int l, int t, int r, int b, int h) {

        int cc = postion % column;
        int cr = postion / column;

        int itemWidth = view.getMeasuredWidth();
        int itemHeight = view.getMeasuredHeight();

        l = cc * itemWidth;
        t = cr * itemHeight+h;
        r = l + itemWidth;
        b = t + itemHeight;
        view.layout(l, t, r, b);

    }

    private int px(float dipValue) {
        Resources r=Resources.getSystem();
        final float scale =r.getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
