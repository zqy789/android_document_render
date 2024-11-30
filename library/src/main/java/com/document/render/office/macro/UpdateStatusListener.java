
package com.document.render.office.macro;

public interface UpdateStatusListener {

    public static final byte ALLPages = -1;


    public void updateStatus();


    public void changeZoom();


    public void changePage();


    public void completeLayout();


    public void updateViewImage(Integer[] views);

}
