

package com.document.render.office.fc.hwpf.usermodel;

import com.document.render.office.fc.poifs.filesystem.DirectoryEntry;
import com.document.render.office.fc.poifs.filesystem.Entry;
import com.document.render.office.fc.util.Internal;
import com.document.render.office.fc.util.POIUtils;

import java.io.FileNotFoundException;
import java.io.IOException;


@Internal
public class ObjectPoolImpl implements ObjectsPool {
    private DirectoryEntry _objectPool;

    public ObjectPoolImpl(DirectoryEntry _objectPool) {
        super();
        this._objectPool = _objectPool;
    }

    public Entry getObjectById(String objId) {
        if (_objectPool == null)
            return null;

        try {
            return _objectPool.getEntry(objId);
        } catch (FileNotFoundException exc) {
            return null;
        }
    }

    @Internal
    public void writeTo(DirectoryEntry directoryEntry) throws IOException {
        if (_objectPool != null)
            POIUtils.copyNodeRecursively(_objectPool, directoryEntry);
    }
}
