

package com.document.render.office.fc.hpsf;

import androidx.annotation.Keep;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;



public class PropertySetFactory {


    @Keep
    public static PropertySet create(final InputStream stream)
            throws NoPropertySetStreamException, MarkUnsupportedException,
            UnsupportedEncodingException, IOException {
        final PropertySet ps = new PropertySet(stream);
        try {
            if (ps.isSummaryInformation())
                return new SummaryInformation(ps);
            else if (ps.isDocumentSummaryInformation())
                return new DocumentSummaryInformation(ps);
            else
                return ps;
        } catch (UnexpectedPropertySetTypeException ex) {

            throw new Error(ex.toString());
        }
    }



    public static SummaryInformation newSummaryInformation() {
        final MutablePropertySet ps = new MutablePropertySet();
        final MutableSection s = (MutableSection) ps.getFirstSection();
        s.setFormatID(SectionIDMap.SUMMARY_INFORMATION_ID);
        try {
            return new SummaryInformation(ps);
        } catch (UnexpectedPropertySetTypeException ex) {

            throw new HPSFRuntimeException(ex);
        }
    }



    public static DocumentSummaryInformation newDocumentSummaryInformation() {
        final MutablePropertySet ps = new MutablePropertySet();
        final MutableSection s = (MutableSection) ps.getFirstSection();
        s.setFormatID(SectionIDMap.DOCUMENT_SUMMARY_INFORMATION_ID[0]);
        try {
            return new DocumentSummaryInformation(ps);
        } catch (UnexpectedPropertySetTypeException ex) {

            throw new HPSFRuntimeException(ex);
        }
    }

}
