
package com.document.render.office.system.beans;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;

import com.document.render.office.constant.MainConstant;
import com.document.render.office.system.IControl;
import com.document.render.office.system.IDialogAction;

import java.util.Vector;


public class ADialog extends Dialog implements OnClickListener {

    protected static final int GAP = MainConstant.GAP;

    protected static final int MARGIN = 30;
    protected IControl control;

    protected int dialogID;

    protected Vector<Object> model;

    protected IDialogAction action;

    protected ADialogFrame dialogFrame;

    protected Button ok;

    protected Button cancel;


    public ADialog(IControl control, Context context, IDialogAction action, Vector<Object> model,
                   int dialogID, int titleResID) {
        this(control, context, action, model, dialogID, context.getResources().getString(titleResID));
    }


    public ADialog(IControl control, Context context, IDialogAction action, Vector<Object> model,
                   int dialogID, String title) {
        super(context);
        this.control = control;
        this.dialogID = dialogID;
        this.model = model;
        this.action = action;
        dialogFrame = new ADialogFrame(context, this);
        setTitle(title);



    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(dialogFrame);
        dialogFrame.post(new Runnable() {
            public void run() {
                doLayout();
            }
        });
    }


    public void onConfigurationChanged(Configuration newConfig) {

    }


    public void onClick(View v) {

    }


    public void onBackPressed() {
        super.onBackPressed();
    }


    public void dismiss() {
        super.dismiss();
        dispose();
    }


    public void doLayout() {
    }


    protected void setSize(int w, int h) {
        dialogFrame.setLayoutParams(new LayoutParams(w, h));
    }


    public void dispose() {
        control = null;
        if (model != null) {
            model.clear();
            model = null;
        }
        action = null;
        if (dialogFrame != null) {
            dialogFrame.dispose();
            dialogFrame = null;
        }
        ok = null;
        cancel = null;
    }
}
