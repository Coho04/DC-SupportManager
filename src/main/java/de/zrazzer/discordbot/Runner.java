package de.zrazzer.discordbot;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Runner {

    private final Runnable dailyTask;
    private final int hour;
    private final int day;
    private final int minute;
    private final int second;
    private String runThreadName;

    public Runner(Calendar timeOfDay, Runnable dailyTask, String runThreadName) {
        this.dailyTask = dailyTask;
        this.hour = timeOfDay.get(Calendar.HOUR_OF_DAY);
        this.day = timeOfDay.get(Calendar.DAY_OF_WEEK);
        this.minute = timeOfDay.get(Calendar.MINUTE);
        this.second = timeOfDay.get(Calendar.SECOND);
        this.runThreadName = runThreadName;
        start();
    }

    public void start() {
        startTimer();
    }

    private void startTimer() {
        new Timer(runThreadName, true).schedule(new TimerTask() {
            @Override
            public void run() {
                dailyTask.run();
                startTimer();
            }
        }, getNextRunTime());
    }


    private Date getNextRunTime() {
        Calendar startTime = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, hour);
        startTime.set(Calendar.DAY_OF_WEEK, day);
        startTime.set(Calendar.MINUTE, minute);
        startTime.set(Calendar.SECOND, second);
        startTime.set(Calendar.MILLISECOND, 0);

        if (startTime.before(now) || startTime.equals(now)) {
            startTime.add(Calendar.DATE, 1);
        }
        return startTime.getTime();
    }
}
