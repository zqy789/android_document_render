
package com.document.render.office.fc.usermodel;


public interface Hyperlink {

    public static final int LINK_URL = 1;


    public static final int LINK_DOCUMENT = 2;


    public static final int LINK_EMAIL = 3;


    public static final int LINK_FILE = 4;



    public String getAddress();


    public void setAddress(String address);


    public String getLabel();


    public void setLabel(String label);


    public int getType();
}
