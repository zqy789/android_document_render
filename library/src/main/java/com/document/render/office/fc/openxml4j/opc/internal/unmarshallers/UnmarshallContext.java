

package com.document.render.office.fc.openxml4j.opc.internal.unmarshallers;

import com.document.render.office.fc.openxml4j.opc.PackagePartName;
import com.document.render.office.fc.openxml4j.opc.ZipPackage;

import java.util.zip.ZipEntry;



public final class UnmarshallContext {

    private ZipPackage _package;

    private PackagePartName partName;

    private ZipEntry zipEntry;

    
    public UnmarshallContext(ZipPackage targetPackage, PackagePartName partName) {
        this._package = targetPackage;
        this.partName = partName;
    }

    
    public ZipPackage getPackage() {
        return _package;
    }

    
    public void setPackage(ZipPackage container) {
        this._package = container;
    }

    
    PackagePartName getPartName() {
        return partName;
    }

    
    public void setPartName(PackagePartName partName) {
        this.partName = partName;
    }

    
    ZipEntry getZipEntry() {
        return zipEntry;
    }

    
    public void setZipEntry(ZipEntry zipEntry) {
        this.zipEntry = zipEntry;
    }
}
