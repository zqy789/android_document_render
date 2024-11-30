

package com.document.render.office.fc.openxml4j.opc.internal;

import com.document.render.office.fc.openxml4j.exceptions.OpenXML4JException;
import com.document.render.office.fc.openxml4j.opc.PackagePart;

import java.io.OutputStream;



public interface PartMarshaller {


    public boolean marshall(PackagePart part, OutputStream out) throws OpenXML4JException;
}
