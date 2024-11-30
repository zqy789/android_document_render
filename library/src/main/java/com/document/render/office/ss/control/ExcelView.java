
package com.document.render.office.ss.control;

import android.content.Context;
import android.widget.RelativeLayout;

import com.document.render.office.constant.EventConstant;
import com.document.render.office.ss.model.baseModel.Sheet;
import com.document.render.office.ss.model.baseModel.Workbook;
import com.document.render.office.ss.sheetbar.SheetBar;
import com.document.render.office.ss.view.SheetView;
import com.document.render.office.system.IControl;


public class ExcelView extends RelativeLayout {

    private boolean isDefaultSheetBar = true;

    private Spreadsheet ss;

    private SheetBar bar;

    private IControl control;


    public ExcelView(Context context, String filepath, Workbook book, IControl control) {
        super(context);
        this.control = control;
        ss = new Spreadsheet(context, filepath, book, control, this);

        addView(ss, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }


    public void init() {
        ss.init();
        initSheetbar();
    }


    private void initSheetbar() {
        if (!isDefaultSheetBar) {
            return;
        }
        bar = new SheetBar(getContext(), control, getResources().getDisplayMetrics().widthPixels);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_BOTTOM);
        addView(bar, params);
    }


    public Spreadsheet getSpreadsheet() {
        return ss;
    }


    public void showSheet(int sheetIndex) {
        ss.showSheet(sheetIndex);

        if (isDefaultSheetBar) {
            bar.setFocusSheetButton(sheetIndex);
        } else {
            control.getMainFrame().doActionEvent(EventConstant.SS_CHANGE_SHEET, sheetIndex);
        }
    }


    public void showSheet(String sheetName) {
        ss.showSheet(sheetName);

        Sheet sheet = ss.getWorkbook().getSheet(sheetName);
        if (sheet == null) {
            return;
        }
        int sheetIndex = ss.getWorkbook().getSheetIndex(sheet);
        if (isDefaultSheetBar) {
            bar.setFocusSheetButton(sheetIndex);
        } else {
            control.getMainFrame().doActionEvent(EventConstant.SS_CHANGE_SHEET, sheetIndex);
        }
    }


    public SheetView getSheetView() {
        return ss.getSheetView();
    }


    public void removeSheetBar() {
        isDefaultSheetBar = false;
        removeView(bar);
    }


    public int getBottomBarHeight() {
        if (isDefaultSheetBar) {
            return bar.getHeight();
        } else {
            return control.getMainFrame().getBottomBarHeight();
        }
    }


    public int getCurrentViewIndex() {
        return ss.getCurrentSheetNumber();
    }


    public void dispose() {
        control = null;
        if (ss != null) {
            ss.dispose();
        }
        bar = null;
    }

}
