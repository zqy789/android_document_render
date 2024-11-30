

package com.document.render.office.fc.dom4j;


public interface Visitor {

    void visit(Document document);


    void visit(DocumentType documentType);


    void visit(Element node);


    void visit(Attribute node);


    void visit(CDATA node);


    void visit(Comment node);


    void visit(Entity node);


    void visit(Namespace namespace);


    void visit(ProcessingInstruction node);


    void visit(Text node);
}


