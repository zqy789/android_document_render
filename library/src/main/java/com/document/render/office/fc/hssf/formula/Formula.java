

package com.document.render.office.fc.hssf.formula;

import androidx.annotation.Keep;

import com.document.render.office.fc.hssf.formula.ptg.ExpPtg;
import com.document.render.office.fc.hssf.formula.ptg.Ptg;
import com.document.render.office.fc.hssf.formula.ptg.TblPtg;
import com.document.render.office.fc.ss.util.CellReference;
import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.LittleEndianByteArrayInputStream;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianOutput;

import java.util.Arrays;



public class Formula {

    private static final Formula EMPTY = new Formula(new byte[0], 0);


    private final byte[] _byteEncoding;
    private final int _encodedTokenLen;

    private Formula(byte[] byteEncoding, int encodedTokenLen) {
        _byteEncoding = byteEncoding;
        _encodedTokenLen = encodedTokenLen;
        if (false) {
            LittleEndianByteArrayInputStream in = new LittleEndianByteArrayInputStream(byteEncoding);
            Ptg.readTokens(encodedTokenLen, in);
            int nUnusedBytes = _byteEncoding.length - in.getReadIndex();
            if (nUnusedBytes > 0) {





                System.out.println(nUnusedBytes + " unused bytes at end of formula");
            }
        }
    }


    public static Formula read(int encodedTokenLen, LittleEndianInput in) {
        return read(encodedTokenLen, in, encodedTokenLen);
    }


    public static Formula read(int encodedTokenLen, LittleEndianInput in, int totalEncodedLen) {
        byte[] byteEncoding = new byte[totalEncodedLen];
        in.readFully(byteEncoding);
        return new Formula(byteEncoding, encodedTokenLen);
    }


    @Keep
    public static Formula create(Ptg[] ptgs) {
        if (ptgs == null || ptgs.length < 1) {
            return EMPTY;
        }
        int totalSize = Ptg.getEncodedSize(ptgs);
        byte[] encodedData = new byte[totalSize];
        Ptg.serializePtgs(ptgs, encodedData, 0);
        int encodedTokenLen = Ptg.getEncodedSizeWithoutArrayData(ptgs);
        return new Formula(encodedData, encodedTokenLen);
    }


    public static Ptg[] getTokens(Formula formula) {
        if (formula == null) {
            return null;
        }
        return formula.getTokens();
    }

    public Ptg[] getTokens() {
        LittleEndianInput in = new LittleEndianByteArrayInputStream(_byteEncoding);
        return Ptg.readTokens(_encodedTokenLen, in);
    }


    public void serialize(LittleEndianOutput out) {
        out.writeShort(_encodedTokenLen);
        out.write(_byteEncoding);
    }

    public void serializeTokens(LittleEndianOutput out) {
        out.write(_byteEncoding, 0, _encodedTokenLen);
    }

    public void serializeArrayConstantData(LittleEndianOutput out) {
        int len = _byteEncoding.length - _encodedTokenLen;
        out.write(_byteEncoding, _encodedTokenLen, len);
    }


    public int getEncodedSize() {
        return 2 + _byteEncoding.length;
    }


    public int getEncodedTokenSize() {
        return _encodedTokenLen;
    }

    public Formula copy() {

        return this;
    }


    public CellReference getExpReference() {
        byte[] data = _byteEncoding;
        if (data.length != 5) {

            return null;
        }
        switch (data[0]) {
            case ExpPtg.sid:
                break;
            case TblPtg.sid:
                break;
            default:
                return null;
        }
        int firstRow = LittleEndian.getUShort(data, 1);
        int firstColumn = LittleEndian.getUShort(data, 3);
        return new CellReference(firstRow, firstColumn);
    }

    public boolean isSame(Formula other) {
        return Arrays.equals(_byteEncoding, other._byteEncoding);
    }
}
