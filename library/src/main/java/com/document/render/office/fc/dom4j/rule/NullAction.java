

package com.document.render.office.fc.dom4j.rule;

import com.document.render.office.fc.dom4j.Node;


public class NullAction implements Action {

    public static final NullAction SINGLETON = new NullAction();

    public void run(Node node) throws Exception {
    }
}


