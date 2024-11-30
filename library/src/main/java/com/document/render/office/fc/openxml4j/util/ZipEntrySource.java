
package com.document.render.office.fc.openxml4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;


public interface ZipEntrySource {

    public Enumeration<? extends ZipEntry> getEntries();


    public InputStream getInputStream(ZipEntry entry) throws IOException;


    public void close() throws IOException;
}
