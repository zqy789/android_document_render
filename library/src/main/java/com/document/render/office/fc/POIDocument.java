

package com.document.render.office.fc;

import com.document.render.office.fc.hpsf.DocumentSummaryInformation;
import com.document.render.office.fc.hpsf.MutablePropertySet;
import com.document.render.office.fc.hpsf.PropertySet;
import com.document.render.office.fc.hpsf.PropertySetFactory;
import com.document.render.office.fc.hpsf.SummaryInformation;
import com.document.render.office.fc.poifs.filesystem.DirectoryEntry;
import com.document.render.office.fc.poifs.filesystem.DirectoryNode;
import com.document.render.office.fc.poifs.filesystem.DocumentInputStream;
import com.document.render.office.fc.poifs.filesystem.Entry;
import com.document.render.office.fc.poifs.filesystem.NPOIFSFileSystem;
import com.document.render.office.fc.poifs.filesystem.POIFSFileSystem;
import com.document.render.office.fc.util.Internal;
import com.document.render.office.fc.util.POILogFactory;
import com.document.render.office.fc.util.POILogger;
import com.document.render.office.fc.util.POIUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;



public abstract class POIDocument {

    private final static POILogger logger = POILogFactory.getLogger(POIDocument.class);

    protected DirectoryNode directory;

    private SummaryInformation sInf;

    private DocumentSummaryInformation dsInf;

    private boolean initialized = false;

    protected POIDocument(DirectoryNode dir) {
        this.directory = dir;
    }


    @Deprecated
    protected POIDocument(DirectoryNode dir, POIFSFileSystem fs) {
        this.directory = dir;
    }

    protected POIDocument(POIFSFileSystem fs) {
        this(fs.getRoot());
    }

    protected POIDocument(NPOIFSFileSystem fs) {
        this(fs.getRoot());
    }


    public DocumentSummaryInformation getDocumentSummaryInformation() {
        if (!initialized)
            readProperties();
        return dsInf;
    }


    public SummaryInformation getSummaryInformation() {
        if (!initialized)
            readProperties();
        return sInf;
    }


    public void createInformationProperties() {
        if (!initialized)
            readProperties();
        if (sInf == null) {
            sInf = PropertySetFactory.newSummaryInformation();
        }
        if (dsInf == null) {
            dsInf = PropertySetFactory.newDocumentSummaryInformation();
        }
    }


    protected void readProperties() {
        PropertySet ps;


        ps = getPropertySet(DocumentSummaryInformation.DEFAULT_STREAM_NAME);
        if (ps != null && ps instanceof DocumentSummaryInformation) {
            dsInf = (DocumentSummaryInformation) ps;
        } else if (ps != null) {
            logger.log(POILogger.WARN,
                    "DocumentSummaryInformation property set came back with wrong class - ",
                    ps.getClass());
        }


        ps = getPropertySet(SummaryInformation.DEFAULT_STREAM_NAME);
        if (ps instanceof SummaryInformation) {
            sInf = (SummaryInformation) ps;
        } else if (ps != null) {
            logger.log(POILogger.WARN,
                    "SummaryInformation property set came back with wrong class - ", ps.getClass());
        }


        initialized = true;
    }


    protected PropertySet getPropertySet(String setName) {

        if (directory == null)
            return null;

        DocumentInputStream dis;
        try {

            dis = directory.createDocumentInputStream(directory.getEntry(setName));
        } catch (IOException ie) {

            logger.log(POILogger.WARN, "Error getting property set with name " + setName + "\n"
                    + ie);
            return null;
        }

        try {

            PropertySet set = PropertySetFactory.create(dis);
            return set;
        } catch (IOException ie) {

            logger.log(POILogger.WARN, "Error creating property set with name " + setName + "\n"
                    + ie);
        } catch (com.document.render.office.fc.hpsf.HPSFException he) {

            logger.log(POILogger.WARN, "Error creating property set with name " + setName + "\n"
                    + he);
        }
        return null;
    }


    protected void writeProperties(POIFSFileSystem outFS) throws IOException {
        writeProperties(outFS, null);
    }


    protected void writeProperties(POIFSFileSystem outFS, List<String> writtenEntries)
            throws IOException {
        SummaryInformation si = getSummaryInformation();
        if (si != null) {
            writePropertySet(SummaryInformation.DEFAULT_STREAM_NAME, si, outFS);
            if (writtenEntries != null) {
                writtenEntries.add(SummaryInformation.DEFAULT_STREAM_NAME);
            }
        }
        DocumentSummaryInformation dsi = getDocumentSummaryInformation();
        if (dsi != null) {
            writePropertySet(DocumentSummaryInformation.DEFAULT_STREAM_NAME, dsi, outFS);
            if (writtenEntries != null) {
                writtenEntries.add(DocumentSummaryInformation.DEFAULT_STREAM_NAME);
            }
        }
    }


    protected void writePropertySet(String name, PropertySet set, POIFSFileSystem outFS)
            throws IOException {
        try {
            MutablePropertySet mSet = new MutablePropertySet(set);
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();

            mSet.write(bOut);
            byte[] data = bOut.toByteArray();
            ByteArrayInputStream bIn = new ByteArrayInputStream(data);
            outFS.createDocument(bIn, name);

            logger.log(POILogger.INFO, "Wrote property set " + name + " of size " + data.length);
        } catch (com.document.render.office.fc.hpsf.WritingNotSupportedException wnse) {
            System.err.println("Couldn't write property set with name " + name
                    + " as not supported by HPSF yet");
        }
    }


    public abstract void write(OutputStream out) throws IOException;


    @Deprecated
    protected void copyNodes(POIFSFileSystem source, POIFSFileSystem target, List<String> excepts)
            throws IOException {
        POIUtils.copyNodes(source, target, excepts);
    }


    @Deprecated
    protected void copyNodes(DirectoryNode sourceRoot, DirectoryNode targetRoot,
                             List<String> excepts) throws IOException {
        POIUtils.copyNodes(sourceRoot, targetRoot, excepts);
    }


    @Internal
    @Deprecated
    protected void copyNodeRecursively(Entry entry, DirectoryEntry target) throws IOException {
        POIUtils.copyNodeRecursively(entry, target);
    }
}
