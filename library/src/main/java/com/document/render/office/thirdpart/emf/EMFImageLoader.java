package com.document.render.office.thirdpart.emf;




import android.graphics.Bitmap;

import com.document.render.office.java.awt.Color;
import com.document.render.office.thirdpart.emf.data.BitmapInfoHeader;
import com.document.render.office.thirdpart.emf.data.BlendFunction;

import java.io.IOException;
import java.util.Arrays;


public class EMFImageLoader {


    public static Bitmap readImage(
            BitmapInfoHeader bmi,
            int width,
            int height,
            EMFInputStream emf,
            int len,
            BlendFunction blendFunction) throws IOException {




        if (bmi.getBitCount() == 1) {









            int blue = emf.readUnsignedByte();
            int green = emf.readUnsignedByte();
            int red = emf.readUnsignedByte();

            emf.readUnsignedByte();

            int color1 = new Color(red, green, blue).getRGB();

            blue = emf.readUnsignedByte();
            green = emf.readUnsignedByte();
            red = emf.readUnsignedByte();

            emf.readUnsignedByte();

            int color2 = new Color(red, green, blue).getRGB();


            Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            int[] data = emf.readUnsignedByte(len - 8);



            int strangeOffset = width % 8;
            if (strangeOffset != 0) {
                strangeOffset = 8 - strangeOffset;
            }


            int pixel = 0;


            int[] mask = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80};


            for (int y = height - 1; y > -1; y--) {
                for (int x = 0; x < width; x++) {
                    int pixelDataGroup = data[pixel / 8];
                    int pixelData = pixelDataGroup & mask[pixel % 8];
                    pixel++;

                    if (pixelData > 0) {

                        result.setPixel(x, y, color2);
                    } else {

                        result.setPixel(x, y, color1);

                    }
                }

                pixel = pixel + strangeOffset;
            }



            return result;

        } else if ((bmi.getBitCount() == 4)
                && (bmi.getCompression() == EMFConstants.BI_RGB)) {








            int colorsUsed = bmi.getClrUsed();







            int[] colors = emf.readUnsignedByte(colorsUsed * 4);




            int[] data = new int[len - (colorsUsed * 4)];
            for (int i = 0; i < (len - (colorsUsed * 4)) / 12; i++) {
                int[] bytes = emf.readUnsignedByte(10);
                emf.readUnsignedByte(2);
                System.arraycopy(bytes, 0, data, i * 10, 10);
            }


            int[] colorTable = new int[256];

            int color = 0;
            for (int i = 0; i < colorsUsed; i++, color = i * 4) {
                colorTable[i] = new Color(
                        colors[color + 2],
                        colors[color + 1],
                        colors[color]).getRGB();
            }



            if (colorsUsed < 256) {
                Arrays.fill(colorTable, colorsUsed, 256, 0);
            }


            Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);


            int pixel = 0;


            for (int y = height - 1; y > -1; y--) {
                for (int x = 0; x < width; x += 2) {
                    if (pixel < data.length) {
                        result.setPixel(x, y, colorTable[data[pixel] % 8]);
                        result.setPixel(x + 1, y, colorTable[data[pixel] % 8]);
                        pixel++;
                    } else {
                        break;
                    }
                }
            }
            return result;
        } else if ((bmi.getBitCount() == 8) &&
                (bmi.getCompression() == EMFConstants.BI_RGB)) {






            int colorsUsed = bmi.getClrUsed();







            int[] colors = emf.readUnsignedByte(colorsUsed * 4);



            int[] data = emf.readUnsignedByte(len - (colorsUsed * 4));


            int[] colorTable = new int[256];

            int color = 0;
            for (int i = 0; i < colorsUsed; i++, color = i * 4) {
                colorTable[i] = new Color(
                        colors[color + 2],
                        colors[color + 1],
                        colors[color]).getRGB();
            }



            if (colorsUsed < 256) {
                Arrays.fill(colorTable, colorsUsed, 256, 0);
            }



            int strangeOffset = width % 4;
            if (strangeOffset != 0) {
                strangeOffset = 4 - strangeOffset;
            }



            Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);


            int pixel = 0;


            for (int y = height - 1; y > -1; y--) {
                for (int x = 0; x < width; x++) {

                    result.setPixel(x, y, colorTable[data[pixel++]]);
                }

                pixel = pixel + strangeOffset;
            }

            return result;
        }




        else if ((bmi.getBitCount() == 16) &&
                (bmi.getCompression() == EMFConstants.BI_RGB)) {









            int[] data = emf.readDWORD(len / 4);



            width = (width + (width % 2)) / 2;

            height = data.length / width / 2;



            Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);





            int off = 0;
            int pixel, neighbor;


            for (int y = height - 1; y > -1; y--, off = off + width) {
                for (int x = 0; x < width; x++) {
                    neighbor = data[off + width];
                    pixel = data[off++];




                    result.setPixel(x, y, new Color(

                            (float) ((pixel & 0x7C00) + (neighbor & 0x7C00)) / 0xF800,
                            (float) ((pixel & 0x3E0) + (neighbor & 0x3E0)) / 0x7C0,
                            (float) ((pixel & 0x1F) + (neighbor & 0x1F)) / 0x3E).getRGB());
                }
            }



            return result;
        }


        else if ((bmi.getBitCount() == 32) &&
                (bmi.getCompression() == EMFConstants.BI_RGB)) {











            Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);


            len /= 4;


            int off = 0;
            int pixel;
            int alpha;








            int sourceConstantAlpha = 0xFF;
            int alphaFormat = EMFConstants.AC_SRC_OVER;

            if (blendFunction != null) {
                sourceConstantAlpha = blendFunction.getSourceConstantAlpha();
                alphaFormat = blendFunction.getAlphaFormat();
            }


            if (alphaFormat != EMFConstants.AC_SRC_ALPHA) {













                for (int y = height - 1; y > -1 && off < len; y--) {
                    for (int x = 0; x < width && off < len; x++, off++) {

                        pixel = emf.readDWORD();


                        result.setPixel(x, y, new Color(
                                (pixel & 0xFF0000) >> 16,
                                (pixel & 0xFF00) >> 8,
                                (pixel & 0xFF),

                                sourceConstantAlpha
                        ).getRGB());
                    }
                }
            }


            else {



                if (sourceConstantAlpha == 0xFF) {








                    for (int y = height - 1; y > -1 && off < len; y--) {
                        for (int x = 0; x < width && off < len; x++, off++) {

                            pixel = emf.readDWORD();
                            alpha = (pixel & 0xFF000000) >> 24;
                            if (alpha == -1) {
                                alpha = 0xFF;
                            }


                            result.setPixel(x, y, new Color(
                                    (pixel & 0xFF0000) >> 16,
                                    (pixel & 0xFF00) >> 8,
                                    (pixel & 0xFF),
                                    alpha
                            ).getRGB());
                        }
                    }
                }






                else {










                    for (int y = height - 1; y > -1 && off < len; y--) {
                        for (int x = 0; x < width && off < len; x++, off++) {

                            pixel = emf.readDWORD();

                            alpha = (pixel & 0xFF000000) >> 24;
                            if (alpha == -1) {
                                alpha = 0xFF;
                            }


                            alpha = alpha * sourceConstantAlpha / 0xFF;


                            result.setPixel(x, y, new Color(
                                    (pixel & 0xFF0000) >> 16,
                                    (pixel & 0xFF00) >> 8,
                                    (pixel & 0xFF),
                                    alpha
                            ).getRGB());
                        }
                    }
                }
            }
                        


            return result;
        }












        else if ((bmi.getBitCount() == 32) &&
                (bmi.getCompression() == EMFConstants.BI_BITFIELDS)) {

            emf.readByte(len);
            return null;
        } else {

            emf.readByte(len);
            return null;
        }
    }
}
