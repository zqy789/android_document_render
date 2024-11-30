

package com.document.render.office.fc.dom4j;

import java.util.Map;


public interface ProcessingInstruction extends Node {

    String getTarget();


    void setTarget(String target);


    String getText();


    String getValue(String name);


    Map getValues();

    void setValues(Map data);

    void setValue(String name, String value);

    boolean removeValue(String name);
}


