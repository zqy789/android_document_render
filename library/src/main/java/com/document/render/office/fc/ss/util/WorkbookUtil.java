

package com.document.render.office.fc.ss.util;

import com.document.render.office.fc.ss.usermodel.Workbook;



public class WorkbookUtil {


    public final static String createSafeSheetName(final String nameProposal) {
        if (nameProposal == null) {
            return "null";
        }
        if (nameProposal.length() < 1) {
            return "empty";
        }
        final int length = Math.min(31, nameProposal.length());
        final String shortenname = nameProposal.substring(0, length);
        final StringBuilder result = new StringBuilder(shortenname);
        for (int i = 0; i < length; i++) {
            char ch = result.charAt(i);
            switch (ch) {
                case '/':
                case '\\':
                case '?':
                case '*':
                case ']':
                case '[':
                    result.setCharAt(i, ' ');
                    break;
                case '\'':
                    if (i == 0 || i == length - 1) {
                        result.setCharAt(i, ' ');
                    }
                    break;
                default:

            }
        }
        return result.toString();
    }


    public static void validateSheetName(String sheetName) {
        if (sheetName == null) {
            throw new IllegalArgumentException("sheetName must not be null");
        }
        int len = sheetName.length();
        if (len < 1 || len > 31) {
            throw new IllegalArgumentException("sheetName '" + sheetName
                    + "' is invalid - character count MUST be greater than or equal to 1 and less than or equal to 31");
        }

        for (int i = 0; i < len; i++) {
            char ch = sheetName.charAt(i);
            switch (ch) {
                case '/':
                case '\\':
                case '?':
                case '*':
                case ']':
                case '[':
                case ':':
                    break;
                default:

                    continue;
            }
            throw new IllegalArgumentException("Invalid char (" + ch
                    + ") found at index (" + i + ") in sheet name '" + sheetName + "'");
        }
        if (sheetName.charAt(0) == '\'' || sheetName.charAt(len - 1) == '\'') {
            throw new IllegalArgumentException("Invalid sheet name '" + sheetName
                    + "'. Sheet names must not begin or end with (').");
        }
    }


    public static void validateSheetState(int state) {
        switch (state) {
            case Workbook.SHEET_STATE_VISIBLE:
                break;
            case Workbook.SHEET_STATE_HIDDEN:
                break;
            case Workbook.SHEET_STATE_VERY_HIDDEN:
                break;
            default:
                throw new IllegalArgumentException("Ivalid sheet state : " + state + "\n" +
                        "Sheet state must beone of the Workbook.SHEET_STATE_* constants");
        }
    }
}
