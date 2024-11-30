
package com.document.render.office.simpletext.view;

import com.document.render.office.constant.wp.WPModelConstant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class ViewContainer {


    private List<IView> paras;

    public ViewContainer() {
        paras = new ArrayList<IView>();
    }


    public synchronized void add(IView para) {
        if (para != null && para.getEndOffset(null) < WPModelConstant.HEADER) {
            paras.add(para);
        }
    }


    public synchronized void sort() {
        try {
            Collections.sort(paras, new Comparator<IView>() {
                public int compare(IView prePara, IView nextPara) {
                    return prePara.getEndOffset(null) <= nextPara.getStartOffset(null) ? -1 : 1;
                }
            });
        } catch (Exception e) {

        }
    }


    public synchronized IView getParagraph(long offset, boolean isBack) {
        int size = paras.size();
        if (size == 0 || offset < 0 || offset >= paras.get(size - 1).getEndOffset(null)) {
            return null;
        }
        int max = size;
        int min = 0;
        IView view;
        long start;
        long end;
        int mid = -1;
        while (true) {
            mid = (max + min) / 2;
            view = paras.get(mid);
            start = view.getStartOffset(null);
            end = view.getEndOffset(null);
            if (offset >= start && offset < end) {
                break;
            } else if (start > offset) {
                max = mid - 1;
            } else if (end <= offset) {
                min = mid + 1;
            }
        }
        return view;
    }


    public synchronized void clear() {
        if (paras != null) {
            paras.clear();
        }
    }


    public synchronized void dispose() {
        if (paras != null) {
            paras.clear();
            paras = null;
        }
    }
}
