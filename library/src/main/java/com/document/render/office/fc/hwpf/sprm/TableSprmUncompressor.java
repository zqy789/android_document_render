

package com.document.render.office.fc.hwpf.sprm;

import com.document.render.office.fc.hwpf.usermodel.BorderCode;
import com.document.render.office.fc.hwpf.usermodel.TableCellDescriptor;
import com.document.render.office.fc.hwpf.usermodel.TableProperties;
import com.document.render.office.fc.util.Internal;
import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.POILogFactory;
import com.document.render.office.fc.util.POILogger;


@Internal
public final class TableSprmUncompressor extends SprmUncompressor {
    private static final POILogger logger = POILogFactory.getLogger(TableSprmUncompressor.class);

    public TableSprmUncompressor() {
    }

    @Deprecated
    public static TableProperties uncompressTAP(byte[] grpprl, int offset) {
        TableProperties newProperties = new TableProperties();

        SprmIterator sprmIt = new SprmIterator(grpprl, offset);

        while (sprmIt.hasNext()) {
            SprmOperation sprm = sprmIt.next();



            if (sprm.getType() == SprmOperation.TAP_TYPE) {
                try {
                    unCompressTAPOperation(newProperties, sprm);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    logger.log(POILogger.ERROR, "Unable to apply ", sprm, ": ", ex, ex);
                }
            }
        }

        return newProperties;
    }

    public static TableProperties uncompressTAP(SprmBuffer sprmBuffer) {
        TableProperties tableProperties;

        SprmOperation sprmOperation = sprmBuffer.findSprm((short) 0xd608);
        if (sprmOperation != null) {
            byte[] grpprl = sprmOperation.getGrpprl();
            int offset = sprmOperation.getGrpprlOffset();
            short itcMac = grpprl[offset];
            tableProperties = new TableProperties(itcMac);
        } else {
            logger.log(POILogger.WARN, "Some table rows didn't specify number of columns in SPRMs");
            tableProperties = new TableProperties((short) 1);
        }
        sprmOperation = sprmBuffer.findSprm((short) 0xd633);
        for (SprmIterator iterator = sprmBuffer.iterator(); iterator.hasNext(); ) {
            SprmOperation sprm = iterator.next();


            if (sprm.getType() == SprmOperation.TYPE_TAP
                    || sprm.getType() == SprmOperation.TYPE_PAP) {
                try {
                    unCompressTAPOperation(tableProperties, sprm);
                } catch (ArrayIndexOutOfBoundsException ex) {
                    logger.log(POILogger.ERROR, "Unable to apply ", sprm, ": ", ex, ex);
                }
            }
        }
        return tableProperties;
    }


    static void unCompressTAPOperation(TableProperties newTAP, SprmOperation sprm) {
        switch (sprm.getOperation()) {
            case 0:
                newTAP.setJc((short) sprm.getOperand());
                break;
            case 0x01: {
                short[] rgdxaCenter = newTAP.getRgdxaCenter();
                short itcMac = newTAP.getItcMac();
                int adjust = sprm.getOperand() - (rgdxaCenter[0] + newTAP.getDxaGapHalf());
                for (int x = 0; x < itcMac; x++) {
                    rgdxaCenter[x] += adjust;
                }
                break;
            }
            case 0x02: {
                short[] rgdxaCenter = newTAP.getRgdxaCenter();
                if (rgdxaCenter != null) {
                    int adjust = newTAP.getDxaGapHalf() - sprm.getOperand();
                    rgdxaCenter[0] += adjust;
                }
                newTAP.setDxaGapHalf(sprm.getOperand());
                break;
            }
            case 0x03:
                newTAP.setFCantSplit(getFlag(sprm.getOperand()));
                break;
            case 0x04:
                newTAP.setFTableHeader(getFlag(sprm.getOperand()));
                break;
            case 0x05: {
                byte[] buf = sprm.getGrpprl();
                int offset = sprm.getGrpprlOffset();
                newTAP.setBrcTop(new BorderCode(buf, offset));
                offset += BorderCode.SIZE;
                newTAP.setBrcLeft(new BorderCode(buf, offset));
                offset += BorderCode.SIZE;
                newTAP.setBrcBottom(new BorderCode(buf, offset));
                offset += BorderCode.SIZE;
                newTAP.setBrcRight(new BorderCode(buf, offset));
                offset += BorderCode.SIZE;
                newTAP.setBrcHorizontal(new BorderCode(buf, offset));
                offset += BorderCode.SIZE;
                newTAP.setBrcVertical(new BorderCode(buf, offset));
                break;
            }
            case 0x06:


                break;
            case 0x07:
                newTAP.setDyaRowHeight(sprm.getOperand());
                break;
            case 0x08: {
                byte[] grpprl = sprm.getGrpprl();
                int offset = sprm.getGrpprlOffset();
                short itcMac = grpprl[offset];
                short[] rgdxaCenter = new short[itcMac + 1];
                TableCellDescriptor[] rgtc = new TableCellDescriptor[itcMac];

                newTAP.setItcMac(itcMac);
                newTAP.setRgdxaCenter(rgdxaCenter);
                newTAP.setRgtc(rgtc);


                for (int x = 0; x < itcMac; x++) {
                    rgdxaCenter[x] = LittleEndian.getShort(grpprl, offset + (1 + (x * 2)));
                }


                int endOfSprm = offset + sprm.size() - 6;
                int startOfTCs = offset + (1 + (itcMac + 1) * 2);

                boolean hasTCs = startOfTCs < endOfSprm;

                for (int x = 0; x < itcMac; x++) {

                    if (hasTCs && offset + (1 + ((itcMac + 1) * 2) + (x * 20)) < grpprl.length)
                        rgtc[x] = TableCellDescriptor.convertBytesToTC(grpprl, offset
                                + (1 + ((itcMac + 1) * 2) + (x * 20)));
                    else
                        rgtc[x] = new TableCellDescriptor();
                }

                rgdxaCenter[itcMac] = LittleEndian.getShort(grpprl, offset + (1 + (itcMac * 2)));
                break;
            }
            case 0x09:


                break;
            case 0x0a:


                break;
            case 0x20:

































                break;
            case 0x21: {
                int param = sprm.getOperand();
                int index = (param & 0xff000000) >> 24;
                int count = (param & 0x00ff0000) >> 16;
                int width = (param & 0x0000ffff);
                int itcMac = newTAP.getItcMac();

                short[] rgdxaCenter = new short[itcMac + count + 1];
                TableCellDescriptor[] rgtc = new TableCellDescriptor[itcMac + count];
                if (index >= itcMac) {
                    index = itcMac;
                    System.arraycopy(newTAP.getRgdxaCenter(), 0, rgdxaCenter, 0, itcMac + 1);
                    System.arraycopy(newTAP.getRgtc(), 0, rgtc, 0, itcMac);
                } else {

                    System.arraycopy(newTAP.getRgdxaCenter(), 0, rgdxaCenter, 0, index + 1);
                    System.arraycopy(newTAP.getRgdxaCenter(), index + 1, rgdxaCenter,
                            index + count, itcMac - (index));

                    System.arraycopy(newTAP.getRgtc(), 0, rgtc, 0, index);
                    System.arraycopy(newTAP.getRgtc(), index, rgtc, index + count, itcMac - index);
                }

                for (int x = index; x < index + count; x++) {
                    rgtc[x] = new TableCellDescriptor();
                    rgdxaCenter[x] = (short) (rgdxaCenter[x - 1] + width);
                }
                rgdxaCenter[index + count] = (short) (rgdxaCenter[(index + count) - 1] + width);
                break;
            }

            case 0x22:
            case 0x23:
            case 0x24:
            case 0x25:
            case 0x26:
            case 0x27:
            case 0x28:
            case 0x29:
            case 0x2a:
            case 0x2b:
            case 0x2c:
                break;
            case 0x33:
                newTAP.setTCellSpacingDefault(LittleEndian.getShort(sprm.getGrpprl(), sprm.getGrpprlOffset() + 4));
                break;
            case 0x34:


                byte itcFirst = sprm.getGrpprl()[sprm.getGrpprlOffset()];
                byte itcLim = sprm.getGrpprl()[sprm.getGrpprlOffset() + 1];
                byte grfbrc = sprm.getGrpprl()[sprm.getGrpprlOffset() + 2];
                byte ftsWidth = sprm.getGrpprl()[sprm.getGrpprlOffset() + 3];
                short wWidth = LittleEndian.getShort(sprm.getGrpprl(), sprm.getGrpprlOffset() + 4);

                for (int c = itcFirst; c < itcLim; c++) {
                    TableCellDescriptor tableCellDescriptor = newTAP.getRgtc()[c];

                    if ((grfbrc & 0x01) != 0) {
                        tableCellDescriptor.setFtsCellPaddingTop(ftsWidth);
                        tableCellDescriptor.setWCellPaddingTop(wWidth);
                    }
                    if ((grfbrc & 0x02) != 0) {
                        tableCellDescriptor.setFtsCellPaddingLeft(ftsWidth);
                        tableCellDescriptor.setWCellPaddingLeft(wWidth);
                    }
                    if ((grfbrc & 0x04) != 0) {
                        tableCellDescriptor.setFtsCellPaddingBottom(ftsWidth);
                        tableCellDescriptor.setWCellPaddingBottom(wWidth);
                    }
                    if ((grfbrc & 0x08) != 0) {
                        tableCellDescriptor.setFtsCellPaddingRight(ftsWidth);
                        tableCellDescriptor.setWCellPaddingRight(wWidth);
                    }
                }
                break;
            case 0x61:
                newTAP.setTableIndent((short) (sprm.getOperand() >> 8));
                break;

            default:
                break;
        }
    }

}
