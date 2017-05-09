package com.boge.calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.boge.library.CalendarBean;
import com.boge.library.CalendarDateView;
import com.boge.library.CalendarUtil;
import com.boge.library.CalendarView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.calendarDateView)
    CalendarDateView calendarDateView;
    @Bind(R.id.date)
    TextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        calendarDateView.initData();
        List<String> dates = new ArrayList<String>();
        dates.add("2017-04-21");
        dates.add("2017-04-22");
        dates.add("2017-04-23");
        dates.add("2017-04-24");
        dates.add("2017-02-25");
        dates.add("2017-02-26");
        dates.add("2017-03-26");
        dates.add("2017-03-25");
        calendarDateView.setStars(dates);
        calendarDateView.setOnItemClickListener(new CalendarView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion, CalendarBean bean) {
                Log.i("test", "position:" + postion);
            }
        });
        calendarDateView.setOnCaledarChangeListener(new CalendarDateView.OnCaledarChangeListener() {
            @Override
            public void onCaledarChangeListener(int year, int mouth, int day) {
                Log.i("test", "现在是北京时间" + year + "年" + mouth + "月" + day + "日");
                date.setText(String.format("%d年%d月%d日", year, mouth, day));
            }
        });
        int[] ymd = CalendarUtil.getYMD(new Date());
        date.setText(String.format("%d年%d月%d日", ymd[0], ymd[1], ymd[2]));
    }

    @OnClick({R.id.next, R.id.prev})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next:
                calendarDateView.setCurrentItem(calendarDateView.getPosition() + 1);
                break;
            case R.id.prev:
                calendarDateView.setCurrentItem(calendarDateView.getPosition() - 1);
                break;
        }
    }
}
