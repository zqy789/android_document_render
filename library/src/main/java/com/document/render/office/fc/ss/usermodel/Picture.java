
package com.document.render.office.fc.ss.usermodel;

import com.document.render.office.fc.hssf.usermodel.IClientAnchor;


public interface Picture {

    
    void resize();

    
    void resize(double scale);

    IClientAnchor getPreferredSize();

    
    PictureData getPictureData();

}
