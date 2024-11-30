

package com.document.render.office.fc.dom4j.rule;

import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.Node;
import com.document.render.office.fc.dom4j.rule.pattern.NodeTypePattern;

import java.util.HashMap;



public class RuleManager {

    private HashMap modes = new HashMap();


    private int appearenceCount;


    private Action valueOfAction;

    public RuleManager() {
    }


    public Mode getMode(String modeName) {
        Mode mode = (Mode) modes.get(modeName);

        if (mode == null) {
            mode = createMode();
            modes.put(modeName, mode);
        }

        return mode;
    }

    public void addRule(Rule rule) {
        rule.setAppearenceCount(++appearenceCount);

        Mode mode = getMode(rule.getMode());
        Rule[] childRules = rule.getUnionRules();

        if (childRules != null) {
            for (int i = 0, size = childRules.length; i < size; i++) {
                mode.addRule(childRules[i]);
            }
        } else {
            mode.addRule(rule);
        }
    }

    public void removeRule(Rule rule) {
        Mode mode = getMode(rule.getMode());
        Rule[] childRules = rule.getUnionRules();

        if (childRules != null) {
            for (int i = 0, size = childRules.length; i < size; i++) {
                mode.removeRule(childRules[i]);
            }
        } else {
            mode.removeRule(rule);
        }
    }


    public Rule getMatchingRule(String modeName, Node node) {
        Mode mode = (Mode) modes.get(modeName);

        if (mode != null) {
            return mode.getMatchingRule(node);
        } else {
            System.out.println("Warning: No Mode for mode: " + mode);

            return null;
        }
    }

    public void clear() {
        modes.clear();
        appearenceCount = 0;
    }





    public Action getValueOfAction() {
        return valueOfAction;
    }


    public void setValueOfAction(Action valueOfAction) {
        this.valueOfAction = valueOfAction;
    }





    protected Mode createMode() {
        Mode mode = new Mode();
        addDefaultRules(mode);

        return mode;
    }


    protected void addDefaultRules(final Mode mode) {

        Action applyTemplates = new Action() {
            public void run(Node node) throws Exception {
                if (node instanceof Element) {
                    mode.applyTemplates((Element) node);
                } else if (node instanceof Document) {
                    mode.applyTemplates((Document) node);
                }
            }
        };

        Action valueOf = getValueOfAction();

        addDefaultRule(mode, NodeTypePattern.ANY_DOCUMENT, applyTemplates);
        addDefaultRule(mode, NodeTypePattern.ANY_ELEMENT, applyTemplates);

        if (valueOf != null) {
            addDefaultRule(mode, NodeTypePattern.ANY_ATTRIBUTE, valueOf);
            addDefaultRule(mode, NodeTypePattern.ANY_TEXT, valueOf);
        }
    }

    protected void addDefaultRule(Mode mode, Pattern pattern, Action action) {
        Rule rule = createDefaultRule(pattern, action);
        mode.addRule(rule);
    }

    protected Rule createDefaultRule(Pattern pattern, Action action) {
        Rule rule = new Rule(pattern, action);
        rule.setImportPrecedence(-1);

        return rule;
    }
}


