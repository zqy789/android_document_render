

package com.document.render.office.fc.openxml4j.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class ZipInputStreamZipEntrySource implements ZipEntrySource {
    private ArrayList<FakeZipEntry> zipEntries;


    public ZipInputStreamZipEntrySource(ZipInputStream inp) throws IOException {
        zipEntries = new ArrayList<FakeZipEntry>();

        boolean going = true;
        while (going) {
            ZipEntry zipEntry = inp.getNextEntry();
            if (zipEntry == null) {
                going = false;
            } else {
                FakeZipEntry entry = new FakeZipEntry(zipEntry, inp);
                inp.closeEntry();

                zipEntries.add(entry);
            }
        }
        inp.close();
    }

    public Enumeration<? extends ZipEntry> getEntries() {
        return new EntryEnumerator();
    }

    public InputStream getInputStream(ZipEntry zipEntry) {
        FakeZipEntry entry = (FakeZipEntry) zipEntry;
        return entry.getInputStream();
    }

    public void close() {

        zipEntries = null;
    }


    public static class FakeZipEntry extends ZipEntry {
        private byte[] data;

        public FakeZipEntry(ZipEntry entry, ZipInputStream inp) throws IOException {
            super(entry.getName());


            ByteArrayOutputStream baos;

            long entrySize = entry.getSize();

            if (entrySize != -1) {
                if (entrySize >= Integer.MAX_VALUE) {
                    throw new IOException("ZIP entry size is too large");
                }

                baos = new ByteArrayOutputStream((int) entrySize);
            } else {
                baos = new ByteArrayOutputStream();
            }

            byte[] buffer = new byte[4096];
            int read = 0;
            while ((read = inp.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }

            data = baos.toByteArray();
        }

        public InputStream getInputStream() {
            return new ByteArrayInputStream(data);
        }
    }


    private class EntryEnumerator implements Enumeration<ZipEntry> {
        private Iterator<? extends ZipEntry> iterator;

        private EntryEnumerator() {
            iterator = zipEntries.iterator();
        }

        public boolean hasMoreElements() {
            return iterator.hasNext();
        }

        public ZipEntry nextElement() {
            return iterator.next();
        }
    }
}
