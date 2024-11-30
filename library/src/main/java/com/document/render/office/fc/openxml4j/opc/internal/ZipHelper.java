

package com.document.render.office.fc.openxml4j.opc.internal;

import com.document.render.office.fc.openxml4j.exceptions.OpenXML4JException;
import com.document.render.office.fc.openxml4j.opc.PackageRelationship;
import com.document.render.office.fc.openxml4j.opc.PackageRelationshipTypes;
import com.document.render.office.fc.openxml4j.opc.ZipPackage;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public final class ZipHelper {


    public static final int READ_WRITE_FILE_BUFFER_SIZE = 8192;

    private final static String FORWARD_SLASH = "/";


    private ZipHelper() {

    }


    public static ZipEntry getCorePropertiesZipEntry(ZipPackage pkg) {
        PackageRelationship corePropsRel = pkg.getRelationshipsByType(
                PackageRelationshipTypes.CORE_PROPERTIES).getRelationship(0);

        if (corePropsRel == null)
            return null;

        return new ZipEntry(corePropsRel.getTargetURI().getPath());
    }


    public static ZipEntry getContentTypeZipEntry(ZipPackage pkg) {
        Enumeration entries = pkg.getZipArchive().getEntries();


        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            if (entry.getName().equals(ContentTypeManager.CONTENT_TYPES_PART_NAME)) {
                return entry;
            }
        }
        return null;
    }


    public static String getOPCNameFromZipItemName(String zipItemName) {
        if (zipItemName == null) {
            throw new IllegalArgumentException("zipItemName");
        }
        if (zipItemName.startsWith(FORWARD_SLASH)) {
            return zipItemName;
        }
        return FORWARD_SLASH + zipItemName;
    }


    public static String getZipItemNameFromOPCName(String opcItemName) {
        if (opcItemName == null) {
            throw new IllegalArgumentException("opcItemName");
        }
        String retVal = opcItemName;
        while (retVal.startsWith(FORWARD_SLASH)) {
            retVal = retVal.substring(1);
        }
        return retVal;
    }


    public static URI getZipURIFromOPCName(String opcItemName) {
        if (opcItemName == null) {
            throw new IllegalArgumentException("opcItemName");
        }
        String retVal = opcItemName;
        while (retVal.startsWith(FORWARD_SLASH)) {
            retVal = retVal.substring(1);
        }
        try {
            return new URI(retVal);
        } catch (URISyntaxException e) {
            return null;
        }
    }


    public static ZipFile openZipFile(String path) {
        File f = new File(path);
        try {
            if (!f.exists()) {
                return null;
            }
            return new ZipFile(f);
        } catch (IOException ioe) {
            return null;
        }
    }
}
