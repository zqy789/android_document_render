
package com.document.render.office.fc.doc;

import android.net.Uri;

import com.document.render.bean.DocSourceType;
import com.document.render.office.constant.MainConstant;
import com.document.render.office.constant.wp.WPModelConstant;
import com.document.render.office.simpletext.model.AttrManage;
import com.document.render.office.simpletext.model.IAttributeSet;
import com.document.render.office.simpletext.model.IDocument;
import com.document.render.office.simpletext.model.LeafElement;
import com.document.render.office.simpletext.model.ParagraphElement;
import com.document.render.office.simpletext.model.SectionElement;
import com.document.render.office.system.AbstractReader;
import com.document.render.office.system.IControl;
import com.document.render.office.thirdpart.mozilla.intl.chardet.CharsetDetector;
import com.document.render.office.wp.model.WPDocument;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;



public class TXTReader extends AbstractReader {


    private long offset;

    private String filePath;
    private int docSourceType;

    private String encoding;

    private IDocument wpdoc;



    public TXTReader(IControl control, String filePath, int docSourceType, String encoding) {
        this.control = control;
        this.filePath = filePath;
        this.docSourceType = docSourceType;
        this.encoding = encoding;
    }


    public boolean authenticate(String password) {
        if (encoding != null) {
            return true;
        } else {
            encoding = password;
            if (encoding != null) {
                try {
                    control.actionEvent(MainConstant.HANDLER_MESSAGE_SUCCESS, getModel());
                    return true;
                } catch (Throwable e) {
                    control.getSysKit().getErrorKit().writerLog(e);
                }
            }
        }

        return false;
    }


    public Object getModel() throws Exception {
        if (wpdoc != null) {
            return wpdoc;
        }
        wpdoc = new WPDocument();
        if (encoding == null) {
            encoding = CharsetDetector.detect(control.getActivity().getContentResolver(), filePath);
        }


        readFile();

        return wpdoc;
    }


    public void readFile() throws Exception {

        SectionElement secElem = new SectionElement();

        IAttributeSet attr = secElem.getAttribute();

        AttrManage.instance().setPageWidth(attr, 11906);

        AttrManage.instance().setPageHeight(attr, 16838);

        AttrManage.instance().setPageMarginLeft(attr, 1800);

        AttrManage.instance().setPageMarginRight(attr, 1800);

        AttrManage.instance().setPageMarginTop(attr, 1440);

        AttrManage.instance().setPageMarginBottom(attr, 1440);
        secElem.setStartOffset(offset);


        InputStream is = null;
        switch (docSourceType) {
            case DocSourceType.URL:
                URL url = new URL(filePath);
                is = url.openStream();
                break;
            case DocSourceType.URI:
                Uri uri = Uri.parse(filePath);
                is = control.getActivity().getContentResolver().openInputStream(uri);
                break;
            case DocSourceType.PATH:
                is = new FileInputStream(filePath);
                break;
            case DocSourceType.ASSETS:
                is = control.getActivity().getAssets().open(filePath);
                break;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(is, encoding));
        String line;
        while ((line = br.readLine()) != null || offset == 0) {
            if (abortReader) {
                break;
            }

            line = line == null ? "\n" : line.concat("\n");
            line = line.replace('\t', ' ');
            int len = line.length();
            if (len > 500) {
                int end = 200;
                int start = 0;
                while (end <= len) {
                    String str = line.substring(start, end).concat("\n");

                    ParagraphElement paraElem = new ParagraphElement();
                    paraElem.setStartOffset(offset);
                    LeafElement leafElem = new LeafElement(str);

                    leafElem.setStartOffset(offset);
                    offset += str.length();
                    leafElem.setEndOffset(offset);
                    paraElem.appendLeaf(leafElem);
                    paraElem.setEndOffset(offset);
                    wpdoc.appendParagraph(paraElem, WPModelConstant.MAIN);
                    if (end == len) {
                        break;
                    }
                    start = end;
                    end += 100;
                    if (end > len) {
                        end = len;
                    }
                }
            } else {
                ParagraphElement paraElem = new ParagraphElement();
                paraElem.setStartOffset(offset);
                LeafElement leafElem = new LeafElement(line);

                leafElem.setStartOffset(offset);
                offset += line.length();
                leafElem.setEndOffset(offset);
                paraElem.appendLeaf(leafElem);
                paraElem.setEndOffset(offset);
                wpdoc.appendParagraph(paraElem, WPModelConstant.MAIN);
            }
        }
        br.close();
        secElem.setEndOffset(offset);

        wpdoc.appendSection(secElem);
    }


    public boolean searchContent(File file, String key) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.indexOf(key) > 0) {
                return true;
            }
        }
        return false;
    }


    public void dispose() {
        if (isReaderFinish()) {
            wpdoc = null;
            filePath = null;
            control = null;
        }
    }
}
