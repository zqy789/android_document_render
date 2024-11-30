

package com.document.render.office.fc.hssf.formula.ptg;

import com.document.render.office.fc.util.BitField;
import com.document.render.office.fc.util.BitFieldFactory;
import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;


public final class AttrPtg extends ControlPtg {
    public final static byte sid = 0x19;
    public static final AttrPtg SUM = new AttrPtg(0x0010, 0, null, -1);
    private final static int SIZE = 4;


    private static final BitField semiVolatile = BitFieldFactory.getInstance(0x01);
    private static final BitField optiIf = BitFieldFactory.getInstance(0x02);
    private static final BitField optiChoose = BitFieldFactory.getInstance(0x04);
    private static final BitField optiSkip = BitFieldFactory.getInstance(0x08);
    private static final BitField optiSum = BitFieldFactory.getInstance(0x10);
    private static final BitField baxcel = BitFieldFactory.getInstance(0x20);
    private static final BitField space = BitFieldFactory.getInstance(0x40);
    private final byte _options;
    private final short _data;

    private final int[] _jumpTable;

    private final int _chooseFuncOffset;

    public AttrPtg(LittleEndianInput in) {
        _options = in.readByte();
        _data = in.readShort();
        if (isOptimizedChoose()) {
            int nCases = _data;
            int[] jumpTable = new int[nCases];
            for (int i = 0; i < jumpTable.length; i++) {
                jumpTable[i] = in.readUShort();
            }
            _jumpTable = jumpTable;
            _chooseFuncOffset = in.readUShort();
        } else {
            _jumpTable = null;
            _chooseFuncOffset = -1;
        }

    }

    private AttrPtg(int options, int data, int[] jt, int chooseFuncOffset) {
        _options = (byte) options;
        _data = (short) data;
        _jumpTable = jt;
        _chooseFuncOffset = chooseFuncOffset;
    }


    public static AttrPtg createSpace(int type, int count) {
        int data = type & 0x00FF | (count << 8) & 0x00FFFF;
        return new AttrPtg(space.set(0), data, null, -1);
    }


    public static AttrPtg createIf(int dist) {
        return new AttrPtg(optiIf.set(0), dist, null, -1);
    }


    public static AttrPtg createSkip(int dist) {
        return new AttrPtg(optiSkip.set(0), dist, null, -1);
    }

    public static AttrPtg getSumSingle() {
        return new AttrPtg(optiSum.set(0), 0, null, -1);
    }

    public boolean isSemiVolatile() {
        return semiVolatile.isSet(_options);
    }

    public boolean isOptimizedIf() {
        return optiIf.isSet(_options);
    }

    public boolean isOptimizedChoose() {
        return optiChoose.isSet(_options);
    }

    public boolean isSum() {
        return optiSum.isSet(_options);
    }

    public boolean isSkip() {
        return optiSkip.isSet(_options);
    }


    private boolean isBaxcel() {
        return baxcel.isSet(_options);
    }

    public boolean isSpace() {
        return space.isSet(_options);
    }

    public short getData() {
        return _data;
    }

    public int[] getJumpTable() {
        return _jumpTable.clone();
    }

    public int getChooseFuncOffset() {
        if (_jumpTable == null) {
            throw new IllegalStateException("Not tAttrChoose");
        }
        return _chooseFuncOffset;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(64);
        sb.append(getClass().getName()).append(" [");

        if (isSemiVolatile()) {
            sb.append("volatile ");
        }
        if (isSpace()) {
            sb.append("space count=").append((_data >> 8) & 0x00FF);
            sb.append(" type=").append(_data & 0x00FF).append(" ");
        }

        if (isOptimizedIf()) {
            sb.append("if dist=").append(_data);
        } else if (isOptimizedChoose()) {
            sb.append("choose nCases=").append(_data);
        } else if (isSkip()) {
            sb.append("skip dist=").append(_data);
        } else if (isSum()) {
            sb.append("sum ");
        } else if (isBaxcel()) {
            sb.append("assign ");
        }
        sb.append("]");
        return sb.toString();
    }

    public void write(LittleEndianOutput out) {
        out.writeByte(sid + getPtgClass());
        out.writeByte(_options);
        out.writeShort(_data);
        int[] jt = _jumpTable;
        if (jt != null) {
            for (int i = 0; i < jt.length; i++) {
                out.writeShort(jt[i]);
            }
            out.writeShort(_chooseFuncOffset);
        }
    }

    public int getSize() {
        if (_jumpTable != null) {
            return SIZE + (_jumpTable.length + 1) * LittleEndian.SHORT_SIZE;
        }
        return SIZE;
    }

    public String toFormulaString(String[] operands) {
        if (space.isSet(_options)) {
            return operands[0];
        } else if (optiIf.isSet(_options)) {
            return toFormulaString() + "(" + operands[0] + ")";
        } else if (optiSkip.isSet(_options)) {
            return toFormulaString() + operands[0];
        } else {
            return toFormulaString() + "(" + operands[0] + ")";
        }
    }

    public int getNumberOfOperands() {
        return 1;
    }

    public int getType() {
        return -1;
    }

    public String toFormulaString() {
        if (semiVolatile.isSet(_options)) {
            return "ATTR(semiVolatile)";
        }
        if (optiIf.isSet(_options)) {
            return "IF";
        }
        if (optiChoose.isSet(_options)) {
            return "CHOOSE";
        }
        if (optiSkip.isSet(_options)) {
            return "";
        }
        if (optiSum.isSet(_options)) {
            return "SUM";
        }
        if (baxcel.isSet(_options)) {
            return "ATTR(baxcel)";
        }
        if (space.isSet(_options)) {
            return "";
        }
        return "UNKNOWN ATTRIBUTE";
    }

    public static final class SpaceType {

        public static final int SPACE_BEFORE = 0x00;

        public static final int CR_BEFORE = 0x01;

        public static final int SPACE_BEFORE_OPEN_PAREN = 0x02;

        public static final int CR_BEFORE_OPEN_PAREN = 0x03;

        public static final int SPACE_BEFORE_CLOSE_PAREN = 0x04;

        public static final int CR_BEFORE_CLOSE_PAREN = 0x05;

        public static final int SPACE_AFTER_EQUALITY = 0x06;
        private SpaceType() {

        }
    }
}
