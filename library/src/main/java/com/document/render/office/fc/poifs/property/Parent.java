


package com.document.render.office.fc.poifs.property;

import java.io.IOException;
import java.util.Iterator;



public interface Parent
        extends Child {



    public Iterator getChildren();



    public void addChild(final Property property)
            throws IOException;



    public void setPreviousChild(final Child child);



    public void setNextChild(final Child child);



}

