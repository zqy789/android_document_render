
package com.document.render.office.pg.model;


public class PGPlaceholderUtil {
    
    public static final String TITLE = "title";
    
    public static final String CTRTITLE = "ctrTitle";
    
    public static final String SUBTITLE = "subTitle";
    
    public static final String BODY = "body";
    
    public static final String HALF = "half";
    
    public static final String DT = "dt";
    
    public static final String FTR = "ftr";
    
    public static final String SLDNUM = "sldNum";
    
    public static final String HEADER = "hdr";
    
    public static final String OBJECT = "obj";
    
    public static final String CHART = "chart";
    
    public static final String TABLE = "tbl";
    
    public static final String CLIPART = "clipArt";
    
    public static final String DIAGRAM = "dgm";
    
    public static final String MEDIA = "media";
    
    public static final String SLIDEIMAGE = "sldImg";
    
    public static final String PICTURE = "pic";

    private static PGPlaceholderUtil kit = new PGPlaceholderUtil();

    
    public static PGPlaceholderUtil instance() {
        return kit;
    }

    
    public boolean isTitle(String type) {
        if (TITLE.equals(type) || CTRTITLE.equals(type)) {
            return true;
        }
        return false;
    }

    
    public boolean isBody(String type) {
        if (TITLE.equals(type) || CTRTITLE.equals(type) || DT.equals(type)
                || FTR.equals(type) || SLDNUM.equals(type)) {
            return false;
        }
        return true;
    }

    
    public boolean isTitleOrBody(String type) {
        if (TITLE.equals(type) || CTRTITLE.equals(type) || SUBTITLE.equals(type)
                || BODY.equals(type)) {
            return true;
        }
        return false;
    }

    
    public boolean isHeaderFooter(String type) {
        if (DT.equals(type) || FTR.equals(type) || SLDNUM.equals(type)) {
            return true;
        }
        return false;
    }

    
    public boolean isDate(String type) {
        if (DT.equals(type)) {
            return true;
        }
        return false;
    }

    
    public boolean isFooter(String type) {
        if (FTR.equals(type)) {
            return true;
        }
        return false;
    }

    
    public boolean isSldNum(String type) {
        if (SLDNUM.equals(type)) {
            return true;
        }
        return false;
    }

    
    public String checkTypeName(String type) {
        if (PGPlaceholderUtil.CTRTITLE.equals(type)) {
            type = PGPlaceholderUtil.TITLE;
        }
        return type;
    }

    
    public String processType(String name, String type) {
        
        return type;
    }
}
