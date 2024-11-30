

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.Branch;
import com.document.render.office.fc.dom4j.Comment;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.IllegalAddException;
import com.document.render.office.fc.dom4j.Namespace;
import com.document.render.office.fc.dom4j.Node;
import com.document.render.office.fc.dom4j.ProcessingInstruction;
import com.document.render.office.fc.dom4j.QName;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;



public abstract class AbstractBranch extends AbstractNode implements Branch {
    protected static final int DEFAULT_CONTENT_LIST_SIZE = 5;

    public AbstractBranch() {
    }

    public boolean isReadOnly() {
        return false;
    }

    public boolean hasContent() {
        return nodeCount() > 0;
    }

    public List content() {
        List backingList = contentList();

        return new ContentListFacade(this, backingList);
    }

    public String getText() {
        List content = contentList();

        if (content != null) {
            int size = content.size();

            if (size >= 1) {
                Object first = content.get(0);
                String firstText = getContentAsText(first);

                if (size == 1) {

                    return firstText;
                } else {
                    StringBuffer buffer = new StringBuffer(firstText);

                    for (int i = 1; i < size; i++) {
                        Object node = content.get(i);
                        buffer.append(getContentAsText(node));
                    }

                    return buffer.toString();
                }
            }
        }

        return "";
    }


    protected String getContentAsText(Object content) {
        if (content instanceof Node) {
            Node node = (Node) content;

            switch (node.getNodeType()) {
                case CDATA_SECTION_NODE:


                case ENTITY_REFERENCE_NODE:
                case TEXT_NODE:
                    return node.getText();

                default:
                    break;
            }
        } else if (content instanceof String) {
            return (String) content;
        }

        return "";
    }


    protected String getContentAsStringValue(Object content) {
        if (content instanceof Node) {
            Node node = (Node) content;

            switch (node.getNodeType()) {
                case CDATA_SECTION_NODE:


                case ENTITY_REFERENCE_NODE:
                case TEXT_NODE:
                case ELEMENT_NODE:
                    return node.getStringValue();

                default:
                    break;
            }
        } else if (content instanceof String) {
            return (String) content;
        }

        return "";
    }

    public String getTextTrim() {
        String text = getText();

        StringBuffer textContent = new StringBuffer();
        StringTokenizer tokenizer = new StringTokenizer(text);

        while (tokenizer.hasMoreTokens()) {
            String str = tokenizer.nextToken();
            textContent.append(str);

            if (tokenizer.hasMoreTokens()) {
                textContent.append(" ");
            }
        }

        return textContent.toString();
    }

    public void setProcessingInstructions(List listOfPIs) {
        for (Iterator iter = listOfPIs.iterator(); iter.hasNext(); ) {
            ProcessingInstruction pi = (ProcessingInstruction) iter.next();
            addNode(pi);
        }
    }

    public Element addElement(String name) {
        Element node = getDocumentFactory().createElement(name);
        add(node);

        return node;
    }

    public Element addElement(String qualifiedName, String namespaceURI) {
        Element node = getDocumentFactory().createElement(qualifiedName, namespaceURI);
        add(node);

        return node;
    }

    public Element addElement(QName qname) {
        Element node = getDocumentFactory().createElement(qname);
        add(node);

        return node;
    }

    public Element addElement(String name, String prefix, String uri) {
        Namespace namespace = Namespace.get(prefix, uri);
        QName qName = getDocumentFactory().createQName(name, namespace);

        return addElement(qName);
    }


    public void add(Node node) {
        switch (node.getNodeType()) {
            case ELEMENT_NODE:
                add((Element) node);

                break;

            case COMMENT_NODE:
                add((Comment) node);

                break;

            case PROCESSING_INSTRUCTION_NODE:
                add((ProcessingInstruction) node);

                break;

            default:
                invalidNodeTypeAddException(node);
        }
    }

    public boolean remove(Node node) {
        switch (node.getNodeType()) {
            case ELEMENT_NODE:
                return remove((Element) node);

            case COMMENT_NODE:
                return remove((Comment) node);

            case PROCESSING_INSTRUCTION_NODE:
                return remove((ProcessingInstruction) node);

            default:
                invalidNodeTypeAddException(node);

                return false;
        }
    }


    public void add(Comment comment) {
        addNode(comment);
    }

    public void add(Element element) {
        addNode(element);
    }

    public void add(ProcessingInstruction pi) {
        addNode(pi);
    }

    public boolean remove(Comment comment) {
        return removeNode(comment);
    }

    public boolean remove(Element element) {
        return removeNode(element);
    }

    public boolean remove(ProcessingInstruction pi) {
        return removeNode(pi);
    }

    public Element elementByID(String elementID) {
        for (int i = 0, size = nodeCount(); i < size; i++) {
            Node node = node(i);

            if (node instanceof Element) {
                Element element = (Element) node;
                String id = elementID(element);

                if ((id != null) && id.equals(elementID)) {
                    return element;
                } else {
                    element = element.elementByID(elementID);

                    if (element != null) {
                        return element;
                    }
                }
            }
        }

        return null;
    }

    public void appendContent(Branch branch) {
        for (int i = 0, size = branch.nodeCount(); i < size; i++) {
            Node node = branch.node(i);
            add((Node) node.clone());
        }
    }

    public Node node(int index) {
        Object object = contentList().get(index);

        if (object instanceof Node) {
            return (Node) object;
        }

        if (object instanceof String) {
            return getDocumentFactory().createText(object.toString());
        }

        return null;
    }

    public int nodeCount() {
        return contentList().size();
    }

    public int indexOf(Node node) {
        return contentList().indexOf(node);
    }

    public Iterator nodeIterator() {
        return contentList().iterator();
    }




    protected String elementID(Element element) {


        return element.attributeValue("ID");
    }


    protected abstract List contentList();


    protected List createContentList() {
        return new ArrayList(DEFAULT_CONTENT_LIST_SIZE);
    }


    protected List createContentList(int size) {
        return new ArrayList(size);
    }


    protected BackedList createResultList() {
        return new BackedList(this, contentList());
    }


    protected List createSingleResultList(Object result) {
        BackedList list = new BackedList(this, contentList(), 1);
        list.addLocal(result);

        return list;
    }


    protected List createEmptyList() {
        return new BackedList(this, contentList(), 0);
    }

    protected abstract void addNode(Node node);

    protected abstract void addNode(int index, Node node);

    protected abstract boolean removeNode(Node node);


    protected abstract void childAdded(Node node);


    protected abstract void childRemoved(Node node);


    protected void contentRemoved() {
        List content = contentList();

        for (int i = 0, size = content.size(); i < size; i++) {
            Object object = content.get(i);

            if (object instanceof Node) {
                childRemoved((Node) object);
            }
        }
    }


    protected void invalidNodeTypeAddException(Node node) {
        throw new IllegalAddException("Invalid node type. Cannot add node: " + node
                + " to this branch: " + this);
    }
}


