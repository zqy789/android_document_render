

package com.document.render.office.fc.openxml4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class ZipFileZipEntrySource implements ZipEntrySource {
    private ZipFile zipArchive;

    public ZipFileZipEntrySource(ZipFile zipFile) {
        this.zipArchive = zipFile;
    }

    public void close() throws IOException {
        zipArchive.close();
        zipArchive = null;
    }

    public Enumeration<? extends ZipEntry> getEntries() {
        return zipArchive.entries();
    }

    public InputStream getInputStream(ZipEntry entry) throws IOException {
        return zipArchive.getInputStream(entry);
    }
}
