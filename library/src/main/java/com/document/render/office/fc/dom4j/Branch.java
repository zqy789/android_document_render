

package com.document.render.office.fc.dom4j;

import java.util.Iterator;
import java.util.List;


public interface Branch extends Node {

    Node node(int index) throws IndexOutOfBoundsException;


    int indexOf(Node node);


    int nodeCount();


    Element elementByID(String elementID);


    List content();


    Iterator nodeIterator();


    void setContent(List content);


    void appendContent(Branch branch);


    void clearContent();


    List processingInstructions();


    List processingInstructions(String target);


    ProcessingInstruction processingInstruction(String target);


    void setProcessingInstructions(List listOfPIs);


    Element addElement(String name);


    Element addElement(QName qname);


    Element addElement(String qualifiedName, String namespaceURI);


    boolean removeProcessingInstruction(String target);


    void add(Node node);


    void add(Comment comment);


    void add(Element element);


    void add(ProcessingInstruction pi);


    boolean remove(Node node);


    boolean remove(Comment comment);


    boolean remove(Element element);


    boolean remove(ProcessingInstruction pi);


    void normalize();
}


