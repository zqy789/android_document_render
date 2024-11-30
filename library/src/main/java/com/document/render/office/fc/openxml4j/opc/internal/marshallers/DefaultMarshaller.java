

package com.document.render.office.fc.openxml4j.opc.internal.marshallers;

import com.document.render.office.fc.openxml4j.exceptions.OpenXML4JException;
import com.document.render.office.fc.openxml4j.opc.PackagePart;
import com.document.render.office.fc.openxml4j.opc.internal.PartMarshaller;

import java.io.OutputStream;



public final class DefaultMarshaller implements PartMarshaller {


    public boolean marshall(PackagePart part, OutputStream out) throws OpenXML4JException {
        return part.save(out);
    }
}
