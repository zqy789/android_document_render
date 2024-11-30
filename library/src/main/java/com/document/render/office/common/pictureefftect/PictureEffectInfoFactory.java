
package com.document.render.office.common.pictureefftect;

import android.graphics.Color;

import com.document.render.office.fc.ddf.EscherOptRecord;
import com.document.render.office.fc.ddf.EscherProperties;
import com.document.render.office.fc.ddf.EscherProperty;
import com.document.render.office.fc.ddf.EscherSimpleProperty;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.fc.ppt.reader.ReaderKit;

import java.util.Iterator;


public class PictureEffectInfoFactory {

    public static PictureEffectInfo getPictureEffectInfor(Element blipFill) {
        if (blipFill == null) {
            return null;
        }

        PictureEffectInfo effectInfor = new PictureEffectInfo();
        boolean validateInfor = false;

        Element e = blipFill.element("srcRect");
        String value = null;
        if (e != null) {


            float left = 0;
            value = e.attributeValue("l");
            if (value != null) {
                left = Integer.parseInt(value) / 100000f;
            }


            float top = 0;
            value = e.attributeValue("t");
            if (value != null) {
                top = Integer.parseInt(value) / 100000f;
            }


            float right = 0;
            value = e.attributeValue("r");
            if (value != null) {
                right = Integer.parseInt(value) / 100000f;
            }


            float bottom = 0;
            value = e.attributeValue("b");
            if (value != null) {
                bottom = Integer.parseInt(value) / 100000f;
            }

            if (left != 0 || top != 0 || right != 0 || bottom != 0) {
                PictureCroppedInfo croppedInfor = new PictureCroppedInfo(left, top, right, bottom);

                validateInfor = true;
                effectInfor.setPictureCroppedInfor(croppedInfor);
            }
        }


        Element blip = blipFill.element("blip");

        if (blip.element("grayscl") != null) {
            validateInfor = true;
            effectInfor.setGrayScale(true);
        }



        e = blip.element("biLevel");
        if (e != null) {
            value = e.attributeValue("thresh");
            if (value != null) {
                validateInfor = true;
                effectInfor.setBlackWhiteThreshold(Integer.parseInt(value) / 100000f * 255);
            }
        }


        e = blip.element("lum");
        if (e != null) {

            value = e.attributeValue("bright");
            if (value != null) {
                validateInfor = true;
                float b = Integer.parseInt(value) / 100000f;
                effectInfor.setBrightness(b * 255);
            }


            value = e.attributeValue("contrast");
            if (value != null) {
                validateInfor = true;
                float c = Integer.parseInt(value) / 100000f;
                if (c > 0) {
                    effectInfor.setContrast(1 + c * 9);
                } else {
                    effectInfor.setContrast(1 + c);
                }

            }
        }


        e = blip.element("clrChange");
        if (e != null && (e = e.element("clrFrom")) != null) {
            validateInfor = true;
            effectInfor.setTransparentColor(ReaderKit.instance().getColor(null, e));
        }

        if (validateInfor) {
            return effectInfor;
        }

        return null;
    }


    public static PictureEffectInfo getPictureEffectInfor_ImageData(Element imagedata) {
        if (imagedata != null) {
            PictureEffectInfo effectInfor = new PictureEffectInfo();
            boolean validateInfor = false;


            float left = 0;
            String value = imagedata.attributeValue("cropleft");
            if (value != null) {
                left = Float.parseFloat(value) / 65535;
            }


            value = imagedata.attributeValue("croptop");
            float top = 0;
            if (value != null) {
                top = Float.parseFloat(value) / 65535;
            }


            value = imagedata.attributeValue("cropright");
            float right = 0;
            if (value != null) {
                right = Float.parseFloat(value) / 65535;
            }


            value = imagedata.attributeValue("cropbottom");
            float bottom = 0;
            if (value != null) {
                bottom = Float.parseFloat(value) / 65535;
            }

            if (left != 0 || top != 0 || right != 0 || bottom != 0) {
                PictureCroppedInfo croppedInfor = new PictureCroppedInfo(left, top, right, bottom);

                validateInfor = true;
                effectInfor.setPictureCroppedInfor(croppedInfor);
            }


            value = imagedata.attributeValue("blacklevel");
            if (value != null) {
                validateInfor = true;
                float blacklevel = 0;
                if (value.contains("f")) {
                    blacklevel = Float.parseFloat(value) / 65535 * 2;
                } else {
                    blacklevel = Float.parseFloat(value) * 2;
                }
                effectInfor.setBrightness(blacklevel * 255);
            }


            value = imagedata.attributeValue("gain");
            if (value != null) {
                validateInfor = true;
                float gain = 0;
                if (value.contains("f")) {
                    gain = Float.parseFloat(value) / 65535;
                } else {
                    gain = Float.parseFloat(value);
                }
                effectInfor.setContrast(gain);
            }


            String grayscale = imagedata.attributeValue("grayscale");
            if (grayscale != null && (grayscale.equalsIgnoreCase("t") || grayscale.equalsIgnoreCase("true"))) {
                validateInfor = true;

                String bilevel = imagedata.attributeValue("bilevel");
                if (bilevel != null && (bilevel.equalsIgnoreCase("t") || bilevel.equalsIgnoreCase("true"))) {
                    effectInfor.setBlackWhiteThreshold(128);
                } else {
                    effectInfor.setGrayScale(true);
                }
            }



            String chromakey = imagedata.attributeValue("chromakey");
            if (chromakey != null) {
                validateInfor = true;
                effectInfor.setTransparentColor(Color.parseColor(chromakey));
            }

            if (validateInfor) {
                return effectInfor;
            }
        }

        return null;
    }


    public static EscherProperty getEscherProperty(EscherOptRecord opt, int propId) {
        if (opt != null)
            for (Iterator iterator = opt.getEscherProperties().iterator(); iterator.hasNext(); ) {
                EscherProperty prop = (EscherProperty) iterator.next();
                if (prop.getPropertyNumber() == propId)
                    return prop;
            }
        return null;
    }

    public static PictureEffectInfo getPictureEffectInfor(com.document.render.office.fc.hwpf.usermodel.Picture pic) {
        if (pic == null) {
            return null;
        }

        PictureEffectInfo effectInfor = new PictureEffectInfo();
        boolean validateInfor = false;

        String value = null;

        float left = pic.getDxaCropLeft();
        float top = pic.getDyaCropTop();
        float right = pic.getDxaCropRight();
        float bottom = pic.getDyaCropBottom();

        if (left != 0 || top != 0 || right != 0 || bottom != 0) {
            PictureCroppedInfo croppedInfor = new PictureCroppedInfo(left, top, right, bottom);

            validateInfor = true;
            effectInfor.setPictureCroppedInfor(croppedInfor);
        }


        if (pic.isSetBright()) {
            validateInfor = true;
            effectInfor.setBrightness(pic.getBright());
        }
        if (pic.isSetContrast()) {
            validateInfor = true;
            effectInfor.setContrast(pic.getContrast());
        }
        if (pic.isSetGrayScl()) {
            validateInfor = true;
            effectInfor.setGrayScale(true);
        }
        if (pic.isSetThreshold()) {
            validateInfor = true;
            effectInfor.setBlackWhiteThreshold(pic.getThreshold());
        }



        if (validateInfor) {
            return effectInfor;
        }

        return null;
    }

    public static PictureEffectInfo getPictureEffectInfor(EscherOptRecord opt) {
        if (opt == null) {
            return null;
        }

        PictureEffectInfo effectInfor = new PictureEffectInfo();
        boolean validateInfor = false;

        String value = null;

        EscherSimpleProperty prop = (EscherSimpleProperty) getEscherProperty(opt, EscherProperties.BLIP__CROPFROMLEFT);
        float left = (prop == null ? 0 : prop.getPropertyValue() / 65536f);


        prop = (EscherSimpleProperty) getEscherProperty(opt, EscherProperties.BLIP__CROPFROMTOP);
        float top = (prop == null ? 0 : prop.getPropertyValue() / 65536f);


        prop = (EscherSimpleProperty) getEscherProperty(opt, EscherProperties.BLIP__CROPFROMRIGHT);
        float right = (prop == null ? 0 : prop.getPropertyValue() / 65536f);


        prop = (EscherSimpleProperty) getEscherProperty(opt, EscherProperties.BLIP__CROPFROMBOTTOM);
        float bottom = (prop == null ? 0 : prop.getPropertyValue() / 65536f);

        if (left != 0 || top != 0 || right != 0 || bottom != 0) {
            PictureCroppedInfo croppedInfor = new PictureCroppedInfo(left, top, right, bottom);

            validateInfor = true;
            effectInfor.setPictureCroppedInfor(croppedInfor);
        }



        prop = (EscherSimpleProperty) getEscherProperty(opt, EscherProperties.BLIP__PICTUREACTIVE);
        if (prop != null) {
            int propValue = (prop.getPropertyValue() & 0x0F);
            if (propValue == 4) {

                validateInfor = true;
                effectInfor.setGrayScale(true);
            } else if (propValue == 6) {

                validateInfor = true;
                effectInfor.setBlackWhiteThreshold(128);
            }

        }


        prop = (EscherSimpleProperty) getEscherProperty(opt, EscherProperties.BLIP__BRIGHTNESSSETTING);
        if (prop != null) {
            validateInfor = true;
            effectInfor.setBrightness(prop.getPropertyValue() / 32768f * 255);
        }


        prop = (EscherSimpleProperty) getEscherProperty(opt, EscherProperties.BLIP__CONTRASTSETTING);
        if (prop != null) {
            validateInfor = true;
            effectInfor.setContrast(Math.min(prop.getPropertyValue() / 65536f, 10));
        }


        prop = (EscherSimpleProperty) getEscherProperty(opt, EscherProperties.BLIP__TRANSPARENTCOLOR);
        if (prop != null) {
            validateInfor = true;
            int color = prop.getPropertyValue();
            int r = color & 0xFF;
            int g = (color & 0xFF00) >> 8;
            int b = (color & 0xFF0000) >> 16;
            effectInfor.setTransparentColor(Color.rgb(r, g, b));
        }

        if (validateInfor) {
            return effectInfor;
        }

        return null;
    }
}
