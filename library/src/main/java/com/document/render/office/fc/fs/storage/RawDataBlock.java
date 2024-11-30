package com.document.render.office.fc.fs.storage;




public class RawDataBlock {

    private byte[] _data;


    public RawDataBlock(byte[] data) {
        this._data = data;
    }


    public byte[] getData() {
        return _data;
    }
} 

