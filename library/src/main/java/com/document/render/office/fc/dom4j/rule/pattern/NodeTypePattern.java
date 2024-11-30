

package com.document.render.office.fc.dom4j.rule.pattern;

import com.document.render.office.fc.dom4j.Node;
import com.document.render.office.fc.dom4j.rule.Pattern;



public class NodeTypePattern implements Pattern {

    public static final NodeTypePattern ANY_ATTRIBUTE = new NodeTypePattern(Node.ATTRIBUTE_NODE);


    public static final NodeTypePattern ANY_COMMENT = new NodeTypePattern(Node.COMMENT_NODE);


    public static final NodeTypePattern ANY_DOCUMENT = new NodeTypePattern(Node.DOCUMENT_NODE);


    public static final NodeTypePattern ANY_ELEMENT = new NodeTypePattern(Node.ELEMENT_NODE);


    public static final NodeTypePattern ANY_PROCESSING_INSTRUCTION = new NodeTypePattern(
            Node.PROCESSING_INSTRUCTION_NODE);


    public static final NodeTypePattern ANY_TEXT = new NodeTypePattern(Node.TEXT_NODE);

    private short nodeType;

    public NodeTypePattern(short nodeType) {
        this.nodeType = nodeType;
    }

    public boolean matches(Node node) {
        return node.getNodeType() == nodeType;
    }

    public double getPriority() {
        return Pattern.DEFAULT_PRIORITY;
    }

    public Pattern[] getUnionPatterns() {
        return null;
    }

    public short getMatchType() {
        return nodeType;
    }

    public String getMatchesNodeName() {
        return null;
    }
}


