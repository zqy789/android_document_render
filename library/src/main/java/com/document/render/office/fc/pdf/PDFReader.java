
package com.document.render.office.fc.pdf;

import com.document.render.office.constant.EventConstant;
import com.document.render.office.system.AbstractReader;
import com.document.render.office.system.IControl;


public class PDFReader extends AbstractReader {

    private String filePath;

    private PDFLib lib;





    public PDFReader(IControl control, String filePath) throws Exception {
        this.control = control;
        this.filePath = filePath;
    }


    public Object getModel() throws Exception {
        control.actionEvent(EventConstant.SYS_SET_PROGRESS_BAR_ID, false);

        lib = PDFLib.getPDFLib();
        lib.openFileSync(filePath);

        return lib;
    }


    public void dispose() {
        super.dispose();
        lib = null;
        control = null;
    }





}
