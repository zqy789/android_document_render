

package com.document.render.office.fc.dom4j.util;


public interface SingletonStrategy {
    
    Object instance();

    
    void reset();

    
    void setSingletonClassName(String singletonClassName);
}


