
package com.document.render.office.system;

import com.document.render.office.constant.EventConstant;


public class BackReaderThread extends Thread {


    private boolean die;

    private IReader reader;

    private IControl control;


    public BackReaderThread(IReader reader, IControl control) {
        super();
        this.reader = reader;
        this.control = control;
    }


    public void run() {
        control.actionEvent(EventConstant.SYS_START_BACK_READER_ID, true);
        while (true) {
            if (die) {
                break;
            }
            try {
                if (!reader.isReaderFinish()) {
                    reader.backReader();
                    sleep(50);
                } else {
                    control.actionEvent(EventConstant.SYS_READER_FINSH_ID, true);
                    control = null;
                    reader = null;
                    break;
                }
            } catch (OutOfMemoryError e) {
                control.getSysKit().getErrorKit().writerLog(e, true);
                control.actionEvent(EventConstant.SYS_READER_FINSH_ID, true);
                control = null;
                reader = null;

                break;
            } catch (Exception e) {
                if (!reader.isAborted()) {
                    control.getSysKit().getErrorKit().writerLog(e, true);
                    control.actionEvent(EventConstant.SYS_READER_FINSH_ID, true);
                    control = null;
                    reader = null;
                }
                break;
            }
        }
    }


    public void setDie(boolean die) {
        this.die = die;
    }
}
