
package com.document.render.office.ss.other;

import com.document.render.office.ss.model.baseModel.Cell;
import com.document.render.office.ss.model.baseModel.Row;
import com.document.render.office.ss.model.baseModel.Sheet;
import com.document.render.office.ss.util.ModelUtil;



public class FindingMgr {
    private Sheet sheet;

    private String value;

    private Cell findedCell;

    public FindingMgr() {
    }


    public Cell findCell(Sheet sheet, String value) {
        if (value == null || sheet == null) {
            return null;
        }
        this.sheet = sheet;
        this.value = value;

        String cellContent;
        if (value != null && value.length() > 0) {
            Row row;

            for (int i = sheet.getActiveCellRow(); i <= sheet.getLastRowNum(); i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                int j = (i == sheet.getActiveCellRow()) ? sheet.getActiveCellColumn() : row.getFirstCol();
                for (; j <= row.getLastCol(); j++) {
                    findedCell = row.getCell(j);
                    if (findedCell == null) {
                        continue;
                    }

                    cellContent = ModelUtil.instance().getFormatContents(sheet.getWorkbook(), findedCell);
                    if (cellContent != null && cellContent.contains(value)) {
                        return findedCell;
                    }
                }
            }


            for (int i = sheet.getFirstRowNum(); i <= sheet.getActiveCellRow(); i++) {
                row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                int j = row.getFirstCol();
                for (; j <= row.getLastCol(); j++) {
                    findedCell = row.getCell(j);
                    if (findedCell == null) {
                        continue;
                    }

                    cellContent = ModelUtil.instance().getFormatContents(sheet.getWorkbook(), findedCell);
                    if (cellContent != null && cellContent.contains(value)) {
                        return findedCell;
                    }
                }
            }
        }

        return null;
    }

    public Cell findBackward() {
        if (findedCell == null || value == null || sheet == null) {
            return null;
        }

        String cellContent;
        Row row;
        Cell cell;
        for (int i = findedCell.getRowNumber(); i >= sheet.getFirstRowNum(); i--) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            int j = (i == findedCell.getRowNumber()) ? findedCell.getColNumber() - 1 : row.getLastCol();


            for (; j >= 0; j--) {
                cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }

                cellContent = ModelUtil.instance().getFormatContents(sheet.getWorkbook(), cell);
                if (cellContent != null && cellContent.contains(value)) {
                    findedCell = cell;
                    return findedCell;
                }
            }
        }

        return null;
    }

    public Cell findForward() {
        if (findedCell == null || value == null || sheet == null) {
            return null;
        }

        String cellContent;
        Row row;
        Cell cell;
        for (int i = findedCell.getRowNumber(); i <= sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);
            if (row == null) {
                continue;
            }

            int j = (i == findedCell.getRowNumber()) ? findedCell.getColNumber() + 1 : row.getFirstCol();


            for (; j <= row.getLastCol(); j++) {
                cell = row.getCell(j);
                if (cell == null) {
                    continue;
                }

                cellContent = ModelUtil.instance().getFormatContents(sheet.getWorkbook(), cell);
                if (cellContent != null && cellContent.contains(value)) {
                    findedCell = cell;
                    return findedCell;
                }
            }
        }

        return null;
    }


    public void dispose() {
        sheet = null;
        value = null;
        findedCell = null;
    }

}
