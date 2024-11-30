
package com.document.render.office.common.bulletnumber;

public class ListData {


    private int listID;

    private byte simpleList;

    private short[] linkStyle;

    private short linkStyleID = -1;

    private boolean isNumber;

    private ListLevel[] levels;

    private byte preParaLevel;

    private byte normalPreParaLevel;


    public int getListID() {
        return listID;
    }


    public void setListID(int listID) {
        this.listID = listID;
    }


    public byte getSimpleList() {
        return simpleList;
    }


    public void setSimpleList(byte simpleList) {
        this.simpleList = simpleList;
    }


    public short[] getLinkStyle() {
        return linkStyle;
    }

    /**
     * @param linkStyle The linkStyle to set.
     */
    public void setLinkStyle(short[] linkStyle) {
        this.linkStyle = linkStyle;
    }

    /**
     * @return Returns the isNumber.
     */
    public boolean isNumber() {
        return isNumber;
    }

    /**
     * @param isNumber The isNumber to set.
     */
    public void setNumber(boolean isNumber) {
        this.isNumber = isNumber;
    }

    /**
     * @return Returns the levels.
     */
    public ListLevel[] getLevels() {
        return levels;
    }

    /**
     * @param levels The levels to set.
     */
    public void setLevels(ListLevel[] levels) {
        this.levels = levels;
    }

    /**
     * @param level
     * @return
     */
    public ListLevel getLevel(int level) {
        if (level < levels.length) {
            return levels[level];
        }
        return null;
    }

    /**
     * @return Returns the preParaLevel.
     */
    public byte getPreParaLevel() {
        return preParaLevel;
    }

    /**
     * @param preParaLevel The preParaLevel to set.
     */
    public void setPreParaLevel(byte preParaLevel) {
        this.preParaLevel = preParaLevel;
    }

    /**
     * @return Returns the normalPreParaLevel.
     */
    public byte getNormalPreParaLevel() {
        return normalPreParaLevel;
    }

    /**
     * @param normalPreParaLevel The normalPreParaLevel to set.
     */
    public void setNormalPreParaLevel(byte normalPreParaLevel) {
        this.normalPreParaLevel = normalPreParaLevel;
    }

    /**
     *
     */
    public void resetForNormalView() {
        if (levels != null) {
            for (ListLevel level : levels) {
                level.setNormalParaCount(0);
            }
        }
    }

    /**
     * @return Returns the linkStyleID.
     */
    public short getLinkStyleID() {
        return linkStyleID;
    }

    /**
     * @param linkStyleID The linkStyleID to set.
     */
    public void setLinkStyleID(short linkStyleID) {
        this.linkStyleID = linkStyleID;
    }

    /**
     *
     */
    public void dispose() {
        if (levels != null) {
            for (ListLevel level : levels) {
                level.dispose();
            }
            levels = null;
        }
    }
}
