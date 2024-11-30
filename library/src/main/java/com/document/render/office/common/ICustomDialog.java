
package com.document.render.office.common;

public interface ICustomDialog {

    public static final byte DIALOGTYPE_PASSWORD = 0;

    public static final byte DIALOGTYPE_ENCODE = 1;

    public static final byte DIALOGTYPE_LOADING = 2;

    public static final byte DIALOGTYPE_ERROR = 3;

    public static final byte DIALOGTYPE_FIND = 4;



    public void showDialog(byte type);


    public void dismissDialog(byte type);
}
