
package com.document.render.office.fc.hssf.record;

import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.util.HexDump;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.fc.util.StringUtil;



public class LbsDataSubRecord extends SubRecord {

    public static final int sid = 0x0013;


    private int _cbFContinued;


    private int _unknownPreFormulaInt;
    private Ptg _linkPtg;
    private Byte _unknownPostFormulaByte;


    private int _cLines;


    private int _iSel;


    private int _flags;


    private int _idEdit;


    private LbsDropData _dropData;


    private String[] _rgLines;


    private boolean[] _bsels;


    public LbsDataSubRecord(LittleEndianInput in, int cbFContinued, int cmoOt) {
        _cbFContinued = cbFContinued;

        int encodedTokenLen = in.readUShort();
        if (encodedTokenLen > 0) {
            int formulaSize = in.readUShort();
            _unknownPreFormulaInt = in.readInt();

            Ptg[] ptgs = Ptg.readTokens(formulaSize, in);
            if (ptgs.length != 1) {
                throw new RecordFormatException("Read " + ptgs.length
                        + " tokens but expected exactly 1");
            }
            _linkPtg = ptgs[0];
            switch (encodedTokenLen - formulaSize - 6) {
                case 1:
                    _unknownPostFormulaByte = in.readByte();
                    break;
                case 0:
                    _unknownPostFormulaByte = null;
                    break;
                default:
                    throw new RecordFormatException("Unexpected leftover bytes");
            }
        }

        _cLines = in.readUShort();
        _iSel = in.readUShort();
        _flags = in.readUShort();
        _idEdit = in.readUShort();



        if (cmoOt == 0x14) {
            _dropData = new LbsDropData(in);
        }



        if ((_flags & 0x2) != 0) {
            _rgLines = new String[_cLines];
            for (int i = 0; i < _cLines; i++) {
                _rgLines[i] = StringUtil.readUnicodeString(in);
            }
        }






        if (((_flags >> 4) & 0x2) != 0) {
            _bsels = new boolean[_cLines];
            for (int i = 0; i < _cLines; i++) {
                _bsels[i] = in.readByte() == 1;
            }
        }

    }

    LbsDataSubRecord() {

    }


    public static LbsDataSubRecord newAutoFilterInstance() {
        LbsDataSubRecord lbs = new LbsDataSubRecord();
        lbs._cbFContinued = 0x1FEE;
        lbs._iSel = 0x000;

        lbs._flags = 0x0301;
        lbs._dropData = new LbsDropData();
        lbs._dropData._wStyle = LbsDropData.STYLE_COMBO_SIMPLE_DROPDOWN;


        lbs._dropData._cLine = 8;
        return lbs;
    }


    @Override
    public boolean isTerminating() {
        return true;
    }

    @Override
    protected int getDataSize() {
        int result = 2;


        if (_linkPtg != null) {
            result += 2;
            result += 4;
            result += _linkPtg.getSize();
            if (_unknownPostFormulaByte != null) {
                result += 1;
            }
        }

        result += 4 * 2;
        if (_dropData != null) {
            result += _dropData.getDataSize();
        }
        if (_rgLines != null) {
            for (String str : _rgLines) {
                result += StringUtil.getEncodedSize(str);
            }
        }
        if (_bsels != null) {
            result += _bsels.length;
        }
        return result;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(sid);
        out.writeShort(_cbFContinued);

        if (_linkPtg == null) {
            out.writeShort(0);
        } else {
            int formulaSize = _linkPtg.getSize();
            int linkSize = formulaSize + 6;
            if (_unknownPostFormulaByte != null) {
                linkSize++;
            }
            out.writeShort(linkSize);
            out.writeShort(formulaSize);
            out.writeInt(_unknownPreFormulaInt);
            _linkPtg.write(out);
            if (_unknownPostFormulaByte != null) {
                out.writeByte(_unknownPostFormulaByte.intValue());
            }
        }

        out.writeShort(_cLines);
        out.writeShort(_iSel);
        out.writeShort(_flags);
        out.writeShort(_idEdit);

        if (_dropData != null) {
            _dropData.serialize(out);
        }

        if (_rgLines != null) {
            for (String str : _rgLines) {
                StringUtil.writeUnicodeString(out, str);
            }
        }

        if (_bsels != null) {
            for (boolean val : _bsels) {
                out.writeByte(val ? 1 : 0);
            }
        }
    }

    @Override
    public Object clone() {
        return this;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(256);

        sb.append("[ftLbsData]\n");
        sb.append("    .unknownShort1 =").append(HexDump.shortToHex(_cbFContinued)).append("\n");
        sb.append("    .formula        = ").append('\n');
        if (_linkPtg != null)
            sb.append(_linkPtg.toString()).append(_linkPtg.getRVAType()).append('\n');
        sb.append("    .nEntryCount   =").append(HexDump.shortToHex(_cLines)).append("\n");
        sb.append("    .selEntryIx    =").append(HexDump.shortToHex(_iSel)).append("\n");
        sb.append("    .style         =").append(HexDump.shortToHex(_flags)).append("\n");
        sb.append("    .unknownShort10=").append(HexDump.shortToHex(_idEdit)).append("\n");
        if (_dropData != null) sb.append('\n').append(_dropData.toString());
        sb.append("[/ftLbsData]\n");
        return sb.toString();
    }


    public Ptg getFormula() {
        return _linkPtg;
    }


    public int getNumberOfItems() {
        return _cLines;
    }


    public static class LbsDropData {

        public static int STYLE_COMBO_DROPDOWN = 0;

        public static int STYLE_COMBO_EDIT_DROPDOWN = 1;

        public static int STYLE_COMBO_SIMPLE_DROPDOWN = 2;


        private int _wStyle;


        private int _cLine;


        private int _dxMin;


        private String _str;


        private Byte _unused;

        public LbsDropData() {
            _str = "";
            _unused = 0;
        }

        public LbsDropData(LittleEndianInput in) {
            _wStyle = in.readUShort();
            _cLine = in.readUShort();
            _dxMin = in.readUShort();
            _str = StringUtil.readUnicodeString(in);
            if (StringUtil.getEncodedSize(_str) % 2 != 0) {
                _unused = in.readByte();
            }
        }


        public void setStyle(int style) {
            _wStyle = style;
        }


        public void setNumLines(int num) {
            _cLine = num;
        }

        public void serialize(LittleEndianOutput out) {
            out.writeShort(_wStyle);
            out.writeShort(_cLine);
            out.writeShort(_dxMin);
            StringUtil.writeUnicodeString(out, _str);
            if (_unused != null) out.writeByte(_unused);
        }

        public int getDataSize() {
            int size = 6;
            size += StringUtil.getEncodedSize(_str);
            if (_unused != null) size++;
            return size;
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("[LbsDropData]\n");
            sb.append("  ._wStyle:  ").append(_wStyle).append('\n');
            sb.append("  ._cLine:  ").append(_cLine).append('\n');
            sb.append("  ._dxMin:  ").append(_dxMin).append('\n');
            sb.append("  ._str:  ").append(_str).append('\n');
            if (_unused != null) sb.append("  ._unused:  ").append(_unused).append('\n');
            sb.append("[/LbsDropData]\n");

            return sb.toString();
        }
    }
}
