

package com.document.render.office.fc.hssf.record;

import androidx.annotation.Keep;

import com.document.render.office.fc.ss.util.HSSFCellRangeAddress;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.HexRead;
import com.document.render.office.fc.util.LittleEndianByteArrayInputStream;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.fc.util.POILogFactory;
import com.document.render.office.fc.util.POILogger;
import com.document.render.office.fc.util.StringUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;



public final class HyperlinkRecord extends StandardRecord {
    @Keep
    public final static short sid = 0x01B8;

    static final int HLINK_URL = 0x01;
    static final int HLINK_ABS = 0x02;
    static final int HLINK_LABEL = 0x14;

    static final int HLINK_PLACE = 0x08;
    final static GUID STD_MONIKER = GUID.parse("79EAC9D0-BAF9-11CE-8C82-00AA004BA90B");
    final static GUID URL_MONIKER = GUID.parse("79EAC9E0-BAF9-11CE-8C82-00AA004BA90B");
    final static GUID FILE_MONIKER = GUID.parse("00000303-0000-0000-C000-000000000046");
    private static final int HLINK_TARGET_FRAME = 0x80;
    private static final int HLINK_UNC_PATH = 0x100;

    private final static byte[] URL_TAIL = HexRead.readFromString("79 58 81 F4  3B 1D 7F 48   AF 2C 82 5D  C4 85 27 63   00 00 00 00  A5 AB 00 00");

    private final static byte[] FILE_TAIL = HexRead.readFromString("FF FF AD DE  00 00 00 00   00 00 00 00  00 00 00 00   00 00 00 00  00 00 00 00");
    private static final int TAIL_SIZE = FILE_TAIL.length;
    private POILogger logger = POILogFactory.getLogger(getClass());

    private HSSFCellRangeAddress _range;

    private GUID _guid;

    private int _fileOpts;

    private int _linkOpts;

    private String _label;
    private String _targetFrame;

    private GUID _moniker;

    private String _shortFilename;

    private String _address;

    private String _textMark;
    private byte[] _uninterpretedTail;


    public HyperlinkRecord() {

    }

    public HyperlinkRecord(RecordInputStream in) {
        _range = new HSSFCellRangeAddress(in);

        _guid = new GUID(in);


        int streamVersion = in.readInt();
        if (streamVersion != 0x00000002) {
            throw new RecordFormatException("Stream Version must be 0x2 but found " + streamVersion);
        }
        _linkOpts = in.readInt();

        if ((_linkOpts & HLINK_LABEL) != 0) {
            int label_len = in.readInt();
            _label = in.readUnicodeLEString(label_len);
        }

        if ((_linkOpts & HLINK_TARGET_FRAME) != 0) {
            int len = in.readInt();
            _targetFrame = in.readUnicodeLEString(len);
        }

        if ((_linkOpts & HLINK_URL) != 0 && (_linkOpts & HLINK_UNC_PATH) != 0) {
            _moniker = null;
            int nChars = in.readInt();
            _address = in.readUnicodeLEString(nChars);
        }

        if ((_linkOpts & HLINK_URL) != 0 && (_linkOpts & HLINK_UNC_PATH) == 0) {
            _moniker = new GUID(in);

            if (URL_MONIKER.equals(_moniker)) {
                int length = in.readInt();

                int remaining = in.remaining();
                if (length == remaining) {
                    int nChars = length / 2;
                    _address = in.readUnicodeLEString(nChars);
                } else {
                    int nChars = (length - TAIL_SIZE) / 2;
                    _address = in.readUnicodeLEString(nChars);

                    _uninterpretedTail = readTail(URL_TAIL, in);
                }
            } else if (FILE_MONIKER.equals(_moniker)) {
                _fileOpts = in.readShort();

                int len = in.readInt();
                _shortFilename = StringUtil.readCompressedUnicode(in, len);
                _uninterpretedTail = readTail(FILE_TAIL, in);
                int size = in.readInt();
                if (size > 0) {
                    int charDataSize = in.readInt();


                    int optFlags = in.readUShort();
                    if (optFlags != 0x0003) {
                        throw new RecordFormatException("Expected 0x3 but found " + optFlags);
                    }
                    _address = StringUtil.readUnicodeLE(in, charDataSize / 2);
                } else {
                    _address = null;
                }
            } else if (STD_MONIKER.equals(_moniker)) {
                _fileOpts = in.readShort();

                int len = in.readInt();

                byte[] path_bytes = new byte[len];
                in.readFully(path_bytes);

                _address = new String(path_bytes);
            }
        }

        if ((_linkOpts & HLINK_PLACE) != 0) {

            int len = in.readInt();
            _textMark = in.readUnicodeLEString(len);
        }

        if (in.remaining() > 0) {
            logger.log(POILogger.WARN,
                    "Hyperlink data remains: " + in.remaining() +
                            " : " + HexDump.toHex(in.readRemainder())
            );
        }
    }

    private static String cleanString(String s) {
        if (s == null) {
            return null;
        }
        int idx = s.indexOf('\u0000');
        if (idx < 0) {
            return s;
        }
        return s.substring(0, idx);
    }

    private static String appendNullTerm(String s) {
        if (s == null) {
            return null;
        }
        return s + '\u0000';
    }

    private static byte[] readTail(byte[] expectedTail, LittleEndianInput in) {
        byte[] result = new byte[TAIL_SIZE];
        in.readFully(result);
        if (false) {
            for (int i = 0; i < expectedTail.length; i++) {
                if (expectedTail[i] != result[i]) {
                    System.err.println("Mismatch in tail byte [" + i + "]"
                            + "expected " + (expectedTail[i] & 0xFF) + " but got " + (result[i] & 0xFF));
                }
            }
        }
        return result;
    }

    private static void writeTail(byte[] tail, LittleEndianOutput out) {
        out.write(tail);
    }


    public int getFirstColumn() {
        return _range.getFirstColumn();
    }


    public void setFirstColumn(int col) {
        _range.setFirstColumn(col);
    }


    public int getLastColumn() {
        return _range.getLastColumn();
    }


    public void setLastColumn(int col) {
        _range.setLastColumn(col);
    }


    public int getFirstRow() {
        return _range.getFirstRow();
    }


    public void setFirstRow(int col) {
        _range.setFirstRow(col);
    }


    public int getLastRow() {
        return _range.getLastRow();
    }


    public void setLastRow(int col) {
        _range.setLastRow(col);
    }


    GUID getGuid() {
        return _guid;
    }


    GUID getMoniker() {
        return _moniker;
    }


    public String getLabel() {
        return cleanString(_label);
    }


    public void setLabel(String label) {
        _label = appendNullTerm(label);
    }

    public String getTargetFrame() {
        return cleanString(_targetFrame);
    }


    public String getAddress() {
        if ((_linkOpts & HLINK_URL) != 0 && FILE_MONIKER.equals(_moniker))
            return cleanString(_address != null ? _address : _shortFilename);
        else if ((_linkOpts & HLINK_PLACE) != 0)
            return cleanString(_textMark);
        else
            return cleanString(_address);
    }


    public void setAddress(String address) {
        if ((_linkOpts & HLINK_URL) != 0 && FILE_MONIKER.equals(_moniker))
            _shortFilename = appendNullTerm(address);
        else if ((_linkOpts & HLINK_PLACE) != 0)
            _textMark = appendNullTerm(address);
        else
            _address = appendNullTerm(address);
    }

    public String getShortFilename() {
        return cleanString(_shortFilename);
    }

    public void setShortFilename(String shortFilename) {
        _shortFilename = appendNullTerm(shortFilename);
    }

    public String getTextMark() {
        return cleanString(_textMark);
    }

    public void setTextMark(String textMark) {
        _textMark = appendNullTerm(textMark);
    }


    int getLinkOptions() {
        return _linkOpts;
    }


    public int getLabelOptions() {
        return 2;
    }


    public int getFileOptions() {
        return _fileOpts;
    }

    public void serialize(LittleEndianOutput out) {
        _range.serialize(out);

        _guid.serialize(out);
        out.writeInt(0x00000002);
        out.writeInt(_linkOpts);

        if ((_linkOpts & HLINK_LABEL) != 0) {
            out.writeInt(_label.length());
            StringUtil.putUnicodeLE(_label, out);
        }
        if ((_linkOpts & HLINK_TARGET_FRAME) != 0) {
            out.writeInt(_targetFrame.length());
            StringUtil.putUnicodeLE(_targetFrame, out);
        }

        if ((_linkOpts & HLINK_URL) != 0 && (_linkOpts & HLINK_UNC_PATH) != 0) {
            out.writeInt(_address.length());
            StringUtil.putUnicodeLE(_address, out);
        }

        if ((_linkOpts & HLINK_URL) != 0 && (_linkOpts & HLINK_UNC_PATH) == 0) {
            _moniker.serialize(out);
            if (URL_MONIKER.equals(_moniker)) {
                if (_uninterpretedTail == null) {
                    out.writeInt(_address.length() * 2);
                    StringUtil.putUnicodeLE(_address, out);
                } else {
                    out.writeInt(_address.length() * 2 + TAIL_SIZE);
                    StringUtil.putUnicodeLE(_address, out);
                    writeTail(_uninterpretedTail, out);
                }
            } else if (FILE_MONIKER.equals(_moniker)) {
                out.writeShort(_fileOpts);
                out.writeInt(_shortFilename.length());
                StringUtil.putCompressedUnicode(_shortFilename, out);
                writeTail(_uninterpretedTail, out);
                if (_address == null) {
                    out.writeInt(0);
                } else {
                    int addrLen = _address.length() * 2;
                    out.writeInt(addrLen + 6);
                    out.writeInt(addrLen);
                    out.writeShort(0x0003);
                    StringUtil.putUnicodeLE(_address, out);
                }
            }
        }
        if ((_linkOpts & HLINK_PLACE) != 0) {
            out.writeInt(_textMark.length());
            StringUtil.putUnicodeLE(_textMark, out);
        }
    }

    protected int getDataSize() {
        int size = 0;
        size += 2 + 2 + 2 + 2;
        size += GUID.ENCODED_SIZE;
        size += 4;
        size += 4;
        if ((_linkOpts & HLINK_LABEL) != 0) {
            size += 4;
            size += _label.length() * 2;
        }
        if ((_linkOpts & HLINK_TARGET_FRAME) != 0) {
            size += 4;
            size += _targetFrame.length() * 2;
        }
        if ((_linkOpts & HLINK_URL) != 0 && (_linkOpts & HLINK_UNC_PATH) != 0) {
            size += 4;
            size += _address.length() * 2;
        }
        if ((_linkOpts & HLINK_URL) != 0 && (_linkOpts & HLINK_UNC_PATH) == 0) {
            size += GUID.ENCODED_SIZE;
            if (URL_MONIKER.equals(_moniker)) {
                size += 4;
                size += _address.length() * 2;
                if (_uninterpretedTail != null) {
                    size += TAIL_SIZE;
                }
            } else if (FILE_MONIKER.equals(_moniker)) {
                size += 2;
                size += 4;
                size += _shortFilename.length();
                size += TAIL_SIZE;
                size += 4;
                if (_address != null) {
                    size += 6;
                    size += _address.length() * 2;
                }

            }
        }
        if ((_linkOpts & HLINK_PLACE) != 0) {
            size += 4;
            size += _textMark.length() * 2;
        }
        return size;
    }

    public short getSid() {
        return HyperlinkRecord.sid;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[HYPERLINK RECORD]\n");
        buffer.append("    .range   = ").append(_range.formatAsString()).append("\n");
        buffer.append("    .guid    = ").append(_guid.formatAsString()).append("\n");
        buffer.append("    .linkOpts= ").append(HexDump.intToHex(_linkOpts)).append("\n");
        buffer.append("    .label   = ").append(getLabel()).append("\n");
        if ((_linkOpts & HLINK_TARGET_FRAME) != 0) {
            buffer.append("    .targetFrame= ").append(getTargetFrame()).append("\n");
        }
        if ((_linkOpts & HLINK_URL) != 0 && _moniker != null) {
            buffer.append("    .moniker   = ").append(_moniker.formatAsString()).append("\n");
        }
        if ((_linkOpts & HLINK_PLACE) != 0) {
            buffer.append("    .textMark= ").append(getTextMark()).append("\n");
        }
        buffer.append("    .address   = ").append(getAddress()).append("\n");
        buffer.append("[/HYPERLINK RECORD]\n");
        return buffer.toString();
    }


    public boolean isUrlLink() {
        return (_linkOpts & HLINK_URL) > 0
                && (_linkOpts & HLINK_ABS) > 0;
    }


    public boolean isFileLink() {
        return (_linkOpts & HLINK_URL) > 0
                && (_linkOpts & HLINK_ABS) == 0;
    }


    public boolean isDocumentLink() {
        return (_linkOpts & HLINK_PLACE) > 0;
    }


    public void newUrlLink() {
        _range = new HSSFCellRangeAddress(0, 0, 0, 0);
        _guid = STD_MONIKER;
        _linkOpts = HLINK_URL | HLINK_ABS | HLINK_LABEL;
        setLabel("");
        _moniker = URL_MONIKER;
        setAddress("");
        _uninterpretedTail = URL_TAIL;
    }


    public void newFileLink() {
        _range = new HSSFCellRangeAddress(0, 0, 0, 0);
        _guid = STD_MONIKER;
        _linkOpts = HLINK_URL | HLINK_LABEL;
        _fileOpts = 0;
        setLabel("");
        _moniker = FILE_MONIKER;
        setAddress(null);
        setShortFilename("");
        _uninterpretedTail = FILE_TAIL;
    }


    public void newDocumentLink() {
        _range = new HSSFCellRangeAddress(0, 0, 0, 0);
        _guid = STD_MONIKER;
        _linkOpts = HLINK_LABEL | HLINK_PLACE;
        setLabel("");
        _moniker = FILE_MONIKER;
        setAddress("");
        setTextMark("");
    }

    public Object clone() {
        HyperlinkRecord rec = new HyperlinkRecord();
        rec._range = _range.copy();
        rec._guid = _guid;
        rec._linkOpts = _linkOpts;
        rec._fileOpts = _fileOpts;
        rec._label = _label;
        rec._address = _address;
        rec._moniker = _moniker;
        rec._shortFilename = _shortFilename;
        rec._targetFrame = _targetFrame;
        rec._textMark = _textMark;
        rec._uninterpretedTail = _uninterpretedTail;
        return rec;
    }

    static final class GUID {
        public static final int ENCODED_SIZE = 16;

        private static final int TEXT_FORMAT_LENGTH = 36;

        private final int _d1;

        private final int _d2;

        private final int _d3;

        private final long _d4;

        public GUID(LittleEndianInput in) {
            this(in.readInt(), in.readUShort(), in.readUShort(), in.readLong());
        }

        public GUID(int d1, int d2, int d3, long d4) {
            _d1 = d1;
            _d2 = d2;
            _d3 = d3;
            _d4 = d4;
        }


        public static GUID parse(String rep) {
            char[] cc = rep.toCharArray();
            if (cc.length != TEXT_FORMAT_LENGTH) {
                throw new RecordFormatException("supplied text is the wrong length for a GUID");
            }
            int d0 = (parseShort(cc, 0) << 16) + (parseShort(cc, 4) << 0);
            int d1 = parseShort(cc, 9);
            int d2 = parseShort(cc, 14);
            for (int i = 23; i > 19; i--) {
                cc[i] = cc[i - 1];
            }
            long d3 = parseLELong(cc, 20);

            return new GUID(d0, d1, d2, d3);
        }

        private static long parseLELong(char[] cc, int startIndex) {
            long acc = 0;
            for (int i = startIndex + 14; i >= startIndex; i -= 2) {
                acc <<= 4;
                acc += parseHexChar(cc[i + 0]);
                acc <<= 4;
                acc += parseHexChar(cc[i + 1]);
            }
            return acc;
        }

        private static int parseShort(char[] cc, int startIndex) {
            int acc = 0;
            for (int i = 0; i < 4; i++) {
                acc <<= 4;
                acc += parseHexChar(cc[startIndex + i]);
            }
            return acc;
        }

        private static int parseHexChar(char c) {
            if (c >= '0' && c <= '9') {
                return c - '0';
            }
            if (c >= 'A' && c <= 'F') {
                return c - 'A' + 10;
            }
            if (c >= 'a' && c <= 'f') {
                return c - 'a' + 10;
            }
            throw new RecordFormatException("Bad hex char '" + c + "'");
        }

        public void serialize(LittleEndianOutput out) {
            out.writeInt(_d1);
            out.writeShort(_d2);
            out.writeShort(_d3);
            out.writeLong(_d4);
        }

        @Override
        public boolean equals(Object obj) {
            GUID other = (GUID) obj;
            if (obj == null || !(obj instanceof GUID))
                return false;
            return _d1 == other._d1 && _d2 == other._d2
                    && _d3 == other._d3 && _d4 == other._d4;
        }

        public int getD1() {
            return _d1;
        }

        public int getD2() {
            return _d2;
        }

        public int getD3() {
            return _d3;
        }

        public long getD4() {

            ByteArrayOutputStream baos = new ByteArrayOutputStream(8);
            try {
                new DataOutputStream(baos).writeLong(_d4);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            byte[] buf = baos.toByteArray();
            return new LittleEndianByteArrayInputStream(buf).readLong();
        }

        public String formatAsString() {

            StringBuilder sb = new StringBuilder(36);

            int PREFIX_LEN = "0x".length();
            sb.append(HexDump.intToHex(_d1), PREFIX_LEN, 8);
            sb.append("-");
            sb.append(HexDump.shortToHex(_d2), PREFIX_LEN, 4);
            sb.append("-");
            sb.append(HexDump.shortToHex(_d3), PREFIX_LEN, 4);
            sb.append("-");
            char[] d4Chars = HexDump.longToHex(getD4());
            sb.append(d4Chars, PREFIX_LEN, 4);
            sb.append("-");
            sb.append(d4Chars, PREFIX_LEN + 4, 12);
            return sb.toString();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(64);
            sb.append(getClass().getName()).append(" [");
            sb.append(formatAsString());
            sb.append("]");
            return sb.toString();
        }
    }
}
