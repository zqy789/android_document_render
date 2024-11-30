

package com.document.render.office.fc.hssf.record.crypto;

import com.document.render.office.fc.hssf.record.BOFRecord;
import com.document.render.office.fc.hssf.record.FilePassRecord;
import com.document.render.office.fc.hssf.record.InterfaceHdrRecord;


final class Biff8RC4 {

    private static final int RC4_REKEYING_INTERVAL = 1024;
    private final Biff8EncryptionKey _key;
    private RC4 _rc4;

    private int _streamPos;
    private int _nextRC4BlockStart;
    private int _currentKeyIndex;
    private boolean _shouldSkipEncryptionOnCurrentRecord;

    public Biff8RC4(int initialOffset, Biff8EncryptionKey key) {
        if (initialOffset >= RC4_REKEYING_INTERVAL) {
            throw new RuntimeException("initialOffset (" + initialOffset + ")>"
                    + RC4_REKEYING_INTERVAL + " not supported yet");
        }
        _key = key;
        _streamPos = 0;
        rekeyForNextBlock();
        _streamPos = initialOffset;
        for (int i = initialOffset; i > 0; i--) {
            _rc4.output();
        }
        _shouldSkipEncryptionOnCurrentRecord = false;
    }


    private static boolean isNeverEncryptedRecord(int sid) {
        switch (sid) {
            case BOFRecord.sid:



            case InterfaceHdrRecord.sid:


            case FilePassRecord.sid:







                return true;
        }
        return false;
    }

    private void rekeyForNextBlock() {
        _currentKeyIndex = _streamPos / RC4_REKEYING_INTERVAL;
        _rc4 = _key.createRC4(_currentKeyIndex);
        _nextRC4BlockStart = (_currentKeyIndex + 1) * RC4_REKEYING_INTERVAL;
    }

    private int getNextRC4Byte() {
        if (_streamPos >= _nextRC4BlockStart) {
            rekeyForNextBlock();
        }
        byte mask = _rc4.output();
        _streamPos++;
        if (_shouldSkipEncryptionOnCurrentRecord) {
            return 0;
        }
        return mask & 0xFF;
    }

    public void startRecord(int currentSid) {
        _shouldSkipEncryptionOnCurrentRecord = isNeverEncryptedRecord(currentSid);
    }


    public void skipTwoBytes() {
        getNextRC4Byte();
        getNextRC4Byte();
    }

    public void xor(byte[] buf, int pOffset, int pLen) {
        int nLeftInBlock;
        nLeftInBlock = _nextRC4BlockStart - _streamPos;
        if (pLen <= nLeftInBlock) {

            _rc4.encrypt(buf, pOffset, pLen);
            _streamPos += pLen;
            return;
        }

        int offset = pOffset;
        int len = pLen;


        if (len > nLeftInBlock) {
            if (nLeftInBlock > 0) {
                _rc4.encrypt(buf, offset, nLeftInBlock);
                _streamPos += nLeftInBlock;
                offset += nLeftInBlock;
                len -= nLeftInBlock;
            }
            rekeyForNextBlock();
        }

        while (len > RC4_REKEYING_INTERVAL) {
            _rc4.encrypt(buf, offset, RC4_REKEYING_INTERVAL);
            _streamPos += RC4_REKEYING_INTERVAL;
            offset += RC4_REKEYING_INTERVAL;
            len -= RC4_REKEYING_INTERVAL;
            rekeyForNextBlock();
        }

        _rc4.encrypt(buf, offset, len);
        _streamPos += len;
    }

    public int xorByte(int rawVal) {
        int mask = getNextRC4Byte();
        return (byte) (rawVal ^ mask);
    }

    public int xorShort(int rawVal) {
        int b0 = getNextRC4Byte();
        int b1 = getNextRC4Byte();
        int mask = (b1 << 8) + (b0 << 0);
        return rawVal ^ mask;
    }

    public int xorInt(int rawVal) {
        int b0 = getNextRC4Byte();
        int b1 = getNextRC4Byte();
        int b2 = getNextRC4Byte();
        int b3 = getNextRC4Byte();
        int mask = (b3 << 24) + (b2 << 16) + (b1 << 8) + (b0 << 0);
        return rawVal ^ mask;
    }

    public long xorLong(long rawVal) {
        int b0 = getNextRC4Byte();
        int b1 = getNextRC4Byte();
        int b2 = getNextRC4Byte();
        int b3 = getNextRC4Byte();
        int b4 = getNextRC4Byte();
        int b5 = getNextRC4Byte();
        int b6 = getNextRC4Byte();
        int b7 = getNextRC4Byte();
        long mask =
                (((long) b7) << 56)
                        + (((long) b6) << 48)
                        + (((long) b5) << 40)
                        + (((long) b4) << 32)
                        + (((long) b3) << 24)
                        + (b2 << 16)
                        + (b1 << 8)
                        + (b0 << 0);
        return rawVal ^ mask;
    }
}
