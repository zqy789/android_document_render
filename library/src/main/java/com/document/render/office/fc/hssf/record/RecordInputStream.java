

package com.document.render.office.fc.hssf.record;

import com.document.render.office.fc.hssf.record.crypto.Biff8DecryptingStream;
import com.document.render.office.fc.hssf.record.crypto.Biff8EncryptionKey;
import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.LittleEndianInput;
import com.document.render.office.fc.util.LittleEndianInputStream;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;



public final class RecordInputStream implements LittleEndianInput {
    
    public final static short MAX_RECORD_DATA_SIZE = 8224;
    private static final int INVALID_SID_VALUE = -1;
    
    private static final int DATA_LEN_NEEDS_TO_BE_READ = -1;
    private static final byte[] EMPTY_BYTE_ARRAY = {};
    
    private final BiffHeaderInput _bhi;
    
    private final LittleEndianInput _dataInput;
    
    private int _currentSid;
    
    private int _currentDataLength;
    
    private int _nextSid;
    
    private int _currentDataOffset;
    public RecordInputStream(InputStream in) throws RecordFormatException {
        this(in, null, 0);
    }

    public RecordInputStream(InputStream in, Biff8EncryptionKey key, int initialOffset) throws RecordFormatException {
        if (key == null) {
            _dataInput = getLEI(in);
            _bhi = new SimpleHeaderInput(in);
        } else {
            Biff8DecryptingStream bds = new Biff8DecryptingStream(in, initialOffset, key);
            _bhi = bds;
            _dataInput = bds;
        }
        _nextSid = readNextSid();
    }

    static LittleEndianInput getLEI(InputStream is) {
        if (is instanceof LittleEndianInput) {
            
            return (LittleEndianInput) is;
        }
        
        return new LittleEndianInputStream(is);
    }

    
    public int available() {
        return remaining();
    }

    public int read(byte[] b, int off, int len) {
        int limit = Math.min(len, remaining());
        if (limit == 0) {
            return 0;
        }
        readFully(b, off, limit);
        return limit;
    }

    public short getSid() {
        return (short) _currentSid;
    }

    
    public boolean hasNextRecord() throws LeftoverDataException {
        if (_currentDataLength != -1 && _currentDataLength != _currentDataOffset) {
            throw new LeftoverDataException(_currentSid, remaining());
        }
        if (_currentDataLength != DATA_LEN_NEEDS_TO_BE_READ) {
            _nextSid = readNextSid();
        }
        return _nextSid != INVALID_SID_VALUE;
    }

    
    private int readNextSid() {
        int nAvailable = _bhi.available();
        if (nAvailable < EOFRecord.ENCODED_SIZE) {
            if (nAvailable > 0) {
                
                
                
            }
            return INVALID_SID_VALUE;
        }
        int result = _bhi.readRecordSID();
        if (result == INVALID_SID_VALUE) {
            throw new RecordFormatException("Found invalid sid (" + result + ")");
        }
        _currentDataLength = DATA_LEN_NEEDS_TO_BE_READ;
        return result;
    }

    
    public void nextRecord() throws RecordFormatException {
        if (_nextSid == INVALID_SID_VALUE) {
            throw new IllegalStateException("EOF - next record not available");
        }
        if (_currentDataLength != DATA_LEN_NEEDS_TO_BE_READ) {
            throw new IllegalStateException("Cannot call nextRecord() without checking hasNextRecord() first");
        }
        _currentSid = _nextSid;
        _currentDataOffset = 0;
        _currentDataLength = _bhi.readDataSize();
        if (_currentDataLength > MAX_RECORD_DATA_SIZE) {
            throw new RecordFormatException("The content of an excel record cannot exceed "
                    + MAX_RECORD_DATA_SIZE + " bytes");
        }
    }

    private void checkRecordPosition(int requiredByteCount) {

        int nAvailable = remaining();
        if (nAvailable >= requiredByteCount) {
            
            return;
        }
        if (nAvailable == 0 && isContinueNext()) {
            nextRecord();
            return;
        }
        throw new RecordFormatException("Not enough data (" + nAvailable
                + ") to read requested (" + requiredByteCount + ") bytes");
    }

    
    public byte readByte() {
        checkRecordPosition(LittleEndian.BYTE_SIZE);
        _currentDataOffset += LittleEndian.BYTE_SIZE;
        return _dataInput.readByte();
    }

    
    public short readShort() {
        checkRecordPosition(LittleEndian.SHORT_SIZE);
        _currentDataOffset += LittleEndian.SHORT_SIZE;
        return _dataInput.readShort();
    }

    
    public int readInt() {
        checkRecordPosition(LittleEndian.INT_SIZE);
        _currentDataOffset += LittleEndian.INT_SIZE;
        return _dataInput.readInt();
    }

    
    public long readLong() {
        checkRecordPosition(LittleEndian.LONG_SIZE);
        _currentDataOffset += LittleEndian.LONG_SIZE;
        return _dataInput.readLong();
    }

    
    public int readUByte() {
        return readByte() & 0x00FF;
    }

    
    public int readUShort() {
        checkRecordPosition(LittleEndian.SHORT_SIZE);
        _currentDataOffset += LittleEndian.SHORT_SIZE;
        return _dataInput.readUShort();
    }

    public double readDouble() {
        long valueLongBits = readLong();
        double result = Double.longBitsToDouble(valueLongBits);
        if (Double.isNaN(result)) {
            
            
            
            
        }
        return result;
    }

    public void readFully(byte[] buf) {
        readFully(buf, 0, buf.length);
    }

    public void readFully(byte[] buf, int off, int len) {
        checkRecordPosition(len);
        _dataInput.readFully(buf, off, len);
        _currentDataOffset += len;
    }

    public String readString() {
        int requestedLength = readUShort();
        byte compressFlag = readByte();
        return readStringCommon(requestedLength, compressFlag == 0);
    }

    
    public String readUnicodeLEString(int requestedLength) {
        return readStringCommon(requestedLength, false);
    }

    public String readCompressedUnicode(int requestedLength) {
        return readStringCommon(requestedLength, true);
    }

    private String readStringCommon(int requestedLength, boolean pIsCompressedEncoding) {
        
        if (requestedLength < 0 || requestedLength > 0x100000) { 
            throw new IllegalArgumentException("Bad requested string length (" + requestedLength + ")");
        }
        char[] buf = new char[requestedLength];
        boolean isCompressedEncoding = pIsCompressedEncoding;
        int curLen = 0;
        while (true) {
            int availableChars = isCompressedEncoding ? remaining() : remaining() / LittleEndian.SHORT_SIZE;
            if (requestedLength - curLen <= availableChars) {
                
                while (curLen < requestedLength) {
                    char ch;
                    if (isCompressedEncoding) {
                        ch = (char) readUByte();
                    } else {
                        ch = (char) readShort();
                    }
                    buf[curLen] = ch;
                    curLen++;
                }
                return new String(buf);
            }
            
            
            while (availableChars > 0) {
                char ch;
                if (isCompressedEncoding) {
                    ch = (char) readUByte();
                } else {
                    ch = (char) readShort();
                }
                buf[curLen] = ch;
                curLen++;
                availableChars--;
            }
            if (!isContinueNext()) {
                throw new RecordFormatException("Expected to find a ContinueRecord in order to read remaining "
                        + (requestedLength - curLen) + " of " + requestedLength + " chars");
            }
            if (remaining() != 0) {
                throw new RecordFormatException("Odd number of bytes(" + remaining() + ") left behind");
            }
            nextRecord();
            
            byte compressFlag = readByte();
            isCompressedEncoding = (compressFlag == 0);
        }
    }

    
    public byte[] readRemainder() {
        int size = remaining();
        if (size == 0) {
            return EMPTY_BYTE_ARRAY;
        }
        byte[] result = new byte[size];
        readFully(result);
        return result;
    }

    
    public byte[] readAllContinuedRemainder() {
        
        
        ByteArrayOutputStream out = new ByteArrayOutputStream(2 * MAX_RECORD_DATA_SIZE);

        while (true) {
            byte[] b = readRemainder();
            out.write(b, 0, b.length);
            if (!isContinueNext()) {
                break;
            }
            nextRecord();
        }
        return out.toByteArray();
    }

    
    public int remaining() {
        if (_currentDataLength == DATA_LEN_NEEDS_TO_BE_READ) {
            
            return 0;
        }
        return _currentDataLength - _currentDataOffset;
    }

    
    private boolean isContinueNext() {
        if (_currentDataLength != DATA_LEN_NEEDS_TO_BE_READ && _currentDataOffset != _currentDataLength) {
            throw new IllegalStateException("Should never be called before end of current record");
        }
        if (!hasNextRecord()) {
            return false;
        }
        
        
        
        
        
        return _nextSid == ContinueRecord.sid;
    }

    
    public int getNextSid() {
        return _nextSid;
    }

    
    @SuppressWarnings("serial")
    public static final class LeftoverDataException extends RuntimeException {
        public LeftoverDataException(int sid, int remainingByteCount) {
            super("Initialisation of record 0x" + Integer.toHexString(sid).toUpperCase()
                    + " left " + remainingByteCount + " bytes remaining still to be read.");
        }
    }

    private static final class SimpleHeaderInput implements BiffHeaderInput {

        private final LittleEndianInput _lei;

        public SimpleHeaderInput(InputStream in) {
            _lei = getLEI(in);
        }

        public int available() {
            return _lei.available();
        }

        public int readDataSize() {
            return _lei.readUShort();
        }

        public int readRecordSID() {
            return _lei.readUShort();
        }
    }
}
