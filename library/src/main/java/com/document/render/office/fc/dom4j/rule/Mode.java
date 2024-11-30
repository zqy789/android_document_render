

package com.document.render.office.fc.dom4j.rule;

import com.document.render.office.fc.dom4j.Attribute;
import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.Node;

import java.util.HashMap;
import java.util.Map;



public class Mode {
    private RuleSet[] ruleSets = new RuleSet[Pattern.NUMBER_OF_TYPES];


    private Map elementNameRuleSets;


    private Map attributeNameRuleSets;

    public Mode() {
    }


    public void fireRule(Node node) throws Exception {
        if (node != null) {
            Rule rule = getMatchingRule(node);

            if (rule != null) {
                Action action = rule.getAction();

                if (action != null) {
                    action.run(node);
                }
            }
        }
    }

    public void applyTemplates(Element element) throws Exception {
        for (int i = 0, size = element.attributeCount(); i < size; i++) {
            Attribute attribute = element.attribute(i);
            fireRule(attribute);
        }

        for (int i = 0, size = element.nodeCount(); i < size; i++) {
            Node node = element.node(i);
            fireRule(node);
        }
    }

    public void applyTemplates(Document document) throws Exception {
        for (int i = 0, size = document.nodeCount(); i < size; i++) {
            Node node = document.node(i);
            fireRule(node);
        }
    }

    public void addRule(Rule rule) {
        int matchType = rule.getMatchType();
        String name = rule.getMatchesNodeName();

        if (name != null) {
            if (matchType == Node.ELEMENT_NODE) {
                elementNameRuleSets = addToNameMap(elementNameRuleSets, name, rule);
            } else if (matchType == Node.ATTRIBUTE_NODE) {
                attributeNameRuleSets = addToNameMap(attributeNameRuleSets, name, rule);
            }
        }

        if (matchType >= Pattern.NUMBER_OF_TYPES) {
            matchType = Pattern.ANY_NODE;
        }

        if (matchType == Pattern.ANY_NODE) {

            for (int i = 1, size = ruleSets.length; i < size; i++) {
                RuleSet ruleSet = ruleSets[i];

                if (ruleSet != null) {
                    ruleSet.addRule(rule);
                }
            }
        }

        getRuleSet(matchType).addRule(rule);
    }

    public void removeRule(Rule rule) {
        int matchType = rule.getMatchType();
        String name = rule.getMatchesNodeName();

        if (name != null) {
            if (matchType == Node.ELEMENT_NODE) {
                removeFromNameMap(elementNameRuleSets, name, rule);
            } else if (matchType == Node.ATTRIBUTE_NODE) {
                removeFromNameMap(attributeNameRuleSets, name, rule);
            }
        }

        if (matchType >= Pattern.NUMBER_OF_TYPES) {
            matchType = Pattern.ANY_NODE;
        }

        getRuleSet(matchType).removeRule(rule);

        if (matchType != Pattern.ANY_NODE) {
            getRuleSet(Pattern.ANY_NODE).removeRule(rule);
        }
    }


    public Rule getMatchingRule(Node node) {
        int matchType = node.getNodeType();

        if (matchType == Node.ELEMENT_NODE) {
            if (elementNameRuleSets != null) {
                String name = node.getName();
                RuleSet ruleSet = (RuleSet) elementNameRuleSets.get(name);

                if (ruleSet != null) {
                    Rule answer = ruleSet.getMatchingRule(node);

                    if (answer != null) {
                        return answer;
                    }
                }
            }
        } else if (matchType == Node.ATTRIBUTE_NODE) {
            if (attributeNameRuleSets != null) {
                String name = node.getName();
                RuleSet ruleSet = (RuleSet) attributeNameRuleSets.get(name);

                if (ruleSet != null) {
                    Rule answer = ruleSet.getMatchingRule(node);

                    if (answer != null) {
                        return answer;
                    }
                }
            }
        }

        if ((matchType < 0) || (matchType >= ruleSets.length)) {
            matchType = Pattern.ANY_NODE;
        }

        Rule answer = null;
        RuleSet ruleSet = ruleSets[matchType];

        if (ruleSet != null) {

            answer = ruleSet.getMatchingRule(node);
        }

        if ((answer == null) && (matchType != Pattern.ANY_NODE)) {

            ruleSet = ruleSets[Pattern.ANY_NODE];

            if (ruleSet != null) {
                answer = ruleSet.getMatchingRule(node);
            }
        }

        return answer;
    }


    protected RuleSet getRuleSet(int matchType) {
        RuleSet ruleSet = ruleSets[matchType];

        if (ruleSet == null) {
            ruleSet = new RuleSet();
            ruleSets[matchType] = ruleSet;


            if (matchType != Pattern.ANY_NODE) {
                RuleSet allRules = ruleSets[Pattern.ANY_NODE];

                if (allRules != null) {
                    ruleSet.addAll(allRules);
                }
            }
        }

        return ruleSet;
    }


    protected Map addToNameMap(Map map, String name, Rule rule) {
        if (map == null) {
            map = new HashMap();
        }

        RuleSet ruleSet = (RuleSet) map.get(name);

        if (ruleSet == null) {
            ruleSet = new RuleSet();
            map.put(name, ruleSet);
        }

        ruleSet.addRule(rule);

        return map;
    }

    protected void removeFromNameMap(Map map, String name, Rule rule) {
        if (map != null) {
            RuleSet ruleSet = (RuleSet) map.get(name);

            if (ruleSet != null) {
                ruleSet.removeRule(rule);
            }
        }
    }
}


