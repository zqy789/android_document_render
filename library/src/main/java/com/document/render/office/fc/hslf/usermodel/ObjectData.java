

package com.document.render.office.fc.hslf.usermodel;

import com.document.render.office.fc.hslf.record.ExOleObjStg;

import java.io.IOException;
import java.io.InputStream;



public class ObjectData {

    private ExOleObjStg storage;


    public ObjectData(ExOleObjStg storage) {
        this.storage = storage;
    }


    public InputStream getData() {
        return storage.getData();
    }


    public void setData(byte[] data) throws IOException {
        storage.setData(data);
    }


    public ExOleObjStg getExOleObjStg() {
        return storage;
    }


    public void dispose() {
        if (storage != null) {
            storage.dispose();
            storage = null;
        }
    }
}
