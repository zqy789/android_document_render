
package com.document.render.office.thirdpart.emf.font;

import java.io.IOException;


public abstract class TTFVersionTable extends TTFTable {

    public int minorVersion;

    public int majorVersion;

    public void readVersion() throws IOException {
        majorVersion = ttf.readUShort();
        minorVersion = ttf.readUShort();
    }

    public String toString() {
        return super.toString() + " v" + majorVersion + "." + minorVersion;
    }

}
