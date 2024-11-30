

package com.document.render.office.fc.hwpf.model.io;

import com.document.render.office.fc.util.Internal;

import java.util.HashMap;
import java.util.Map;


@Internal
public final class HWPFFileSystem {
    Map<String, HWPFOutputStream> _streams = new HashMap<String, HWPFOutputStream>();

    public HWPFFileSystem() {
        _streams.put("WordDocument", new HWPFOutputStream());
        _streams.put("1Table", new HWPFOutputStream());
        _streams.put("Data", new HWPFOutputStream());
    }

    public HWPFOutputStream getStream(String name) {
        return _streams.get(name);
    }

}
