
package com.document.render.office.system.beans;

import android.content.Context;
import android.content.res.Configuration;
import android.widget.LinearLayout;


public class ADialogFrame extends LinearLayout {

    private ADialog dialog;



    public ADialogFrame(Context context, ADialog dialog) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);
        this.dialog = dialog;
    }


    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dialog.onConfigurationChanged(newConfig);
    }


    public void dispose() {
        dialog = null;
    }
}
