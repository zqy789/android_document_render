

package com.document.render.office.fc.hwpf.sprm;

import com.document.render.office.fc.hwpf.usermodel.BorderCode;
import com.document.render.office.fc.hwpf.usermodel.DateAndTime;
import com.document.render.office.fc.hwpf.usermodel.DropCapSpecifier;
import com.document.render.office.fc.hwpf.usermodel.LineSpacingDescriptor;
import com.document.render.office.fc.hwpf.usermodel.ParagraphProperties;
import com.document.render.office.fc.hwpf.usermodel.ShadingDescriptor;
import com.document.render.office.fc.util.Internal;
import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.POILogFactory;
import com.document.render.office.fc.util.POILogger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Internal
public final class ParagraphSprmUncompressor extends SprmUncompressor {
    private static final POILogger logger = POILogFactory
            .getLogger(ParagraphSprmUncompressor.class);

    public ParagraphSprmUncompressor() {
    }

    public static ParagraphProperties uncompressPAP(ParagraphProperties parent, byte[] grpprl,
                                                    int offset) {
        ParagraphProperties newProperties = null;
        try {
            newProperties = (ParagraphProperties) parent.clone();
        } catch (CloneNotSupportedException cnse) {
            throw new RuntimeException("There is no way this exception should happen!!");
        }
        SprmIterator sprmIt = new SprmIterator(grpprl, offset);

        while (sprmIt.hasNext()) {
            SprmOperation sprm = sprmIt.next();



            if (sprm.getType() == SprmOperation.TYPE_PAP) {
                try {
                    unCompressPAPOperation(newProperties, sprm);
                } catch (Exception exc) {
                    logger.log(POILogger.ERROR,
                            "Unable to apply SPRM operation '" + sprm.getOperation() + "': ", exc);
                }
            }
        }

        return newProperties;
    }


    static void unCompressPAPOperation(ParagraphProperties newPAP, SprmOperation sprm) {
        switch (sprm.getOperation()) {
            case 0:
                newPAP.setIstd(sprm.getOperand());
                break;
            case 0x1:








                break;
            case 0x2:
                if (newPAP.getIstd() <= 9 || newPAP.getIstd() >= 1) {
                    byte paramTmp = (byte) sprm.getOperand();
                    newPAP.setIstd(newPAP.getIstd() + paramTmp);
                    newPAP.setLvl((byte) (newPAP.getLvl() + paramTmp));

                    if (((paramTmp >> 7) & 0x01) == 1) {
                        newPAP.setIstd(Math.max(newPAP.getIstd(), 1));
                    } else {
                        newPAP.setIstd(Math.min(newPAP.getIstd(), 9));
                    }

                }
                break;
            case 0x3:

                newPAP.setJc((byte) sprm.getOperand());
                break;
            case 0x4:
                newPAP.setFSideBySide(sprm.getOperand() != 0);
                break;
            case 0x5:
                newPAP.setFKeep(sprm.getOperand() != 0);
                break;
            case 0x6:
                newPAP.setFKeepFollow(sprm.getOperand() != 0);
                break;
            case 0x7:
                newPAP.setFPageBreakBefore(sprm.getOperand() != 0);
                break;
            case 0x8:
                newPAP.setBrcl((byte) sprm.getOperand());
                break;
            case 0x9:
                newPAP.setBrcp((byte) sprm.getOperand());
                break;
            case 0xa:
                newPAP.setIlvl((byte) sprm.getOperand());
                break;
            case 0xb:
                newPAP.setIlfo(sprm.getOperand());
                break;
            case 0xc:
                newPAP.setFNoLnn(sprm.getOperand() != 0);
                break;
            case 0xd:

                handleTabs(newPAP, sprm);
                break;
            case 0xe:
                newPAP.setDxaRight(sprm.getOperand());
                break;
            case 0xf:
                newPAP.setDxaLeft(sprm.getOperand());
                break;
            case 0x10:


                newPAP.setDxaLeft(newPAP.getDxaLeft() + sprm.getOperand());
                newPAP.setDxaLeft(Math.max(0, newPAP.getDxaLeft()));
                break;
            case 0x11:
                newPAP.setDxaLeft1(sprm.getOperand());
                break;
            case 0x12:
                newPAP.setLspd(new LineSpacingDescriptor(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            case 0x13:
                newPAP.setDyaBefore(sprm.getOperand());
                break;
            case 0x14:
                newPAP.setDyaAfter(sprm.getOperand());
                break;
            case 0x15:


                break;
            case 0x16:

                newPAP.setFInTable(sprm.getOperand() != 0);
                break;
            case 0x17:
                newPAP.setFTtp(sprm.getOperand() != 0);
                break;
            case 0x18:
                newPAP.setDxaAbs(sprm.getOperand());
                break;
            case 0x19:
                newPAP.setDyaAbs(sprm.getOperand());
                break;
            case 0x1a:
                newPAP.setDxaWidth(sprm.getOperand());
                break;
            case 0x1b:
                byte param = (byte) sprm.getOperand();

                byte pcVert = (byte) ((param & 0x0c) >> 2);
                byte pcHorz = (byte) (param & 0x03);
                if (pcVert != 3) {
                    newPAP.setPcVert(pcVert);
                }
                if (pcHorz != 3) {
                    newPAP.setPcHorz(pcHorz);
                }
                break;


            case 0x1c:


                break;
            case 0x1d:


                break;
            case 0x1e:


                break;
            case 0x1f:


                break;
            case 0x20:


                break;
            case 0x21:


                break;
            case 0x22:
                newPAP.setDxaFromText(sprm.getOperand());
                break;
            case 0x23:
                newPAP.setWr((byte) sprm.getOperand());
                break;
            case 0x24:
                newPAP.setBrcTop(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            case 0x25:
                newPAP.setBrcLeft(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            case 0x26:
                newPAP.setBrcBottom(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            case 0x27:
                newPAP.setBrcRight(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            case 0x28:
                newPAP.setBrcBetween(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            case 0x29:
                newPAP.setBrcBar(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            case 0x2a:
                newPAP.setFNoAutoHyph(sprm.getOperand() != 0);
                break;
            case 0x2b:
                newPAP.setDyaHeight(sprm.getOperand());
                break;
            case 0x2c:
                newPAP.setDcs(new DropCapSpecifier((short) sprm.getOperand()));
                break;
            case 0x2d:
                newPAP.setShd(new ShadingDescriptor((short) sprm.getOperand()));
                break;
            case 0x2e:
                newPAP.setDyaFromText(sprm.getOperand());
                break;
            case 0x2f:
                newPAP.setDxaFromText(sprm.getOperand());
                break;
            case 0x30:
                newPAP.setFLocked(sprm.getOperand() != 0);
                break;
            case 0x31:
                newPAP.setFWidowControl(sprm.getOperand() != 0);
                break;
            case 0x32:


                break;
            case 0x33:
                newPAP.setFKinsoku(sprm.getOperand() != 0);
                break;
            case 0x34:
                newPAP.setFWordWrap(sprm.getOperand() != 0);
                break;
            case 0x35:
                newPAP.setFOverflowPunct(sprm.getOperand() != 0);
                break;
            case 0x36:
                newPAP.setFTopLinePunct(sprm.getOperand() != 0);
                break;
            case 0x37:
                newPAP.setFAutoSpaceDE(sprm.getOperand() != 0);
                break;
            case 0x38:
                newPAP.setFAutoSpaceDN(sprm.getOperand() != 0);
                break;
            case 0x39:
                newPAP.setWAlignFont(sprm.getOperand());
                break;
            case 0x3a:
                newPAP.setFontAlign((short) sprm.getOperand());
                break;
            case 0x3b:


                break;
            case 0x3e: {
                byte[] buf = new byte[sprm.size() - 3];
                System.arraycopy(buf, 0, sprm.getGrpprl(), sprm.getGrpprlOffset(), buf.length);
                newPAP.setAnld(buf);
                break;
            }
            case 0x3f:


                try {
                    byte[] varParam = sprm.getGrpprl();
                    int offset = sprm.getGrpprlOffset();
                    newPAP.setFPropRMark(varParam[offset] != 0);
                    newPAP.setIbstPropRMark(LittleEndian.getShort(varParam, offset + 1));
                    newPAP.setDttmPropRMark(new DateAndTime(varParam, offset + 3));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 0x40:




            {
                newPAP.setLvl((byte) sprm.getOperand());
            }
            break;
            case 0x41:

                newPAP.setFBiDi(sprm.getOperand() != 0);
                break;
            case 0x43:


                newPAP.setFNumRMIns(sprm.getOperand() != 0);
                break;
            case 0x44:


                break;
            case 0x45:
                if (sprm.getSizeCode() == 6) {
                    byte[] buf = new byte[sprm.size() - 3];
                    System.arraycopy(buf, 0, sprm.getGrpprl(), sprm.getGrpprlOffset(), buf.length);
                    newPAP.setNumrm(buf);
                } else {

                }
                break;

            case 0x47:
                newPAP.setFUsePgsuSettings(sprm.getOperand() != 0);
                break;
            case 0x48:
                newPAP.setFAdjustRight(sprm.getOperand() != 0);
                break;
            case 0x49:

                newPAP.setItap(sprm.getOperand());
                break;
            case 0x4a:

                newPAP.setItap((byte) (newPAP.getItap() + sprm.getOperand()));
                break;
            case 0x4b:

                newPAP.setFInnerTableCell(sprm.getOperand() != 0);
                break;
            case 0x4c:

                newPAP.setFTtpEmbedded(sprm.getOperand() != 0);
                break;
            case 0x61:

                newPAP.setJustificationLogical((byte) sprm.getOperand());
                break;
            default:
                break;
        }
    }

    private static void handleTabs(ParagraphProperties pap, SprmOperation sprm) {
        byte[] grpprl = sprm.getGrpprl();
        int offset = sprm.getGrpprlOffset();
        int delSize = grpprl[offset++];
        int[] tabPositions = pap.getRgdxaTab();
        byte[] tabDescriptors = pap.getRgtbd();

        Map<Integer, Byte> tabMap = new HashMap<Integer, Byte>();
        for (int x = 0; x < tabPositions.length; x++) {
            tabMap.put(Integer.valueOf(tabPositions[x]), Byte.valueOf(tabDescriptors[x]));
        }

        for (int x = 0; x < delSize; x++) {
            short clearPosition = LittleEndian.getShort(grpprl, offset);
            ;
            tabMap.remove(Integer.valueOf(clearPosition));
            pap.setTabClearPosition((short) Math.max(pap.getTabClearPosition(), clearPosition));
            offset += LittleEndian.SHORT_SIZE;
        }

        int addSize = grpprl[offset++];
        int start = offset;
        for (int x = 0; x < addSize; x++) {
            Integer key = Integer.valueOf(LittleEndian.getShort(grpprl, offset));
            Byte val = Byte.valueOf(grpprl[start + ((LittleEndian.SHORT_SIZE * addSize) + x)]);
            tabMap.put(key, val);
            offset += LittleEndian.SHORT_SIZE;
        }

        tabPositions = new int[tabMap.size()];
        tabDescriptors = new byte[tabPositions.length];

        List<Integer> list = new ArrayList<Integer>(tabMap.keySet());
        Collections.sort(list);

        for (int x = 0; x < tabPositions.length; x++) {
            Integer key = list.get(x);
            tabPositions[x] = key.intValue();
            tabDescriptors[x] = tabMap.get(key).byteValue();
        }

        pap.setRgdxaTab(tabPositions);
        pap.setRgtbd(tabDescriptors);
    }




















































}
