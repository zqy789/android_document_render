

package com.document.render.office.fc.openxml4j.opc.internal.marshallers;

import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.DocumentHelper;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.Namespace;
import com.document.render.office.fc.dom4j.QName;
import com.document.render.office.fc.openxml4j.exceptions.OpenXML4JException;
import com.document.render.office.fc.openxml4j.opc.PackagePart;
import com.document.render.office.fc.openxml4j.opc.internal.PackagePropertiesPart;
import com.document.render.office.fc.openxml4j.opc.internal.PartMarshaller;

import java.io.OutputStream;



public class PackagePropertiesMarshaller implements PartMarshaller {

    protected static final String KEYWORD_CATEGORY = "category";
    protected static final String KEYWORD_CONTENT_STATUS = "contentStatus";
    protected static final String KEYWORD_CONTENT_TYPE = "contentType";
    protected static final String KEYWORD_CREATED = "created";
    protected static final String KEYWORD_CREATOR = "creator";
    protected static final String KEYWORD_DESCRIPTION = "description";
    protected static final String KEYWORD_IDENTIFIER = "identifier";
    protected static final String KEYWORD_KEYWORDS = "keywords";
    protected static final String KEYWORD_LANGUAGE = "language";
    protected static final String KEYWORD_LAST_MODIFIED_BY = "lastModifiedBy";
    protected static final String KEYWORD_LAST_PRINTED = "lastPrinted";
    protected static final String KEYWORD_MODIFIED = "modified";
    protected static final String KEYWORD_REVISION = "revision";
    protected static final String KEYWORD_SUBJECT = "subject";
    protected static final String KEYWORD_TITLE = "title";
    protected static final String KEYWORD_VERSION = "version";
    private final static Namespace namespaceDC = new Namespace("dc",
            PackagePropertiesPart.NAMESPACE_DC_URI);
    private final static Namespace namespaceCoreProperties = new Namespace("",
            PackagePropertiesPart.NAMESPACE_CP_URI);
    private final static Namespace namespaceDcTerms = new Namespace("dcterms",
            PackagePropertiesPart.NAMESPACE_DCTERMS_URI);
    private final static Namespace namespaceXSI = new Namespace("xsi",
            PackagePropertiesPart.NAMESPACE_XSI_URI);
    PackagePropertiesPart propsPart;


    Document xmlDoc = null;


    public boolean marshall(PackagePart part, OutputStream out) throws OpenXML4JException {
        if (!(part instanceof PackagePropertiesPart))
            throw new IllegalArgumentException("'part' must be a PackagePropertiesPart instance.");
        propsPart = (PackagePropertiesPart) part;


        xmlDoc = DocumentHelper.createDocument();
        Element rootElem = xmlDoc.addElement(new QName("coreProperties", namespaceCoreProperties));
        rootElem.addNamespace("cp", PackagePropertiesPart.NAMESPACE_CP_URI);
        rootElem.addNamespace("dc", PackagePropertiesPart.NAMESPACE_DC_URI);
        rootElem.addNamespace("dcterms", PackagePropertiesPart.NAMESPACE_DCTERMS_URI);
        rootElem.addNamespace("xsi", PackagePropertiesPart.NAMESPACE_XSI_URI);

        addCategory();
        addContentStatus();
        addContentType();
        addCreated();
        addCreator();
        addDescription();
        addIdentifier();
        addKeywords();
        addLanguage();
        addLastModifiedBy();
        addLastPrinted();
        addModified();
        addRevision();
        addSubject();
        addTitle();
        addVersion();
        return true;
    }


    private void addCategory() {
        if (!propsPart.getCategoryProperty().hasValue())
            return;

        Element elem = xmlDoc.getRootElement().element(
                new QName(KEYWORD_CATEGORY, namespaceCoreProperties));
        if (elem == null) {

            elem = xmlDoc.getRootElement().addElement(
                    new QName(KEYWORD_CATEGORY, namespaceCoreProperties));
        } else {
            elem.clearContent();
        }
        elem.addText(propsPart.getCategoryProperty().getValue());
    }


    private void addContentStatus() {
        if (!propsPart.getContentStatusProperty().hasValue())
            return;

        Element elem = xmlDoc.getRootElement().element(
                new QName(KEYWORD_CONTENT_STATUS, namespaceCoreProperties));
        if (elem == null) {

            elem = xmlDoc.getRootElement().addElement(
                    new QName(KEYWORD_CONTENT_STATUS, namespaceCoreProperties));
        } else {
            elem.clearContent();
        }
        elem.addText(propsPart.getContentStatusProperty().getValue());
    }


    private void addContentType() {
        if (!propsPart.getContentTypeProperty().hasValue())
            return;

        Element elem = xmlDoc.getRootElement().element(
                new QName(KEYWORD_CONTENT_TYPE, namespaceCoreProperties));
        if (elem == null) {

            elem = xmlDoc.getRootElement().addElement(
                    new QName(KEYWORD_CONTENT_TYPE, namespaceCoreProperties));
        } else {
            elem.clearContent();
        }
        elem.addText(propsPart.getContentTypeProperty().getValue());
    }


    private void addCreated() {
        if (!propsPart.getCreatedProperty().hasValue())
            return;

        Element elem = xmlDoc.getRootElement()
                .element(new QName(KEYWORD_CREATED, namespaceDcTerms));
        if (elem == null) {

            elem = xmlDoc.getRootElement().addElement(new QName(KEYWORD_CREATED, namespaceDcTerms));
        } else {
            elem.clearContent();
        }
        elem.addAttribute(new QName("type", namespaceXSI), "dcterms:W3CDTF");
        elem.addText(propsPart.getCreatedPropertyString());
    }


    private void addCreator() {
        if (!propsPart.getCreatorProperty().hasValue())
            return;

        Element elem = xmlDoc.getRootElement().element(new QName(KEYWORD_CREATOR, namespaceDC));
        if (elem == null) {

            elem = xmlDoc.getRootElement().addElement(new QName(KEYWORD_CREATOR, namespaceDC));
        } else {
            elem.clearContent();
        }
        elem.addText(propsPart.getCreatorProperty().getValue());
    }


    private void addDescription() {
        if (!propsPart.getDescriptionProperty().hasValue())
            return;

        Element elem = xmlDoc.getRootElement().element(new QName(KEYWORD_DESCRIPTION, namespaceDC));
        if (elem == null) {

            elem = xmlDoc.getRootElement().addElement(new QName(KEYWORD_DESCRIPTION, namespaceDC));
        } else {
            elem.clearContent();
        }
        elem.addText(propsPart.getDescriptionProperty().getValue());
    }


    private void addIdentifier() {
        if (!propsPart.getIdentifierProperty().hasValue())
            return;

        Element elem = xmlDoc.getRootElement().element(new QName(KEYWORD_IDENTIFIER, namespaceDC));
        if (elem == null) {

            elem = xmlDoc.getRootElement().addElement(new QName(KEYWORD_IDENTIFIER, namespaceDC));
        } else {
            elem.clearContent();
        }
        elem.addText(propsPart.getIdentifierProperty().getValue());
    }


    private void addKeywords() {
        if (!propsPart.getKeywordsProperty().hasValue())
            return;

        Element elem = xmlDoc.getRootElement().element(
                new QName(KEYWORD_KEYWORDS, namespaceCoreProperties));
        if (elem == null) {

            elem = xmlDoc.getRootElement().addElement(
                    new QName(KEYWORD_KEYWORDS, namespaceCoreProperties));
        } else {
            elem.clearContent();
        }
        elem.addText(propsPart.getKeywordsProperty().getValue());
    }


    private void addLanguage() {
        if (!propsPart.getLanguageProperty().hasValue())
            return;

        Element elem = xmlDoc.getRootElement().element(new QName(KEYWORD_LANGUAGE, namespaceDC));
        if (elem == null) {

            elem = xmlDoc.getRootElement().addElement(new QName(KEYWORD_LANGUAGE, namespaceDC));
        } else {
            elem.clearContent();
        }
        elem.addText(propsPart.getLanguageProperty().getValue());
    }


    private void addLastModifiedBy() {
        if (!propsPart.getLastModifiedByProperty().hasValue())
            return;

        Element elem = xmlDoc.getRootElement().element(
                new QName(KEYWORD_LAST_MODIFIED_BY, namespaceCoreProperties));
        if (elem == null) {

            elem = xmlDoc.getRootElement().addElement(
                    new QName(KEYWORD_LAST_MODIFIED_BY, namespaceCoreProperties));
        } else {
            elem.clearContent();
        }
        elem.addText(propsPart.getLastModifiedByProperty().getValue());
    }


    private void addLastPrinted() {
        if (!propsPart.getLastPrintedProperty().hasValue())
            return;

        Element elem = xmlDoc.getRootElement().element(
                new QName(KEYWORD_LAST_PRINTED, namespaceCoreProperties));
        if (elem == null) {

            elem = xmlDoc.getRootElement().addElement(
                    new QName(KEYWORD_LAST_PRINTED, namespaceCoreProperties));
        } else {
            elem.clearContent();
        }
        elem.addText(propsPart.getLastPrintedPropertyString());
    }


    private void addModified() {
        if (!propsPart.getModifiedProperty().hasValue())
            return;

        Element elem = xmlDoc.getRootElement().element(
                new QName(KEYWORD_MODIFIED, namespaceDcTerms));
        if (elem == null) {

            elem = xmlDoc.getRootElement()
                    .addElement(new QName(KEYWORD_MODIFIED, namespaceDcTerms));
        } else {
            elem.clearContent();
        }
        elem.addAttribute(new QName("type", namespaceXSI), "dcterms:W3CDTF");
        elem.addText(propsPart.getModifiedPropertyString());
    }


    private void addRevision() {
        if (!propsPart.getRevisionProperty().hasValue())
            return;

        Element elem = xmlDoc.getRootElement().element(
                new QName(KEYWORD_REVISION, namespaceCoreProperties));
        if (elem == null) {

            elem = xmlDoc.getRootElement().addElement(
                    new QName(KEYWORD_REVISION, namespaceCoreProperties));
        } else {
            elem.clearContent();
        }
        elem.addText(propsPart.getRevisionProperty().getValue());
    }


    private void addSubject() {
        if (!propsPart.getSubjectProperty().hasValue())
            return;

        Element elem = xmlDoc.getRootElement().element(new QName(KEYWORD_SUBJECT, namespaceDC));
        if (elem == null) {

            elem = xmlDoc.getRootElement().addElement(new QName(KEYWORD_SUBJECT, namespaceDC));
        } else {
            elem.clearContent();
        }
        elem.addText(propsPart.getSubjectProperty().getValue());
    }


    private void addTitle() {
        if (!propsPart.getTitleProperty().hasValue())
            return;

        Element elem = xmlDoc.getRootElement().element(new QName(KEYWORD_TITLE, namespaceDC));
        if (elem == null) {

            elem = xmlDoc.getRootElement().addElement(new QName(KEYWORD_TITLE, namespaceDC));
        } else {
            elem.clearContent();
        }
        elem.addText(propsPart.getTitleProperty().getValue());
    }

    private void addVersion() {
        if (!propsPart.getVersionProperty().hasValue())
            return;

        Element elem = xmlDoc.getRootElement().element(
                new QName(KEYWORD_VERSION, namespaceCoreProperties));
        if (elem == null) {

            elem = xmlDoc.getRootElement().addElement(
                    new QName(KEYWORD_VERSION, namespaceCoreProperties));
        } else {
            elem.clearContent();
        }
        elem.addText(propsPart.getVersionProperty().getValue());
    }
}
