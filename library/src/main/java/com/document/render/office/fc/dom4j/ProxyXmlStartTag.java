

package com.document.render.office.fc.dom4j;

import org.xmlpull.v1.XmlPullParserException;

import java.util.Iterator;





public class ProxyXmlStartTag
{

    private Element element;


    private DocumentFactory factory = DocumentFactory.getInstance();

    public ProxyXmlStartTag() {
    }

    public ProxyXmlStartTag(Element element) {
        this.element = element;
    }



    public void resetStartTag() {
        this.element = null;
    }

    public int getAttributeCount() {
        return (element != null) ? element.attributeCount() : 0;
    }

    public String getAttributeNamespaceUri(int index) {
        if (element != null) {
            Attribute attribute = element.attribute(index);

            if (attribute != null) {
                return attribute.getNamespaceURI();
            }
        }

        return null;
    }

    public String getAttributeLocalName(int index) {
        if (element != null) {
            Attribute attribute = element.attribute(index);

            if (attribute != null) {
                return attribute.getName();
            }
        }

        return null;
    }

    public String getAttributePrefix(int index) {
        if (element != null) {
            Attribute attribute = element.attribute(index);

            if (attribute != null) {
                String prefix = attribute.getNamespacePrefix();

                if ((prefix != null) && (prefix.length() > 0)) {
                    return prefix;
                }
            }
        }

        return null;
    }

    public String getAttributeRawName(int index) {
        if (element != null) {
            Attribute attribute = element.attribute(index);

            if (attribute != null) {
                return attribute.getQualifiedName();
            }
        }

        return null;
    }

    public String getAttributeValue(int index) {
        if (element != null) {
            Attribute attribute = element.attribute(index);

            if (attribute != null) {
                return attribute.getValue();
            }
        }

        return null;
    }

    public String getAttributeValueFromRawName(String rawName) {
        if (element != null) {
            for (Iterator iter = element.attributeIterator(); iter.hasNext(); ) {
                Attribute attribute = (Attribute) iter.next();

                if (rawName.equals(attribute.getQualifiedName())) {
                    return attribute.getValue();
                }
            }
        }

        return null;
    }

    public String getAttributeValueFromName(String namespaceURI, String localName) {
        if (element != null) {
            for (Iterator iter = element.attributeIterator(); iter.hasNext(); ) {
                Attribute attribute = (Attribute) iter.next();

                if (namespaceURI.equals(attribute.getNamespaceURI())
                        && localName.equals(attribute.getName())) {
                    return attribute.getValue();
                }
            }
        }

        return null;
    }

    public boolean isAttributeNamespaceDeclaration(int index) {
        if (element != null) {
            Attribute attribute = element.attribute(index);

            if (attribute != null) {
                return "xmlns".equals(attribute.getNamespacePrefix());
            }
        }

        return false;
    }





    public String getLocalName() {
        return element.getName();
    }

    public String getNamespaceUri() {
        return element.getNamespaceURI();
    }

    public String getPrefix() {
        return element.getNamespacePrefix();
    }

    public String getRawName() {
        return element.getQualifiedName();
    }

    public void modifyTag(String namespaceURI, String lName, String rawName) {
        this.element = factory.createElement(rawName, namespaceURI);
    }

    public void resetTag() {
        this.element = null;
    }







    public DocumentFactory getDocumentFactory() {
        return factory;
    }

    public void setDocumentFactory(DocumentFactory documentFactory) {
        this.factory = documentFactory;
    }

    public Element getElement() {
        return element;
    }
}


