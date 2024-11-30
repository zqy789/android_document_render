

package com.document.render.office.fc.dom4j.util;

import com.document.render.office.fc.dom4j.Attribute;
import com.document.render.office.fc.dom4j.Branch;
import com.document.render.office.fc.dom4j.CDATA;
import com.document.render.office.fc.dom4j.CharacterData;
import com.document.render.office.fc.dom4j.Comment;
import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.DocumentType;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.Entity;
import com.document.render.office.fc.dom4j.Namespace;
import com.document.render.office.fc.dom4j.Node;
import com.document.render.office.fc.dom4j.ProcessingInstruction;
import com.document.render.office.fc.dom4j.QName;
import com.document.render.office.fc.dom4j.Text;

import java.util.Comparator;



public class NodeComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        if (o1 == o2) {
            return 0;
        } else if (o1 == null) {

            return -1;
        } else if (o2 == null) {
            return 1;
        }

        if (o1 instanceof Node) {
            if (o2 instanceof Node) {
                return compare((Node) o1, (Node) o2);
            } else {

                return 1;
            }
        } else {
            if (o2 instanceof Node) {

                return -1;
            } else {
                if (o1 instanceof Comparable) {
                    Comparable c1 = (Comparable) o1;

                    return c1.compareTo(o2);
                } else {
                    String name1 = o1.getClass().getName();
                    String name2 = o2.getClass().getName();

                    return name1.compareTo(name2);
                }
            }
        }
    }

    public int compare(Node n1, Node n2) {
        int nodeType1 = n1.getNodeType();
        int nodeType2 = n2.getNodeType();
        int answer = nodeType1 - nodeType2;

        if (answer != 0) {
            return answer;
        } else {
            switch (nodeType1) {
                case Node.ELEMENT_NODE:
                    return compare((Element) n1, (Element) n2);

                case Node.DOCUMENT_NODE:
                    return compare((Document) n1, (Document) n2);

                case Node.ATTRIBUTE_NODE:
                    return compare((Attribute) n1, (Attribute) n2);

                case Node.TEXT_NODE:
                    return compare((Text) n1, (Text) n2);

                case Node.CDATA_SECTION_NODE:
                    return compare((CDATA) n1, (CDATA) n2);

                case Node.ENTITY_REFERENCE_NODE:
                    return compare((Entity) n1, (Entity) n2);

                case Node.PROCESSING_INSTRUCTION_NODE:
                    return compare((ProcessingInstruction) n1, (ProcessingInstruction) n2);

                case Node.COMMENT_NODE:
                    return compare((Comment) n1, (Comment) n2);

                case Node.DOCUMENT_TYPE_NODE:
                    return compare((DocumentType) n1, (DocumentType) n2);

                case Node.NAMESPACE_NODE:
                    return compare((Namespace) n1, (Namespace) n2);

                default:
                    throw new RuntimeException("Invalid node types. node1: " + n1 + " and node2: "
                            + n2);
            }
        }
    }

    public int compare(Document n1, Document n2) {
        int answer = compare(n1.getDocType(), n2.getDocType());

        if (answer == 0) {
            answer = compareContent(n1, n2);
        }

        return answer;
    }

    public int compare(Element n1, Element n2) {
        int answer = compare(n1.getQName(), n2.getQName());

        if (answer == 0) {

            int c1 = n1.attributeCount();
            int c2 = n2.attributeCount();
            answer = c1 - c2;

            if (answer == 0) {
                for (int i = 0; i < c1; i++) {
                    Attribute a1 = n1.attribute(i);
                    Attribute a2 = n2.attribute(a1.getQName());
                    answer = compare(a1, a2);

                    if (answer != 0) {
                        return answer;
                    }
                }

                answer = compareContent(n1, n2);
            }
        }

        return answer;
    }

    public int compare(Attribute n1, Attribute n2) {
        int answer = compare(n1.getQName(), n2.getQName());

        if (answer == 0) {
            answer = compare(n1.getValue(), n2.getValue());
        }

        return answer;
    }

    public int compare(QName n1, QName n2) {
        int answer = compare(n1.getNamespaceURI(), n2.getNamespaceURI());

        if (answer == 0) {
            answer = compare(n1.getQualifiedName(), n2.getQualifiedName());
        }

        return answer;
    }

    public int compare(Namespace n1, Namespace n2) {
        int answer = compare(n1.getURI(), n2.getURI());

        if (answer == 0) {
            answer = compare(n1.getPrefix(), n2.getPrefix());
        }

        return answer;
    }

    public int compare(CharacterData t1, CharacterData t2) {
        return compare(t1.getText(), t2.getText());
    }

    public int compare(DocumentType o1, DocumentType o2) {
        if (o1 == o2) {
            return 0;
        } else if (o1 == null) {

            return -1;
        } else if (o2 == null) {
            return 1;
        }

        int answer = compare(o1.getPublicID(), o2.getPublicID());

        if (answer == 0) {
            answer = compare(o1.getSystemID(), o2.getSystemID());

            if (answer == 0) {
                answer = compare(o1.getName(), o2.getName());
            }
        }

        return answer;
    }

    public int compare(Entity n1, Entity n2) {
        int answer = compare(n1.getName(), n2.getName());

        if (answer == 0) {
            answer = compare(n1.getText(), n2.getText());
        }

        return answer;
    }

    public int compare(ProcessingInstruction n1, ProcessingInstruction n2) {
        int answer = compare(n1.getTarget(), n2.getTarget());

        if (answer == 0) {
            answer = compare(n1.getText(), n2.getText());
        }

        return answer;
    }

    public int compareContent(Branch b1, Branch b2) {
        int c1 = b1.nodeCount();
        int c2 = b2.nodeCount();
        int answer = c1 - c2;

        if (answer == 0) {
            for (int i = 0; i < c1; i++) {
                Node n1 = b1.node(i);
                Node n2 = b2.node(i);
                answer = compare(n1, n2);

                if (answer != 0) {
                    break;
                }
            }
        }

        return answer;
    }

    public int compare(String o1, String o2) {
        if (o1 == o2) {
            return 0;
        } else if (o1 == null) {

            return -1;
        } else if (o2 == null) {
            return 1;
        }

        return o1.compareTo(o2);
    }
}


