

package com.document.render.office.fc.dom4j.rule;

import com.document.render.office.fc.dom4j.Node;


public class Rule implements Comparable {
    
    private String mode;

    
    private int importPrecedence;

    
    private double priority;

    
    private int appearenceCount;

    
    private Pattern pattern;

    
    private Action action;

    public Rule() {
        this.priority = Pattern.DEFAULT_PRIORITY;
    }

    public Rule(Pattern pattern) {
        this.pattern = pattern;
        this.priority = pattern.getPriority();
    }

    public Rule(Pattern pattern, Action action) {
        this(pattern);
        this.action = action;
    }

    
    public Rule(Rule that, Pattern pattern) {
        this.mode = that.mode;
        this.importPrecedence = that.importPrecedence;
        this.priority = that.priority;
        this.appearenceCount = that.appearenceCount;
        this.action = that.action;
        this.pattern = pattern;
    }

    public boolean equals(Object that) {
        if (that instanceof Rule) {
            return compareTo((Rule) that) == 0;
        }

        return false;
    }

    public int hashCode() {
        return importPrecedence + appearenceCount;
    }

    public int compareTo(Object that) {
        if (that instanceof Rule) {
            return compareTo((Rule) that);
        }

        return getClass().getName().compareTo(that.getClass().getName());
    }

    
    public int compareTo(Rule that) {
        int answer = this.importPrecedence - that.importPrecedence;

        if (answer == 0) {
            answer = (int) Math.round(this.priority - that.priority);

            if (answer == 0) {
                answer = this.appearenceCount - that.appearenceCount;
            }
        }

        return answer;
    }

    public String toString() {
        return super.toString() + "[ pattern: " + getPattern() + " action: " + getAction() + " ]";
    }

    
    public final boolean matches(Node node) {
        return pattern.matches(node);
    }

    
    public Rule[] getUnionRules() {
        Pattern[] patterns = pattern.getUnionPatterns();

        if (patterns == null) {
            return null;
        }

        int size = patterns.length;
        Rule[] answer = new Rule[size];

        for (int i = 0; i < size; i++) {
            answer[i] = new Rule(this, patterns[i]);
        }

        return answer;
    }

    
    public final short getMatchType() {
        return pattern.getMatchType();
    }

    
    public final String getMatchesNodeName() {
        return pattern.getMatchesNodeName();
    }

    
    public String getMode() {
        return mode;
    }

    
    public void setMode(String mode) {
        this.mode = mode;
    }

    
    public int getImportPrecedence() {
        return importPrecedence;
    }

    
    public void setImportPrecedence(int importPrecedence) {
        this.importPrecedence = importPrecedence;
    }

    
    public double getPriority() {
        return priority;
    }

    
    public void setPriority(double priority) {
        this.priority = priority;
    }

    
    public int getAppearenceCount() {
        return appearenceCount;
    }

    
    public void setAppearenceCount(int appearenceCount) {
        this.appearenceCount = appearenceCount;
    }

    
    public Pattern getPattern() {
        return pattern;
    }

    
    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    
    public Action getAction() {
        return action;
    }

    
    public void setAction(Action action) {
        this.action = action;
    }
}


