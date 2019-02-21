package com.hapis.customer.ui.custom.countdowntimer;

import android.app.Activity;
import android.widget.TextView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimerForCheckInOut {

    private static final int ONE_SECOND_IN_MILLISECOND = 1000;
    private Activity timerActivity;
    private TextView textView;
    private Timer timer;
    public long checkInTime, currentTime, checkOutTime;

    public TimerForCheckInOut(Activity timerActivity, TextView textView) {
        this.timerActivity = timerActivity;
        this.textView = textView;
    }

    public void start() {
        if (timer == null) {
            checkInTime = new Date().getTime();
            startTimerAndScheduleTask();
        }
    }

    private void startTimerAndScheduleTask() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                updateTextInUiThread();
            }
        };
        timer.schedule(task, 0, ONE_SECOND_IN_MILLISECOND);
    }

    public void stop() {
        if (timer != null) {
            checkOutTime = new Date().getTime();
            timer.cancel();
            timer = null;
        }
    }

    private void updateTextInUiThread() {
        timerActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateText();
            }
        });
    }

    private void updateText() {
        currentTime = new Date().getTime();
        final long differenceTime = currentTime-checkInTime;
        textView.setText(TimeConversionUtil.getTimeStringFromMilliseconds(differenceTime));
    }
}
