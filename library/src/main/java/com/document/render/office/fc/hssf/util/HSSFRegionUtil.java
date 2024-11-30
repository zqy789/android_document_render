

package com.document.render.office.fc.hssf.util;


import com.document.render.office.fc.hssf.usermodel.HSSFSheet;
import com.document.render.office.fc.hssf.usermodel.HSSFWorkbook;
import com.document.render.office.fc.ss.util.HSSFCellRangeAddress;
import com.document.render.office.fc.ss.util.Region;
import com.document.render.office.fc.ss.util.RegionUtil;



public final class HSSFRegionUtil {

    private HSSFRegionUtil() {

    }

    private static HSSFCellRangeAddress toCRA(Region region) {
        return Region.convertToCellRangeAddress(region);
    }


    public static void setBorderLeft(short border, Region region, HSSFSheet sheet,
                                     HSSFWorkbook workbook) {
        setBorderLeft(border, toCRA(region), sheet, workbook);
    }


    public static void setBorderLeft(int border, HSSFCellRangeAddress region, HSSFSheet sheet,
                                     HSSFWorkbook workbook) {
        RegionUtil.setBorderLeft(border, region, sheet, workbook);
    }


    public static void setLeftBorderColor(short color, Region region, HSSFSheet sheet,
                                          HSSFWorkbook workbook) {
        setLeftBorderColor(color, toCRA(region), sheet, workbook);
    }


    public static void setLeftBorderColor(int color, HSSFCellRangeAddress region, HSSFSheet sheet,
                                          HSSFWorkbook workbook) {
        RegionUtil.setLeftBorderColor(color, region, sheet, workbook);
    }


    public static void setBorderRight(short border, Region region, HSSFSheet sheet,
                                      HSSFWorkbook workbook) {
        setBorderRight(border, toCRA(region), sheet, workbook);
    }


    public static void setBorderRight(int border, HSSFCellRangeAddress region, HSSFSheet sheet,
                                      HSSFWorkbook workbook) {
        RegionUtil.setBorderRight(border, region, sheet, workbook);
    }


    public static void setRightBorderColor(short color, Region region, HSSFSheet sheet,
                                           HSSFWorkbook workbook) {
        setRightBorderColor(color, toCRA(region), sheet, workbook);
    }


    public static void setRightBorderColor(int color, HSSFCellRangeAddress region, HSSFSheet sheet,
                                           HSSFWorkbook workbook) {
        RegionUtil.setRightBorderColor(color, region, sheet, workbook);
    }


    public static void setBorderBottom(short border, Region region, HSSFSheet sheet,
                                       HSSFWorkbook workbook) {
        setBorderBottom(border, toCRA(region), sheet, workbook);
    }


    public static void setBorderBottom(int border, HSSFCellRangeAddress region, HSSFSheet sheet,
                                       HSSFWorkbook workbook) {
        RegionUtil.setBorderBottom(border, region, sheet, workbook);
    }


    public static void setBottomBorderColor(short color, Region region, HSSFSheet sheet,
                                            HSSFWorkbook workbook) {
        setBottomBorderColor(color, toCRA(region), sheet, workbook);
    }


    public static void setBottomBorderColor(int color, HSSFCellRangeAddress region, HSSFSheet sheet,
                                            HSSFWorkbook workbook) {
        RegionUtil.setBottomBorderColor(color, region, sheet, workbook);
    }


    public static void setBorderTop(short border, Region region, HSSFSheet sheet,
                                    HSSFWorkbook workbook) {
        setBorderTop(border, toCRA(region), sheet, workbook);
    }


    public static void setBorderTop(int border, HSSFCellRangeAddress region, HSSFSheet sheet,
                                    HSSFWorkbook workbook) {
        RegionUtil.setBorderTop(border, region, sheet, workbook);
    }


    public static void setTopBorderColor(short color, Region region, HSSFSheet sheet,
                                         HSSFWorkbook workbook) {
        setTopBorderColor(color, toCRA(region), sheet, workbook);
    }


    public static void setTopBorderColor(int color, HSSFCellRangeAddress region, HSSFSheet sheet,
                                         HSSFWorkbook workbook) {
        RegionUtil.setTopBorderColor(color, region, sheet, workbook);
    }
}
