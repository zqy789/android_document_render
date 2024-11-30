
package com.document.render.office.wp.view;

import com.document.render.office.simpletext.control.IWord;
import com.document.render.office.simpletext.view.IRoot;
import com.document.render.office.simpletext.view.IView;


public class LayoutThread extends Thread {
    private boolean isDied;
    private IRoot root;


    public LayoutThread(IRoot root) {
        this.root = root;
    }


    public void run() {
        while (true) {
            if (isDied) {
                break;
            }
            try {
                if (root.canBackLayout()) {
                    root.backLayout();
                    sleep(50);
                    continue;
                } else {
                    sleep(1000);
                }
            } catch (Exception e) {
                if (root != null) {
                    IWord word = ((IView) root).getContainer();
                    if (word != null && word.getControl() != null) {
                        word.getControl().getSysKit().getErrorKit().writerLog(e);
                    }
                }
                break;
            }
        }
    }


    public void setDied() {
        isDied = true;
    }


    public void dispose() {
        root = null;
        isDied = true;
    }
}
