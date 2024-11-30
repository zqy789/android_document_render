
package com.document.render.office.system.beans;

import com.document.render.office.system.ITimerListener;

import java.util.Timer;
import java.util.TimerTask;


public class ATimer {

    private boolean isRunning;

    private int delay;

    private Timer timer;

    private ITimerListener listener;



    public ATimer(int delay, ITimerListener listener) {
        super();
        this.delay = delay;
        this.listener = listener;
    }


    public void start() {
        if (isRunning) {
            return;
        }
        timer = new Timer();
        timer.schedule(new ATimerTask(), delay);
        isRunning = true;
    }


    public boolean isRunning() {
        return this.isRunning;
    }


    public void stop() {
        if (isRunning) {
            timer.cancel();
            timer.purge();
            isRunning = false;
        }
    }


    public void restart() {
        stop();
        start();
    }


    public void dispose() {
        if (isRunning) {
            timer.cancel();
            timer.purge();
            isRunning = false;
        }
        timer = null;
        listener = null;
    }

    class ATimerTask extends TimerTask {
        public ATimerTask() {
        }

        public void run() {
            try {
                timer.schedule(new ATimerTask(), delay);
                listener.actionPerformed();
            } catch (Exception e) {
            }
        }

    }
}
