
package com.document.render.office.wp.control;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.document.render.office.system.IControl;
import com.document.render.office.system.beans.pagelist.APageListItem;
import com.document.render.office.system.beans.pagelist.APageListView;
import com.document.render.office.wp.view.PageRoot;
import com.document.render.office.wp.view.PageView;


public class WPPageListItem extends APageListItem {
    private static final int BACKGROUND_COLOR = 0xFFFFFFFF;

    private boolean isInit = true;

    private PageRoot pageRoot;


    public WPPageListItem(APageListView listView, IControl control, int pageWidth, int pageHeight) {
        super(listView, pageWidth, pageHeight);
        this.control = control;
        this.pageRoot = (PageRoot) listView.getModel();
        this.setBackgroundColor(BACKGROUND_COLOR);
    }


    public void onDraw(Canvas canvas) {
        PageView pv = pageRoot.getPageView(pageIndex);
        if (pv != null) {
            float zoom = listView.getZoom();
            canvas.save();
            canvas.translate(-pv.getX() * zoom, -pv.getY() * zoom);
            pv.drawForPrintMode(canvas, 0, 0, zoom);
            canvas.restore();
        }
    }


    public void setPageItemRawData(final int pIndex, int pageWidth, int pageHeight) {
        super.setPageItemRawData(pIndex, pageWidth, pageHeight);

        if ((int) (listView.getZoom() * 100) == 100
                || (isInit && pIndex == 0)) {
            listView.exportImage(this, null);
        }
        isInit = false;
    }


    protected void addRepaintImageView(Bitmap bmp) {
        postInvalidate();
        listView.exportImage(this, null);
    }


    protected void removeRepaintImageView() {

    }


    public void dispose() {
        super.dispose();
        control = null;
        pageRoot = null;
    }

}
