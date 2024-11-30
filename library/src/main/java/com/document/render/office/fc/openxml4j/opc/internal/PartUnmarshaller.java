

package com.document.render.office.fc.openxml4j.opc.internal;

import com.document.render.office.fc.openxml4j.exceptions.InvalidFormatException;
import com.document.render.office.fc.openxml4j.exceptions.OpenXML4JException;
import com.document.render.office.fc.openxml4j.opc.PackagePart;
import com.document.render.office.fc.openxml4j.opc.internal.unmarshallers.UnmarshallContext;

import java.io.IOException;
import java.io.InputStream;



public interface PartUnmarshaller {


    public PackagePart unmarshall(UnmarshallContext context, InputStream in)
            throws InvalidFormatException, IOException;
}
