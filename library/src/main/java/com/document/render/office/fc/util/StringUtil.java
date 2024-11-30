

package com.document.render.office.fc.util;

import com.document.render.office.fc.hssf.record.RecordInputStream;

import java.io.UnsupportedEncodingException;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Iterator;


public class StringUtil {
    private static final String ENCODING_ISO_8859_1 = "ISO-8859-1";

    private StringUtil() {

    }


    public static String getFromUnicodeLE(
            final byte[] string,
            final int offset,
            final int len)
            throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if ((offset < 0) || (offset >= string.length)) {
            throw new ArrayIndexOutOfBoundsException("Illegal offset " + offset + " (String data is of length " + string.length + ")");
        }
        if ((len < 0) || (((string.length - offset) / 2) < len)) {
            throw new IllegalArgumentException("Illegal length " + len);
        }

        try {
            return new String(string, offset, len * 2, "UTF-16LE");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    public static String getFromUnicodeLE(byte[] string) {
        if (string.length == 0) {
            return "";
        }
        return getFromUnicodeLE(string, 0, string.length / 2);
    }


    public static String getFromCompressedUnicode(
            final byte[] string,
            final int offset,
            final int len) {
        try {
            int len_to_use = Math.min(len, string.length - offset);
            return new String(string, offset, len_to_use, ENCODING_ISO_8859_1);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readCompressedUnicode(LittleEndianInput in, int nChars) {
        char[] buf = new char[nChars];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (char) in.readUByte();
        }
        return new String(buf);
    }


    public static String readUnicodeString(LittleEndianInput in) {

        int nChars = in.readUShort();
        byte flag = in.readByte();
        if ((flag & 0x01) == 0) {
            return readCompressedUnicode(in, nChars);
        }
        return readUnicodeLE(in, nChars);
    }


    public static String readUnicodeString(LittleEndianInput in, int nChars) {
        byte is16Bit = in.readByte();
        if ((is16Bit & 0x01) == 0) {
            return readCompressedUnicode(in, nChars);
        }
        return readUnicodeLE(in, nChars);
    }


    public static void writeUnicodeString(LittleEndianOutput out, String value) {

        int nChars = value.length();
        out.writeShort(nChars);
        boolean is16Bit = hasMultibyte(value);
        out.writeByte(is16Bit ? 0x01 : 0x00);
        if (is16Bit) {
            putUnicodeLE(value, out);
        } else {
            putCompressedUnicode(value, out);
        }
    }


    public static void writeUnicodeStringFlagAndData(LittleEndianOutput out, String value) {
        boolean is16Bit = hasMultibyte(value);
        out.writeByte(is16Bit ? 0x01 : 0x00);
        if (is16Bit) {
            putUnicodeLE(value, out);
        } else {
            putCompressedUnicode(value, out);
        }
    }


    public static int getEncodedSize(String value) {
        int result = 2 + 1;
        result += value.length() * (StringUtil.hasMultibyte(value) ? 2 : 1);
        return result;
    }


    public static void putCompressedUnicode(String input, byte[] output, int offset) {
        byte[] bytes;
        try {
            bytes = input.getBytes(ENCODING_ISO_8859_1);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        System.arraycopy(bytes, 0, output, offset, bytes.length);
    }

    public static void putCompressedUnicode(String input, LittleEndianOutput out) {
        byte[] bytes;
        try {
            bytes = input.getBytes(ENCODING_ISO_8859_1);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        out.write(bytes);
    }


    public static void putUnicodeLE(String input, byte[] output, int offset) {
        byte[] bytes;
        try {
            bytes = input.getBytes("UTF-16LE");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        System.arraycopy(bytes, 0, output, offset, bytes.length);
    }

    public static void putUnicodeLE(String input, LittleEndianOutput out) {
        byte[] bytes;
        try {
            bytes = input.getBytes("UTF-16LE");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        out.write(bytes);
    }

    public static String readUnicodeLE(LittleEndianInput in, int nChars) {
        char[] buf = new char[nChars];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (char) in.readUShort();
        }
        return new String(buf);
    }


    public static String format(String message, Object[] params) {
        int currentParamNumber = 0;
        StringBuffer formattedMessage = new StringBuffer();
        for (int i = 0; i < message.length(); i++) {
            if (message.charAt(i) == '%') {
                if (currentParamNumber >= params.length) {
                    formattedMessage.append("?missing data?");
                } else if (
                        (params[currentParamNumber] instanceof Number)
                                && (i + 1 < message.length())) {
                    i
                            += matchOptionalFormatting(
                            (Number) params[currentParamNumber++],
                            message.substring(i + 1),
                            formattedMessage);
                } else {
                    formattedMessage.append(
                            params[currentParamNumber++].toString());
                }
            } else {
                if ((message.charAt(i) == '\\')
                        && (i + 1 < message.length())
                        && (message.charAt(i + 1) == '%')) {
                    formattedMessage.append('%');
                    i++;
                } else {
                    formattedMessage.append(message.charAt(i));
                }
            }
        }
        return formattedMessage.toString();
    }


    private static int matchOptionalFormatting(
            Number number,
            String formatting,
            StringBuffer outputTo) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        if ((0 < formatting.length())
                && Character.isDigit(formatting.charAt(0))) {
            numberFormat.setMinimumIntegerDigits(
                    Integer.parseInt(formatting.charAt(0) + ""));
            if ((2 < formatting.length())
                    && (formatting.charAt(1) == '.')
                    && Character.isDigit(formatting.charAt(2))) {
                numberFormat.setMaximumFractionDigits(
                        Integer.parseInt(formatting.charAt(2) + ""));
                numberFormat.format(number, outputTo, new FieldPosition(0));
                return 3;
            }
            numberFormat.format(number, outputTo, new FieldPosition(0));
            return 1;
        } else if (
                (0 < formatting.length()) && (formatting.charAt(0) == '.')) {
            if ((1 < formatting.length())
                    && Character.isDigit(formatting.charAt(1))) {
                numberFormat.setMaximumFractionDigits(
                        Integer.parseInt(formatting.charAt(1) + ""));
                numberFormat.format(number, outputTo, new FieldPosition(0));
                return 2;
            }
        }
        numberFormat.format(number, outputTo, new FieldPosition(0));
        return 1;
    }


    public static String getPreferredEncoding() {
        return ENCODING_ISO_8859_1;
    }


    public static boolean hasMultibyte(String value) {
        if (value == null)
            return false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c > 0xFF) {
                return true;
            }
        }
        return false;
    }


    public static boolean isUnicodeString(final String value) {
        try {
            return !value.equals(new String(value.getBytes(ENCODING_ISO_8859_1),
                    ENCODING_ISO_8859_1));
        } catch (UnsupportedEncodingException e) {
            return true;
        }
    }


    public static class StringsIterator implements Iterator<String> {
        private String[] strings;
        private int position = 0;

        public StringsIterator(String[] strings) {
            if (strings != null) {
                this.strings = strings;
            } else {
                this.strings = new String[0];
            }
        }

        public boolean hasNext() {
            return position < strings.length;
        }

        public String next() {
            int ourPos = position++;
            if (ourPos >= strings.length)
                throw new ArrayIndexOutOfBoundsException(ourPos);
            return strings[ourPos];
        }

        public void remove() {
        }
    }
}
