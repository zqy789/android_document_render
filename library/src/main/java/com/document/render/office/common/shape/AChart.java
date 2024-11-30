
package com.document.render.office.common.shape;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;

import com.document.render.office.common.PaintKit;
import com.document.render.office.common.picture.Picture;
import com.document.render.office.system.IControl;
import com.document.render.office.thirdpart.achartengine.chart.AbstractChart;

import java.io.File;
import java.io.FileOutputStream;


public class AChart extends AbstractShape {
    
    private int picIndex = -1;
    private AbstractChart chart;

    public AChart() {
        super();
    }

    
    public short getType() {
        return SHAPE_CHART;
    }

    
    public AbstractChart getAChart() {
        return chart;
    }

    
    public void setAChart(AbstractChart chart) {
        this.chart = chart;
    }

    private void saveChartToPicture(IControl control) {

        Bitmap bmp = null;
        try {
            int width = (int) (rect.width * getAChart().getZoomRate());
            int height = (int) (rect.height * getAChart().getZoomRate());
            bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);

            
            chart.draw(canvas, control, 0, 0, width, height, PaintKit.instance().getPaint());

            canvas.save();

            canvas.restore();
            Picture pic = new Picture();

            String name = String.valueOf(System.currentTimeMillis()) + ".tmp";
            File file = new File(control.getSysKit().getPictureManage().getPicTempPath() + File.separator + name);
            file.createNewFile();

            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            bmp.recycle();
            out.close();

            pic.setTempFilePath(file.getAbsolutePath());
            picIndex = control.getSysKit().getPictureManage().addPicture(pic);
        } catch (Exception e) {
            if (bmp != null) {
                bmp.recycle();
            }
            control.getSysKit().getErrorKit().writerLog(e);
        }
    }

    
    public int getDrawingPicture(IControl control) {
        if (picIndex == -1) {
            saveChartToPicture(control);
        }

        return picIndex;
    }

    public void dispose() {
        super.dispose();
        chart = null;
    }
}
