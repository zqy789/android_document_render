

package com.document.render.office.fc.hssf.usermodel;


import com.document.render.office.fc.hssf.record.FooterRecord;
import com.document.render.office.fc.hssf.record.aggregates.PageSettingsBlock;
import com.document.render.office.fc.ss.usermodel.Footer;



public final class HSSFFooter extends HeaderFooter implements Footer {
    private final PageSettingsBlock _psb;

    protected HSSFFooter(PageSettingsBlock psb) {
        _psb = psb;
    }

    protected String getRawText() {
        FooterRecord hf = _psb.getFooter();
        if (hf == null) {
            return "";
        }
        return hf.getText();
    }

    @Override
    protected void setHeaderFooterText(String text) {
        FooterRecord hfr = _psb.getFooter();
        if (hfr == null) {
            hfr = new FooterRecord(text);
            _psb.setFooter(hfr);
        } else {
            hfr.setText(text);
        }
    }
}
