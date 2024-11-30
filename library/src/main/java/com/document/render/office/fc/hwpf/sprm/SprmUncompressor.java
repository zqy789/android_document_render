

package com.document.render.office.fc.hwpf.sprm;

import com.document.render.office.fc.util.Internal;

@Internal
public abstract class SprmUncompressor {
    protected SprmUncompressor() {
    }


    public static boolean getFlag(int x) {
        if (x != 0) {
            return true;
        }
        return false;
    }

}
