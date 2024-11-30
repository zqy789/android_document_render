
package com.document.render.office.simpletext.model;

import com.document.render.office.constant.wp.AttrIDConstant;



public class AttributeSetImpl implements IAttributeSet {

    public static final int CAPACITY = 5;

    private int size = 0;

    private int ID;

    private short[] arrayID;

    private int[] arrayValue;


    public AttributeSetImpl() {
        arrayID = new short[10];
        arrayValue = new int[10];
    }


    public int getID() {
        return this.ID;
    }


    public void setAttribute(short attrID, int value) {
        if (size >= arrayID.length) {
            ensureCapacity();
        }
        int index = getIDIndex(attrID);
        if (index >= 0) {
            arrayValue[index] = value;
        } else {
            arrayID[size] = attrID;
            arrayValue[size] = value;
            size++;
        }

    }


    public void removeAttribute(short attrID) {
        int index = getIDIndex(attrID);
        if (index >= 0) {
            for (int i = index + 1; i < size; i++) {
                arrayID[i - 1] = arrayID[i];
                arrayValue[i - 1] = arrayValue[i];
            }
            size--;
        }
    }


    public int getAttribute(short attrID) {
        return getAttribute(attrID, true);
    }


    private int getAttribute(short attrID, boolean pStyle) {
        int index = getIDIndex(attrID);
        if (index >= 0) {
            return arrayValue[index];
        }
        if (!pStyle) {
            return Integer.MIN_VALUE;
        }

        Style style = null;
        int value = Integer.MIN_VALUE;

        if (attrID < 0x0fff) {
            index = getIDIndex(AttrIDConstant.FONT_STYLE_ID);
            if (index >= 0) {
                style = StyleManage.instance().getStyle(arrayValue[index]);
                value = getAttributeForStyle(style, attrID);
            }
        }
        if (value != Integer.MIN_VALUE) {
            return value;
        }

        index = getIDIndex(AttrIDConstant.PARA_STYLE_ID);
        if (index >= 0) {
            style = StyleManage.instance().getStyle(arrayValue[index]);
            value = getAttributeForStyle(style, attrID);
        }

        return value;
    }


    private int getAttributeForStyle(Style style, short attrID) {

        AttributeSetImpl attr = (AttributeSetImpl) style.getAttrbuteSet();
        int value = attr.getAttribute(attrID, false);
        if (value != Integer.MIN_VALUE) {
            return value;
        }
        if (style.getBaseID() >= 0) {
            style = StyleManage.instance().getStyle(style.getBaseID());
            return getAttributeForStyle(style, attrID);
        }
        return Integer.MIN_VALUE;
    }


    public void mergeAttribute(IAttributeSet attr) {
        if (!(attr instanceof AttributeSetImpl)) {
            return;
        }
        AttributeSetImpl attrSet = (AttributeSetImpl) attr;
        int len = attrSet.arrayID.length;
        int index;
        for (int i = 0; i < len; i++) {
            index = getIDIndex(attrSet.arrayID[i]);
            if (index > 0) {
                arrayValue[index] = attrSet.arrayValue[i];
                continue;
            }
            if (size >= arrayID.length) {
                ensureCapacity();
            }
            arrayID[size] = attrSet.arrayID[i];
            arrayValue[size] = attrSet.arrayValue[i];
            size++;
        }
    }


    public IAttributeSet clone() {
        AttributeSetImpl attr = new AttributeSetImpl();
        attr.size = size;
        short[] aID = new short[size];
        System.arraycopy(arrayID, 0, aID, 0, size);
        attr.arrayID = aID;
        int[] aValue = new int[size];
        System.arraycopy(arrayValue, 0, aValue, 0, size);
        attr.arrayValue = aValue;
        return attr;
    }
    ;


    private int getIDIndex(int attrID) {
        for (int i = 0; i < size; i++) {
            if (arrayID[i] == attrID) {
                return i;
            }
        }
        return -1;
    }


    private void ensureCapacity() {
        int len = size + CAPACITY;
        short[] aID = new short[len];
        System.arraycopy(arrayID, 0, aID, 0, size);
        arrayID = aID;
        int[] aValue = new int[len];
        System.arraycopy(arrayValue, 0, aValue, 0, size);
        arrayValue = aValue;
    }


    public void dispose() {
        arrayID = null;
        arrayValue = null;
    }


}
