

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.Branch;
import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.Namespace;
import com.document.render.office.fc.dom4j.QName;

import java.util.List;



public class BaseElement extends AbstractElement {

    protected List content;

    protected List attributes;

    private QName qname;

    private Branch parentBranch;

    public BaseElement(String name) {
        this.qname = getDocumentFactory().createQName(name);
    }

    public BaseElement(QName qname) {
        this.qname = qname;
    }

    public BaseElement(String name, Namespace namespace) {
        this.qname = getDocumentFactory().createQName(name, namespace);
    }

    public Element getParent() {
        Element result = null;

        if (parentBranch instanceof Element) {
            result = (Element) parentBranch;
        }

        return result;
    }

    public void setParent(Element parent) {
        if (parentBranch instanceof Element || (parent != null)) {
            parentBranch = parent;
        }
    }

    public Document getDocument() {
        if (parentBranch instanceof Document) {
            return (Document) parentBranch;
        } else if (parentBranch instanceof Element) {
            Element parent = (Element) parentBranch;

            return parent.getDocument();
        }

        return null;
    }

    public void setDocument(Document document) {
        if (parentBranch instanceof Document || (document != null)) {
            parentBranch = document;
        }
    }

    public boolean supportsParent() {
        return true;
    }

    public QName getQName() {
        return qname;
    }

    public void setQName(QName name) {
        this.qname = name;
    }

    public void clearContent() {
        contentList().clear();
    }

    public void setContent(List content) {
        this.content = content;

        if (content instanceof ContentListFacade) {
            this.content = ((ContentListFacade) content).getBackingList();
        }
    }

    public void setAttributes(List attributes) {
        this.attributes = attributes;

        if (attributes instanceof ContentListFacade) {
            this.attributes = ((ContentListFacade) attributes).getBackingList();
        }
    }



    protected List contentList() {
        if (content == null) {
            content = createContentList();
        }

        return content;
    }

    protected List attributeList() {
        if (attributes == null) {
            attributes = createAttributeList();
        }

        return attributes;
    }

    protected List attributeList(int size) {
        if (attributes == null) {
            attributes = createAttributeList(size);
        }

        return attributes;
    }

    protected void setAttributeList(List attributeList) {
        this.attributes = attributeList;
    }
}


