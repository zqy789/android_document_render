

package com.document.render.office.fc.hwpf.model;

import com.document.render.office.fc.util.Internal;

@Internal
public interface CharIndexTranslator {

    int getByteIndex(int charPos);


    int getCharIndex(int bytePos);


    int getCharIndex(int bytePos, int startCP);



    boolean isIndexInTable(int bytePos);


    public int lookIndexForward(int bytePos);


    public int lookIndexBackward(int bytePos);

}
