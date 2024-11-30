
package com.document.render.office.ss.sheetbar;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SheetButton extends LinearLayout {
    private static final int FONT_SIZE = 18;

    private static final int SHEET_BUTTON_MIN_WIDTH = 100;
    private SheetbarResManager sheetbarResManager;

    private int sheetIndex;

    private View left;

    private TextView textView;

    private View right;
    private boolean active;


    public SheetButton(Context context, String sheetName, int sheetIndex, SheetbarResManager sheetbarResManager) {
        super(context);
        setOrientation(HORIZONTAL);
        this.sheetIndex = sheetIndex;
        this.sheetbarResManager = sheetbarResManager;

        init(context, sheetName);
    }


    private void init(Context context, String sheetName) {

        left = new View(context);

        left.setBackgroundDrawable(sheetbarResManager.getDrawable(SheetbarResConstant.RESID_SHEETBUTTON_NORMAL_LEFT));
        addView(left);


        textView = new TextView(context);
        textView.setBackgroundDrawable(sheetbarResManager.getDrawable(SheetbarResConstant.RESID_SHEETBUTTON_NORMAL_MIDDLE));
        textView.setText(sheetName);
        textView.setTextSize(FONT_SIZE);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.BLACK);
        int w = (int) textView.getPaint().measureText(sheetName);
        w = Math.max(w, SHEET_BUTTON_MIN_WIDTH);
        addView(textView, new LayoutParams(w, LayoutParams.MATCH_PARENT));


        right = new View(context);
        right.setBackgroundDrawable(sheetbarResManager.getDrawable(SheetbarResConstant.RESID_SHEETBUTTON_NORMAL_RIGHT));
        addView(right);
    }
    ;


    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (!active) {
                    left.setBackgroundDrawable(sheetbarResManager.getDrawable(SheetbarResConstant.RESID_SHEETBUTTON_PUSH_LEFT));
                    textView.setBackgroundDrawable(sheetbarResManager.getDrawable(SheetbarResConstant.RESID_SHEETBUTTON_PUSH_MIDDLE));
                    right.setBackgroundDrawable(sheetbarResManager.getDrawable(SheetbarResConstant.RESID_SHEETBUTTON_PUSH_RIGHT));
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!active) {
                    left.setBackgroundDrawable(sheetbarResManager.getDrawable(SheetbarResConstant.RESID_SHEETBUTTON_NORMAL_LEFT));
                    textView.setBackgroundDrawable(sheetbarResManager.getDrawable(SheetbarResConstant.RESID_SHEETBUTTON_NORMAL_MIDDLE));
                    right.setBackgroundDrawable(sheetbarResManager.getDrawable(SheetbarResConstant.RESID_SHEETBUTTON_NORMAL_RIGHT));
                }
                break;
        }
        return super.onTouchEvent(event);
    }


    public void changeFocus(boolean gainFocus) {
        active = gainFocus;

        left.setBackgroundDrawable(gainFocus ? sheetbarResManager.getDrawable(SheetbarResConstant.RESID_SHEETBUTTON_FOCUS_LEFT) :
                sheetbarResManager.getDrawable(SheetbarResConstant.RESID_SHEETBUTTON_NORMAL_LEFT));
        textView.setBackgroundDrawable(gainFocus ? sheetbarResManager.getDrawable(SheetbarResConstant.RESID_SHEETBUTTON_FOCUS_MIDDLE) :
                sheetbarResManager.getDrawable(SheetbarResConstant.RESID_SHEETBUTTON_NORMAL_MIDDLE));
        right.setBackgroundDrawable(gainFocus ? sheetbarResManager.getDrawable(SheetbarResConstant.RESID_SHEETBUTTON_FOCUS_RIGHT) :
                sheetbarResManager.getDrawable(SheetbarResConstant.RESID_SHEETBUTTON_NORMAL_RIGHT));
    }


    public int getSheetIndex() {
        return this.sheetIndex;
    }


    public void dispose() {
        sheetbarResManager = null;

        left = null;
        textView = null;
        right = null;
    }
}
