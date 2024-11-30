
package com.document.render.office.fc.hssf.usermodel;

import com.document.render.office.fc.hssf.record.ExtendedFormatRecord;
import com.document.render.office.fc.hssf.record.FontRecord;
import com.document.render.office.fc.hssf.record.common.UnicodeString;

import java.util.HashSet;
import java.util.Iterator;



public class HSSFOptimiser {

    public static void optimiseFonts(HSSFWorkbook workbook) {


        short[] newPos =
                new short[workbook.getWorkbook().getNumberOfFontRecords() + 1];
        boolean[] zapRecords = new boolean[newPos.length];
        for (int i = 0; i < newPos.length; i++) {
            newPos[i] = (short) i;
            zapRecords[i] = false;
        }



        FontRecord[] frecs = new FontRecord[newPos.length];
        for (int i = 0; i < newPos.length; i++) {

            if (i == 4) continue;

            frecs[i] = workbook.getWorkbook().getFontRecordAt(i);
        }






        for (int i = 5; i < newPos.length; i++) {


            int earlierDuplicate = -1;
            for (int j = 0; j < i && earlierDuplicate == -1; j++) {
                if (j == 4) continue;

                FontRecord frCheck = workbook.getWorkbook().getFontRecordAt(j);
                if (frCheck.sameProperties(frecs[i])) {
                    earlierDuplicate = j;
                }
            }


            if (earlierDuplicate != -1) {
                newPos[i] = (short) earlierDuplicate;
                zapRecords[i] = true;
            }
        }





        for (int i = 5; i < newPos.length; i++) {


            short preDeletePos = newPos[i];
            short newPosition = preDeletePos;
            for (int j = 0; j < preDeletePos; j++) {
                if (zapRecords[j]) newPosition--;
            }


            newPos[i] = newPosition;
        }


        for (int i = 5; i < newPos.length; i++) {
            if (zapRecords[i]) {
                workbook.getWorkbook().removeFontRecord(
                        frecs[i]
                );
            }
        }



        workbook.resetFontCache();



        for (int i = 0; i < workbook.getWorkbook().getNumExFormats(); i++) {
            ExtendedFormatRecord xfr = workbook.getWorkbook().getExFormatAt(i);
            xfr.setFontIndex(
                    newPos[xfr.getFontIndex()]
            );
        }





        HashSet doneUnicodeStrings = new HashSet();
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            HSSFSheet s = workbook.getSheetAt(sheetNum);
            Iterator rIt = s.rowIterator();
            while (rIt.hasNext()) {
                HSSFRow row = (HSSFRow) rIt.next();
                Iterator cIt = row.cellIterator();
                while (cIt.hasNext()) {
                    HSSFCell cell = (HSSFCell) cIt.next();
                    if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                        HSSFRichTextString rtr = cell.getRichStringCellValue();
                        UnicodeString u = rtr.getRawUnicodeString();


                        if (!doneUnicodeStrings.contains(u)) {

                            for (short i = 5; i < newPos.length; i++) {
                                if (i != newPos[i]) {
                                    u.swapFontUse(i, newPos[i]);
                                }
                            }


                            doneUnicodeStrings.add(u);
                        }
                    }
                }
            }
        }
    }


    public static void optimiseCellStyles(HSSFWorkbook workbook) {


        short[] newPos =
                new short[workbook.getWorkbook().getNumExFormats()];
        boolean[] zapRecords = new boolean[newPos.length];
        for (int i = 0; i < newPos.length; i++) {
            newPos[i] = (short) i;
            zapRecords[i] = false;
        }



        ExtendedFormatRecord[] xfrs = new ExtendedFormatRecord[newPos.length];
        for (int i = 0; i < newPos.length; i++) {
            xfrs[i] = workbook.getWorkbook().getExFormatAt(i);
        }






        for (int i = 21; i < newPos.length; i++) {


            int earlierDuplicate = -1;
            for (int j = 0; j < i && earlierDuplicate == -1; j++) {
                ExtendedFormatRecord xfCheck = workbook.getWorkbook().getExFormatAt(j);
                if (xfCheck.equals(xfrs[i])) {
                    earlierDuplicate = j;
                }
            }


            if (earlierDuplicate != -1) {
                newPos[i] = (short) earlierDuplicate;
                zapRecords[i] = true;
            }
        }





        for (int i = 21; i < newPos.length; i++) {


            short preDeletePos = newPos[i];
            short newPosition = preDeletePos;
            for (int j = 0; j < preDeletePos; j++) {
                if (zapRecords[j]) newPosition--;
            }


            newPos[i] = newPosition;
        }


        for (int i = 21; i < newPos.length; i++) {
            if (zapRecords[i]) {
                workbook.getWorkbook().removeExFormatRecord(
                        xfrs[i]
                );
            }
        }



        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
            HSSFSheet s = workbook.getSheetAt(sheetNum);
            Iterator rIt = s.rowIterator();
            while (rIt.hasNext()) {
                HSSFRow row = (HSSFRow) rIt.next();
                Iterator cIt = row.cellIterator();
                while (cIt.hasNext()) {
                    HSSFCell cell = (HSSFCell) cIt.next();
                    short oldXf = cell.getCellValueRecord().getXFIndex();
                    HSSFCellStyle newStyle = workbook.getCellStyleAt(
                            newPos[oldXf]
                    );
                    cell.setCellStyle(newStyle);
                }
            }
        }
    }
}
