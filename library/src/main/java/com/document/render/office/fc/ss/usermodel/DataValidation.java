
package com.document.render.office.fc.ss.usermodel;

import com.document.render.office.fc.ss.util.CellRangeAddressList;


public interface DataValidation {
    public abstract DataValidationConstraint getValidationConstraint();


    public abstract int getErrorStyle();


    public abstract void setErrorStyle(int error_style);


    public abstract boolean getEmptyCellAllowed();


    public abstract void setEmptyCellAllowed(boolean allowed);


    public abstract boolean getSuppressDropDownArrow();


    public abstract void setSuppressDropDownArrow(boolean suppress);


    public abstract boolean getShowPromptBox();


    public abstract void setShowPromptBox(boolean show);


    public abstract boolean getShowErrorBox();


    public abstract void setShowErrorBox(boolean show);


    public abstract void createPromptBox(String title, String text);


    public abstract String getPromptBoxTitle();


    public abstract String getPromptBoxText();


    public abstract void createErrorBox(String title, String text);


    public abstract String getErrorBoxTitle();


    public abstract String getErrorBoxText();

    public abstract CellRangeAddressList getRegions();


    public static final class ErrorStyle {

        public static final int STOP = 0x00;

        public static final int WARNING = 0x01;

        public static final int INFO = 0x02;
    }

}
