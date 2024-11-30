
package com.document.render.office.fc.ss.usermodel;

import com.document.render.office.fc.hssf.usermodel.IClientAnchor;


public interface Drawing {

    Picture createPicture(IClientAnchor anchor, int pictureIndex);


    Comment createCellComment(IClientAnchor anchor);


    Chart createChart(IClientAnchor anchor);


    IClientAnchor createAnchor(int dx1, int dy1, int dx2, int dy2, int col1, int row1, int col2, int row2);
}
