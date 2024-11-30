

package com.document.render.office.fc.hssf.eventusermodel;

import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.RecordFactoryInputStream;
import com.document.render.office.fc.poifs.filesystem.DirectoryNode;
import com.document.render.office.fc.poifs.filesystem.POIFSFileSystem;

import java.io.IOException;
import java.io.InputStream;



public class HSSFEventFactory {

    public HSSFEventFactory() {

    }


    public void processWorkbookEvents(HSSFRequest req, POIFSFileSystem fs) throws IOException {
        processWorkbookEvents(req, fs.getRoot());
    }


    public void processWorkbookEvents(HSSFRequest req, DirectoryNode dir) throws IOException {
        InputStream in = dir.createDocumentInputStream("Workbook");

        processEvents(req, in);
    }


    public short abortableProcessWorkbookEvents(HSSFRequest req, POIFSFileSystem fs)
            throws IOException, HSSFUserException {
        return abortableProcessWorkbookEvents(req, fs.getRoot());
    }


    public short abortableProcessWorkbookEvents(HSSFRequest req, DirectoryNode dir)
            throws IOException, HSSFUserException {
        InputStream in = dir.createDocumentInputStream("Workbook");
        return abortableProcessEvents(req, in);
    }


    public void processEvents(HSSFRequest req, InputStream in) {
        try {
            genericProcessEvents(req, in);
        } catch (HSSFUserException hue) {

        }
    }



    public short abortableProcessEvents(HSSFRequest req, InputStream in)
            throws HSSFUserException {
        return genericProcessEvents(req, in);
    }


    private short genericProcessEvents(HSSFRequest req, InputStream in)
            throws HSSFUserException {
        short userCode = 0;


        RecordFactoryInputStream recordStream = new RecordFactoryInputStream(in, false);


        while (true) {
            Record r = recordStream.nextRecord();
            if (r == null) {
                break;
            }
            userCode = req.processRecord(r);
            if (userCode != 0) {
                break;
            }
        }


        return userCode;
    }
}
