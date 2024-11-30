

package com.document.render.office.fc.dom4j.rule;

import com.document.render.office.fc.dom4j.Node;
import com.document.render.office.fc.dom4j.NodeFilter;


public interface Pattern extends NodeFilter {



    short ANY_NODE = 0;


    short NONE = 9999;


    short NUMBER_OF_TYPES = Node.UNKNOWN_NODE;


    double DEFAULT_PRIORITY = 0.5;


    boolean matches(Node node);


    double getPriority();


    Pattern[] getUnionPatterns();


    short getMatchType();


    String getMatchesNodeName();
}


