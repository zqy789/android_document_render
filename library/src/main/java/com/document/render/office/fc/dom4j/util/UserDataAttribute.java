

package com.document.render.office.fc.dom4j.util;

import com.document.render.office.fc.dom4j.QName;
import com.document.render.office.fc.dom4j.tree.DefaultAttribute;



public class UserDataAttribute extends DefaultAttribute {

    private Object data;

    public UserDataAttribute(QName qname) {
        super(qname);
    }

    public UserDataAttribute(QName qname, String text) {
        super(qname, text);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}


