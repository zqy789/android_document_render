

package com.document.render.office.fc.dom4j.rule.pattern;

import com.document.render.office.fc.dom4j.Node;
import com.document.render.office.fc.dom4j.NodeFilter;
import com.document.render.office.fc.dom4j.rule.Pattern;



public class DefaultPattern implements Pattern {
    private NodeFilter filter;

    public DefaultPattern(NodeFilter filter) {
        this.filter = filter;
    }

    public boolean matches(Node node) {
        return filter.matches(node);
    }

    public double getPriority() {
        return Pattern.DEFAULT_PRIORITY;
    }

    public Pattern[] getUnionPatterns() {
        return null;
    }

    public short getMatchType() {
        return ANY_NODE;
    }

    public String getMatchesNodeName() {
        return null;
    }
}


