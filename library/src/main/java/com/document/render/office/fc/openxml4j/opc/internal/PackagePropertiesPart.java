

package com.document.render.office.fc.openxml4j.opc.internal;

import com.document.render.office.fc.openxml4j.exceptions.InvalidFormatException;
import com.document.render.office.fc.openxml4j.exceptions.InvalidOperationException;
import com.document.render.office.fc.openxml4j.opc.ContentTypes;
import com.document.render.office.fc.openxml4j.opc.PackagePart;
import com.document.render.office.fc.openxml4j.opc.PackagePartName;
import com.document.render.office.fc.openxml4j.opc.PackageProperties;
import com.document.render.office.fc.openxml4j.opc.ZipPackage;
import com.document.render.office.fc.openxml4j.util.Nullable;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;



public final class PackagePropertiesPart extends PackagePart implements PackageProperties {

    public final static String NAMESPACE_DC_URI = "http://purl.org/dc/elements/1.1/";

    public final static String NAMESPACE_CP_URI = "http://schemas.openxmlformats.org/package/2006/metadata/core-properties";

    public final static String NAMESPACE_DCTERMS_URI = "http://purl.org/dc/terms/";

    public final static String NAMESPACE_XSI_URI = "http://www.w3.org/2001/XMLSchema-instance";
    /**
     * A categorization of the content of this package.
     * <p>
     * [Example: Example values for this property might include: Resume, Letter,
     * Financial Forecast, Proposal, Technical Presentation, and so on. This
     * value might be used by an application's user interface to facilitate
     * navigation of a large set of documents. end example]
     */
    protected Nullable<String> category = new Nullable<String>();

    protected Nullable<String> contentStatus = new Nullable<String>();

    protected Nullable<String> contentType = new Nullable<String>();

    protected Nullable<Date> created = new Nullable<Date>();

    protected Nullable<String> creator = new Nullable<String>();

    protected Nullable<String> description = new Nullable<String>();

    protected Nullable<String> identifier = new Nullable<String>();

    protected Nullable<String> keywords = new Nullable<String>();

    protected Nullable<String> language = new Nullable<String>();

    protected Nullable<String> lastModifiedBy = new Nullable<String>();

    protected Nullable<Date> lastPrinted = new Nullable<Date>();

    protected Nullable<Date> modified = new Nullable<Date>();

    protected Nullable<String> revision = new Nullable<String>();

    protected Nullable<String> subject = new Nullable<String>();

    protected Nullable<String> title = new Nullable<String>();

    protected Nullable<String> version = new Nullable<String>();


    public PackagePropertiesPart(ZipPackage pack, PackagePartName partName)
            throws InvalidFormatException {
        super(pack, partName, ContentTypes.CORE_PROPERTIES_PART);
    }




    public Nullable<String> getCategoryProperty() {
        return category;
    }


    public void setCategoryProperty(String category) {
        this.category = setStringValue(category);
    }


    public Nullable<String> getContentStatusProperty() {
        return contentStatus;
    }


    public void setContentStatusProperty(String contentStatus) {
        this.contentStatus = setStringValue(contentStatus);
    }


    public Nullable<String> getContentTypeProperty() {
        return contentType;
    }


    public void setContentTypeProperty(String contentType) {
        this.contentType = setStringValue(contentType);
    }


    public Nullable<Date> getCreatedProperty() {
        return created;
    }


    public void setCreatedProperty(String created) {
        try {
            this.created = setDateValue(created);
        } catch (InvalidFormatException e) {
            new IllegalArgumentException("created  : " + e.getLocalizedMessage());
        }
    }


    public void setCreatedProperty(Nullable<Date> created) {
        if (created.hasValue())
            this.created = created;
    }


    public String getCreatedPropertyString() {
        return getDateValue(created);
    }


    public Nullable<String> getCreatorProperty() {
        return creator;
    }


    public void setCreatorProperty(String creator) {
        this.creator = setStringValue(creator);
    }


    public Nullable<String> getDescriptionProperty() {
        return description;
    }


    public void setDescriptionProperty(String description) {
        this.description = setStringValue(description);
    }


    public Nullable<String> getIdentifierProperty() {
        return identifier;
    }


    public void setIdentifierProperty(String identifier) {
        this.identifier = setStringValue(identifier);
    }


    public Nullable<String> getKeywordsProperty() {
        return keywords;
    }


    public void setKeywordsProperty(String keywords) {
        this.keywords = setStringValue(keywords);
    }


    public Nullable<String> getLanguageProperty() {
        return language;
    }


    public void setLanguageProperty(String language) {
        this.language = setStringValue(language);
    }


    public Nullable<String> getLastModifiedByProperty() {
        return lastModifiedBy;
    }


    public void setLastModifiedByProperty(String lastModifiedBy) {
        this.lastModifiedBy = setStringValue(lastModifiedBy);
    }


    public Nullable<Date> getLastPrintedProperty() {
        return lastPrinted;
    }


    public void setLastPrintedProperty(String lastPrinted) {
        try {
            this.lastPrinted = setDateValue(lastPrinted);
        } catch (InvalidFormatException e) {
            new IllegalArgumentException("lastPrinted  : " + e.getLocalizedMessage());
        }
    }


    public void setLastPrintedProperty(Nullable<Date> lastPrinted) {
        if (lastPrinted.hasValue())
            this.lastPrinted = lastPrinted;
    }


    public String getLastPrintedPropertyString() {
        return getDateValue(lastPrinted);
    }


    public Nullable<Date> getModifiedProperty() {
        return modified;
    }


    public void setModifiedProperty(String modified) {
        try {
            this.modified = setDateValue(modified);
        } catch (InvalidFormatException e) {
            new IllegalArgumentException("modified  : " + e.getLocalizedMessage());
        }
    }


    public void setModifiedProperty(Nullable<Date> modified) {
        if (modified.hasValue())
            this.modified = modified;
    }


    public String getModifiedPropertyString() {
        if (modified.hasValue()) {
            return getDateValue(modified);
        }
        return getDateValue(new Nullable<Date>(new Date()));
    }


    public Nullable<String> getRevisionProperty() {
        return revision;
    }


    public void setRevisionProperty(String revision) {
        this.revision = setStringValue(revision);
    }


    public Nullable<String> getSubjectProperty() {
        return subject;
    }


    public void setSubjectProperty(String subject) {
        this.subject = setStringValue(subject);
    }


    public Nullable<String> getTitleProperty() {
        return title;
    }


    public void setTitleProperty(String title) {
        this.title = setStringValue(title);
    }


    public Nullable<String> getVersionProperty() {
        return version;
    }


    public void setVersionProperty(String version) {
        this.version = setStringValue(version);
    }


    private Nullable<String> setStringValue(String s) {
        if (s == null || s.equals("")) {
            return new Nullable<String>();
        }
        return new Nullable<String>(s);
    }


    private Nullable<Date> setDateValue(String s) throws InvalidFormatException {
        if (s == null || s.equals("")) {
            return new Nullable<Date>();
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date d = df.parse(s, new ParsePosition(0));
        if (d == null) {
            throw new InvalidFormatException("Date not well formated");
        }
        return new Nullable<Date>(d);
    }


    private String getDateValue(Nullable<Date> d) {
        if (d == null) {
            return "";
        }
        Date date = d.getValue();
        if (date == null) {
            return "";
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(date);
    }

    @Override
    protected InputStream getInputStreamImpl() {
        throw new InvalidOperationException(
                "Operation not authorized. This part may only be manipulated using the getters and setters on PackagePropertiesPart");
    }

    @Override
    protected OutputStream getOutputStreamImpl() {
        throw new InvalidOperationException("Can't use output stream to set properties !");
    }

    @Override
    public boolean save(OutputStream zos) {
        throw new InvalidOperationException(
                "Operation not authorized. This part may only be manipulated using the getters and setters on PackagePropertiesPart");
    }

    @Override
    public boolean load(InputStream ios) {
        throw new InvalidOperationException(
                "Operation not authorized. This part may only be manipulated using the getters and setters on PackagePropertiesPart");
    }

    @Override
    public void close() {

    }

    @Override
    public void flush() {

    }
}
