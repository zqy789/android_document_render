

package com.document.render.office.fc.hssf.formula.ptg;

import com.document.render.office.fc.util.LittleEndianByteArrayOutputStream;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;

import java.util.ArrayList;
import java.util.List;



public abstract class Ptg {
    public static final Ptg[] EMPTY_PTG_ARRAY = {};
    public static final byte CLASS_REF = 0x00;
    public static final byte CLASS_VALUE = 0x20;
    public static final byte CLASS_ARRAY = 0x40;
    private byte ptgClass = CLASS_REF;


    public static Ptg[] readTokens(int size, LittleEndianInput in) {
        List<Ptg> temp = new ArrayList<Ptg>(4 + size / 2);
        int pos = 0;
        boolean hasArrayPtgs = false;
        while (pos < size) {
            Ptg ptg = Ptg.createPtg(in);
            if (ptg instanceof ArrayPtg.Initial) {
                hasArrayPtgs = true;
            }
            pos += ptg.getSize();
            temp.add(ptg);
        }
        if (pos != size) {
            throw new RuntimeException("Ptg array size mismatch");
        }
        if (hasArrayPtgs) {
            Ptg[] result = toPtgArray(temp);
            for (int i = 0; i < result.length; i++) {
                if (result[i] instanceof ArrayPtg.Initial) {
                    result[i] = ((ArrayPtg.Initial) result[i]).finishReading(in);
                }
            }
            return result;
        }
        return toPtgArray(temp);
    }

    public static Ptg createPtg(LittleEndianInput in) {
        byte id = in.readByte();

        if (id < 0x20) {
            return createBasePtg(id, in);
        }

        Ptg retval = createClassifiedPtg(id, in);

        if (id >= 0x60) {
            retval.setClass(CLASS_ARRAY);
        } else if (id >= 0x40) {
            retval.setClass(CLASS_VALUE);
        } else {
            retval.setClass(CLASS_REF);
        }
        return retval;
    }

    private static Ptg createClassifiedPtg(byte id, LittleEndianInput in) {

        int baseId = id & 0x1F | 0x20;

        switch (baseId) {
            case ArrayPtg.sid:
                return new ArrayPtg.Initial(in);
            case FuncPtg.sid:
                return FuncPtg.create(in);
            case FuncVarPtg.sid:
                return FuncVarPtg.create(in);
            case NamePtg.sid:
                return new NamePtg(in);
            case RefPtg.sid:
                return new RefPtg(in);
            case AreaPtg.sid:
                return new AreaPtg(in);
            case MemAreaPtg.sid:
                return new MemAreaPtg(in);
            case MemErrPtg.sid:
                return new MemErrPtg(in);
            case MemFuncPtg.sid:
                return new MemFuncPtg(in);
            case RefErrorPtg.sid:
                return new RefErrorPtg(in);
            case AreaErrPtg.sid:
                return new AreaErrPtg(in);
            case RefNPtg.sid:
                return new RefNPtg(in);
            case AreaNPtg.sid:
                return new AreaNPtg(in);

            case NameXPtg.sid:
                return new NameXPtg(in);
            case Ref3DPtg.sid:
                return new Ref3DPtg(in);
            case Area3DPtg.sid:
                return new Area3DPtg(in);
            case DeletedRef3DPtg.sid:
                return new DeletedRef3DPtg(in);
            case DeletedArea3DPtg.sid:
                return new DeletedArea3DPtg(in);
        }
        throw new UnsupportedOperationException(" Unknown Ptg in Formula: 0x"
                + Integer.toHexString(id) + " (" + (int) id + ")");
    }

    private static Ptg createBasePtg(byte id, LittleEndianInput in) {
        switch (id) {
            case 0x00:
                return new UnknownPtg(id);
            case ExpPtg.sid:
                return new ExpPtg(in);
            case TblPtg.sid:
                return new TblPtg(in);
            case AddPtg.sid:
                return AddPtg.instance;
            case SubtractPtg.sid:
                return SubtractPtg.instance;
            case MultiplyPtg.sid:
                return MultiplyPtg.instance;
            case DividePtg.sid:
                return DividePtg.instance;
            case PowerPtg.sid:
                return PowerPtg.instance;
            case ConcatPtg.sid:
                return ConcatPtg.instance;
            case LessThanPtg.sid:
                return LessThanPtg.instance;
            case LessEqualPtg.sid:
                return LessEqualPtg.instance;
            case EqualPtg.sid:
                return EqualPtg.instance;
            case GreaterEqualPtg.sid:
                return GreaterEqualPtg.instance;
            case GreaterThanPtg.sid:
                return GreaterThanPtg.instance;
            case NotEqualPtg.sid:
                return NotEqualPtg.instance;
            case IntersectionPtg.sid:
                return IntersectionPtg.instance;
            case UnionPtg.sid:
                return UnionPtg.instance;
            case RangePtg.sid:
                return RangePtg.instance;
            case UnaryPlusPtg.sid:
                return UnaryPlusPtg.instance;
            case UnaryMinusPtg.sid:
                return UnaryMinusPtg.instance;
            case PercentPtg.sid:
                return PercentPtg.instance;
            case ParenthesisPtg.sid:
                return ParenthesisPtg.instance;
            case MissingArgPtg.sid:
                return MissingArgPtg.instance;

            case StringPtg.sid:
                return new StringPtg(in);
            case AttrPtg.sid:
                return new AttrPtg(in);
            case ErrPtg.sid:
                return ErrPtg.read(in);
            case BoolPtg.sid:
                return BoolPtg.read(in);
            case IntPtg.sid:
                return new IntPtg(in);
            case NumberPtg.sid:
                return new NumberPtg(in);
        }
        throw new RuntimeException("Unexpected base token id (" + id + ")");
    }

    private static Ptg[] toPtgArray(List<Ptg> l) {
        if (l.isEmpty()) {
            return EMPTY_PTG_ARRAY;
        }
        Ptg[] result = new Ptg[l.size()];
        l.toArray(result);
        return result;
    }


    public static int getEncodedSize(Ptg[] ptgs) {
        int result = 0;
        for (int i = 0; i < ptgs.length; i++) {
            result += ptgs[i].getSize();
        }
        return result;
    }


    public static int getEncodedSizeWithoutArrayData(Ptg[] ptgs) {
        int result = 0;
        for (int i = 0; i < ptgs.length; i++) {
            Ptg ptg = ptgs[i];
            if (ptg instanceof ArrayPtg) {
                result += ArrayPtg.PLAIN_TOKEN_SIZE;
            } else {
                result += ptg.getSize();
            }
        }
        return result;
    }


    public static int serializePtgs(Ptg[] ptgs, byte[] array, int offset) {
        int nTokens = ptgs.length;

        LittleEndianByteArrayOutputStream out = new LittleEndianByteArrayOutputStream(array, offset);

        List<Ptg> arrayPtgs = null;

        for (int k = 0; k < nTokens; k++) {
            Ptg ptg = ptgs[k];

            ptg.write(out);
            if (ptg instanceof ArrayPtg) {
                if (arrayPtgs == null) {
                    arrayPtgs = new ArrayList<Ptg>(5);
                }
                arrayPtgs.add(ptg);
            }
        }
        if (arrayPtgs != null) {
            for (int i = 0; i < arrayPtgs.size(); i++) {
                ArrayPtg p = (ArrayPtg) arrayPtgs.get(i);
                p.writeTokenValueBytes(out);
            }
        }
        return out.getWriteIndex() - offset;
    }

    public static boolean doesFormulaReferToDeletedCell(Ptg[] ptgs) {
        for (int i = 0; i < ptgs.length; i++) {
            if (isDeletedCellRef(ptgs[i])) {
                return true;
            }
        }
        return false;
    }

    private static boolean isDeletedCellRef(Ptg ptg) {
        if (ptg == ErrPtg.REF_INVALID) {
            return true;
        }
        if (ptg instanceof DeletedArea3DPtg) {
            return true;
        }
        if (ptg instanceof DeletedRef3DPtg) {
            return true;
        }
        if (ptg instanceof AreaErrPtg) {
            return true;
        }
        if (ptg instanceof RefErrorPtg) {
            return true;
        }
        return false;
    }


    public abstract int getSize();

    public abstract void write(LittleEndianOutput out);


    public abstract String toFormulaString();


    public String toString() {
        return this.getClass().toString();
    }

    public final void setClass(byte thePtgClass) {
        if (isBaseToken()) {
            throw new RuntimeException("setClass should not be called on a base token");
        }
        ptgClass = thePtgClass;
    }


    public final byte getPtgClass() {
        return ptgClass;
    }


    public final char getRVAType() {
        if (isBaseToken()) {
            return '.';
        }
        switch (ptgClass) {
            case Ptg.CLASS_REF:
                return 'R';
            case Ptg.CLASS_VALUE:
                return 'V';
            case Ptg.CLASS_ARRAY:
                return 'A';
        }
        throw new RuntimeException("Unknown operand class (" + ptgClass + ")");
    }

    public abstract byte getDefaultOperandClass();


    public abstract boolean isBaseToken();
}
