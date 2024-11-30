

package com.document.render.office.fc.pdf;


public class PDFOutlineItem {

    private final int level;
    private final String title;
    private final int pageNumber;



    public PDFOutlineItem(int _level, String _title, int pageNumber) {
        level = _level;
        title = _title;
        this.pageNumber = pageNumber;
    }


    public int getLevel() {
        return level;
    }


    public String getTitle() {
        return title;
    }


    public int getPageNumber() {
        return pageNumber;
    }
}
