

package com.document.render.office.fc.openxml4j.opc;

import com.document.render.office.fc.dom4j.Attribute;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.dom4j.ElementHandler;
import com.document.render.office.fc.dom4j.ElementPath;
import com.document.render.office.fc.dom4j.io.SAXReader;
import com.document.render.office.fc.openxml4j.exceptions.InvalidFormatException;
import com.document.render.office.fc.openxml4j.exceptions.InvalidOperationException;
import com.document.render.office.fc.util.POILogFactory;
import com.document.render.office.fc.util.POILogger;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;



public final class PackageRelationshipCollection implements Iterable<PackageRelationship> {

    private static POILogger logger = POILogFactory.getLogger(PackageRelationshipCollection.class);
    boolean fCorePropertiesRelationship;

    private TreeMap<String, PackageRelationship> relationshipsByID;

    private TreeMap<String, PackageRelationship> relationshipsByType;

    private PackagePart relationshipPart;

    private PackagePart sourcePart;

    private PackagePartName partName;

    private ZipPackage container;


    PackageRelationshipCollection() {
        relationshipsByID = new TreeMap<String, PackageRelationship>();
        relationshipsByType = new TreeMap<String, PackageRelationship>();
    }


    public PackageRelationshipCollection(PackageRelationshipCollection coll, String filter) {
        this();
        for (PackageRelationship rel : coll.relationshipsByID.values()) {
            if (filter == null || rel.getRelationshipType().equals(filter)) {
                addRelationship(rel);
            }
        }
    }


    public PackageRelationshipCollection(ZipPackage container) throws InvalidFormatException {
        this(container, null);
    }


    public PackageRelationshipCollection(PackagePart part) throws InvalidFormatException {
        this(part._container, part);
    }


    public PackageRelationshipCollection(ZipPackage container, PackagePart part)
            throws InvalidFormatException {
        this();

        if (container == null)
            throw new IllegalArgumentException("container");


        if (part != null && part.isRelationshipPart())
            throw new IllegalArgumentException("part");

        this.container = container;
        this.sourcePart = part;
        this.partName = getRelationshipPartName(part);
        if (container.containPart(this.partName)) {
            relationshipPart = container.getPart(this.partName);
            parseRelationshipsPart(relationshipPart);
        }
    }


    private static PackagePartName getRelationshipPartName(PackagePart part)
            throws InvalidOperationException {
        PackagePartName partName;
        if (part == null) {
            partName = PackagingURIHelper.PACKAGE_ROOT_PART_NAME;
        } else {
            partName = part.getPartName();
        }
        return PackagingURIHelper.getRelationshipPartName(partName);
    }


    public void addRelationship(PackageRelationship relPart) {
        relationshipsByID.put(relPart.getId(), relPart);
        relationshipsByType.put(relPart.getRelationshipType(), relPart);
    }


    public PackageRelationship addRelationship(URI targetUri, TargetMode targetMode,
                                               String relationshipType, String id) {

        if (id == null) {

            int i = 0;
            do {
                id = "rId" + ++i;
            }
            while (relationshipsByID.get(id) != null);
        }

        PackageRelationship rel = new PackageRelationship(container, sourcePart, targetUri,
                targetMode, relationshipType, id);
        relationshipsByID.put(rel.getId(), rel);
        relationshipsByType.put(rel.getRelationshipType(), rel);
        return rel;
    }


    public void removeRelationship(String id) {
        if (relationshipsByID != null && relationshipsByType != null) {
            PackageRelationship rel = relationshipsByID.get(id);
            if (rel != null) {
                relationshipsByID.remove(rel.getId());
                relationshipsByType.values().remove(rel);
            }
        }
    }


    public void removeRelationship(PackageRelationship rel) {
        if (rel == null)
            throw new IllegalArgumentException("rel");

        relationshipsByID.values().remove(rel);
        relationshipsByType.values().remove(rel);
    }


    public PackageRelationship getRelationship(int index) {
        if (index < 0 || index > relationshipsByID.values().size())
            throw new IllegalArgumentException("index");

        PackageRelationship retRel = null;
        int i = 0;
        for (PackageRelationship rel : relationshipsByID.values()) {
            if (index == i++)
                return rel;
        }
        return retRel;
    }


    public PackageRelationship getRelationshipByID(String id) {
        return relationshipsByID.get(id);
    }


    public int size() {
        return relationshipsByID.values().size();
    }


    private void parseRelationshipsPart(PackagePart relPart) throws InvalidFormatException {
        try {

            fCorePropertiesRelationship = false;

            SAXReader reader = new SAXReader();
            logger.log(POILogger.DEBUG, "Parsing relationship: " + relPart.getPartName());
            InputStream in = relPart.getInputStream();

            SaxHandler saxHandler = new SaxHandler();
            reader.addHandler("/Relationships/Relationship", saxHandler);

            reader.read(in);

            in.close();
        } catch (Exception e) {
            logger.log(POILogger.ERROR, e);
            throw new InvalidFormatException(e.getMessage());
        }
    }


    public PackageRelationshipCollection getRelationships(String typeFilter) {
        PackageRelationshipCollection coll = new PackageRelationshipCollection(this, typeFilter);
        return coll;
    }


    public Iterator<PackageRelationship> iterator() {
        return relationshipsByID.values().iterator();
    }


    public Iterator<PackageRelationship> iterator(String typeFilter) {
        ArrayList<PackageRelationship> retArr = new ArrayList<PackageRelationship>();
        for (PackageRelationship rel : relationshipsByID.values()) {
            if (rel.getRelationshipType().equals(typeFilter)) {
                retArr.add(rel);
            }
        }
        return retArr.iterator();
    }


    public void clear() {
        relationshipsByID.clear();
        relationshipsByType.clear();
    }

    @Override
    public String toString() {
        String str;
        if (relationshipsByID == null) {
            str = "relationshipsByID=null";
        } else {
            str = relationshipsByID.size() + " relationship(s) = [";
        }
        if ((relationshipPart != null) && (relationshipPart._partName != null)) {
            str = str + "," + relationshipPart._partName;
        } else {
            str = str + ",relationshipPart=null";
        }


        if ((sourcePart != null) && (sourcePart._partName != null)) {
            str = str + "," + sourcePart._partName;
        } else {
            str = str + ",sourcePart=null";
        }
        if (partName != null) {
            str = str + "," + partName;
        } else {
            str = str + ",uri=null)";
        }
        return str + "]";
    }



    class SaxHandler implements ElementHandler {


        public void onStart(ElementPath elementPath) {

        }


        public void onEnd(ElementPath elementPath) {
            Element element = elementPath.getCurrent();
            String name = element.getName();
            try {
                if (name.equals("Relationship")) {

                    String id = element.attribute(PackageRelationship.ID_ATTRIBUTE_NAME).getValue();

                    String type = element.attribute(PackageRelationship.TYPE_ATTRIBUTE_NAME).getValue();



                    if (type.equals(PackageRelationshipTypes.CORE_PROPERTIES))
                        if (!fCorePropertiesRelationship)
                            fCorePropertiesRelationship = true;
                        else
                            throw new InvalidFormatException(
                                    "OPC Compliance error [M4.1]: there is more than one core properties relationship in the package !");




                    Attribute targetModeAttr = element
                            .attribute(PackageRelationship.TARGET_MODE_ATTRIBUTE_NAME);
                    TargetMode targetMode = TargetMode.INTERNAL;
                    if (targetModeAttr != null) {
                        targetMode = targetModeAttr.getValue().toLowerCase().equals("internal")
                                ? TargetMode.INTERNAL : TargetMode.EXTERNAL;
                    }


                    URI target;
                    String value = "";
                    try {
                        value = element.attribute(PackageRelationship.TARGET_ATTRIBUTE_NAME).getValue();

                        target = PackagingURIHelper.toURI(value);

                        addRelationship(target, targetMode, type, id);
                    } catch (URISyntaxException e) {
                        logger.log(POILogger.ERROR, "Cannot convert " + value
                                + " in a valid relationship URI-> ignored", e);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            element.detach();
        }

    }
}
