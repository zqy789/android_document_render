
package com.document.render.office.wp.control;


public class StatusManage {


    private boolean selectText;

    private long pressOffset;

    private boolean isTouchDown;


    public boolean isSelectTextStatus() {
        return selectText;
    }


    public void setSelectTextStatus(boolean selectText) {
        this.selectText = selectText;
    }


    public long getPressOffset() {
        return pressOffset;
    }


    public void setPressOffset(long pressOffset) {
        this.pressOffset = pressOffset;
    }


    public boolean isTouchDown() {
        return isTouchDown;
    }


    public void setTouchDown(boolean isTouchDown) {
        this.isTouchDown = isTouchDown;
    }


    public void dispose() {

    }

}
