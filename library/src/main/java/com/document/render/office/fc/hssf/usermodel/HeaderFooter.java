

package com.document.render.office.fc.hssf.usermodel;



public abstract class HeaderFooter implements com.document.render.office.fc.ss.usermodel.HeaderFooter {

    protected HeaderFooter() {

    }


    public static String fontSize(short size) {
        return "&" + size;
    }


    public static String font(String font, String style) {
        return "&\"" + font + "," + style + "\"";
    }


    public static String page() {
        return MarkupTag.PAGE_FIELD.getRepresentation();
    }


    public static String numPages() {
        return MarkupTag.NUM_PAGES_FIELD.getRepresentation();
    }


    public static String date() {
        return MarkupTag.DATE_FIELD.getRepresentation();
    }


    public static String time() {
        return MarkupTag.TIME_FIELD.getRepresentation();
    }


    public static String file() {
        return MarkupTag.FILE_FIELD.getRepresentation();
    }


    public static String tab() {
        return MarkupTag.SHEET_NAME_FIELD.getRepresentation();
    }


    public static String startBold() {
        return MarkupTag.BOLD_FIELD.getRepresentation();
    }


    public static String endBold() {
        return MarkupTag.BOLD_FIELD.getRepresentation();
    }


    public static String startUnderline() {
        return MarkupTag.UNDERLINE_FIELD.getRepresentation();
    }


    public static String endUnderline() {
        return MarkupTag.UNDERLINE_FIELD.getRepresentation();
    }


    public static String startDoubleUnderline() {
        return MarkupTag.DOUBLE_UNDERLINE_FIELD.getRepresentation();
    }


    public static String endDoubleUnderline() {
        return MarkupTag.DOUBLE_UNDERLINE_FIELD.getRepresentation();
    }


    public static String stripFields(String pText) {
        int pos;


        if (pText == null || pText.length() == 0) {
            return pText;
        }

        String text = pText;


        for (MarkupTag mt : MarkupTag.values()) {
            String seq = mt.getRepresentation();
            while ((pos = text.indexOf(seq)) > -1) {
                text = text.substring(0, pos) + text.substring(pos + seq.length());
            }
        }



        text = text.replaceAll("\\&\\d+", "");
        text = text.replaceAll("\\&\".*?,.*?\"", "");


        return text;
    }


    protected abstract String getRawText();

    private String[] splitParts() {
        String text = getRawText();

        String _left = "";
        String _center = "";
        String _right = "";

        outer:
        while (text.length() > 1) {
            if (text.charAt(0) != '&') {

                _center = text;
                break;
            }
            int pos = text.length();
            switch (text.charAt(1)) {
                case 'L':
                    if (text.indexOf("&C") >= 0) {
                        pos = Math.min(pos, text.indexOf("&C"));
                    }
                    if (text.indexOf("&R") >= 0) {
                        pos = Math.min(pos, text.indexOf("&R"));
                    }
                    _left = text.substring(2, pos);
                    text = text.substring(pos);
                    break;
                case 'C':
                    if (text.indexOf("&L") >= 0) {
                        pos = Math.min(pos, text.indexOf("&L"));
                    }
                    if (text.indexOf("&R") >= 0) {
                        pos = Math.min(pos, text.indexOf("&R"));
                    }
                    _center = text.substring(2, pos);
                    text = text.substring(pos);
                    break;
                case 'R':
                    if (text.indexOf("&C") >= 0) {
                        pos = Math.min(pos, text.indexOf("&C"));
                    }
                    if (text.indexOf("&L") >= 0) {
                        pos = Math.min(pos, text.indexOf("&L"));
                    }
                    _right = text.substring(2, pos);
                    text = text.substring(pos);
                    break;
                default:

                    _center = text;
                    break outer;
            }
        }
        return new String[]{_left, _center, _right,};
    }


    public final String getLeft() {
        return splitParts()[0];
    }


    public final void setLeft(String newLeft) {
        updatePart(0, newLeft);
    }


    public final String getCenter() {
        return splitParts()[1];
    }


    public final void setCenter(String newCenter) {
        updatePart(1, newCenter);
    }


    public final String getRight() {
        return splitParts()[2];
    }


    public final void setRight(String newRight) {
        updatePart(2, newRight);
    }

    private void updatePart(int partIndex, String newValue) {
        String[] parts = splitParts();
        parts[partIndex] = newValue == null ? "" : newValue;
        updateHeaderFooterText(parts);
    }


    private void updateHeaderFooterText(String[] parts) {
        String _left = parts[0];
        String _center = parts[1];
        String _right = parts[2];

        if (_center.length() < 1 && _left.length() < 1 && _right.length() < 1) {
            setHeaderFooterText("");
            return;
        }
        StringBuilder sb = new StringBuilder(64);
        sb.append("&C");
        sb.append(_center);
        sb.append("&L");
        sb.append(_left);
        sb.append("&R");
        sb.append(_right);
        String text = sb.toString();
        setHeaderFooterText(text);
    }


    protected abstract void setHeaderFooterText(String text);

    private enum MarkupTag {
        SHEET_NAME_FIELD("&A", false),
        DATE_FIELD("&D", false),
        FILE_FIELD("&F", false),
        FULL_FILE_FIELD("&Z", false),
        PAGE_FIELD("&P", false),
        TIME_FIELD("&T", false),
        NUM_PAGES_FIELD("&N", false),

        PICTURE_FIELD("&G", false),

        BOLD_FIELD("&B", true),
        ITALIC_FIELD("&I", true),
        STRIKETHROUGH_FIELD("&S", true),
        SUBSCRIPT_FIELD("&Y", true),
        SUPERSCRIPT_FIELD("&X", true),
        UNDERLINE_FIELD("&U", true),
        DOUBLE_UNDERLINE_FIELD("&E", true),
        ;

        private final String _representation;
        private final boolean _occursInPairs;

        private MarkupTag(String sequence, boolean occursInPairs) {
            _representation = sequence;
            _occursInPairs = occursInPairs;
        }


        public String getRepresentation() {
            return _representation;
        }


        public boolean occursPairs() {
            return _occursInPairs;
        }
    }
}
