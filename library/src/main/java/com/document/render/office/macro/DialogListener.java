
package com.document.render.office.macro;

import com.document.render.office.common.ICustomDialog;

public interface DialogListener {

    public static final byte DIALOGTYPE_PASSWORD = ICustomDialog.DIALOGTYPE_PASSWORD;

    public static final byte DIALOGTYPE_ENCODE = ICustomDialog.DIALOGTYPE_ENCODE;

    public static final byte DIALOGTYPE_LOADING = ICustomDialog.DIALOGTYPE_LOADING;

    public static final byte DIALOGTYPE_ERROR = ICustomDialog.DIALOGTYPE_ERROR;

    public static final byte DIALOGTYPE_FIND = ICustomDialog.DIALOGTYPE_FIND;



    public void showDialog(byte type);


    public void dismissDialog(byte type);
}
