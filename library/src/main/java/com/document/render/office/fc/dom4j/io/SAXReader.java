

package com.document.render.office.fc.dom4j.io;

import androidx.annotation.Keep;

import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.DocumentException;
import com.document.render.office.fc.dom4j.DocumentFactory;
import com.document.render.office.fc.dom4j.ElementHandler;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;



public class SAXReader {
    private static final String SAX_STRING_INTERNING = "http://xml.org/sax/features/string-interning";
    private static final String SAX_NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
    private static final String SAX_NAMESPACES = "http://xml.org/sax/features/namespaces";
    private static final String SAX_DECL_HANDLER = "http://xml.org/sax/properties/declaration-handler";
    private static final String SAX_LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
    private static final String SAX_LEXICALHANDLER = "http://xml.org/sax/handlers/LexicalHandler";


    private DocumentFactory factory;


    private XMLReader xmlReader;


    private boolean validating;


    private DispatchHandler dispatchHandler;


    private ErrorHandler errorHandler;


    private EntityResolver entityResolver;


    private boolean stringInternEnabled = true;


    private boolean includeInternalDTDDeclarations = false;


    private boolean includeExternalDTDDeclarations = false;


    private boolean mergeAdjacentText = false;


    private boolean stripWhitespaceText = false;


    private boolean ignoreComments = false;


    private String encoding = null;





    private XMLFilter xmlFilter;

    public SAXReader() {
    }

    public SAXReader(boolean validating) {
        this.validating = validating;
    }

    public SAXReader(DocumentFactory factory) {
        this.factory = factory;
    }

    public SAXReader(DocumentFactory factory, boolean validating) {
        this.factory = factory;
        this.validating = validating;
    }

    public SAXReader(XMLReader xmlReader) {
        this.xmlReader = xmlReader;
    }

    public SAXReader(XMLReader xmlReader, boolean validating) {
        this.xmlReader = xmlReader;
        this.validating = validating;
    }

    public SAXReader(String xmlReaderClassName) throws SAXException {
        if (xmlReaderClassName != null) {
            this.xmlReader = XMLReaderFactory.createXMLReader(xmlReaderClassName);
        }
    }

    public SAXReader(String xmlReaderClassName, boolean validating) throws SAXException {
        if (xmlReaderClassName != null) {
            this.xmlReader = XMLReaderFactory.createXMLReader(xmlReaderClassName);
        }

        this.validating = validating;
    }


    public void setProperty(String name, Object value) throws SAXException {
        getXMLReader().setProperty(name, value);
    }


    public void setFeature(String name, boolean value) throws SAXException {
        getXMLReader().setFeature(name, value);
    }


    public Document read(File file) throws DocumentException {
        try {

            InputSource source = new InputSource(new FileInputStream(file));
            if (this.encoding != null) {
                source.setEncoding(this.encoding);
            }
            String path = file.getAbsolutePath();

            if (path != null) {
                // Code taken from Ant FileUtils
                StringBuffer sb = new StringBuffer("file://");


                if (!path.startsWith(File.separator)) {
                    sb.append("/");
                }

                path = path.replace('\\', '/');
                sb.append(path);

                source.setSystemId(sb.toString());
            }

            return read(source);
        } catch (FileNotFoundException e) {
            throw new DocumentException(e.getMessage(), e);
        }
    }


    public Document read(URL url) throws DocumentException {
        String systemID = url.toExternalForm();

        InputSource source = new InputSource(systemID);
        if (this.encoding != null) {
            source.setEncoding(this.encoding);
        }

        return read(source);
    }


    public Document read(String systemId) throws DocumentException {
        InputSource source = new InputSource(systemId);
        if (this.encoding != null) {
            source.setEncoding(this.encoding);
        }

        return read(source);
    }


    public Document read(InputStream in) throws DocumentException {
        InputSource source = new InputSource(in);
        if (this.encoding != null) {
            source.setEncoding(this.encoding);
        }

        return read(source);
    }


    public Document read(Reader reader) throws DocumentException {
        InputSource source = new InputSource(reader);
        if (this.encoding != null) {
            source.setEncoding(this.encoding);
        }

        return read(source);
    }


    public Document read(InputStream in, String systemId) throws DocumentException {
        InputSource source = new InputSource(in);
        source.setSystemId(systemId);
        if (this.encoding != null) {
            source.setEncoding(this.encoding);
        }

        return read(source);
    }


    public Document read(Reader reader, String systemId) throws DocumentException {
        InputSource source = new InputSource(reader);
        source.setSystemId(systemId);
        if (this.encoding != null) {
            source.setEncoding(this.encoding);
        }

        return read(source);
    }


    public Document read(InputSource in) throws DocumentException {
        try {
            XMLReader reader = getXMLReader();

            reader = installXMLFilter(reader);

            EntityResolver thatEntityResolver = this.entityResolver;

            if (thatEntityResolver == null) {
                thatEntityResolver = createDefaultEntityResolver(in.getSystemId());
                this.entityResolver = thatEntityResolver;
            }

            reader.setEntityResolver(thatEntityResolver);

            SAXContentHandler contentHandler = createContentHandler(reader);
            contentHandler.setEntityResolver(thatEntityResolver);
            contentHandler.setInputSource(in);

            boolean internal = isIncludeInternalDTDDeclarations();
            boolean external = isIncludeExternalDTDDeclarations();

            contentHandler.setIncludeInternalDTDDeclarations(internal);
            contentHandler.setIncludeExternalDTDDeclarations(external);
            contentHandler.setMergeAdjacentText(isMergeAdjacentText());
            contentHandler.setStripWhitespaceText(isStripWhitespaceText());
            contentHandler.setIgnoreComments(isIgnoreComments());
            reader.setContentHandler(contentHandler);

            configureReader(reader, contentHandler);

            reader.parse(in);

            return contentHandler.getDocument();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof SAXParseException) {

                SAXParseException parseException = (SAXParseException) e;
                String systemId = parseException.getSystemId();

                if (systemId == null) {
                    systemId = "";
                }

                String message = "Error on line " + parseException.getLineNumber()
                        + " of document " + systemId + " : " + parseException.getMessage();

                throw new DocumentException(message, e);
            } else {
                throw new DocumentException(e.getMessage(), e);
            }
        }
    }





    public boolean isValidating() {
        return validating;
    }


    public void setValidation(boolean validation) {
        this.validating = validation;
    }


    public boolean isIncludeInternalDTDDeclarations() {
        return includeInternalDTDDeclarations;
    }


    public void setIncludeInternalDTDDeclarations(boolean include) {
        this.includeInternalDTDDeclarations = include;
    }


    public boolean isIncludeExternalDTDDeclarations() {
        return includeExternalDTDDeclarations;
    }


    public void setIncludeExternalDTDDeclarations(boolean include) {
        this.includeExternalDTDDeclarations = include;
    }


    public boolean isStringInternEnabled() {
        return stringInternEnabled;
    }


    public void setStringInternEnabled(boolean stringInternEnabled) {
        this.stringInternEnabled = stringInternEnabled;
    }


    public boolean isMergeAdjacentText() {
        return mergeAdjacentText;
    }


    public void setMergeAdjacentText(boolean mergeAdjacentText) {
        this.mergeAdjacentText = mergeAdjacentText;
    }


    public boolean isStripWhitespaceText() {
        return stripWhitespaceText;
    }


    public void setStripWhitespaceText(boolean stripWhitespaceText) {
        this.stripWhitespaceText = stripWhitespaceText;
    }


    public boolean isIgnoreComments() {
        return ignoreComments;
    }


    public void setIgnoreComments(boolean ignoreComments) {
        this.ignoreComments = ignoreComments;
    }


    public DocumentFactory getDocumentFactory() {
        if (factory == null) {
            factory = DocumentFactory.getInstance();
        }

        return factory;
    }


    public void setDocumentFactory(DocumentFactory documentFactory) {
        this.factory = documentFactory;
    }


    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }


    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }


    public EntityResolver getEntityResolver() {
        return entityResolver;
    }


    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }


    public XMLReader getXMLReader() throws SAXException {
        if (xmlReader == null) {
            xmlReader = createXMLReader();
        }

        return xmlReader;
    }


    public void setXMLReader(XMLReader reader) {
        this.xmlReader = reader;
    }


    @Keep
    public String getEncoding() {
        return encoding;
    }


    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }


    public void setXMLReaderClassName(String xmlReaderClassName) throws SAXException {
        setXMLReader(XMLReaderFactory.createXMLReader(xmlReaderClassName));
    }


    public void addHandler(String path, ElementHandler handler) {
        getDispatchHandler().addHandler(path, handler);
    }


    public void removeHandler(String path) {
        getDispatchHandler().removeHandler(path);
    }


    public void setDefaultHandler(ElementHandler handler) {
        getDispatchHandler().setDefaultHandler(handler);
    }


    public void resetHandlers() {
        getDispatchHandler().resetHandlers();
    }


    public XMLFilter getXMLFilter() {
        return xmlFilter;
    }


    public void setXMLFilter(XMLFilter filter) {
        this.xmlFilter = filter;
    }





    protected XMLReader installXMLFilter(XMLReader reader) {
        XMLFilter filter = getXMLFilter();

        if (filter != null) {

            XMLFilter root = filter;

            while (true) {
                XMLReader parent = root.getParent();

                if (parent instanceof XMLFilter) {
                    root = (XMLFilter) parent;
                } else {
                    break;
                }
            }

            root.setParent(reader);

            return filter;
        }

        return reader;
    }

    protected DispatchHandler getDispatchHandler() {
        if (dispatchHandler == null) {
            dispatchHandler = new DispatchHandler();
        }

        return dispatchHandler;
    }

    protected void setDispatchHandler(DispatchHandler dispatchHandler) {
        this.dispatchHandler = dispatchHandler;
    }


    protected XMLReader createXMLReader() throws SAXException {
        return SAXHelper.createXMLReader(isValidating());
    }


    protected void configureReader(XMLReader reader, DefaultHandler handler)
            throws DocumentException {

        SAXHelper.setParserProperty(reader, SAX_LEXICALHANDLER, handler);


        SAXHelper.setParserProperty(reader, SAX_LEXICAL_HANDLER, handler);


        if (includeInternalDTDDeclarations || includeExternalDTDDeclarations) {
            SAXHelper.setParserProperty(reader, SAX_DECL_HANDLER, handler);
        }


        SAXHelper.setParserFeature(reader, SAX_NAMESPACES, true);

        SAXHelper.setParserFeature(reader, SAX_NAMESPACE_PREFIXES, false);


        SAXHelper.setParserFeature(reader, SAX_STRING_INTERNING, isStringInternEnabled());

        // external entites
        /*
         * SAXHelper.setParserFeature( reader,
         * "http://xml.org/sax/properties/external-general-entities",
         * includeExternalGeneralEntities ); SAXHelper.setParserFeature( reader,
         * "http://xml.org/sax/properties/external-parameter-entities",
         * includeExternalParameterEntities );
         */
        // use Locator2 if possible
        SAXHelper.setParserFeature(reader, "http://xml.org/sax/features/use-locator2", true);

        try {
            // configure validation support
            reader.setFeature("http://xml.org/sax/features/validation", isValidating());

            if (errorHandler != null) {
                reader.setErrorHandler(errorHandler);
            } else {
                reader.setErrorHandler(handler);
            }
        } catch (Exception e) {
            if (isValidating()) {
                throw new DocumentException("Validation not supported for" + " XMLReader: "
                        + reader, e);
            }
        }
    }


    protected SAXContentHandler createContentHandler(XMLReader reader) {
        return new SAXContentHandler(getDocumentFactory(), dispatchHandler);
    }

    protected EntityResolver createDefaultEntityResolver(String systemId) {
        String prefix = null;

        if ((systemId != null) && (systemId.length() > 0)) {
            int idx = systemId.lastIndexOf('/');

            if (idx > 0) {
                prefix = systemId.substring(0, idx + 1);
            }
        }

        return new SAXEntityResolver(prefix);
    }

    protected static class SAXEntityResolver implements EntityResolver, Serializable {
        protected String uriPrefix;

        public SAXEntityResolver(String uriPrefix) {
            this.uriPrefix = uriPrefix;
        }

        public InputSource resolveEntity(String publicId, String systemId) {

            if ((systemId != null) && (systemId.length() > 0)) {
                if ((uriPrefix != null) && (systemId.indexOf(':') <= 0)) {
                    systemId = uriPrefix + systemId;
                }
            }

            return new InputSource(systemId);
        }
    }
}


