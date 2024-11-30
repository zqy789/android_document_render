

package com.document.render.office.fc.util;


import com.document.render.office.fc.hssf.usermodel.HSSFSheet;
import com.document.render.office.fc.hssf.usermodel.HSSFWorkbook;
import com.document.render.office.fc.poifs.filesystem.POIFSFileSystem;

import java.io.FileInputStream;
import java.io.IOException;


public class DrawingDump {
    public static void main(String[] args) throws IOException {
        POIFSFileSystem fs =
                new POIFSFileSystem(new FileInputStream(args[0]));
        HSSFWorkbook wb = new HSSFWorkbook(fs);
        System.out.println("Drawing group:");
        wb.dumpDrawingGroupRecords(true);

        for (int sheetNum = 1; sheetNum <= wb.getNumberOfSheets(); sheetNum++) {
            System.out.println("Sheet " + sheetNum + ":");
            HSSFSheet sheet = wb.getSheetAt(sheetNum - 1);
            sheet.dumpDrawingRecords(true);
        }

    }
}
