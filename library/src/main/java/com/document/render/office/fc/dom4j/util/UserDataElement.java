

package com.document.render.office.fc.dom4j.util;

import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.QName;
import com.document.render.office.fc.dom4j.tree.DefaultElement;



public class UserDataElement extends DefaultElement {

    private Object data;

    public UserDataElement(String name) {
        super(name);
    }

    public UserDataElement(QName qname) {
        super(qname);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String toString() {
        return super.toString() + " userData: " + data;
    }

    public Object clone() {
        UserDataElement answer = (UserDataElement) super.clone();

        if (answer != this) {
            answer.data = getCopyOfUserData();
        }

        return answer;
    }





    protected Object getCopyOfUserData() {
        return data;
    }

    protected Element createElement(String name) {
        Element answer = getDocumentFactory().createElement(name);
        answer.setData(getCopyOfUserData());

        return answer;
    }

    protected Element createElement(QName qName) {
        Element answer = getDocumentFactory().createElement(qName);
        answer.setData(getCopyOfUserData());

        return answer;
    }




}


