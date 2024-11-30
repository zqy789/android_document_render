
package com.document.render.office.system;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.document.render.bean.FileType;
import com.document.render.office.constant.MainConstant;
import com.document.render.office.fc.doc.DOCReader;
import com.document.render.office.fc.doc.DOCXReader;
import com.document.render.office.fc.doc.TXTReader;
import com.document.render.office.fc.pdf.PDFReader;
import com.document.render.office.fc.ppt.PPTReader;
import com.document.render.office.fc.ppt.PPTXReader;
import com.document.render.office.fc.xls.XLSReader;
import com.document.render.office.fc.xls.XLSXReader;


public class FileReaderThread extends Thread {


    private String encoding;

    private String filePath;
    private int fileType;
    private int docSourceType;

    private Handler handler;

    private IControl control;

    public FileReaderThread(IControl control, Handler handler, String filePath, int docSourceType, int fileType, String encoding) {
        this.control = control;
        this.handler = handler;
        this.filePath = filePath;
        this.fileType = fileType;
        this.docSourceType = docSourceType;
        this.encoding = encoding;
    }


    public void run() {

        Message msg = new Message();
        msg.what = MainConstant.HANDLER_MESSAGE_SHOW_PROGRESS;
        handler.handleMessage(msg);
        msg = new Message();
        msg.what = MainConstant.HANDLER_MESSAGE_DISMISS_PROGRESS;
        try {
            IReader reader = null;
            String fileName = filePath.toLowerCase();

            if (fileName.endsWith(MainConstant.FILE_TYPE_DOC)
                    || fileName.endsWith(MainConstant.FILE_TYPE_DOT)
                    || fileType == FileType.DOC) {
                reader = new DOCReader(control, filePath, docSourceType);
            }

            else if (fileName.endsWith(MainConstant.FILE_TYPE_DOCX)
                    || fileName.endsWith(MainConstant.FILE_TYPE_DOTX)
                    || fileName.endsWith(MainConstant.FILE_TYPE_DOTM)
                    || fileType == FileType.DOCX) {
                reader = new DOCXReader(control, filePath, docSourceType);
            }

            else if (fileName.endsWith(MainConstant.FILE_TYPE_TXT) || fileType == FileType.TXT) {
                reader = new TXTReader(control, filePath, docSourceType, encoding);
            }

            else if (fileName.endsWith(MainConstant.FILE_TYPE_XLS)
                    || fileName.endsWith(MainConstant.FILE_TYPE_XLT)
                    || fileType == FileType.XLS) {
                reader = new XLSReader(control, filePath, docSourceType);
            }

            else if (fileName.endsWith(MainConstant.FILE_TYPE_XLSX)
                    || fileName.endsWith(MainConstant.FILE_TYPE_XLTX)
                    || fileName.endsWith(MainConstant.FILE_TYPE_XLTM)
                    || fileName.endsWith(MainConstant.FILE_TYPE_XLSM)
                    || fileType == FileType.XLSX) {
                reader = new XLSXReader(control, filePath, docSourceType);
            }

            else if (fileName.endsWith(MainConstant.FILE_TYPE_PPT)
                    || fileName.endsWith(MainConstant.FILE_TYPE_POT)
                    || fileType == FileType.PPT) {
                reader = new PPTReader(control, filePath, docSourceType);
            }

            else if (fileName.endsWith(MainConstant.FILE_TYPE_PPTX)
                    || fileName.endsWith(MainConstant.FILE_TYPE_PPTM)
                    || fileName.endsWith(MainConstant.FILE_TYPE_POTX)
                    || fileName.endsWith(MainConstant.FILE_TYPE_POTM)
                    || fileType == FileType.PPTX) {
                reader = new PPTXReader(control, filePath, docSourceType);
            }

            else if (fileName.endsWith(MainConstant.FILE_TYPE_PDF) || fileType == FileType.PDF) {
                reader = new PDFReader(control, filePath);
            }

            else {
                reader = new TXTReader(control, filePath, docSourceType, encoding);
            }
            Log.e(getName(), "reader = " + reader.getClass().getSimpleName());

            Message mesReader = new Message();
            mesReader.obj = reader;
            mesReader.what = MainConstant.HANDLER_MESSAGE_SEND_READER_INSTANCE;
            handler.handleMessage(mesReader);
            msg.obj = reader.getModel();
            reader.dispose();
            msg.what = MainConstant.HANDLER_MESSAGE_SUCCESS;

        } catch (OutOfMemoryError eee) {
            msg.what = MainConstant.HANDLER_MESSAGE_ERROR;
            msg.obj = eee;
        } catch (Exception ee) {
            msg.what = MainConstant.HANDLER_MESSAGE_ERROR;
            msg.obj = ee;
        } catch (AbortReaderError ee) {
            msg.what = MainConstant.HANDLER_MESSAGE_ERROR;
            msg.obj = ee;
        } finally {
            handler.handleMessage(msg);
            control = null;
            handler = null;
            encoding = null;
            filePath = null;
        }
    }
}
