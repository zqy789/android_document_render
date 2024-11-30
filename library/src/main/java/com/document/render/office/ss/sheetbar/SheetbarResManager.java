
package com.document.render.office.ss.sheetbar;

import android.content.Context;
import android.graphics.drawable.Drawable;


public class SheetbarResManager {
    private Context context;
    private Drawable sheetbarBG;
    private Drawable sheetbarLeftShadow, sheetbarRightShadow;
    private Drawable hSeparator;

    private Drawable normalLeft;
    private Drawable pushLeft;
    private Drawable focusLeft;

    private Drawable normalMiddle;
    private Drawable pushMiddle;
    private Drawable focusMiddle;

    private Drawable normalRight;
    private Drawable pushRight;
    private Drawable focusRight;

    public SheetbarResManager(Context context) {
        this.context = context;

        ClassLoader loader = context.getClassLoader();

        sheetbarBG = Drawable.createFromResourceStream(context.getResources(), null,
                loader.getResourceAsStream(SheetbarResConstant.RESNAME_SHEETBAR_BG),
                SheetbarResConstant.RESNAME_SHEETBAR_BG);


        sheetbarLeftShadow = Drawable.createFromResourceStream(context.getResources(), null,
                loader.getResourceAsStream(SheetbarResConstant.RESNAME_SHEETBAR_SHADOW_LEFT),
                SheetbarResConstant.RESNAME_SHEETBAR_SHADOW_LEFT);

        sheetbarRightShadow = Drawable.createFromResourceStream(context.getResources(), null,
                loader.getResourceAsStream(SheetbarResConstant.RESNAME_SHEETBAR_SHADOW_RIGHT),
                SheetbarResConstant.RESNAME_SHEETBAR_SHADOW_RIGHT);


        hSeparator = Drawable.createFromResourceStream(context.getResources(), null,
                loader.getResourceAsStream(SheetbarResConstant.RESNAME_SHEETBAR_SEPARATOR_H),
                SheetbarResConstant.RESNAME_SHEETBAR_SEPARATOR_H);


        normalLeft = Drawable.createFromResourceStream(context.getResources(), null,
                loader.getResourceAsStream(SheetbarResConstant.RESNAME_SHEETBUTTON_NORMAL_LEFT),
                SheetbarResConstant.RESNAME_SHEETBUTTON_NORMAL_LEFT);
        normalRight = Drawable.createFromResourceStream(context.getResources(), null,
                loader.getResourceAsStream(SheetbarResConstant.RESNAME_SHEETBUTTON_NORMAL_RIGHT),
                SheetbarResConstant.RESNAME_SHEETBUTTON_NORMAL_RIGHT);
        normalMiddle = Drawable.createFromResourceStream(context.getResources(), null,
                loader.getResourceAsStream(SheetbarResConstant.RESNAME_SHEETBUTTON_NORMAL_MIDDLE),
                SheetbarResConstant.RESNAME_SHEETBUTTON_NORMAL_MIDDLE);


        pushLeft = Drawable.createFromResourceStream(context.getResources(), null,
                loader.getResourceAsStream(SheetbarResConstant.RESNAME_SHEETBUTTON_PUSH_LEFT),
                SheetbarResConstant.RESNAME_SHEETBUTTON_PUSH_LEFT);
        pushMiddle = Drawable.createFromResourceStream(context.getResources(), null,
                loader.getResourceAsStream(SheetbarResConstant.RESNAME_SHEETBUTTON_PUSH_MIDDLE),
                SheetbarResConstant.RESNAME_SHEETBUTTON_PUSH_MIDDLE);
        pushRight = Drawable.createFromResourceStream(context.getResources(), null,
                loader.getResourceAsStream(SheetbarResConstant.RESNAME_SHEETBUTTON_PUSH_RIGHT),
                SheetbarResConstant.RESNAME_SHEETBUTTON_PUSH_RIGHT);


        focusLeft = Drawable.createFromResourceStream(context.getResources(), null,
                loader.getResourceAsStream(SheetbarResConstant.RESNAME_SHEETBUTTON_FOCUS_LEFT),
                SheetbarResConstant.RESNAME_SHEETBUTTON_FOCUS_LEFT);
        focusMiddle = Drawable.createFromResourceStream(context.getResources(), null,
                loader.getResourceAsStream(SheetbarResConstant.RESNAME_SHEETBUTTON_FOCUS_MIDDLE),
                SheetbarResConstant.RESNAME_SHEETBUTTON_FOCUS_MIDDLE);
        focusRight = Drawable.createFromResourceStream(context.getResources(), null,
                loader.getResourceAsStream(SheetbarResConstant.RESNAME_SHEETBUTTON_FOCUS_RIGHT),
                SheetbarResConstant.RESNAME_SHEETBUTTON_FOCUS_RIGHT);

    }

    public Drawable getDrawable(short resID) {
        switch (resID) {
            case SheetbarResConstant.RESID_SHEETBAR_BG:
                return sheetbarBG;

            case SheetbarResConstant.RESID_SHEETBAR_SHADOW_LEFT:
                return sheetbarLeftShadow;

            case SheetbarResConstant.RESID_SHEETBAR_SHADOW_RIGHT:
                return sheetbarRightShadow;

            case SheetbarResConstant.RESID_SHEETBAR_SEPARATOR_H:
                return hSeparator;

            case SheetbarResConstant.RESID_SHEETBUTTON_NORMAL_LEFT:
                return normalLeft;

            case SheetbarResConstant.RESID_SHEETBUTTON_NORMAL_MIDDLE:
                return Drawable.createFromResourceStream(context.getResources(), null,
                        context.getClassLoader().getResourceAsStream(SheetbarResConstant.RESNAME_SHEETBUTTON_NORMAL_MIDDLE),
                        SheetbarResConstant.RESNAME_SHEETBUTTON_NORMAL_MIDDLE);

            case SheetbarResConstant.RESID_SHEETBUTTON_NORMAL_RIGHT:
                return normalRight;

            case SheetbarResConstant.RESID_SHEETBUTTON_PUSH_LEFT:
                return pushLeft;

            case SheetbarResConstant.RESID_SHEETBUTTON_PUSH_MIDDLE:
                return pushMiddle;

            case SheetbarResConstant.RESID_SHEETBUTTON_PUSH_RIGHT:
                return pushRight;

            case SheetbarResConstant.RESID_SHEETBUTTON_FOCUS_LEFT:
                return focusLeft;

            case SheetbarResConstant.RESID_SHEETBUTTON_FOCUS_MIDDLE:
                return focusMiddle;

            case SheetbarResConstant.RESID_SHEETBUTTON_FOCUS_RIGHT:
                return focusRight;
        }

        return null;
    }

    public void dispose() {
        sheetbarBG = null;

        sheetbarLeftShadow = null;
        sheetbarRightShadow = null;

        hSeparator = null;

        normalLeft = null;
        normalMiddle = null;
        normalRight = null;

        pushLeft = null;
        pushMiddle = null;
        pushRight = null;

        focusLeft = null;
        focusMiddle = null;
        focusRight = null;
    }
}
