

package com.document.render.office.fc.poifs.storage;


public final class DataInputBlock {


    private final byte[] _buf;
    private int _readIndex;
    private int _maxIndex;

    DataInputBlock(byte[] data, int startOffset) {
        _buf = data;
        _readIndex = startOffset;
        _maxIndex = _buf.length;
    }

    public int available() {
        return _maxIndex - _readIndex;
    }

    public int readUByte() {
        return _buf[_readIndex++] & 0xFF;
    }


    public int readUShortLE() {
        int i = _readIndex;

        int b0 = _buf[i++] & 0xFF;
        int b1 = _buf[i++] & 0xFF;
        _readIndex = i;
        return (b1 << 8) + (b0 << 0);
    }


    public int readUShortLE(DataInputBlock prevBlock) {

        int i = prevBlock._buf.length - 1;

        int b0 = prevBlock._buf[i++] & 0xFF;
        int b1 = _buf[_readIndex++] & 0xFF;
        return (b1 << 8) + (b0 << 0);
    }


    public int readIntLE() {
        int i = _readIndex;

        int b0 = _buf[i++] & 0xFF;
        int b1 = _buf[i++] & 0xFF;
        int b2 = _buf[i++] & 0xFF;
        int b3 = _buf[i++] & 0xFF;
        _readIndex = i;
        return (b3 << 24) + (b2 << 16) + (b1 << 8) + (b0 << 0);
    }


    public int readIntLE(DataInputBlock prevBlock, int prevBlockAvailable) {
        byte[] buf = new byte[4];

        readSpanning(prevBlock, prevBlockAvailable, buf);
        int b0 = buf[0] & 0xFF;
        int b1 = buf[1] & 0xFF;
        int b2 = buf[2] & 0xFF;
        int b3 = buf[3] & 0xFF;
        return (b3 << 24) + (b2 << 16) + (b1 << 8) + (b0 << 0);
    }


    public long readLongLE() {
        int i = _readIndex;

        int b0 = _buf[i++] & 0xFF;
        int b1 = _buf[i++] & 0xFF;
        int b2 = _buf[i++] & 0xFF;
        int b3 = _buf[i++] & 0xFF;
        int b4 = _buf[i++] & 0xFF;
        int b5 = _buf[i++] & 0xFF;
        int b6 = _buf[i++] & 0xFF;
        int b7 = _buf[i++] & 0xFF;
        _readIndex = i;
        return (((long) b7 << 56) +
                ((long) b6 << 48) +
                ((long) b5 << 40) +
                ((long) b4 << 32) +
                ((long) b3 << 24) +
                (b2 << 16) +
                (b1 << 8) +
                (b0 << 0));
    }


    public long readLongLE(DataInputBlock prevBlock, int prevBlockAvailable) {
        byte[] buf = new byte[8];

        readSpanning(prevBlock, prevBlockAvailable, buf);

        int b0 = buf[0] & 0xFF;
        int b1 = buf[1] & 0xFF;
        int b2 = buf[2] & 0xFF;
        int b3 = buf[3] & 0xFF;
        int b4 = buf[4] & 0xFF;
        int b5 = buf[5] & 0xFF;
        int b6 = buf[6] & 0xFF;
        int b7 = buf[7] & 0xFF;
        return (((long) b7 << 56) +
                ((long) b6 << 48) +
                ((long) b5 << 40) +
                ((long) b4 << 32) +
                ((long) b3 << 24) +
                (b2 << 16) +
                (b1 << 8) +
                (b0 << 0));
    }


    private void readSpanning(DataInputBlock prevBlock, int prevBlockAvailable, byte[] buf) {
        System.arraycopy(prevBlock._buf, prevBlock._readIndex, buf, 0, prevBlockAvailable);
        int secondReadLen = buf.length - prevBlockAvailable;
        System.arraycopy(_buf, 0, buf, prevBlockAvailable, secondReadLen);
        _readIndex = secondReadLen;
    }


    public void readFully(byte[] buf, int off, int len) {
        System.arraycopy(_buf, _readIndex, buf, off, len);
        _readIndex += len;
    }
}
