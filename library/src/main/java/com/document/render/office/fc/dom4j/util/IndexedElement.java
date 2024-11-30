

package com.document.render.office.fc.dom4j.util;

import com.document.render.office.fc.dom4j.Attribute;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.Node;
import com.document.render.office.fc.dom4j.QName;
import com.document.render.office.fc.dom4j.tree.BackedList;
import com.document.render.office.fc.dom4j.tree.DefaultElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public class IndexedElement extends DefaultElement {
    
    private Map elementIndex;

    
    private Map attributeIndex;

    public IndexedElement(String name) {
        super(name);
    }

    public IndexedElement(QName qname) {
        super(qname);
    }

    public IndexedElement(QName qname, int attributeCount) {
        super(qname, attributeCount);
    }

    public Attribute attribute(String name) {
        return (Attribute) attributeIndex().get(name);
    }

    public Attribute attribute(QName qName) {
        return (Attribute) attributeIndex().get(qName);
    }

    public Element element(String name) {
        return asElement(elementIndex().get(name));
    }

    public Element element(QName qName) {
        return asElement(elementIndex().get(qName));
    }

    public List elements(String name) {
        return asElementList(elementIndex().get(name));
    }

    public List elements(QName qName) {
        return asElementList(elementIndex().get(qName));
    }

    
    
    protected Element asElement(Object object) {
        if (object instanceof Element) {
            return (Element) object;
        } else if (object != null) {
            List list = (List) object;

            if (list.size() >= 1) {
                return (Element) list.get(0);
            }
        }

        return null;
    }

    protected List asElementList(Object object) {
        if (object instanceof Element) {
            return createSingleResultList(object);
        } else if (object != null) {
            List list = (List) object;
            BackedList answer = createResultList();

            for (int i = 0, size = list.size(); i < size; i++) {
                answer.addLocal(list.get(i));
            }

            return answer;
        }

        return createEmptyList();
    }

    
    protected Iterator asElementIterator(Object object) {
        return asElementList(object).iterator();
    }

    
    protected void addNode(Node node) {
        super.addNode(node);

        if ((elementIndex != null) && node instanceof Element) {
            addToElementIndex((Element) node);
        } else if ((attributeIndex != null) && node instanceof Attribute) {
            addToAttributeIndex((Attribute) node);
        }
    }

    protected boolean removeNode(Node node) {
        if (super.removeNode(node)) {
            if ((elementIndex != null) && node instanceof Element) {
                removeFromElementIndex((Element) node);
            } else if ((attributeIndex != null) && node instanceof Attribute) {
                removeFromAttributeIndex((Attribute) node);
            }

            return true;
        }

        return false;
    }

    protected Map attributeIndex() {
        if (attributeIndex == null) {
            attributeIndex = createAttributeIndex();

            for (Iterator iter = attributeIterator(); iter.hasNext(); ) {
                addToAttributeIndex((Attribute) iter.next());
            }
        }

        return attributeIndex;
    }

    protected Map elementIndex() {
        if (elementIndex == null) {
            elementIndex = createElementIndex();

            for (Iterator iter = elementIterator(); iter.hasNext(); ) {
                addToElementIndex((Element) iter.next());
            }
        }

        return elementIndex;
    }

    
    protected Map createAttributeIndex() {
        Map answer = createIndex();

        return answer;
    }

    
    protected Map createElementIndex() {
        Map answer = createIndex();

        return answer;
    }

    protected void addToElementIndex(Element element) {
        QName qName = element.getQName();
        String name = qName.getName();
        addToElementIndex(qName, element);
        addToElementIndex(name, element);
    }

    protected void addToElementIndex(Object key, Element value) {
        Object oldValue = elementIndex.get(key);

        if (oldValue == null) {
            elementIndex.put(key, value);
        } else {
            if (oldValue instanceof List) {
                List list = (List) oldValue;
                list.add(value);
            } else {
                List list = createList();
                list.add(oldValue);
                list.add(value);
                elementIndex.put(key, list);
            }
        }
    }

    protected void removeFromElementIndex(Element element) {
        QName qName = element.getQName();
        String name = qName.getName();
        removeFromElementIndex(qName, element);
        removeFromElementIndex(name, element);
    }

    protected void removeFromElementIndex(Object key, Element value) {
        Object oldValue = elementIndex.get(key);

        if (oldValue instanceof List) {
            List list = (List) oldValue;
            list.remove(value);
        } else {
            elementIndex.remove(key);
        }
    }

    protected void addToAttributeIndex(Attribute attribute) {
        QName qName = attribute.getQName();
        String name = qName.getName();
        addToAttributeIndex(qName, attribute);
        addToAttributeIndex(name, attribute);
    }

    protected void addToAttributeIndex(Object key, Attribute value) {
        Object oldValue = attributeIndex.get(key);

        if (oldValue != null) {
            attributeIndex.put(key, value);
        }
    }

    protected void removeFromAttributeIndex(Attribute attribute) {
        QName qName = attribute.getQName();
        String name = qName.getName();
        removeFromAttributeIndex(qName, attribute);
        removeFromAttributeIndex(name, attribute);
    }

    protected void removeFromAttributeIndex(Object key, Attribute value) {
        Object oldValue = attributeIndex.get(key);

        if ((oldValue != null) && oldValue.equals(value)) {
            attributeIndex.remove(key);
        }
    }

    
    protected Map createIndex() {
        return new HashMap();
    }

    
    protected List createList() {
        return new ArrayList();
    }
}


