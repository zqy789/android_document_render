
package com.document.render.office.common.bulletnumber;


public class ListLevel {


    private int startAt;

    private int numberFormat;

    private char[] numberText;

    private byte align;

    private byte followChar;

    private int textIndent;

    private int specialIndent;

    private int paraCount;

    private int normalParaCount;


    public int getStartAt() {
        return startAt;
    }


    public void setStartAt(int startAt) {
        this.startAt = startAt;
    }


    public int getNumberFormat() {
        return numberFormat;
    }

    /**
     * @param numberFormat The numberFormat to set.
     */
    public void setNumberFormat(int numberFormat) {
        this.numberFormat = numberFormat;
    }

    /**
     * @return Returns the numberText.
     */
    public char[] getNumberText() {
        return numberText;
    }

    /**
     * @param numberText The numberText to set.
     */
    public void setNumberText(char[] numberText) {
        this.numberText = numberText;
    }

    /**
     * @return Returns the align.
     */
    public byte getAlign() {
        return align;
    }

    /**
     * @param align The align to set.
     */
    public void setAlign(byte align) {
        this.align = align;
    }

    /**
     * @return Returns the followChar.
     */
    public byte getFollowChar() {
        return followChar;
    }

    /**
     * @param followChar The followChar to set.
     */
    public void setFollowChar(byte followChar) {
        this.followChar = followChar;
    }

    /**
     * @return Returns the textIndent.
     */
    public int getTextIndent() {
        return textIndent;
    }

    /**
     * @param textIndent The textIndent to set.
     */
    public void setTextIndent(int textIndent) {
        this.textIndent = textIndent;
    }

    /**
     * @return Returns the specialIndent.
     */
    public int getSpecialIndent() {
        return specialIndent;
    }

    /**
     * @param specialIndent The specialIndent to set.
     */
    public void setSpecialIndent(int specialIndent) {
        this.specialIndent = specialIndent;
    }

    /**
     * @return Returns the paraCount.
     */
    public int getParaCount() {
        return paraCount;
    }

    /**
     * @param paraCount The paraCount to set.
     */
    public void setParaCount(int paraCount) {
        this.paraCount = paraCount;
    }

    /**
     * @return Returns the normalParaCount.
     */
    public int getNormalParaCount() {
        return normalParaCount;
    }

    /**
     * @param normalParaCount The normalParaCount to set.
     */
    public void setNormalParaCount(int normalParaCount) {
        this.normalParaCount = normalParaCount;
    }

    /**
     *
     */
    public void dispose() {
        numberText = null;
    }

}
