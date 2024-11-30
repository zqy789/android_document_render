

package com.document.render.office.fc.dom4j.util;

import com.document.render.office.fc.dom4j.DocumentHelper;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.QName;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;



public class XMLErrorHandler implements ErrorHandler {
    protected static final QName ERROR_QNAME = QName.get("error");

    protected static final QName FATALERROR_QNAME = QName.get("fatalError");

    protected static final QName WARNING_QNAME = QName.get("warning");


    private Element errors;


    private QName errorQName = ERROR_QNAME;


    private QName fatalErrorQName = FATALERROR_QNAME;


    private QName warningQName = WARNING_QNAME;

    public XMLErrorHandler() {
        this.errors = DocumentHelper.createElement("errors");
    }

    public XMLErrorHandler(Element errors) {
        this.errors = errors;
    }

    public void error(SAXParseException e) {
        Element element = errors.addElement(errorQName);
        addException(element, e);
    }

    public void fatalError(SAXParseException e) {
        Element element = errors.addElement(fatalErrorQName);
        addException(element, e);
    }

    public void warning(SAXParseException e) {
        Element element = errors.addElement(warningQName);
        addException(element, e);
    }



    public Element getErrors() {
        return errors;
    }

    public void setErrors(Element errors) {
        this.errors = errors;
    }


    public QName getErrorQName() {
        return errorQName;
    }

    public void setErrorQName(QName errorQName) {
        this.errorQName = errorQName;
    }

    public QName getFatalErrorQName() {
        return fatalErrorQName;
    }

    public void setFatalErrorQName(QName fatalErrorQName) {
        this.fatalErrorQName = fatalErrorQName;
    }

    public QName getWarningQName() {
        return warningQName;
    }

    public void setWarningQName(QName warningQName) {
        this.warningQName = warningQName;
    }





    protected void addException(Element element, SAXParseException e) {
        element.addAttribute("column", Integer.toString(e.getColumnNumber()));
        element.addAttribute("line", Integer.toString(e.getLineNumber()));

        String publicID = e.getPublicId();

        if ((publicID != null) && (publicID.length() > 0)) {
            element.addAttribute("publicID", publicID);
        }

        String systemID = e.getSystemId();

        if ((systemID != null) && (systemID.length() > 0)) {
            element.addAttribute("systemID", systemID);
        }

        element.addText(e.getMessage());
    }
}


