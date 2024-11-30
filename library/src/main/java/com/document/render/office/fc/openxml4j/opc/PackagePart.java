

package com.document.render.office.fc.openxml4j.opc;

import com.document.render.office.fc.openxml4j.exceptions.InvalidFormatException;
import com.document.render.office.fc.openxml4j.exceptions.InvalidOperationException;
import com.document.render.office.fc.openxml4j.exceptions.OpenXML4JException;
import com.document.render.office.fc.openxml4j.opc.internal.ContentType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;



public abstract class PackagePart implements RelationshipSource {


    protected ZipPackage _container;


    protected PackagePartName _partName;


    protected ContentType _contentType;


    private boolean _isRelationshipPart;


    private boolean _isDeleted;


    private PackageRelationshipCollection _relationships;


    protected PackagePart(ZipPackage pack, PackagePartName partName, ContentType contentType)
            throws InvalidFormatException {
        this(pack, partName, contentType, true);
    }


    protected PackagePart(ZipPackage pack, PackagePartName partName, ContentType contentType,
                          boolean loadRelationships) throws InvalidFormatException {
        _partName = partName;
        _contentType = contentType;
        _container = pack;


        _isRelationshipPart = this._partName.isRelationshipPartURI();


        if (loadRelationships)
            loadRelationships();
    }


    public PackagePart(ZipPackage pack, PackagePartName partName, String contentType)
            throws InvalidFormatException {
        this(pack, partName, new ContentType(contentType));
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

        if (_relationships == null) {
            _relationships = new PackageRelationshipCollection();
        }

        URI targetURI;
        try {
            targetURI = new URI(target);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid target - " + e);
        }

        return _relationships.addRelationship(targetURI, TargetMode.EXTERNAL, relationshipType, id);
    }


    public PackageRelationship addRelationship(PackagePartName targetPartName,
                                               TargetMode targetMode, String relationshipType) {
        return addRelationship(targetPartName, targetMode, relationshipType, null);
    }


    public PackageRelationship addRelationship(PackagePartName targetPartName,
                                               TargetMode targetMode, String relationshipType, String id) {

        if (targetPartName == null) {
            throw new IllegalArgumentException("targetPartName");
        }
        if (targetMode == null) {
            throw new IllegalArgumentException("targetMode");
        }
        if (relationshipType == null) {
            throw new IllegalArgumentException("relationshipType");
        }

        if (this._isRelationshipPart || targetPartName.isRelationshipPartURI()) {
            throw new InvalidOperationException(
                    "Rule M1.25: The Relationships part shall not have relationships to any other part.");
        }

        if (_relationships == null) {
            _relationships = new PackageRelationshipCollection();
        }

        return _relationships.addRelationship(targetPartName.getURI(), targetMode,
                relationshipType, id);
    }


    public PackageRelationship addRelationship(URI targetURI, TargetMode targetMode,
                                               String relationshipType) {
        return addRelationship(targetURI, targetMode, relationshipType, null);
    }


    public PackageRelationship addRelationship(URI targetURI, TargetMode targetMode,
                                               String relationshipType, String id) {
        if (targetURI == null) {
            throw new IllegalArgumentException("targetPartName");
        }
        if (targetMode == null) {
            throw new IllegalArgumentException("targetMode");
        }
        if (relationshipType == null) {
            throw new IllegalArgumentException("relationshipType");
        }



        if (this._isRelationshipPart || PackagingURIHelper.isRelationshipPartURI(targetURI)) {
            throw new InvalidOperationException(
                    "Rule M1.25: The Relationships part shall not have relationships to any other part.");
        }

        if (_relationships == null) {
            _relationships = new PackageRelationshipCollection();
        }

        return _relationships.addRelationship(targetURI, targetMode, relationshipType, id);
    }


    public void clearRelationships() {
        if (_relationships != null) {
            _relationships.clear();
        }
    }


    public void removeRelationship(String id) {
        if (this._relationships != null) {
            this._relationships.removeRelationship(id);
        }
    }


    public PackageRelationshipCollection getRelationships() throws InvalidFormatException {
        return getRelationshipsCore(null);
    }


    public PackageRelationship getRelationship(String id) {
        return this._relationships.getRelationshipByID(id);
    }


    public PackageRelationshipCollection getRelationshipsByType(String relationshipType)
            throws InvalidFormatException {
        return getRelationshipsCore(relationshipType);
    }


    private PackageRelationshipCollection getRelationshipsCore(String filter)
            throws InvalidFormatException {
        if (_relationships == null) {
            this.throwExceptionIfRelationship();
            _relationships = new PackageRelationshipCollection(this);
        }
        return new PackageRelationshipCollection(_relationships, filter);
    }


    public boolean hasRelationships() {
        return (!this._isRelationshipPart && (_relationships != null && _relationships.size() > 0));
    }


    public boolean isRelationshipExists(PackageRelationship rel) {
        try {
            for (PackageRelationship r : this.getRelationships()) {
                if (r == rel)
                    return true;
            }
        } catch (InvalidFormatException e) {
            ;
        }
        return false;
    }


    public InputStream getInputStream() throws IOException {
        InputStream inStream = this.getInputStreamImpl();
        if (inStream == null) {
            throw new IOException("Can't obtain the input stream from " + _partName.getName());
        }
        return inStream;
    }


    public OutputStream getOutputStream() {
        OutputStream outStream;


        if (this instanceof ZipPackagePart) {

            _container.removePart(this._partName);


            PackagePart part = _container.createPart(this._partName, this._contentType.toString(),
                    false);
            if (part == null) {
                throw new InvalidOperationException("Can't create a temporary part !");
            }
            part._relationships = this._relationships;
            outStream = part.getOutputStreamImpl();
        } else {
            outStream = this.getOutputStreamImpl();
        }
        return outStream;
    }


    private void throwExceptionIfRelationship() throws InvalidOperationException {
        if (this._isRelationshipPart)
            throw new InvalidOperationException("Can do this operation on a relationship part !");
    }


    public void loadRelationships() throws InvalidFormatException {
        if ((this._relationships == null || _relationships.size() == 0)
                && !this._isRelationshipPart) {
            this.throwExceptionIfRelationship();
            _relationships = new PackageRelationshipCollection(this);
        }
    }




    public PackagePartName getPartName() {
        return _partName;
    }


    public String getContentType() {
        return _contentType.toString();
    }


    public void setContentType(String contentType) throws InvalidFormatException {
        if (_container == null)
            this._contentType = new ContentType(contentType);
        else
            throw new InvalidOperationException("You can't change the content type of a part.");
    }

    public ZipPackage getPackage() {
        return _container;
    }


    public boolean isRelationshipPart() {
        return this._isRelationshipPart;
    }


    public boolean isDeleted() {
        return _isDeleted;
    }


    public void setDeleted(boolean isDeleted) {
        this._isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "Name: " + this._partName + " - Content Type: " + this._contentType.toString();
    }




    protected abstract InputStream getInputStreamImpl() throws IOException;


    protected abstract OutputStream getOutputStreamImpl();


    public abstract boolean save(OutputStream zos) throws OpenXML4JException;


    public abstract boolean load(InputStream ios) throws InvalidFormatException;


    public abstract void close();


    public abstract void flush();
}
