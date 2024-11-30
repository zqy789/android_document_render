

package com.document.render.office.fc.util;


public interface DelayableLittleEndianOutput extends LittleEndianOutput {

    LittleEndianOutput createDelayedOutput(int size);
}
