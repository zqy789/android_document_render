
package com.document.render.office.common.hyperlink;


public class Hyperlink {

    public static final int LINK_URL = 1;

    public static final int LINK_DOCUMENT = 2;

    public static final int LINK_EMAIL = 3;

    public static final int LINK_FILE = 4;

    public static final int LINK_BOOKMARK = 5;

    private int id = -1;

    private int type;

    private String address;

    private String title;


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public int getLinkType() {
        return type;
    }


    public void setLinkType(int type) {
        this.type = type;
    }

    /**
     * @return Returns the address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address The address to set.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     */
    public void dispose() {
        address = null;
        title = null;
    }
}
