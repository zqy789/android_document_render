
package com.document.render.office.simpletext.view;


public class ParaAttr {

    public int leftIndent;

    public int rightIndent;



    public int specialIndentValue;

    public byte lineSpaceType;

    public float lineSpaceValue;

    public int beforeSpace;

    public int afterSpace;

    public byte verticalAlignment;

    public byte horizontalAlignment;

    public int listID;

    public byte listLevel;

    public int listTextIndent;

    public int listAlignIndent;

    public int pgBulletID;

    public int tabClearPosition;


    public void reset() {
        leftIndent = 0;
        rightIndent = 0;

        specialIndentValue = 0;
        lineSpaceType = -1;
        lineSpaceValue = 0;
        beforeSpace = 0;
        afterSpace = 0;
        verticalAlignment = 0;
        horizontalAlignment = 0;
        listID = -1;
        listLevel = -1;
        listTextIndent = 0;
        listAlignIndent = 0;
        pgBulletID = -1;
        tabClearPosition = 0;
    }


    public void dispose() {

    }

}
