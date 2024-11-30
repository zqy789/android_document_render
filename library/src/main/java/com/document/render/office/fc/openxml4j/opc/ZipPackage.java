

package com.document.render.office.fc.openxml4j.opc;

import com.document.render.office.fc.EncryptedDocumentException;
import com.document.render.office.fc.fs.storage.HeaderBlock;
import com.document.render.office.fc.fs.storage.LittleEndian;
import com.document.render.office.fc.openxml4j.exceptions.InvalidFormatException;
import com.document.render.office.fc.openxml4j.exceptions.InvalidOperationException;
import com.document.render.office.fc.openxml4j.exceptions.OpenXML4JException;
import com.document.render.office.fc.openxml4j.exceptions.OpenXML4JRuntimeException;
import com.document.render.office.fc.openxml4j.opc.internal.ContentType;
import com.document.render.office.fc.openxml4j.opc.internal.ContentTypeManager;
import com.document.render.office.fc.openxml4j.opc.internal.FileHelper;
import com.document.render.office.fc.openxml4j.opc.internal.PackagePropertiesPart;
import com.document.render.office.fc.openxml4j.opc.internal.PartMarshaller;
import com.document.render.office.fc.openxml4j.opc.internal.PartUnmarshaller;
import com.document.render.office.fc.openxml4j.opc.internal.ZipHelper;
import com.document.render.office.fc.openxml4j.opc.internal.marshallers.DefaultMarshaller;
import com.document.render.office.fc.openxml4j.opc.internal.marshallers.ZipPackagePropertiesMarshaller;
import com.document.render.office.fc.openxml4j.opc.internal.marshallers.ZipPartMarshaller;
import com.document.render.office.fc.openxml4j.opc.internal.unmarshallers.PackagePropertiesUnmarshaller;
import com.document.render.office.fc.openxml4j.opc.internal.unmarshallers.UnmarshallContext;
import com.document.render.office.fc.openxml4j.util.ZipEntrySource;
import com.document.render.office.fc.openxml4j.util.ZipFileZipEntrySource;
import com.document.render.office.fc.openxml4j.util.ZipInputStreamZipEntrySource;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;



public class ZipPackage implements RelationshipSource, Closeable {

    protected PackagePartCollection partList;

    protected PackageRelationshipCollection relationships;

    protected Hashtable<ContentType, PartMarshaller> partMarshallers;

    protected PartMarshaller defaultPartMarshaller;

    protected Hashtable<ContentType, PartUnmarshaller> partUnmarshallers;

    protected PackagePropertiesPart packageProperties;




    protected ContentTypeManager contentTypeManager;

    protected boolean isDirty = false;

    protected String originalPackagePath;

    protected OutputStream output;

    private ZipEntrySource zipArchive;


    public ZipPackage(String path) {
        if (path == null || "".equals(path.trim())
                || (new File(path).exists() && new File(path).isDirectory())) {
            throw new IllegalArgumentException("path");
        }
        init();
        try {
            ZipFile zipFile = new ZipFile(new File(path));
            zipArchive = new ZipFileZipEntrySource(zipFile);
            getParts();
            originalPackagePath = new File(path).getAbsolutePath();
        } catch (Exception e) {
            File file = new File(path);
            if (file.length() == 0) {
                throw new EncryptedDocumentException("Format error");
            }
            try {
                FileInputStream in = new FileInputStream(file);
                byte[] b = new byte[16];
                in.read(b);

                long signature = LittleEndian.getLong(b, 0);
                if (signature == HeaderBlock._signature) {
                    throw new EncryptedDocumentException("Cannot process encrypted office files!");
                }
            } catch (IOException ioe) {
            }
            throw new EncryptedDocumentException("Invalid header signature");

        }
    }


    public ZipPackage(InputStream in) throws IOException {
        try {
            init();
            zipArchive = new ZipInputStreamZipEntrySource(new ZipInputStream(in));
            getParts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void init() {
        this.partMarshallers = new Hashtable<ContentType, PartMarshaller>(5);
        this.partUnmarshallers = new Hashtable<ContentType, PartUnmarshaller>(2);
        try {

            this.partUnmarshallers.put(new ContentType(ContentTypes.CORE_PROPERTIES_PART),
                    new PackagePropertiesUnmarshaller());

            this.defaultPartMarshaller = new DefaultMarshaller();

            this.partMarshallers.put(new ContentType(ContentTypes.CORE_PROPERTIES_PART),
                    new ZipPackagePropertiesMarshaller());
        } catch (InvalidFormatException e) {

            throw new OpenXML4JRuntimeException(
                    "Package.init() : this exception should never happen, "
                            + "if you read this message please send a mail to the developers team. : "
                            + e.getMessage());
        }
    }


    public void flush() {
        if (this.packageProperties != null) {
            this.packageProperties.flush();
        }
    }


    public void close() throws IOException {

        ReentrantReadWriteLock l = new ReentrantReadWriteLock();
        try {
            l.writeLock().lock();
            if (this.originalPackagePath != null && !"".equals(this.originalPackagePath.trim())) {
                File targetFile = new File(originalPackagePath);
                if (!targetFile.exists() || !(originalPackagePath.equalsIgnoreCase(targetFile.getAbsolutePath()))) {

                    save(targetFile);
                } else {
                    closeImpl();
                }
            } else if (this.output != null) {
                save(this.output);
                output.close();
            }
        } finally {
            l.writeLock().unlock();
        }


        this.contentTypeManager.clearAll();
    }


    public void revert() {
        revertImpl();
    }


    public PackageProperties getPackageProperties() throws InvalidFormatException {

        if (this.packageProperties == null) {
            this.packageProperties = new PackagePropertiesPart(this,
                    PackagingURIHelper.CORE_PROPERTIES_PART_NAME);
        }
        return this.packageProperties;
    }


    public PackagePart getPart(URI partName) {
        if (partName == null) {
            throw new IllegalArgumentException("partName");
        }
        try {
            if (partList == null) {
                getParts();
            }
            return getPartImpl(PackagingURIHelper.createPartName(partName));
        } catch (InvalidFormatException e) {
            return null;
        }
    }


    public PackagePart getPart(PackagePartName partName) {
        if (partName == null) {
            throw new IllegalArgumentException("partName");
        }
        if (partList == null) {
            try {
                getParts();
            } catch (InvalidFormatException e) {
                return null;
            }
        }
        return getPartImpl(partName);
    }


    public ArrayList<PackagePart> getPartsByContentType(String contentType) {
        ArrayList<PackagePart> retArr = new ArrayList<PackagePart>();
        for (PackagePart part : partList.values()) {
            if (part.getContentType().equals(contentType))
                retArr.add(part);
        }
        return retArr;
    }


    public ArrayList<PackagePart> getPartsByRelationshipType(String relationshipType) {
        if (relationshipType == null)
            throw new IllegalArgumentException("relationshipType");
        ArrayList<PackagePart> retArr = new ArrayList<PackagePart>();
        for (PackageRelationship rel : getRelationshipsByType(relationshipType)) {
            retArr.add(getPart(rel));
        }
        return retArr;
    }

    public List<PackagePart> getPartsByName(final Pattern namePattern) {
        if (namePattern == null) {
            throw new IllegalArgumentException("name pattern must not be null");
        }
        ArrayList<PackagePart> result = new ArrayList<PackagePart>();
        for (PackagePart part : partList.values()) {
            PackagePartName partName = part.getPartName();
            String name = partName.getName();
            Matcher matcher = namePattern.matcher(name);
            if (matcher.matches()) {
                result.add(part);
            }
        }
        return result;
    }


    public PackagePart getPart(PackageRelationship partRel) {
        PackagePart retPart = null;
        ensureRelationships();
        for (PackageRelationship rel : relationships) {
            if (rel.getRelationshipType().equals(partRel.getRelationshipType())) {
                try {
                    retPart = getPart(PackagingURIHelper.createPartName(rel.getTargetURI()));
                } catch (InvalidFormatException e) {
                    continue;
                }
                break;
            }
        }
        return retPart;
    }


    public ArrayList<PackagePart> getParts() throws InvalidFormatException {

        if (partList == null) {
            try {
                partList = new PackagePartCollection();
                Enumeration<? extends ZipEntry> entries = this.zipArchive.getEntries();


                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (entry.getName().equalsIgnoreCase(ContentTypeManager.CONTENT_TYPES_PART_NAME)) {
                        InputStream in = zipArchive.getInputStream(entry);
                        contentTypeManager = new ContentTypeManager(in, this);
                        in.close();
                        break;
                    }
                }

                entries = this.zipArchive.getEntries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    PackagePartName partName = buildPartName(entry);
                    if (partName == null) {
                        continue;
                    }

                    String contentType = contentTypeManager.getContentType(partName);
                    if (contentType != null) {
                        PackagePart part = new ZipPackagePart(this, entry, partName, contentType);

                        if (contentType.equals(ContentTypes.CORE_PROPERTIES_PART)) {
                            PartUnmarshaller partUnmarshaller = partUnmarshallers.get(contentType);
                            if (partUnmarshaller != null) {
                                UnmarshallContext context = new UnmarshallContext(this, part._partName);
                                PackagePart unmarshallPart = partUnmarshaller.unmarshall(context, part.getInputStream());
                                partList.put(unmarshallPart._partName, unmarshallPart);

                                if (unmarshallPart instanceof PackagePropertiesPart) {
                                    packageProperties = (PackagePropertiesPart) unmarshallPart;
                                }
                            }
                        } else {
                            partList.put(partName, part);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ArrayList<PackagePart> list = new ArrayList<PackagePart>(partList.values());
        for (PackagePart part : list) {
            part.loadRelationships();
        }
        return list;
    }


    public PackagePart createPart(PackagePartName partName, String contentType) {
        return createPart(partName, contentType, true);
    }


    protected PackagePart createPart(PackagePartName partName, String contentType, boolean loadRelationships) {
        if (partName == null) {
            throw new IllegalArgumentException("partName");
        }
        if (contentType == null || contentType.equals("")) {
            throw new IllegalArgumentException("contentType");
        }


        if (partList.containsKey(partName) && !partList.get(partName).isDeleted()) {
            throw new InvalidOperationException(
                    "A part with the name '"
                            + partName.getName()
                            + "' already exists : Packages shall not contain equivalent part names and package implementers shall neither create nor recognize packages with equivalent part names. [M1.12]");
        }









        if (contentType.equals(ContentTypes.CORE_PROPERTIES_PART)) {
            if (this.packageProperties != null)
                throw new InvalidOperationException(
                        "OPC Compliance error [M4.1]: you try to add more than one core properties relationship in the package !");
        }



        PackagePart part = this.createPartImpl(partName, contentType, loadRelationships);
        this.contentTypeManager.addContentType(partName, contentType);
        this.partList.put(partName, part);
        this.isDirty = true;
        return part;
    }


    public PackagePart createPart(PackagePartName partName, String contentType,
                                  ByteArrayOutputStream content) {
        PackagePart addedPart = this.createPart(partName, contentType);
        if (addedPart == null) {
            return null;
        }

        if (content != null) {
            try {
                OutputStream partOutput = addedPart.getOutputStream();
                if (partOutput == null) {
                    return null;
                }

                partOutput.write(content.toByteArray(), 0, content.size());
                partOutput.close();

            } catch (IOException ioe) {
                return null;
            }
        } else {
            return null;
        }
        return addedPart;
    }


    protected PackagePart addPackagePart(PackagePart part) {
        if (part == null) {
            throw new IllegalArgumentException("part");
        }

        if (partList.containsKey(part._partName)) {
            if (!partList.get(part._partName).isDeleted()) {
                throw new InvalidOperationException(
                        "A part with the name '"
                                + part._partName.getName()
                                + "' already exists : Packages shall not contain equivalent part names and package implementers shall neither create nor recognize packages with equivalent part names. [M1.12]");
            }


            part.setDeleted(false);

            this.partList.remove(part._partName);
        }
        this.partList.put(part._partName, part);
        this.isDirty = true;
        return part;
    }


    public void removePart(PackagePart part) {
        if (part != null) {
            removePart(part.getPartName());
        }
    }


    public void removePart(PackagePartName partName) {
        if (partName == null || !this.containPart(partName))
            throw new IllegalArgumentException("partName");


        if (this.partList.containsKey(partName)) {
            this.partList.get(partName).setDeleted(true);
            this.partList.remove(partName);
        }

        this.contentTypeManager.removeContentType(partName);



        if (partName.isRelationshipPartURI()) {
            URI sourceURI = PackagingURIHelper.getSourcePartUriFromRelationshipPartUri(partName
                    .getURI());
            PackagePartName sourcePartName;
            try {
                sourcePartName = PackagingURIHelper.createPartName(sourceURI);
            } catch (InvalidFormatException e) {
                return;
            }
            if (sourcePartName.getURI().equals(PackagingURIHelper.PACKAGE_ROOT_URI)) {
                clearRelationships();
            } else if (containPart(sourcePartName)) {
                PackagePart part = getPart(sourcePartName);
                if (part != null)
                    part.clearRelationships();
            }
        }

        this.isDirty = true;
    }


    public void removePartRecursive(PackagePartName partName) throws InvalidFormatException {

        PackagePart relPart = this.partList.get(PackagingURIHelper.getRelationshipPartName(partName));

        PackagePart partToRemove = this.partList.get(partName);
        if (relPart != null) {
            PackageRelationshipCollection partRels = new PackageRelationshipCollection(partToRemove);
            for (PackageRelationship rel : partRels) {
                PackagePartName partNameToRemove = PackagingURIHelper
                        .createPartName(PackagingURIHelper.resolvePartUri(rel.getSourceURI(),
                                rel.getTargetURI()));
                removePart(partNameToRemove);
            }


            this.removePart(relPart._partName);
        }


        this.removePart(partToRemove._partName);
    }


    public void deletePart(PackagePartName partName) {
        if (partName == null)
            throw new IllegalArgumentException("partName");


        this.removePart(partName);

        this.removePart(PackagingURIHelper.getRelationshipPartName(partName));
    }


    public void deletePartRecursive(PackagePartName partName) {
        if (partName == null || !this.containPart(partName)) {
            throw new IllegalArgumentException("partName");
        }
        PackagePart partToDelete = this.getPart(partName);

        this.removePart(partName);

        try {
            for (PackageRelationship relationship : partToDelete.getRelationships()) {
                PackagePartName targetPartName = PackagingURIHelper
                        .createPartName(PackagingURIHelper.resolvePartUri(partName.getURI(),
                                relationship.getTargetURI()));
                this.deletePartRecursive(targetPartName);
            }
        } catch (InvalidFormatException e) {
            return;
        }

        PackagePartName relationshipPartName = PackagingURIHelper.getRelationshipPartName(partName);
        if (relationshipPartName != null && containPart(relationshipPartName))
            this.removePart(relationshipPartName);
    }


    public boolean containPart(PackagePartName partName) {
        return (this.getPart(partName) != null);
    }


    public PackageRelationship addRelationship(PackagePartName targetPartName,
                                               TargetMode targetMode, String relationshipType, String relID) {








        if (relationshipType.equals(PackageRelationshipTypes.CORE_PROPERTIES)
                && this.packageProperties != null)
            throw new InvalidOperationException(
                    "OPC Compliance error [M4.1]: can't add another core properties part ! Use the built-in package method instead.");


        if (targetPartName.isRelationshipPartURI()) {
            throw new InvalidOperationException(
                    "Rule M1.25: The Relationships part shall not have relationships to any other part.");
        }



        ensureRelationships();
        PackageRelationship retRel = relationships.addRelationship(targetPartName.getURI(),
                targetMode, relationshipType, relID);
        this.isDirty = true;
        return retRel;
    }


    public PackageRelationship addRelationship(PackagePartName targetPartName,
                                               TargetMode targetMode, String relationshipType) {
        return this.addRelationship(targetPartName, targetMode, relationshipType, null);
    }


    public PackageRelationship addExternalRelationship(String target, String relationshipType) {
        return addExternalRelationship(target, relationshipType, null);
    }


    public PackageRelationship addExternalRelationship(String target, String relationshipType,
                                                       String id) {
        if (target == null) {
            throw new IllegalArgumentException("target");
        }
        if (relationshipType == null) {
            throw new IllegalArgumentException("relationshipType");
        }
        URI targetURI;
        try {
            targetURI = new URI(target);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid target - " + e);
        }
        ensureRelationships();
        PackageRelationship retRel = relationships.addRelationship(targetURI, TargetMode.EXTERNAL,
                relationshipType, id);
        this.isDirty = true;
        return retRel;
    }


    public void removeRelationship(String id) {
        if (relationships != null) {
            relationships.removeRelationship(id);
            this.isDirty = true;
        }
    }


    public PackageRelationshipCollection getRelationships() {
        return getRelationshipsHelper(null);
    }


    public PackageRelationshipCollection getRelationshipsByType(String relationshipType) {
        if (relationshipType == null) {
            throw new IllegalArgumentException("relationshipType");
        }
        return getRelationshipsHelper(relationshipType);
    }


    private PackageRelationshipCollection getRelationshipsHelper(String id) {
        ensureRelationships();
        return this.relationships.getRelationships(id);
    }


    public void clearRelationships() {
        if (relationships != null) {
            relationships.clear();
            this.isDirty = true;
        }
    }


    public void ensureRelationships() {
        if (this.relationships == null) {
            try {
                this.relationships = new PackageRelationshipCollection(this);
            } catch (InvalidFormatException e) {
                this.relationships = new PackageRelationshipCollection();
            }
        }
    }


    public PackageRelationship getRelationship(String id) {
        return this.relationships.getRelationshipByID(id);
    }


    public boolean hasRelationships() {
        return (relationships.size() > 0);
    }


    public boolean isRelationshipExists(PackageRelationship rel) {
        for (PackageRelationship r : this.getRelationships()) {
            if (r == rel)
                return true;
        }
        return false;
    }


    public void addMarshaller(String contentType, PartMarshaller marshaller) {
        try {
            partMarshallers.put(new ContentType(contentType), marshaller);
        } catch (InvalidFormatException e) {
        }
    }


    public void addUnmarshaller(String contentType, PartUnmarshaller unmarshaller) {
        try {
            partUnmarshallers.put(new ContentType(contentType), unmarshaller);
        } catch (InvalidFormatException e) {
        }
    }


    public void removeMarshaller(String contentType) {
        partMarshallers.remove(contentType);
    }


    public void removeUnmarshaller(String contentType) {
        partUnmarshallers.remove(contentType);
    }


    public boolean validatePackage(ZipPackage pkg) throws InvalidFormatException {
        throw new InvalidOperationException("Not implemented yet !!!");
    }


    public void save(File targetFile) throws IOException {
        if (targetFile == null) {
            throw new IllegalArgumentException("targetFile");
        }


        if (targetFile.exists() && targetFile.getAbsolutePath().equals(this.originalPackagePath)) {
            throw new InvalidOperationException(
                    "You can't call save(File) to save to the currently open "
                            + "file. To save to the current file, please just call close()");
        }


        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(targetFile);
        } catch (FileNotFoundException e) {
            throw new IOException(e.getLocalizedMessage());
        }
        this.save(fos);
        fos.close();
    }


    public void save(OutputStream outputStream) throws IOException {
        this.saveImpl(outputStream);
    }


    private PackagePartName buildPartName(ZipEntry entry) {
        try {


            if (entry.getName().equalsIgnoreCase(ContentTypeManager.CONTENT_TYPES_PART_NAME)) {
                return null;
            }
            return PackagingURIHelper.createPartName(ZipHelper.getOPCNameFromZipItemName(entry
                    .getName()));
        } catch (Exception e) {
            return null;
        }
    }


    protected PackagePart createPartImpl(PackagePartName partName, String contentType,
                                         boolean loadRelationships) {

        return null;
    }


    protected void closeImpl() throws IOException {

        flush();


        if (this.originalPackagePath != null && !"".equals(this.originalPackagePath)) {
            File targetFile = new File(this.originalPackagePath);
            if (targetFile.exists()) {


                File tempFile = File.createTempFile(
                        generateTempFileName(FileHelper.getDirectory(targetFile)), ".tmp");


                try {
                    save(tempFile);



                    this.zipArchive.close();

                    FileHelper.copyFile(tempFile, targetFile);
                } finally {


                    if (!tempFile.delete()) {
                    }
                }
            } else {
                throw new InvalidOperationException(
                        "Can't close a package not previously open with the open() method !");
            }
        }
    }


    private synchronized String generateTempFileName(File directory) {
        File tmpFilename;
        do {
            tmpFilename = new File(directory.getAbsoluteFile() + File.separator + "OpenXML4J"
                    + System.nanoTime());
        }
        while (tmpFilename.exists());
        return FileHelper.getFilename(tmpFilename.getAbsoluteFile());
    }


    protected void revertImpl() {
        try {
            if (this.zipArchive != null)
                this.zipArchive.close();
        } catch (IOException e) {

        }
    }


    protected PackagePart getPartImpl(PackagePartName partName) {
        if (partList.containsKey(partName)) {
            return partList.get(partName);
        }
        return null;
    }


    public void saveImpl(OutputStream outputStream) {

        ZipOutputStream zos = null;

        try {
            if (!(outputStream instanceof ZipOutputStream))
                zos = new ZipOutputStream(outputStream);
            else
                zos = (ZipOutputStream) outputStream;



            if (this.getPartsByRelationshipType(PackageRelationshipTypes.CORE_PROPERTIES).size() == 0
                    && getPartsByRelationshipType(PackageRelationshipTypes.CORE_PROPERTIES_ECMA376).size() == 0) {


                new ZipPackagePropertiesMarshaller().marshall(this.packageProperties, zos);

                this.relationships.addRelationship(this.packageProperties.getPartName().getURI(),
                        TargetMode.INTERNAL, PackageRelationshipTypes.CORE_PROPERTIES, null);

                if (!this.contentTypeManager
                        .isContentTypeRegister(ContentTypes.CORE_PROPERTIES_PART)) {
                    this.contentTypeManager.addContentType(this.packageProperties.getPartName(),
                            ContentTypes.CORE_PROPERTIES_PART);
                }
            }


            ZipPartMarshaller.marshallRelationshipPart(this.getRelationships(),
                    PackagingURIHelper.PACKAGE_RELATIONSHIPS_ROOT_PART_NAME, zos);

            contentTypeManager.save(zos);


            for (PackagePart part : getParts()) {


                if (part.isRelationshipPart()) {
                    continue;
                }
                PartMarshaller marshaller = partMarshallers.get(part._contentType);
                if (marshaller != null) {
                    if (!marshaller.marshall(part, zos)) {
                        throw new OpenXML4JException("The part " + part.getPartName().getURI()
                                + " fail to be saved in the stream with marshaller " + marshaller);
                    }
                } else {
                    if (!defaultPartMarshaller.marshall(part, zos))
                        throw new OpenXML4JException("The part " + part.getPartName().getURI()
                                + " fail to be saved in the stream with marshaller "
                                + defaultPartMarshaller);
                }
            }
            zos.close();
        } catch (Exception e) {
            throw new OpenXML4JRuntimeException(
                    "Fail to save: an error occurs while saving the package : " + e.getMessage(), e);
        }
    }


    public ZipEntrySource getZipArchive() {
        return zipArchive;
    }
}
