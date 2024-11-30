

package com.document.render.office.fc.openxml4j.opc.internal;

import com.document.render.office.fc.dom4j.Document;
import com.document.render.office.fc.dom4j.DocumentException;
import com.document.render.office.fc.dom4j.DocumentHelper;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.ElementHandler;
import com.document.render.office.fc.dom4j.ElementPath;
import com.document.render.office.fc.dom4j.Namespace;
import com.document.render.office.fc.dom4j.QName;
import com.document.render.office.fc.dom4j.io.SAXReader;
import com.document.render.office.fc.openxml4j.exceptions.InvalidFormatException;
import com.document.render.office.fc.openxml4j.exceptions.InvalidOperationException;
import com.document.render.office.fc.openxml4j.exceptions.OpenXML4JRuntimeException;
import com.document.render.office.fc.openxml4j.opc.PackagePart;
import com.document.render.office.fc.openxml4j.opc.PackagePartName;
import com.document.render.office.fc.openxml4j.opc.PackagingURIHelper;
import com.document.render.office.fc.openxml4j.opc.ZipPackage;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map.Entry;
import java.util.TreeMap;



public class ContentTypeManager {


    public static final String CONTENT_TYPES_PART_NAME = "[Content_Types].xml";

    /**
     * Content type namespace
     */
    public static final String TYPES_NAMESPACE_URI = "http://schemas.openxmlformats.org/package/2006/content-types";



    private static final String TYPES_TAG_NAME = "Types";

    private static final String DEFAULT_TAG_NAME = "Default";

    private static final String EXTENSION_ATTRIBUTE_NAME = "Extension";

    private static final String CONTENT_TYPE_ATTRIBUTE_NAME = "ContentType";

    private static final String OVERRIDE_TAG_NAME = "Override";

    private static final String PART_NAME_ATTRIBUTE_NAME = "PartName";


    protected ZipPackage container;


    private TreeMap<String, String> defaultContentType;


    private TreeMap<PackagePartName, String> overrideContentType;


    public ContentTypeManager(InputStream in, ZipPackage pkg) throws InvalidFormatException {
        this.container = pkg;
        this.defaultContentType = new TreeMap<String, String>();
        if (in != null) {
            try {
                parseContentTypesFile(in);
            } catch (InvalidFormatException e) {
                throw new InvalidFormatException("Can't read content types part !");
            }
        }
    }


    public void addContentType(PackagePartName partName, String contentType) {
        boolean defaultCTExists = false;
        String extension = partName.getExtension().toLowerCase();
        if ((extension.length() == 0)
                || (this.defaultContentType.containsKey(extension)
                && !(defaultCTExists = this.defaultContentType.containsValue(contentType)))) {
            this.addOverrideContentType(partName, contentType);
        } else if (!defaultCTExists) {
            this.addDefaultContentType(extension, contentType);
        }
    }


    private void addOverrideContentType(PackagePartName partName, String contentType) {
        if (overrideContentType == null) {
            overrideContentType = new TreeMap<PackagePartName, String>();
        }
        overrideContentType.put(partName, contentType);
    }


    private void addDefaultContentType(String extension, String contentType) {


        defaultContentType.put(extension.toLowerCase(), contentType);
    }


    public void removeContentType(PackagePartName partName) throws InvalidOperationException {
        if (partName == null) {
            throw new IllegalArgumentException("partName");
        }


        if (this.overrideContentType != null && (this.overrideContentType.get(partName) != null)) {

            this.overrideContentType.remove(partName);
            return;
        }


        String extensionToDelete = partName.getExtension();
        boolean deleteDefaultContentTypeFlag = true;
        if (this.container != null) {
            try {
                for (PackagePart part : this.container.getParts()) {
                    if (!part.getPartName().equals(partName)
                            && part.getPartName().getExtension().equalsIgnoreCase(extensionToDelete)) {
                        deleteDefaultContentTypeFlag = false;
                        break;
                    }
                }
            } catch (InvalidFormatException e) {
                throw new InvalidOperationException(e.getMessage());
            }
        }


        if (deleteDefaultContentTypeFlag) {
            this.defaultContentType.remove(extensionToDelete);
        }


        if (this.container != null) {
            try {
                for (PackagePart part : this.container.getParts()) {
                    if (!part.getPartName().equals(partName)
                            && this.getContentType(part.getPartName()) == null)
                        throw new InvalidOperationException(
                                "Rule M2.4 is not respected: Nor a default element or override element is associated with the part: "
                                        + part.getPartName().getName());
                }
            } catch (InvalidFormatException e) {
                throw new InvalidOperationException(e.getMessage());
            }
        }
    }


    public boolean isContentTypeRegister(String contentType) {
        if (contentType == null) {
            throw new IllegalArgumentException("contentType");
        }

        return (this.defaultContentType.values().contains(contentType)
                || (this.overrideContentType != null && this.overrideContentType.values().contains(contentType)));
    }


    public String getContentType(PackagePartName partName) {
        if (partName == null) {
            throw new IllegalArgumentException("partName");
        }
        if ((this.overrideContentType != null) && this.overrideContentType.containsKey(partName)) {
            return this.overrideContentType.get(partName);
        }
        String extension = partName.getExtension().toLowerCase();
        if (this.defaultContentType.containsKey(extension)) {
            return this.defaultContentType.get(extension);
        }


        if (this.container != null && this.container.getPart(partName) != null) {
            throw new OpenXML4JRuntimeException(
                    "Rule M2.4 exception : this error should NEVER happen, if so please send a mail to the developers team, thanks !");
        }
        return null;
    }


    public void clearAll() {
        defaultContentType.clear();
        if (overrideContentType != null) {
            overrideContentType.clear();
        }
    }


    public void clearOverrideContentTypes() {
        if (overrideContentType != null) {
            overrideContentType.clear();
        }
    }


    private void parseContentTypesFile(InputStream in) throws InvalidFormatException {
        try {
            SAXReader xmlReader = new SAXReader();

            XLSXSaxHandler xLSXSaxHandler = new XLSXSaxHandler();
            xmlReader.addHandler("/Types/Default", xLSXSaxHandler);
            xmlReader.addHandler("/Types/Override", xLSXSaxHandler);

            xmlReader.read(in);


        } catch (DocumentException e) {
            throw new InvalidFormatException(e.getMessage());
        }
    }


    public boolean save(OutputStream outStream) {
        Document xmlOutDoc = DocumentHelper.createDocument();


        Namespace dfNs = Namespace.get("", TYPES_NAMESPACE_URI);
        Element typesElem = xmlOutDoc.addElement(new QName(TYPES_TAG_NAME, dfNs));


        for (Entry<String, String> entry : defaultContentType.entrySet()) {
            appendDefaultType(typesElem, entry);
        }


        if (overrideContentType != null) {
            for (Entry<PackagePartName, String> entry : overrideContentType.entrySet()) {
                appendSpecificTypes(typesElem, entry);
            }
        }
        xmlOutDoc.normalize();


        return this.saveImpl(xmlOutDoc, outStream);
    }


    private void appendSpecificTypes(Element root, Entry<PackagePartName, String> entry) {
        root.addElement(OVERRIDE_TAG_NAME)
                .addAttribute(PART_NAME_ATTRIBUTE_NAME, entry.getKey().getName())
                .addAttribute(CONTENT_TYPE_ATTRIBUTE_NAME, entry.getValue());
    }


    private void appendDefaultType(Element root, Entry<String, String> entry) {
        root.addElement(DEFAULT_TAG_NAME).addAttribute(EXTENSION_ATTRIBUTE_NAME, entry.getKey())
                .addAttribute(CONTENT_TYPE_ATTRIBUTE_NAME, entry.getValue());

    }


    public boolean saveImpl(Document content, OutputStream out) {


        return true;

    }


    class XLSXSaxHandler implements ElementHandler {


        public void onStart(ElementPath elementPath) {

        }


        public void onEnd(ElementPath elementPath) {
            Element elem = elementPath.getCurrent();
            String name = elem.getName();
            if (name.equals(DEFAULT_TAG_NAME)) {

                String extension = elem.attribute(EXTENSION_ATTRIBUTE_NAME).getValue();
                String contentType = elem.attribute(CONTENT_TYPE_ATTRIBUTE_NAME).getValue();
                addDefaultContentType(extension, contentType);
            } else if (name.equals(OVERRIDE_TAG_NAME)) {
                try {

                    URI uri = new URI(elem.attribute(PART_NAME_ATTRIBUTE_NAME).getValue());
                    PackagePartName partName = PackagingURIHelper.createPartName(uri);
                    String contentType = elem.attribute(CONTENT_TYPE_ATTRIBUTE_NAME).getValue();
                    addOverrideContentType(partName, contentType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            elem.detach();
        }

    }

}
