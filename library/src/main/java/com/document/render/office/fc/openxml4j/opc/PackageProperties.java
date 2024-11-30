

package com.document.render.office.fc.openxml4j.opc;

import com.document.render.office.fc.openxml4j.util.Nullable;

import java.util.Date;



public interface PackageProperties {

    /**
     * Dublin Core Terms URI.
     */
    public final static String NAMESPACE_DCTERMS = "http://purl.org/dc/terms/";

    /**
     * Dublin Core namespace URI.
     */
    public final static String NAMESPACE_DC = "http://purl.org/dc/elements/1.1/";



    public abstract Nullable<String> getCategoryProperty();


    public abstract void setCategoryProperty(String category);


    public abstract Nullable<String> getContentStatusProperty();


    public abstract void setContentStatusProperty(String contentStatus);


    public abstract Nullable<String> getContentTypeProperty();


    public abstract void setContentTypeProperty(String contentType);


    public abstract Nullable<Date> getCreatedProperty();


    public abstract void setCreatedProperty(String created);


    public abstract void setCreatedProperty(Nullable<Date> created);


    public abstract Nullable<String> getCreatorProperty();


    public abstract void setCreatorProperty(String creator);


    public abstract Nullable<String> getDescriptionProperty();


    public abstract void setDescriptionProperty(String description);


    public abstract Nullable<String> getIdentifierProperty();


    public abstract void setIdentifierProperty(String identifier);


    public abstract Nullable<String> getKeywordsProperty();


    public abstract void setKeywordsProperty(String keywords);


    public abstract Nullable<String> getLanguageProperty();


    public abstract void setLanguageProperty(String language);


    public abstract Nullable<String> getLastModifiedByProperty();


    public abstract void setLastModifiedByProperty(String lastModifiedBy);


    public abstract Nullable<Date> getLastPrintedProperty();


    public abstract void setLastPrintedProperty(String lastPrinted);


    public abstract void setLastPrintedProperty(Nullable<Date> lastPrinted);


    public abstract Nullable<Date> getModifiedProperty();


    public abstract void setModifiedProperty(String modified);


    public abstract void setModifiedProperty(Nullable<Date> modified);


    public abstract Nullable<String> getRevisionProperty();


    public abstract void setRevisionProperty(String revision);


    public abstract Nullable<String> getSubjectProperty();


    public abstract void setSubjectProperty(String subject);


    public abstract Nullable<String> getTitleProperty();


    public abstract void setTitleProperty(String title);


    public abstract Nullable<String> getVersionProperty();


    public abstract void setVersionProperty(String version);
}
