
package com.document.render.office.ss.model.baseModel;

import com.document.render.office.common.hyperlink.Hyperlink;
import com.document.render.office.simpletext.view.STRoot;
import com.document.render.office.ss.model.table.SSTable;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;


public class CellProperty {

    public static final short CELLPROPID_NUMERICTYPE = 0;

    public static final short CELLPROPID_MERGEDRANGADDRESS = 1;

    public static final short CELLPROPID_EXPANDRANGADDRESS = 2;

    public static final short CELLPROPID_HYPERLINK = 3;

    public static final short CELLPROPID_STROOT = 4;

    public static final short CELLPROPID_TABLEINFO = 5;

    private Map<Short, Object> props;

    public CellProperty() {
        props = new TreeMap<Short, Object>();
    }


    public void setCellProp(short id, Object value) {
        props.put(id, value);
    }


    public Object getCellProp(short id) {
        return props.get(id);
    }


    public short getCellNumericType() {
        Object obj = props.get(CELLPROPID_NUMERICTYPE);
        if (obj != null) {
            return (Short) obj;
        } else {
            return -1;
        }
    }


    public int getCellMergeRangeAddressIndex() {
        Object obj = props.get(CELLPROPID_MERGEDRANGADDRESS);
        if (obj != null) {
            return (Integer) obj;
        } else {
            return -1;
        }
    }


    public int getExpandCellRangeAddressIndex() {
        Object obj = props.get(CELLPROPID_EXPANDRANGADDRESS);
        if (obj != null) {
            return (Integer) obj;
        } else {
            return -1;
        }
    }


    public Hyperlink getCellHyperlink() {
        Object obj = props.get(CELLPROPID_HYPERLINK);
        if (obj != null) {
            return (Hyperlink) obj;
        } else {
            return null;
        }
    }


    public int getCellSTRoot() {
        Object obj = props.get(CELLPROPID_STROOT);
        if (obj != null) {
            return (Integer) obj;
        } else {
            return -1;
        }
    }

    public SSTable getTableInfo() {
        Object obj = props.get(CELLPROPID_TABLEINFO);
        if (obj != null) {
            return (SSTable) obj;
        } else {
            return null;
        }
    }


    public void removeCellSTRoot() {
        props.remove(CELLPROPID_STROOT);
    }

    public void dispose() {
        Collection<Object> objs = props.values();
        for (Object obj : objs) {
            if (obj instanceof Hyperlink) {
                ((Hyperlink) obj).dispose();
            } else if (obj instanceof STRoot) {
                ((STRoot) obj).dispose();
            }
        }
    }

}
