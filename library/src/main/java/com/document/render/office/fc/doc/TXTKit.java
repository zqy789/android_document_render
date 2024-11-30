
package com.document.render.office.fc.doc;

import android.content.ContentResolver;
import android.os.Handler;

import com.document.render.office.common.ICustomDialog;
import com.document.render.office.constant.DialogConstant;
import com.document.render.office.fc.fs.storage.HeaderBlock;
import com.document.render.office.fc.fs.storage.LittleEndian;
import com.document.render.office.fc.util.StreamUtils;
import com.document.render.office.system.FileReaderThread;
import com.document.render.office.system.IControl;
import com.document.render.office.system.IDialogAction;
import com.document.render.office.thirdpart.mozilla.intl.chardet.CharsetDetector;
import com.document.render.office.wp.dialog.TXTEncodingDialog;

import java.io.InputStream;
import java.util.Vector;


public class TXTKit {

    private static TXTKit kit = new TXTKit();


    public static TXTKit instance() {
        return kit;
    }


    public void readText(final IControl control, final Handler handler, final String filePath, final int docSourceType) {
        try {
            ContentResolver resolver = control.getActivity().getContentResolver();
            InputStream in = StreamUtils.getInputStream(resolver, filePath);
            byte[] b = new byte[16];
            in.read(b);
            long signature = LittleEndian.getLong(b, 0);
            if (signature == HeaderBlock._signature
                    || signature == 0x0006001404034b50L)
            {
                in.close();
                control.getSysKit().getErrorKit().writerLog(new Exception("Format error"), true);
                return;
            }
            signature = signature & 0x00FFFFFFFFFFFFFFL;
            if (signature == 0x002e312d46445025L) {
                in.close();
                control.getSysKit().getErrorKit().writerLog(new Exception("Format error"), true);
                return;
            }
            in.close();

            String code = control.isAutoTest() ? "UTF-8" : CharsetDetector.detect(resolver, filePath);
            if (code != null) {
                new FileReaderThread(control, handler, filePath, docSourceType, -1, code).start();
                return;
            }

            if (control.getMainFrame().isShowTXTEncodeDlg()) {
                Vector<Object> vector = new Vector<Object>();
                vector.add(filePath);
                IDialogAction da = new IDialogAction() {

                    public IControl getControl() {
                        return control;
                    }


                    public void doAction(int id, Vector<Object> model) {
                        if (TXTEncodingDialog.BACK_PRESSED.equals(model.get(0))) {
                            control.getMainFrame().getActivity().onBackPressed();
                        } else {
                            new FileReaderThread(control, handler, filePath, docSourceType, -1, model.get(0).toString()).start();
                        }
                    }


                    public void dispose() {

                    }
                };

                new TXTEncodingDialog(control, control.getMainFrame().getActivity(), da,
                        vector, DialogConstant.ENCODING_DIALOG_ID).show();

            } else {
                String encode = control.getMainFrame().getTXTDefaultEncode();
                if (encode == null) {
                    ICustomDialog dlgListener = control.getCustomDialog();
                    if (dlgListener != null) {
                        dlgListener.showDialog(ICustomDialog.DIALOGTYPE_ENCODE);
                    } else {
                        new FileReaderThread(control, handler, filePath, docSourceType, -1, "UTF-8").start();
                    }
                } else {
                    new FileReaderThread(control, handler, filePath, docSourceType, -1, encode).start();
                }
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }


    public void reopenFile(final IControl control, final Handler handler, final String filePath, final int docSourceType, String encode) {
        new FileReaderThread(control, handler, filePath, docSourceType, -1, encode).start();
    }
}
