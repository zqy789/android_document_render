

package com.document.render.office.fc.dom4j;


public interface ElementPath {

    int size();


    Element getElement(int depth);


    String getPath();


    Element getCurrent();


    void addHandler(String path, ElementHandler handler);


    void removeHandler(String path);
}


