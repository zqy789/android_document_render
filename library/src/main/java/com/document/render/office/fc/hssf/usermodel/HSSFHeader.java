

package com.document.render.office.fc.hssf.usermodel;


import com.document.render.office.fc.hssf.record.HeaderRecord;
import com.document.render.office.fc.hssf.record.aggregates.PageSettingsBlock;
import com.document.render.office.fc.ss.usermodel.Header;



public final class HSSFHeader extends HeaderFooter implements Header {

    private final PageSettingsBlock _psb;

    protected HSSFHeader(PageSettingsBlock psb) {
        _psb = psb;
    }

    protected String getRawText() {
        HeaderRecord hf = _psb.getHeader();
        if (hf == null) {
            return "";
        }
        return hf.getText();
    }

    @Override
    protected void setHeaderFooterText(String text) {
        HeaderRecord hfr = _psb.getHeader();
        if (hfr == null) {
            hfr = new HeaderRecord(text);
            _psb.setHeader(hfr);
        } else {
            hfr.setText(text);
        }
    }
}
