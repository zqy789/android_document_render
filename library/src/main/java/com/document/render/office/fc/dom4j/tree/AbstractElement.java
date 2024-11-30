

package com.document.render.office.fc.dom4j.tree;

import com.document.render.office.fc.dom4j.Attribute;
import com.document.render.office.fc.dom4j.CDATA;
import com.document.render.office.fc.dom4j.CharacterData;
import com.document.render.office.fc.dom4j.Comment;
import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.DocumentFactory;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.Entity;
import com.document.render.office.fc.dom4j.IllegalAddException;
import com.document.render.office.fc.dom4j.Namespace;
import com.document.render.office.fc.dom4j.Node;
import com.document.render.office.fc.dom4j.ProcessingInstruction;
import com.document.render.office.fc.dom4j.QName;
import com.document.render.office.fc.dom4j.Text;
import com.document.render.office.fc.dom4j.Visitor;
import com.document.render.office.fc.dom4j.io.OutputFormat;
import com.document.render.office.fc.dom4j.io.XMLWriter;

import org.xml.sax.Attributes;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public abstract class AbstractElement extends AbstractBranch implements Element {
    protected static final List EMPTY_LIST = Collections.EMPTY_LIST;
    protected static final Iterator EMPTY_ITERATOR = EMPTY_LIST.iterator();
    protected static final boolean VERBOSE_TOSTRING = false;
    protected static final boolean USE_STRINGVALUE_SEPARATOR = false;

    private static final DocumentFactory DOCUMENT_FACTORY = DocumentFactory.getInstance();

    public AbstractElement() {
    }

    public short getNodeType() {
        return ELEMENT_NODE;
    }

    public boolean isRootElement() {
        Document document = getDocument();

        if (document != null) {
            Element root = document.getRootElement();

            if (root == this) {
                return true;
            }
        }

        return false;
    }


    public String getXPathNameStep() {
        String uri = getNamespaceURI();

        if ((uri == null) || (uri.length() == 0)) {
            return getName();
        }

        String prefix = getNamespacePrefix();

        if ((prefix == null) || (prefix.length() == 0)) {
            return "*[name()='" + getName() + "']";
        }

        return getQualifiedName();
    }

    public String getPath(Element context) {
        if (this == context) {
            return ".";
        }

        Element parent = getParent();

        if (parent == null) {
            return "/" + getXPathNameStep();
        } else if (parent == context) {
            return getXPathNameStep();
        }

        return parent.getPath(context) + "/" + getXPathNameStep();
    }

    public String getUniquePath(Element context) {
        Element parent = getParent();

        if (parent == null) {
            return "/" + getXPathNameStep();
        }

        StringBuffer buffer = new StringBuffer();

        if (parent != context) {
            buffer.append(parent.getUniquePath(context));

            buffer.append("/");
        }

        buffer.append(getXPathNameStep());

        List mySiblings = parent.elements(getQName());

        if (mySiblings.size() > 1) {
            int idx = mySiblings.indexOf(this);

            if (idx >= 0) {
                buffer.append("[");

                buffer.append(Integer.toString(++idx));

                buffer.append("]");
            }
        }

        return buffer.toString();
    }

    public String asXML() {
        try {
            StringWriter out = new StringWriter();
            XMLWriter writer = new XMLWriter(out, new OutputFormat());

            writer.write(this);
            writer.flush();

            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException("IOException while generating " + "textual representation: "
                    + e.getMessage());
        }
    }

    public void write(Writer out) throws IOException {
        XMLWriter writer = new XMLWriter(out, new OutputFormat());
        writer.write(this);
    }


    public void accept(Visitor visitor) {
        visitor.visit(this);


        for (int i = 0, size = attributeCount(); i < size; i++) {
            Attribute attribute = attribute(i);

            visitor.visit(attribute);
        }


        for (int i = 0, size = nodeCount(); i < size; i++) {
            Node node = node(i);

            node.accept(visitor);
        }
    }

    public String toString() {
        String uri = getNamespaceURI();

        if ((uri != null) && (uri.length() > 0)) {
            if (VERBOSE_TOSTRING) {
                return super.toString() + " [Element: <" + getQualifiedName() + " uri: " + uri
                        + " attributes: " + attributeList() + " content: " + contentList() + " />]";
            } else {
                return super.toString() + " [Element: <" + getQualifiedName() + " uri: " + uri
                        + " attributes: " + attributeList() + "/>]";
            }
        } else {
            if (VERBOSE_TOSTRING) {
                return super.toString() + " [Element: <" + getQualifiedName() + " attributes: "
                        + attributeList() + " content: " + contentList() + " />]";
            } else {
                return super.toString() + " [Element: <" + getQualifiedName() + " attributes: "
                        + attributeList() + "/>]";
            }
        }
    }



    public Namespace getNamespace() {
        return getQName().getNamespace();
    }

    public void setNamespace(Namespace namespace) {
        setQName(getDocumentFactory().createQName(getName(), namespace));
    }

    public String getName() {
        return getQName().getName();
    }

    public void setName(String name) {
        setQName(getDocumentFactory().createQName(name));
    }

    public String getNamespacePrefix() {
        return getQName().getNamespacePrefix();
    }

    public String getNamespaceURI() {
        return getQName().getNamespaceURI();
    }

    public String getQualifiedName() {
        return getQName().getQualifiedName();
    }

    public Object getData() {
        return getText();
    }

    public void setData(Object data) {

    }



    public Node node(int index) {
        if (index >= 0) {
            List list = contentList();

            if (index >= list.size()) {
                return null;
            }

            Object node = list.get(index);

            if (node != null) {
                if (node instanceof Node) {
                    return (Node) node;
                } else {
                    return getDocumentFactory().createText(node.toString());
                }
            }
        }

        return null;
    }

    public int indexOf(Node node) {
        return contentList().indexOf(node);
    }

    public int nodeCount() {
        return contentList().size();
    }

    public Iterator nodeIterator() {
        return contentList().iterator();
    }



    public Element element(String name) {
        List list = contentList();

        int size = list.size();

        for (int i = 0; i < size; i++) {
            Object object = list.get(i);

            if (object instanceof Element) {
                Element element = (Element) object;

                if (name.equals(element.getName())) {
                    return element;
                }
            }
        }

        return null;
    }

    public Element element(QName qName) {
        List list = contentList();

        int size = list.size();

        for (int i = 0; i < size; i++) {
            Object object = list.get(i);

            if (object instanceof Element) {
                Element element = (Element) object;

                if (qName.equals(element.getQName())) {
                    return element;
                }
            }
        }

        return null;
    }

    public Element element(String name, Namespace namespace) {
        return element(getDocumentFactory().createQName(name, namespace));
    }

    public List elements() {
        List list = contentList();

        BackedList answer = createResultList();

        int size = list.size();

        for (int i = 0; i < size; i++) {
            Object object = list.get(i);

            if (object instanceof Element) {
                answer.addLocal(object);
            }
        }

        return answer;
    }

    public List elements(String name) {
        List list = contentList();

        BackedList answer = createResultList();

        int size = list.size();

        for (int i = 0; i < size; i++) {
            Object object = list.get(i);

            if (object instanceof Element) {
                Element element = (Element) object;

                if (name.equals(element.getName())) {
                    answer.addLocal(element);
                }
            }
        }

        return answer;
    }

    public List elements(QName qName) {
        List list = contentList();

        BackedList answer = createResultList();

        int size = list.size();

        for (int i = 0; i < size; i++) {
            Object object = list.get(i);

            if (object instanceof Element) {
                Element element = (Element) object;

                if (qName.equals(element.getQName())) {
                    answer.addLocal(element);
                }
            }
        }

        return answer;
    }

    public List elements(String name, Namespace namespace) {
        return elements(getDocumentFactory().createQName(name, namespace));
    }

    public Iterator elementIterator() {
        List list = elements();

        return list.iterator();
    }

    public Iterator elementIterator(String name) {
        List list = elements(name);

        return list.iterator();
    }

    public Iterator elementIterator(QName qName) {
        List list = elements(qName);

        return list.iterator();
    }

    public Iterator elementIterator(String name, Namespace ns) {
        return elementIterator(getDocumentFactory().createQName(name, ns));
    }



    public List attributes() {
        return new ContentListFacade(this, attributeList());
    }

    public Iterator attributeIterator() {
        return attributeList().iterator();
    }

    public Attribute attribute(int index) {
        return (Attribute) attributeList().get(index);
    }

    public int attributeCount() {
        return attributeList().size();
    }

    public Attribute attribute(String name) {
        List list = attributeList();

        int size = list.size();

        for (int i = 0; i < size; i++) {
            Attribute attribute = (Attribute) list.get(i);

            if (name.equals(attribute.getName())) {
                return attribute;
            }
        }

        return null;
    }

    public Attribute attribute(QName qName) {
        List list = attributeList();

        int size = list.size();

        for (int i = 0; i < size; i++) {
            Attribute attribute = (Attribute) list.get(i);

            if (qName.equals(attribute.getQName())) {
                return attribute;
            }
        }

        return null;
    }

    public Attribute attribute(String name, Namespace namespace) {
        return attribute(getDocumentFactory().createQName(name, namespace));
    }


    public void setAttributes(Attributes attributes, NamespaceStack namespaceStack,
                              boolean noNamespaceAttributes) {

        int size = attributes.getLength();

        if (size > 0) {
            DocumentFactory factory = getDocumentFactory();

            if (size == 1) {

                String name = attributes.getQName(0);

                if (noNamespaceAttributes || !name.startsWith("xmlns")) {
                    String attributeURI = attributes.getURI(0);

                    String attributeLocalName = attributes.getLocalName(0);

                    String attributeValue = attributes.getValue(0);

                    QName attributeQName = namespaceStack.getAttributeQName(attributeURI,
                            attributeLocalName, name);

                    add(factory.createAttribute(this, attributeQName, attributeValue));
                }
            } else {
                List list = attributeList(size);

                list.clear();

                for (int i = 0; i < size; i++) {


                    String attributeName = attributes.getQName(i);

                    if (noNamespaceAttributes || !attributeName.startsWith("xmlns")) {
                        String attributeURI = attributes.getURI(i);

                        String attributeLocalName = attributes.getLocalName(i);

                        String attributeValue = attributes.getValue(i);

                        QName attributeQName = namespaceStack.getAttributeQName(attributeURI,
                                attributeLocalName, attributeName);

                        Attribute attribute = factory.createAttribute(this, attributeQName,
                                attributeValue);

                        list.add(attribute);

                        childAdded(attribute);
                    }
                }
            }
        }
    }

    public String attributeValue(String name) {
        Attribute attrib = attribute(name);

        if (attrib == null) {
            return null;
        } else {
            return attrib.getValue();
        }
    }

    public String attributeValue(QName qName) {
        Attribute attrib = attribute(qName);

        if (attrib == null) {
            return null;
        } else {
            return attrib.getValue();
        }
    }

    public String attributeValue(String name, String defaultValue) {
        String answer = attributeValue(name);

        return (answer != null) ? answer : defaultValue;
    }

    public String attributeValue(QName qName, String defaultValue) {
        String answer = attributeValue(qName);

        return (answer != null) ? answer : defaultValue;
    }


    public void setAttributeValue(String name, String value) {
        addAttribute(name, value);
    }


    public void setAttributeValue(QName qName, String value) {
        addAttribute(qName, value);
    }

    public void add(Attribute attribute) {
        if (attribute.getParent() != null) {
            String message = "The Attribute already has an existing parent \""
                    + attribute.getParent().getQualifiedName() + "\"";

            throw new IllegalAddException(this, attribute, message);
        }

        if (attribute.getValue() == null) {



            Attribute oldAttribute = attribute(attribute.getQName());

            if (oldAttribute != null) {
                remove(oldAttribute);
            }
        } else {
            attributeList().add(attribute);

            childAdded(attribute);
        }
    }

    public boolean remove(Attribute attribute) {
        List list = attributeList();

        boolean answer = list.remove(attribute);

        if (answer) {
            childRemoved(attribute);
        } else {

            Attribute copy = attribute(attribute.getQName());

            if (copy != null) {
                list.remove(copy);

                answer = true;
            }
        }

        return answer;
    }



    public List processingInstructions() {
        List list = contentList();

        BackedList answer = createResultList();

        int size = list.size();

        for (int i = 0; i < size; i++) {
            Object object = list.get(i);

            if (object instanceof ProcessingInstruction) {
                answer.addLocal(object);
            }
        }

        return answer;
    }

    public List processingInstructions(String target) {
        List list = contentList();

        BackedList answer = createResultList();

        int size = list.size();

        for (int i = 0; i < size; i++) {
            Object object = list.get(i);

            if (object instanceof ProcessingInstruction) {
                ProcessingInstruction pi = (ProcessingInstruction) object;

                if (target.equals(pi.getName())) {
                    answer.addLocal(pi);
                }
            }
        }

        return answer;
    }

    public ProcessingInstruction processingInstruction(String target) {
        List list = contentList();

        int size = list.size();

        for (int i = 0; i < size; i++) {
            Object object = list.get(i);

            if (object instanceof ProcessingInstruction) {
                ProcessingInstruction pi = (ProcessingInstruction) object;

                if (target.equals(pi.getName())) {
                    return pi;
                }
            }
        }

        return null;
    }

    public boolean removeProcessingInstruction(String target) {
        List list = contentList();

        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            Object object = iter.next();

            if (object instanceof ProcessingInstruction) {
                ProcessingInstruction pi = (ProcessingInstruction) object;

                if (target.equals(pi.getName())) {
                    iter.remove();

                    return true;
                }
            }
        }

        return false;
    }



    public Node getXPathResult(int index) {
        Node answer = node(index);

        if ((answer != null) && !answer.supportsParent()) {
            return answer.asXPathResult(this);
        }

        return answer;
    }

    public Element addAttribute(String name, String value) {

        Attribute attribute = attribute(name);

        if (value != null) {
            if (attribute == null) {
                add(getDocumentFactory().createAttribute(this, name, value));
            } else if (attribute.isReadOnly()) {
                remove(attribute);

                add(getDocumentFactory().createAttribute(this, name, value));
            } else {
                attribute.setValue(value);
            }
        } else if (attribute != null) {
            remove(attribute);
        }

        return this;
    }

    public Element addAttribute(QName qName, String value) {

        Attribute attribute = attribute(qName);

        if (value != null) {
            if (attribute == null) {
                add(getDocumentFactory().createAttribute(this, qName, value));
            } else if (attribute.isReadOnly()) {
                remove(attribute);

                add(getDocumentFactory().createAttribute(this, qName, value));
            } else {
                attribute.setValue(value);
            }
        } else if (attribute != null) {
            remove(attribute);
        }

        return this;
    }

    public Element addCDATA(String cdata) {
        CDATA node = getDocumentFactory().createCDATA(cdata);

        addNewNode(node);

        return this;
    }

    public Element addComment(String comment) {
        Comment node = getDocumentFactory().createComment(comment);

        addNewNode(node);

        return this;
    }

    public Element addElement(String name) {
        DocumentFactory factory = getDocumentFactory();

        int index = name.indexOf(":");

        String prefix = "";

        String localName = name;

        Namespace namespace = null;

        if (index > 0) {
            prefix = name.substring(0, index);

            localName = name.substring(index + 1);

            namespace = getNamespaceForPrefix(prefix);

            if (namespace == null) {
                throw new IllegalAddException("No such namespace prefix: " + prefix
                        + " is in scope on: " + this + " so cannot add element: " + name);
            }
        } else {
            namespace = getNamespaceForPrefix("");
        }

        Element node;

        if (namespace != null) {
            QName qname = factory.createQName(localName, namespace);

            node = factory.createElement(qname);
        } else {
            node = factory.createElement(name);
        }

        addNewNode(node);

        return node;
    }

    public Element addEntity(String name, String text) {
        Entity node = getDocumentFactory().createEntity(name, text);

        addNewNode(node);

        return this;
    }

    public Element addNamespace(String prefix, String uri) {
        Namespace node = getDocumentFactory().createNamespace(prefix, uri);
        addNewNode(node);
        return this;
    }

    public Element addProcessingInstruction(String target, String data) {
        ProcessingInstruction node = getDocumentFactory().createProcessingInstruction(target, data);
        addNewNode(node);
        return this;
    }

    public Element addProcessingInstruction(String target, Map data) {
        ProcessingInstruction node = getDocumentFactory().createProcessingInstruction(target, data);

        addNewNode(node);

        return this;
    }

    public Element addText(String text) {
        Text node = getDocumentFactory().createText(text);

        addNewNode(node);

        return this;
    }


    public void add(Node node) {
        switch (node.getNodeType()) {
            case ELEMENT_NODE:
                add((Element) node);

                break;

            case ATTRIBUTE_NODE:
                add((Attribute) node);

                break;

            case TEXT_NODE:
                add((Text) node);

                break;

            case CDATA_SECTION_NODE:
                add((CDATA) node);

                break;

            case ENTITY_REFERENCE_NODE:
                add((Entity) node);

                break;

            case PROCESSING_INSTRUCTION_NODE:
                add((ProcessingInstruction) node);

                break;

            case COMMENT_NODE:
                add((Comment) node);

                break;


            case NAMESPACE_NODE:
                add((Namespace) node);

                break;

            default:
                invalidNodeTypeAddException(node);
        }
    }

    public boolean remove(Node node) {
        switch (node.getNodeType()) {
            case ELEMENT_NODE:
                return remove((Element) node);

            case ATTRIBUTE_NODE:
                return remove((Attribute) node);

            case TEXT_NODE:
                return remove((Text) node);

            case CDATA_SECTION_NODE:
                return remove((CDATA) node);

            case ENTITY_REFERENCE_NODE:
                return remove((Entity) node);

            case PROCESSING_INSTRUCTION_NODE:
                return remove((ProcessingInstruction) node);

            case COMMENT_NODE:
                return remove((Comment) node);


            case NAMESPACE_NODE:
                return remove((Namespace) node);

            default:
                return false;
        }
    }


    public void add(CDATA cdata) {
        addNode(cdata);
    }

    public void add(Comment comment) {
        addNode(comment);
    }

    public void add(Element element) {
        addNode(element);
    }

    public void add(Entity entity) {
        addNode(entity);
    }

    public void add(Namespace namespace) {
        addNode(namespace);
    }

    public void add(ProcessingInstruction pi) {
        addNode(pi);
    }

    public void add(Text text) {
        addNode(text);
    }

    public boolean remove(CDATA cdata) {
        return removeNode(cdata);
    }

    public boolean remove(Comment comment) {
        return removeNode(comment);
    }

    public boolean remove(Element element) {
        return removeNode(element);
    }

    public boolean remove(Entity entity) {
        return removeNode(entity);
    }

    public boolean remove(Namespace namespace) {
        return removeNode(namespace);
    }

    public boolean remove(ProcessingInstruction pi) {
        return removeNode(pi);
    }

    public boolean remove(Text text) {
        return removeNode(text);
    }



    public boolean hasMixedContent() {
        List content = contentList();

        if ((content == null) || content.isEmpty() || (content.size() < 2)) {
            return false;
        }

        Class prevClass = null;

        for (Iterator iter = content.iterator(); iter.hasNext(); ) {
            Object object = iter.next();

            Class newClass = object.getClass();

            if (newClass != prevClass) {
                if (prevClass != null) {
                    return true;
                }

                prevClass = newClass;
            }
        }

        return false;
    }

    public boolean isTextOnly() {
        List content = contentList();

        if ((content == null) || content.isEmpty()) {
            return true;
        }

        for (Iterator iter = content.iterator(); iter.hasNext(); ) {
            Object object = iter.next();

            if (!(object instanceof CharacterData) && !(object instanceof String)) {
                return false;
            }
        }

        return true;
    }

    public void setText(String text) {

        List allContent = contentList();

        if (allContent != null) {
            Iterator it = allContent.iterator();

            while (it.hasNext()) {
                Node node = (Node) it.next();

                switch (node.getNodeType()) {
                    case CDATA_SECTION_NODE:


                    case ENTITY_REFERENCE_NODE:
                    case TEXT_NODE:
                        it.remove();

                    default:
                        break;
                }
            }
        }

        addText(text);
    }

    public String getStringValue() {
        List list = contentList();

        int size = list.size();

        if (size > 0) {
            if (size == 1) {

                return getContentAsStringValue(list.get(0));
            } else {
                StringBuffer buffer = new StringBuffer();

                for (int i = 0; i < size; i++) {
                    Object node = list.get(i);

                    String string = getContentAsStringValue(node);

                    if (string.length() > 0) {
                        if (USE_STRINGVALUE_SEPARATOR) {
                            if (buffer.length() > 0) {
                                buffer.append(' ');
                            }
                        }

                        buffer.append(string);
                    }
                }

                return buffer.toString();
            }
        }

        return "";
    }


    public void normalize() {
        List content = contentList();

        Text previousText = null;

        int i = 0;

        while (i < content.size()) {
            Node node = (Node) content.get(i);

            if (node instanceof Text) {
                Text text = (Text) node;

                if (previousText != null) {
                    previousText.appendText(text.getText());

                    remove(text);
                } else {
                    String value = text.getText();



                    if ((value == null) || (value.length() <= 0)) {
                        remove(text);
                    } else {
                        previousText = text;

                        i++;
                    }
                }
            } else {
                if (node instanceof Element) {
                    Element element = (Element) node;

                    element.normalize();
                }

                previousText = null;

                i++;
            }
        }
    }

    public String elementText(String name) {
        Element element = element(name);

        return (element != null) ? element.getText() : null;
    }

    public String elementText(QName qName) {
        Element element = element(qName);

        return (element != null) ? element.getText() : null;
    }

    public String elementTextTrim(String name) {
        Element element = element(name);

        return (element != null) ? element.getTextTrim() : null;
    }

    public String elementTextTrim(QName qName) {
        Element element = element(qName);

        return (element != null) ? element.getTextTrim() : null;
    }



    public void appendAttributes(Element element) {
        for (int i = 0, size = element.attributeCount(); i < size; i++) {
            Attribute attribute = element.attribute(i);

            if (attribute.supportsParent()) {
                addAttribute(attribute.getQName(), attribute.getValue());
            } else {
                add(attribute);
            }
        }
    }




    public Element createCopy() {
        Element clone = createElement(getQName());

        clone.appendAttributes(this);

        clone.appendContent(this);

        return clone;
    }

    public Element createCopy(String name) {
        Element clone = createElement(name);

        clone.appendAttributes(this);

        clone.appendContent(this);

        return clone;
    }

    public Element createCopy(QName qName) {
        Element clone = createElement(qName);

        clone.appendAttributes(this);

        clone.appendContent(this);

        return clone;
    }

    public QName getQName(String qualifiedName) {
        String prefix = "";

        String localName = qualifiedName;

        int index = qualifiedName.indexOf(":");

        if (index > 0) {
            prefix = qualifiedName.substring(0, index);

            localName = qualifiedName.substring(index + 1);
        }

        Namespace namespace = getNamespaceForPrefix(prefix);

        if (namespace != null) {
            return getDocumentFactory().createQName(localName, namespace);
        } else {
            return getDocumentFactory().createQName(localName);
        }
    }

    public Namespace getNamespaceForPrefix(String prefix) {
        if (prefix == null) {
            prefix = "";
        }

        if (prefix.equals(getNamespacePrefix())) {
            return getNamespace();
        } else if (prefix.equals("xml")) {
            return Namespace.XML_NAMESPACE;
        } else {
            List list = contentList();

            int size = list.size();

            for (int i = 0; i < size; i++) {
                Object object = list.get(i);

                if (object instanceof Namespace) {
                    Namespace namespace = (Namespace) object;

                    if (prefix.equals(namespace.getPrefix())) {
                        return namespace;
                    }
                }
            }
        }

        Element parent = getParent();

        if (parent != null) {
            Namespace answer = parent.getNamespaceForPrefix(prefix);

            if (answer != null) {
                return answer;
            }
        }

        if ((prefix == null) || (prefix.length() <= 0)) {
            return Namespace.NO_NAMESPACE;
        }

        return null;
    }

    public Namespace getNamespaceForURI(String uri) {
        if ((uri == null) || (uri.length() <= 0)) {
            return Namespace.NO_NAMESPACE;
        } else if (uri.equals(getNamespaceURI())) {
            return getNamespace();
        } else {
            List list = contentList();

            int size = list.size();

            for (int i = 0; i < size; i++) {
                Object object = list.get(i);

                if (object instanceof Namespace) {
                    Namespace namespace = (Namespace) object;

                    if (uri.equals(namespace.getURI())) {
                        return namespace;
                    }
                }
            }

            return null;
        }
    }

    public List getNamespacesForURI(String uri) {
        BackedList answer = createResultList();






        List list = contentList();

        int size = list.size();

        for (int i = 0; i < size; i++) {
            Object object = list.get(i);

            if ((object instanceof Namespace) && ((Namespace) object).getURI().equals(uri)) {
                answer.addLocal(object);
            }
        }

        return answer;
    }

    public List declaredNamespaces() {
        BackedList answer = createResultList();







        List list = contentList();

        int size = list.size();

        for (int i = 0; i < size; i++) {
            Object object = list.get(i);

            if (object instanceof Namespace) {
                answer.addLocal(object);
            }
        }

        return answer;
    }

    public List additionalNamespaces() {
        List list = contentList();

        int size = list.size();

        BackedList answer = createResultList();

        for (int i = 0; i < size; i++) {
            Object object = list.get(i);

            if (object instanceof Namespace) {
                Namespace namespace = (Namespace) object;

                if (!namespace.equals(getNamespace())) {
                    answer.addLocal(namespace);
                }
            }
        }

        return answer;
    }

    public List additionalNamespaces(String defaultNamespaceURI) {
        List list = contentList();

        BackedList answer = createResultList();

        int size = list.size();

        for (int i = 0; i < size; i++) {
            Object object = list.get(i);

            if (object instanceof Namespace) {
                Namespace namespace = (Namespace) object;

                if (!defaultNamespaceURI.equals(namespace.getURI())) {
                    answer.addLocal(namespace);
                }
            }
        }

        return answer;
    }





    public void ensureAttributesCapacity(int minCapacity) {
        if (minCapacity > 1) {
            List list = attributeList();

            if (list instanceof ArrayList) {
                ArrayList arrayList = (ArrayList) list;

                arrayList.ensureCapacity(minCapacity);
            }
        }
    }



    protected Element createElement(String name) {
        return getDocumentFactory().createElement(name);
    }

    protected Element createElement(QName qName) {
        return getDocumentFactory().createElement(qName);
    }

    protected void addNode(Node node) {
        if (node.getParent() != null) {

            String message = "The Node already has an existing parent of \""
                    + node.getParent().getQualifiedName() + "\"";

            throw new IllegalAddException(this, node, message);
        }

        addNewNode(node);
    }

    protected void addNode(int index, Node node) {
        if (node.getParent() != null) {

            String message = "The Node already has an existing parent of \""
                    + node.getParent().getQualifiedName() + "\"";

            throw new IllegalAddException(this, node, message);
        }

        addNewNode(index, node);
    }


    protected void addNewNode(Node node) {
        contentList().add(node);

        childAdded(node);
    }

    protected void addNewNode(int index, Node node) {
        contentList().add(index, node);

        childAdded(node);
    }

    protected boolean removeNode(Node node) {
        boolean answer = contentList().remove(node);

        if (answer) {
            childRemoved(node);
        }

        return answer;
    }


    protected void childAdded(Node node) {
        if (node != null) {
            node.setParent(this);
        }
    }

    protected void childRemoved(Node node) {
        if (node != null) {
            node.setParent(null);

            node.setDocument(null);
        }
    }


    protected abstract List attributeList();


    protected abstract List attributeList(int attributeCount);

    protected DocumentFactory getDocumentFactory() {
        QName qName = getQName();


        if (qName != null) {
            DocumentFactory factory = qName.getDocumentFactory();

            if (factory != null) {
                return factory;
            }
        }

        return DOCUMENT_FACTORY;
    }


    protected List createAttributeList() {
        return createAttributeList(DEFAULT_CONTENT_LIST_SIZE);
    }


    protected List createAttributeList(int size) {
        return new ArrayList(size);
    }

    protected Iterator createSingleIterator(Object result) {
        return new SingleIterator(result);
    }
}


