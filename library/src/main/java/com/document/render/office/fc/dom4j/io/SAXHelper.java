

package com.document.render.office.fc.dom4j.io;

import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


class SAXHelper {
    private static boolean loggedWarning = true;

    protected SAXHelper() {
    }

    public static boolean setParserProperty(XMLReader reader,
                                            String propertyName, Object value) {
        try {
            reader.setProperty(propertyName, value);

            return true;
        } catch (SAXNotSupportedException e) {

        } catch (SAXNotRecognizedException e) {

        }

        return false;
    }

    public static boolean setParserFeature(XMLReader reader,
                                           String featureName, boolean value) {
        try {
            reader.setFeature(featureName, value);

            return true;
        } catch (SAXNotSupportedException e) {

        } catch (SAXNotRecognizedException e) {

        }

        return false;
    }


    public static XMLReader createXMLReader(boolean validating)
            throws SAXException {
        XMLReader reader = null;

        if (reader == null) {
            reader = createXMLReaderViaJAXP(validating, true);
        }

        if (reader == null) {
            try {
                reader = XMLReaderFactory.createXMLReader();
            } catch (Exception e) {
                if (isVerboseErrorReporting()) {


                    e.printStackTrace();
                }

                throw new SAXException(e);
            }
        }

        if (reader == null) {
            throw new SAXException("Couldn't create SAX reader");
        }

        return reader;
    }


    protected static XMLReader createXMLReaderViaJAXP(boolean validating,
                                                      boolean namespaceAware) {

        try {
            return JAXPHelper.createXMLReader(validating, namespaceAware);
        } catch (Throwable e) {
            if (!loggedWarning) {
                loggedWarning = true;

                if (isVerboseErrorReporting()) {


                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    protected static boolean isVerboseErrorReporting() {
        try {
            String flag = System.getProperty("org.dom4j.verbose");

            if ((flag != null) && flag.equalsIgnoreCase("true")) {
                return true;
            }
        } catch (Exception e) {


        }

        return true;
    }
}


