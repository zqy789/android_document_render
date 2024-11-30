
package com.document.render.office.simpletext.view;


public class ViewKit {
    private static final ViewKit kit = new ViewKit();


    public static ViewKit instance() {
        return kit;
    }


    public int setBitValue(int flag, int pos, boolean b) {
        int temp = b ? flag : ~flag;
        temp = (temp >>> pos) | 1;
        temp = (temp << pos);
        temp = b ? temp | flag : (~temp) & flag;
        return temp;
    }



    public boolean getBitValue(int flag, int pos) {
        return ((flag >>> pos) & 1) == 1;
    }


    public IView getParentView(IView view, short viewType) {
        IView p = view.getParentView();
        while (p != null && p.getType() != viewType) {
            p = p.getParentView();
        }
        return p;
    }
}
