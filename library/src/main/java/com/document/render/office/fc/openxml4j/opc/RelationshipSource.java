

package com.document.render.office.fc.openxml4j.opc;

import com.document.render.office.fc.openxml4j.exceptions.InvalidFormatException;
import com.document.render.office.fc.openxml4j.exceptions.InvalidOperationException;
import com.document.render.office.fc.openxml4j.exceptions.OpenXML4JException;

public interface RelationshipSource {


    public abstract PackageRelationship addRelationship(PackagePartName targetPartName,
                                                        TargetMode targetMode, String relationshipType);


    public abstract PackageRelationship addRelationship(PackagePartName targetPartName,
                                                        TargetMode targetMode, String relationshipType, String id);


    public PackageRelationship addExternalRelationship(String target, String relationshipType);


    public PackageRelationship addExternalRelationship(String target, String relationshipType,
                                                       String id);


    public abstract void clearRelationships();


    public abstract void removeRelationship(String id);


    public abstract PackageRelationshipCollection getRelationships() throws InvalidFormatException,
            OpenXML4JException;


    public abstract PackageRelationship getRelationship(String id);


    public abstract PackageRelationshipCollection getRelationshipsByType(String relationshipType)
            throws InvalidFormatException, IllegalArgumentException, OpenXML4JException;


    public abstract boolean hasRelationships();


    public abstract boolean isRelationshipExists(PackageRelationship rel);

}
