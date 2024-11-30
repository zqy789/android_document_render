

package com.document.render.office.fc.hssf.record.cont;


import com.document.render.office.fc.hssf.record.ContinueRecord;
import com.document.render.office.fc.util.DelayableLittleEndianOutput;
import com.document.render.office.fc.util.LittleEndianOutput;
import com.document.render.office.fc.util.StringUtil;



public final class ContinuableRecordOutput implements LittleEndianOutput {


    private static final LittleEndianOutput NOPOutput = new DelayableLittleEndianOutput() {

        public LittleEndianOutput createDelayedOutput(int size) {
            return this;
        }

        public void write(byte[] b) {

        }

        public void write(byte[] b, int offset, int len) {

        }

        public void writeByte(int v) {

        }

        public void writeDouble(double v) {

        }

        public void writeInt(int v) {

        }

        public void writeLong(long v) {

        }

        public void writeShort(int v) {

        }
    };
    private final LittleEndianOutput _out;
    private UnknownLengthRecordOutput _ulrOutput;
    private int _totalPreviousRecordsSize;

    public ContinuableRecordOutput(LittleEndianOutput out, int sid) {
        _ulrOutput = new UnknownLengthRecordOutput(out, sid);
        _out = out;
        _totalPreviousRecordsSize = 0;
    }

    public static ContinuableRecordOutput createForCountingOnly() {
        return new ContinuableRecordOutput(NOPOutput, -777);
    }


    public int getTotalSize() {
        return _totalPreviousRecordsSize + _ulrOutput.getTotalSize();
    }


    void terminate() {
        _ulrOutput.terminate();
    }


    public int getAvailableSpace() {
        return _ulrOutput.getAvailableSpace();
    }


    public void writeContinue() {
        _ulrOutput.terminate();
        _totalPreviousRecordsSize += _ulrOutput.getTotalSize();
        _ulrOutput = new UnknownLengthRecordOutput(_out, ContinueRecord.sid);
    }


    public void writeContinueIfRequired(int requiredContinuousSize) {
        if (_ulrOutput.getAvailableSpace() < requiredContinuousSize) {
            writeContinue();
        }
    }


    public void writeStringData(String text) {
        boolean is16bitEncoded = StringUtil.hasMultibyte(text);

        int keepTogetherSize = 1 + 1;
        int optionFlags = 0x00;
        if (is16bitEncoded) {
            optionFlags |= 0x01;
            keepTogetherSize += 1;
        }
        writeContinueIfRequired(keepTogetherSize);
        writeByte(optionFlags);
        writeCharacterData(text, is16bitEncoded);
    }


    public void writeString(String text, int numberOfRichTextRuns, int extendedDataSize) {
        boolean is16bitEncoded = StringUtil.hasMultibyte(text);

        int keepTogetherSize = 2 + 1 + 1;
        int optionFlags = 0x00;
        if (is16bitEncoded) {
            optionFlags |= 0x01;
            keepTogetherSize += 1;
        }
        if (numberOfRichTextRuns > 0) {
            optionFlags |= 0x08;
            keepTogetherSize += 2;
        }
        if (extendedDataSize > 0) {
            optionFlags |= 0x04;
            keepTogetherSize += 4;
        }
        writeContinueIfRequired(keepTogetherSize);
        writeShort(text.length());
        writeByte(optionFlags);
        if (numberOfRichTextRuns > 0) {
            writeShort(numberOfRichTextRuns);
        }
        if (extendedDataSize > 0) {
            writeInt(extendedDataSize);
        }
        writeCharacterData(text, is16bitEncoded);
    }

    private void writeCharacterData(String text, boolean is16bitEncoded) {
        int nChars = text.length();
        int i = 0;
        if (is16bitEncoded) {
            while (true) {
                int nWritableChars = Math.min(nChars - i, _ulrOutput.getAvailableSpace() / 2);
                for (; nWritableChars > 0; nWritableChars--) {
                    _ulrOutput.writeShort(text.charAt(i++));
                }
                if (i >= nChars) {
                    break;
                }
                writeContinue();
                writeByte(0x01);
            }
        } else {
            while (true) {
                int nWritableChars = Math.min(nChars - i, _ulrOutput.getAvailableSpace() / 1);
                for (; nWritableChars > 0; nWritableChars--) {
                    _ulrOutput.writeByte(text.charAt(i++));
                }
                if (i >= nChars) {
                    break;
                }
                writeContinue();
                writeByte(0x00);
            }
        }
    }

    public void write(byte[] b) {
        writeContinueIfRequired(b.length);
        _ulrOutput.write(b);
    }

    public void write(byte[] b, int offset, int len) {

        int i = 0;
        while (true) {
            int nWritableChars = Math.min(len - i, _ulrOutput.getAvailableSpace() / 1);
            for (; nWritableChars > 0; nWritableChars--) {
                _ulrOutput.writeByte(b[offset + i++]);
            }
            if (i >= len) {
                break;
            }
            writeContinue();
        }
    }

    public void writeByte(int v) {
        writeContinueIfRequired(1);
        _ulrOutput.writeByte(v);
    }

    public void writeDouble(double v) {
        writeContinueIfRequired(8);
        _ulrOutput.writeDouble(v);
    }

    public void writeInt(int v) {
        writeContinueIfRequired(4);
        _ulrOutput.writeInt(v);
    }

    public void writeLong(long v) {
        writeContinueIfRequired(8);
        _ulrOutput.writeLong(v);
    }

    public void writeShort(int v) {
        writeContinueIfRequired(2);
        _ulrOutput.writeShort(v);
    }
}
