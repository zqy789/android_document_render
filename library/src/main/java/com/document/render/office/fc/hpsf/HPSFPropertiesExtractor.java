

package com.document.render.office.fc.hpsf;

import com.document.render.office.fc.POIDocument;
import com.document.render.office.fc.POITextExtractor;
import com.document.render.office.fc.poifs.filesystem.NPOIFSFileSystem;
import com.document.render.office.fc.poifs.filesystem.POIFSFileSystem;
import com.document.render.office.fc.util.LittleEndian;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;



public class HPSFPropertiesExtractor extends POITextExtractor {
    public HPSFPropertiesExtractor(POITextExtractor mainExtractor) {
        super(mainExtractor);
    }

    public HPSFPropertiesExtractor(POIDocument doc) {
        super(doc);
    }

    public HPSFPropertiesExtractor(POIFSFileSystem fs) {
        super(new PropertiesOnlyDocument(fs));
    }

    public HPSFPropertiesExtractor(NPOIFSFileSystem fs) {
        super(new PropertiesOnlyDocument(fs));
    }

    private static String getPropertiesText(SpecialPropertySet ps) {
        if (ps == null) {

            return "";
        }

        StringBuffer text = new StringBuffer();

        PropertyIDMap idMap = ps.getPropertySetIDMap();
        Property[] props = ps.getProperties();
        for (int i = 0; i < props.length; i++) {
            String type = Long.toString(props[i].getID());
            Object typeObj = idMap.get(props[i].getID());
            if (typeObj != null) {
                type = typeObj.toString();
            }

            String val = getPropertyValueText(props[i].getValue());
            text.append(type + " = " + val + "\n");
        }

        return text.toString();
    }

    private static String getPropertyValueText(Object val) {
        if (val == null) {
            return "(not set)";
        }
        if (val instanceof byte[]) {
            byte[] b = (byte[]) val;
            if (b.length == 0) {
                return "";
            }
            if (b.length == 1) {
                return Byte.toString(b[0]);
            }
            if (b.length == 2) {
                return Integer.toString(LittleEndian.getUShort(b));
            }
            if (b.length == 4) {
                return Long.toString(LittleEndian.getUInt(b));
            }

            return new String(b);
        }
        return val.toString();
    }

    public static void main(String[] args) throws IOException {
        for (String file : args) {
            HPSFPropertiesExtractor ext = new HPSFPropertiesExtractor(
                    new NPOIFSFileSystem(new File(file))
            );
            System.out.println(ext.getText());
        }
    }

    public String getDocumentSummaryInformationText() {
        DocumentSummaryInformation dsi = document.getDocumentSummaryInformation();
        StringBuffer text = new StringBuffer();


        text.append(getPropertiesText(dsi));


        CustomProperties cps = dsi == null ? null : dsi.getCustomProperties();
        if (cps != null) {
            Iterator<String> keys = cps.nameSet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                String val = getPropertyValueText(cps.get(key));
                text.append(key + " = " + val + "\n");
            }
        }


        return text.toString();
    }

    public String getSummaryInformationText() {
        SummaryInformation si = document.getSummaryInformation();


        return getPropertiesText(si);
    }


    public String getText() {
        return getSummaryInformationText() + getDocumentSummaryInformationText();
    }


    public POITextExtractor getMetadataTextExtractor() {
        throw new IllegalStateException("You already have the Metadata Text Extractor, not recursing!");
    }


    private static final class PropertiesOnlyDocument extends POIDocument {
        public PropertiesOnlyDocument(NPOIFSFileSystem fs) {
            super(fs.getRoot());
        }

        public PropertiesOnlyDocument(POIFSFileSystem fs) {
            super(fs);
        }

        public void write(OutputStream out) {
            throw new IllegalStateException("Unable to write, only for properties!");
        }
    }
}
