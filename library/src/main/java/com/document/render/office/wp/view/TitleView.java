
package com.document.render.office.wp.view;

import com.document.render.office.constant.wp.WPViewConstant;
import com.document.render.office.simpletext.control.IWord;
import com.document.render.office.simpletext.model.IDocument;
import com.document.render.office.simpletext.model.IElement;
import com.document.render.office.simpletext.view.AbstractView;
import com.document.render.office.simpletext.view.IView;
import com.document.render.office.system.IControl;


public class TitleView extends AbstractView {


    private IView pageRoot;


    public TitleView(IElement elem) {
        super();
        this.elem = elem;
    }


    public short getType() {
        return WPViewConstant.TITLE_VIEW;
    }


    public IWord getContainer() {
        if (pageRoot != null) {
            return pageRoot.getContainer();
        }
        return null;
    }


    public IControl getControl() {
        if (pageRoot != null) {
            return pageRoot.getControl();
        }
        return null;
    }


    public IDocument getDocument() {
        if (pageRoot != null) {
            return pageRoot.getDocument();
        }
        return null;
    }


    public void setPageRoot(IView root) {
        pageRoot = root;
    }

    public void dispose() {
        super.dispose();
        pageRoot = null;
    }
}
