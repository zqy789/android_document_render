

package com.document.render.office.fc.dom4j.rule;

import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.Node;
import com.document.render.office.fc.dom4j.XPath;

import java.util.Iterator;
import java.util.List;



public class Stylesheet {
    private RuleManager ruleManager = new RuleManager();


    private String modeName;


    public Stylesheet() {
    }


    public void addRule(Rule rule) {
        ruleManager.addRule(rule);
    }


    public void removeRule(Rule rule) {
        ruleManager.removeRule(rule);
    }


    public void run(Object input) throws Exception {
        run(input, this.modeName);
    }

    public void run(Object input, String mode) throws Exception {
        if (input instanceof Node) {
            run((Node) input, mode);
        } else if (input instanceof List) {
            run((List) input, mode);
        }
    }

    public void run(List list) throws Exception {
        run(list, this.modeName);
    }

    public void run(List list, String mode) throws Exception {
        for (int i = 0, size = list.size(); i < size; i++) {
            Object object = list.get(i);

            if (object instanceof Node) {
                run((Node) object, mode);
            }
        }
    }

    public void run(Node node) throws Exception {
        run(node, this.modeName);
    }

    public void run(Node node, String mode) throws Exception {
        Mode mod = ruleManager.getMode(mode);
        mod.fireRule(node);
    }


    public void applyTemplates(Object input, XPath xpath) throws Exception {
        applyTemplates(input, xpath, this.modeName);
    }


    public void applyTemplates(Object input, XPath xpath, String mode) throws Exception {
        Mode mod = ruleManager.getMode(mode);

        List list = xpath.selectNodes(input);
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Node current = (Node) it.next();
            mod.fireRule(current);
        }
    }


    public void applyTemplates(Object input) throws Exception {
        applyTemplates(input, this.modeName);
    }


    public void applyTemplates(Object input, String mode) throws Exception {
        Mode mod = ruleManager.getMode(mode);

        if (input instanceof Element) {

            Element element = (Element) input;
            for (int i = 0, size = element.nodeCount(); i < size; i++) {
                Node node = element.node(i);
                mod.fireRule(node);
            }
        } else if (input instanceof Document) {

            Document document = (Document) input;
            for (int i = 0, size = document.nodeCount(); i < size; i++) {
                Node node = document.node(i);
                mod.fireRule(node);
            }
        } else if (input instanceof List) {
            List list = (List) input;

            for (int i = 0, size = list.size(); i < size; i++) {
                Object object = list.get(i);

                if (object instanceof Element) {
                    applyTemplates((Element) object, mode);
                } else if (object instanceof Document) {
                    applyTemplates((Document) object, mode);
                }
            }
        }
    }

    public void clear() {
        ruleManager.clear();
    }





    public String getModeName() {
        return modeName;
    }


    public void setModeName(String modeName) {
        this.modeName = modeName;
    }


    public Action getValueOfAction() {
        return ruleManager.getValueOfAction();
    }


    public void setValueOfAction(Action valueOfAction) {
        ruleManager.setValueOfAction(valueOfAction);
    }
}


