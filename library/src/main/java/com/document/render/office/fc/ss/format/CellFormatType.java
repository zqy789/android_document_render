

package com.document.render.office.fc.ss.format;


public enum CellFormatType {


    GENERAL {
        CellFormatter formatter(String pattern) {
            return new CellGeneralFormatter();
        }

        boolean isSpecial(char ch) {
            return false;
        }
    },

    NUMBER {
        boolean isSpecial(char ch) {
            return false;
        }

        CellFormatter formatter(String pattern) {
            return new CellNumberFormatter(pattern);
        }
    },

    DATE {
        boolean isSpecial(char ch) {
            return ch == '\'' || (ch <= '\u007f' && Character.isLetter(ch));
        }

        CellFormatter formatter(String pattern) {
            return new CellDateFormatter(pattern);
        }
    },

    ELAPSED {
        boolean isSpecial(char ch) {
            return false;
        }

        CellFormatter formatter(String pattern) {
            return new CellElapsedFormatter(pattern);
        }
    },

    TEXT {
        boolean isSpecial(char ch) {
            return false;
        }

        CellFormatter formatter(String pattern) {
            return new CellTextFormatter(pattern);
        }
    };


    abstract boolean isSpecial(char ch);


    abstract CellFormatter formatter(String pattern);
}
