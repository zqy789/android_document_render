

package com.document.render.office.fc.dom4j.xpath;

import com.document.render.office.fc.dom4j.Node;
import com.document.render.office.fc.dom4j.rule.Pattern;



public class XPathPattern implements Pattern {
    private String text;

    private Pattern pattern;



    public XPathPattern(Pattern pattern) {

    }

    public XPathPattern(String text) {

    }

    public boolean matches(Node node) {

        return false;
    }

    public String getText() {
        return text;
    }

    public double getPriority() {
        return pattern.getPriority();
    }

    public Pattern[] getUnionPatterns() {
        Pattern[] patterns = pattern.getUnionPatterns();

        if (patterns != null) {
            int size = patterns.length;
            XPathPattern[] answer = new XPathPattern[size];

            for (int i = 0; i < size; i++) {
                answer[i] = new XPathPattern(patterns[i]);
            }

            return answer;
        }

        return null;
    }

    public short getMatchType() {
        return pattern.getMatchType();
    }

    public String getMatchesNodeName() {
        return pattern.getMatchesNodeName();
    }


}


