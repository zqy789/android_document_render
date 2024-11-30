
package com.document.render.office.wp.control;

import com.document.render.office.constant.DialogConstant;
import com.document.render.office.system.IControl;
import com.document.render.office.system.IDialogAction;

import java.util.Vector;



public class WPDialogAction implements IDialogAction {


    public IControl control;

    public WPDialogAction(IControl control) {
        this.control = control;
    }


    public void doAction(int id, Vector<Object> model) {
        switch (id) {
            case DialogConstant.ENCODING_DIALOG_ID:
                break;

            default:
                break;
        }
    }


    public IControl getControl() {
        return this.control;
    }


    public void dispose() {
        control = null;
    }
}
