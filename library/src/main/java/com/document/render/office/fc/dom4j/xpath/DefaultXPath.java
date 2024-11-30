

package com.document.render.office.fc.dom4j.xpath;

import com.document.render.office.fc.dom4j.InvalidXPathException;
import com.document.render.office.fc.dom4j.Node;
import com.document.render.office.fc.dom4j.NodeFilter;
import com.document.render.office.fc.dom4j.XPath;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;



public class DefaultXPath implements XPath, NodeFilter, Serializable {
    private String text;

    private XPath xpath;

    private NamespaceContext namespaceContext;


    public DefaultXPath(String text) throws InvalidXPathException {
        this.text = text;
        this.xpath = parse(text);
    }

    protected static XPath parse(String text) {

        return null;
    }



    public String toString() {
        return "[XPath: " + xpath + "]";
    }




    public String getText() {
        return text;
    }



    public NamespaceContext getNamespaceContext() {
        return namespaceContext;
    }





    public void setNamespaceContext(NamespaceContext namespaceContext) {
        this.namespaceContext = namespaceContext;
        xpath.setNamespaceContext(namespaceContext);
    }

    public Object evaluate(Object context) {

        return null;
    }

    public Object selectObject(Object context) {
        return evaluate(context);
    }

    public List selectNodes(Object context) {

        return Collections.EMPTY_LIST;
    }

    public List selectNodes(Object context, XPath sortXPath) {
        List answer = selectNodes(context);
        sortXPath.sort(answer);

        return answer;
    }

    public List selectNodes(Object context, XPath sortXPath, boolean distinct) {
        List answer = selectNodes(context);
        sortXPath.sort(answer, distinct);

        return answer;
    }

    public Node selectSingleNode(Object context) {

        return null;
    }

    public String valueOf(Object context) {

        return "";
    }

    public Number numberValueOf(Object context) {

        return null;
    }

    public boolean booleanValueOf(Object context) {

        return false;
    }


    public void sort(List list) {
        sort(list, false);
    }


    public void sort(List list, boolean distinct) {
        if ((list != null) && !list.isEmpty()) {
            int size = list.size();
            HashMap sortValues = new HashMap(size);

            for (int i = 0; i < size; i++) {
                Object object = list.get(i);

                if (object instanceof Node) {
                    Node node = (Node) object;
                    Object expression = getCompareValue(node);
                    sortValues.put(node, expression);
                }
            }

            sort(list, sortValues);

            if (distinct) {
                removeDuplicates(list, sortValues);
            }
        }
    }

    public boolean matches(Node node) {

        return false;
    }




    protected void sort(List list, final Map sortValues) {
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                o1 = sortValues.get(o1);
                o2 = sortValues.get(o2);

                if (o1 == o2) {
                    return 0;
                } else if (o1 instanceof Comparable) {
                    Comparable c1 = (Comparable) o1;

                    return c1.compareTo(o2);
                } else if (o1 == null) {
                    return 1;
                } else if (o2 == null) {
                    return -1;
                } else {
                    return o1.equals(o2) ? 0 : (-1);
                }
            }
        });
    }


    protected void removeDuplicates(List list, Map sortValues) {

        HashSet distinctValues = new HashSet();

        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            Object node = iter.next();
            Object value = sortValues.get(node);

            if (distinctValues.contains(value)) {
                iter.remove();
            } else {
                distinctValues.add(value);
            }
        }
    }


    protected Object getCompareValue(Node node) {
        return valueOf(node);
    }

    protected void setNSContext(Object context) {
        if (namespaceContext == null) {
            xpath.setNamespaceContext(DefaultNamespaceContext.create(context));
        }
    }




    public void setNamespaceContext1(NamespaceContext namespaceContext) {


    }

    @Override
    public void setNamespaceURIs(Map map) {


    }
}


