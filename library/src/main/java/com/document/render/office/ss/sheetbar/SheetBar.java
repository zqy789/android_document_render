
package com.document.render.office.ss.sheetbar;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.document.render.R;
import com.document.render.office.constant.EventConstant;
import com.document.render.office.system.IControl;

import java.util.Vector;


public class SheetBar extends HorizontalScrollView implements OnClickListener {


    private int minimumWidth;

    private SheetbarResManager sheetbarResManager;

    private int sheetbarHeight;

    private SheetButton currentSheet;

    private IControl control;

    private LinearLayout sheetbarFrame;

    public SheetBar(Context context) {
        super(context);
    }

    public SheetBar(Context context, IControl control, int minimumWidth) {
        super(context);
        this.control = control;
        this.setVerticalFadingEdgeEnabled(false);
        this.setFadingEdgeLength(0);
        if (minimumWidth == getResources().getDisplayMetrics().widthPixels) {
            this.minimumWidth = -1;
        } else {
            this.minimumWidth = minimumWidth;
        }
        init();
    }


    public void onConfigurationChanged(Configuration newConfig) {
        sheetbarFrame.setMinimumWidth(minimumWidth == -1 ? getResources().getDisplayMetrics().widthPixels
                : minimumWidth);
    }


    private void init() {
        Context context = this.getContext();
        sheetbarFrame = new LinearLayout(context);
        sheetbarFrame.setGravity(Gravity.BOTTOM);


        sheetbarResManager = new SheetbarResManager(context);




        Drawable drawable = getResources().getDrawable(R.drawable.ss_sheetbar_bg);


        sheetbarFrame.setBackground(drawable);
        sheetbarFrame.setOrientation(LinearLayout.HORIZONTAL);
        sheetbarFrame.setMinimumWidth(minimumWidth == -1 ? getResources().getDisplayMetrics().widthPixels
                : minimumWidth);
        sheetbarHeight = drawable.getIntrinsicHeight();

        drawable = sheetbarResManager.getDrawable((short) R.drawable.ss_sheetbar_shadow_left);
        LayoutParams parmas = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        View left = new View(context);
        left.setBackground(drawable);
        sheetbarFrame.addView(left, parmas);


        @SuppressWarnings("unchecked")
        Vector<String> vec = (Vector<String>) control.getActionValue(EventConstant.SS_GET_ALL_SHEET_NAME, null);
        drawable = sheetbarResManager.getDrawable(SheetbarResConstant.RESID_SHEETBUTTON_NORMAL_LEFT);
        LayoutParams parmasButton = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int count = vec.size();
        for (int i = 0; i < count; i++) {
            SheetButton sb = new SheetButton(context, vec.get(i), i, sheetbarResManager);
            if (currentSheet == null) {
                currentSheet = sb;
                currentSheet.changeFocus(true);
            }
            sb.setOnClickListener(this);
            sheetbarFrame.addView(sb, parmasButton);

            if (i < count - 1) {
                View view = new View(context);
                drawable = sheetbarResManager.getDrawable(SheetbarResConstant.RESID_SHEETBAR_SEPARATOR_H);
                view.setBackground(drawable);
                sheetbarFrame.addView(view, parmasButton);
            }
        }


        View right = new View(context);
        drawable = sheetbarResManager.getDrawable(SheetbarResConstant.RESID_SHEETBAR_SHADOW_RIGHT);
        right.setBackgroundDrawable(drawable);
        sheetbarFrame.addView(right, parmas);


        addView(sheetbarFrame, new LayoutParams(LayoutParams.WRAP_CONTENT, sheetbarHeight));
    }


    public void onClick(View v) {
        currentSheet.changeFocus(false);

        SheetButton sb = (SheetButton) v;
        sb.changeFocus(true);
        currentSheet = sb;

        control.actionEvent(EventConstant.SS_SHOW_SHEET, currentSheet.getSheetIndex());
    }


    public void setFocusSheetButton(int index) {
        if (currentSheet.getSheetIndex() == index) {
            return;
        }

        int count = sheetbarFrame.getChildCount();
        View view = null;
        for (int i = 0; i < count; i++) {
            view = sheetbarFrame.getChildAt(i);
            if (view instanceof SheetButton && ((SheetButton) view).getSheetIndex() == index) {
                currentSheet.changeFocus(false);

                currentSheet = (SheetButton) view;
                currentSheet.changeFocus(true);
                break;
            }
        }


        int screenWidth = control.getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int barWidth = sheetbarFrame.getWidth();
        if (barWidth > screenWidth) {
            int left = view.getLeft();
            int right = view.getRight();
            int off = (screenWidth - (right - left)) / 2;

            off = left - off;
            if (off < 0) {
                off = 0;
            } else if (off + screenWidth > barWidth) {
                off = barWidth - screenWidth;
            }

            scrollTo(off, 0);
        }
    }


    public int getSheetbarHeight() {
        return sheetbarHeight;
    }


    public void dispose() {
        sheetbarResManager.dispose();
        sheetbarResManager = null;

        currentSheet = null;
        if (sheetbarFrame != null) {
            int count = sheetbarFrame.getChildCount();
            View v;
            for (int i = 0; i < count; i++) {
                v = sheetbarFrame.getChildAt(i);
                if (v instanceof SheetButton) {
                    ((SheetButton) v).dispose();
                }
            }
            sheetbarFrame = null;
        }
    }

}
