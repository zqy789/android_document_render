
package com.document.render.office.fc;


public abstract class POITextExtractor {
    
    protected POIDocument document;

    
    public POITextExtractor(POIDocument document) {
        this.document = document;
    }

    
    protected POITextExtractor(POITextExtractor otherExtractor) {
        this.document = otherExtractor.document;
    }

    
    public abstract String getText();

    
    public abstract POITextExtractor getMetadataTextExtractor();
}
