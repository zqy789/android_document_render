

package com.document.render.office.fc.hwpf.sprm;

import com.document.render.office.fc.hwpf.usermodel.BorderCode;
import com.document.render.office.fc.hwpf.usermodel.SectionProperties;
import com.document.render.office.fc.util.Internal;


@Internal
public final class SectionSprmUncompressor extends SprmUncompressor {
    public SectionSprmUncompressor() {
    }

    public static SectionProperties uncompressSEP(byte[] grpprl, int offset) {
        SectionProperties newProperties = new SectionProperties();

        SprmIterator sprmIt = new SprmIterator(grpprl, offset);

        while (sprmIt.hasNext()) {
            SprmOperation sprm = sprmIt.next();
            unCompressSEPOperation(newProperties, sprm);
        }

        return newProperties;
    }

    
    static void unCompressSEPOperation(SectionProperties newSEP, SprmOperation sprm) {
        switch (sprm.getOperation()) {
            case 0:
                newSEP.setCnsPgn((byte) sprm.getOperand());
                break;
            case 0x1:
                newSEP.setIHeadingPgn((byte) sprm.getOperand());
                break;
            case 0x2:
                byte[] buf = new byte[sprm.size() - 3];
                System.arraycopy(sprm.getGrpprl(), sprm.getGrpprlOffset(), buf, 0, buf.length);
                newSEP.setOlstAnm(buf);
                break;
            case 0x3:
                
                break;
            case 0x4:
                
                break;
            case 0x5:
                newSEP.setFEvenlySpaced(getFlag(sprm.getOperand()));
                break;
            case 0x6:
                newSEP.setFUnlocked(getFlag(sprm.getOperand()));
                break;
            case 0x7:
                newSEP.setDmBinFirst((short) sprm.getOperand());
                break;
            case 0x8:
                newSEP.setDmBinOther((short) sprm.getOperand());
                break;
            case 0x9:
                newSEP.setBkc((byte) sprm.getOperand());
                break;
            case 0xa:
                newSEP.setFTitlePage(getFlag(sprm.getOperand()));
                break;
            case 0xb:
                newSEP.setCcolM1((short) sprm.getOperand());
                break;
            case 0xc:
                newSEP.setDxaColumns(sprm.getOperand());
                break;
            case 0xd:
                newSEP.setFAutoPgn(getFlag(sprm.getOperand()));
                break;
            case 0xe:
                newSEP.setNfcPgn((byte) sprm.getOperand());
                break;
            case 0xf:
                newSEP.setDyaPgn((short) sprm.getOperand());
                break;
            case 0x10:
                newSEP.setDxaPgn((short) sprm.getOperand());
                break;
            case 0x11:
                newSEP.setFPgnRestart(getFlag(sprm.getOperand()));
                break;
            case 0x12:
                newSEP.setFEndNote(getFlag(sprm.getOperand()));
                break;
            case 0x13:
                newSEP.setLnc((byte) sprm.getOperand());
                break;
            case 0x14:
                newSEP.setGrpfIhdt((byte) sprm.getOperand());
                break;
            case 0x15:
                newSEP.setNLnnMod((short) sprm.getOperand());
                break;
            case 0x16:
                newSEP.setDxaLnn(sprm.getOperand());
                break;
            case 0x17:
                newSEP.setDyaHdrTop(sprm.getOperand());
                break;
            case 0x18:
                newSEP.setDyaHdrBottom(sprm.getOperand());
                break;
            case 0x19:
                newSEP.setFLBetween(getFlag(sprm.getOperand()));
                break;
            case 0x1a:
                newSEP.setVjc((byte) sprm.getOperand());
                break;
            case 0x1b:
                newSEP.setLnnMin((short) sprm.getOperand());
                break;
            case 0x1c:
                newSEP.setPgnStart((short) sprm.getOperand());
                break;
            case 0x1d:
                newSEP.setDmOrientPage(sprm.getOperand() != 0);
                break;
            case 0x1e:

                
                break;
            case 0x1f:
                newSEP.setXaPage(sprm.getOperand());
                break;
            case 0x20:
                newSEP.setYaPage(sprm.getOperand());
                break;
            case 0x21:
                newSEP.setDxaLeft(sprm.getOperand());
                break;
            case 0x22:
                newSEP.setDxaRight(sprm.getOperand());
                break;
            case 0x23:
                newSEP.setDyaTop(sprm.getOperand());
                break;
            case 0x24:
                newSEP.setDyaBottom(sprm.getOperand());
                break;
            case 0x25:
                newSEP.setDzaGutter(sprm.getOperand());
                break;
            case 0x26:
                newSEP.setDmPaperReq((short) sprm.getOperand());
                break;
            case 0x27:
                newSEP.setFPropMark(getFlag(sprm.getOperand()));
                break;
            case 0x28:
                break;
            case 0x29:
                break;
            case 0x2a:
                break;
            case 0x2b:
                newSEP.setBrcTop(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            case 0x2c:
                newSEP.setBrcLeft(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            case 0x2d:
                newSEP.setBrcBottom(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            case 0x2e:
                newSEP.setBrcRight(new BorderCode(sprm.getGrpprl(), sprm.getGrpprlOffset()));
                break;
            case 0x2f:
                newSEP.setPgbProp(sprm.getOperand());
                break;
            case 0x30:
                newSEP.setDxtCharSpace(sprm.getOperand());
                break;
            case 0x31:
                newSEP.setDyaLinePitch(sprm.getOperand());
                break;
            case 0x32:
                newSEP.setClm(sprm.getOperand());
                break;
            case 0x33:
                newSEP.setWTextFlow((short) sprm.getOperand());
                break;
            default:
                break;
        }

    }


}
