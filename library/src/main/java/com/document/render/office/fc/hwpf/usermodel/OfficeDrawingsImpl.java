

package com.document.render.office.fc.hwpf.usermodel;

import com.document.render.office.common.pictureefftect.PictureEffectInfo;
import com.document.render.office.common.pictureefftect.PictureEffectInfoFactory;
import com.document.render.office.fc.ddf.DefaultEscherRecordFactory;
import com.document.render.office.fc.ddf.EscherBSERecord;
import com.document.render.office.fc.ddf.EscherBlipRecord;
import com.document.render.office.fc.ddf.EscherContainerRecord;
import com.document.render.office.fc.ddf.EscherMetafileBlip;
import com.document.render.office.fc.ddf.EscherOptRecord;
import com.document.render.office.fc.ddf.EscherProperties;
import com.document.render.office.fc.ddf.EscherRecord;
import com.document.render.office.fc.ddf.EscherRecordFactory;
import com.document.render.office.fc.ddf.EscherSimpleProperty;
import com.document.render.office.fc.ddf.EscherSpRecord;
import com.document.render.office.fc.ddf.EscherTertiaryOptRecord;
import com.document.render.office.fc.hwpf.model.EscherRecordHolder;
import com.document.render.office.fc.hwpf.model.FSPA;
import com.document.render.office.fc.hwpf.model.FSPATable;
import com.document.render.office.system.IControl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class OfficeDrawingsImpl implements OfficeDrawings {
    private final EscherRecordHolder _escherRecordHolder;
    private final FSPATable _fspaTable;
    private final byte[] _mainStream;

    public OfficeDrawingsImpl(FSPATable fspaTable, EscherRecordHolder escherRecordHolder,
                              byte[] mainStream) {
        this._fspaTable = fspaTable;
        this._escherRecordHolder = escherRecordHolder;
        this._mainStream = mainStream;
    }

    public EscherBlipRecord getBitmapRecord(IControl control, int bitmapIndex) {
        List<? extends EscherContainerRecord> bContainers = _escherRecordHolder
                .getBStoreContainers();
        if (bContainers == null || bContainers.size() != 1)
            return null;

        EscherContainerRecord bContainer = bContainers.get(0);
        final List<EscherRecord> bitmapRecords = bContainer.getChildRecords();

        if (bitmapRecords.size() < bitmapIndex)
            return null;

        EscherRecord imageRecord = bitmapRecords.get(bitmapIndex - 1);

        if (imageRecord instanceof EscherBlipRecord) {
            return (EscherBlipRecord) imageRecord;
        }

        if (imageRecord instanceof EscherBSERecord) {
            EscherBSERecord bseRecord = (EscherBSERecord) imageRecord;

            EscherBlipRecord blip = bseRecord.getBlipRecord();
            if (blip != null) {
                return blip;
            }

            if (bseRecord.getOffset() > 0) {

                EscherRecordFactory recordFactory = new DefaultEscherRecordFactory();
                EscherRecord record = recordFactory
                        .createRecord(_mainStream, bseRecord.getOffset());

                if (record instanceof EscherBlipRecord) {
                    EscherBlipRecord blipRecord = (EscherBlipRecord) record;
                    if (blipRecord instanceof EscherMetafileBlip) {
                        blipRecord.fillFields(_mainStream, bseRecord.getOffset(), recordFactory);
                        blipRecord.setTempFilePath(control.getSysKit().getPictureManage().writeTempFile(blipRecord.getPicturedata()));
                    } else {
                        int bytesAfterHeader = blipRecord.readHeader(_mainStream, bseRecord.getOffset());
                        int pos = bseRecord.getOffset() + 8;
                        int skip = 17;
                        byte[] b = new byte[Math.min(64, bytesAfterHeader)];

                        System.arraycopy(_mainStream, pos + skip, b, 0, b.length);
                        blipRecord.setPictureData(b);

                        blipRecord.setTempFilePath(control.getSysKit().getPictureManage().writeTempFile(_mainStream, pos + skip, bytesAfterHeader - skip));
                    }




                    return blipRecord;
                }
            }
        }

        return null;
    }

    private boolean findEscherShapeRecordContainer(EscherContainerRecord spContainer, final int shapeId) {
        if (spContainer.getRecordId() == EscherContainerRecord.SPGR_CONTAINER) {
            for (EscherRecord escherRecord : spContainer.getChildRecords()) {
                return findEscherShapeRecordContainer((EscherContainerRecord) escherRecord, shapeId);
            }
        } else {
            EscherSpRecord escherSpRecord = spContainer.getChildById(EscherSpRecord.RECORD_ID);
            if (escherSpRecord != null && escherSpRecord.getShapeId() == shapeId) {
                return true;
            }
        }
        return false;
    }

    private EscherContainerRecord getEscherShapeRecordContainer(final int shapeId) {
        for (EscherContainerRecord spContainer : _escherRecordHolder.getSpContainers()) {
            if (spContainer.getRecordId() == EscherContainerRecord.SPGR_CONTAINER) {
                if (findEscherShapeRecordContainer(spContainer, shapeId)) {
                    return spContainer;
                }
            } else {
                EscherSpRecord escherSpRecord = spContainer.getChildById(EscherSpRecord.RECORD_ID);
                if (escherSpRecord != null && escherSpRecord.getShapeId() == shapeId) {
                    return spContainer;
                }
            }
        }

        return null;
    }


    private OfficeDrawing getOfficeDrawing(final FSPA fspa) {
        return new OfficeDrawingImpl(fspa, this);
    }

    public OfficeDrawing getOfficeDrawingAt(int characterPosition) {
        final FSPA fspa = _fspaTable.getFspaFromCp(characterPosition);
        if (fspa == null)
            return null;

        return getOfficeDrawing(fspa);
    }

    public Collection<OfficeDrawing> getOfficeDrawings() {
        List<OfficeDrawing> result = new ArrayList<OfficeDrawing>();
        for (FSPA fspa : _fspaTable.getShapes()) {
            result.add(getOfficeDrawing(fspa));
        }
        return Collections.unmodifiableList(result);
    }


    private static class OfficeDrawingImpl implements OfficeDrawing {

        private FSPA fspa;
        private OfficeDrawingsImpl darwings;
        private EscherBlipRecord blipRecord;


        public OfficeDrawingImpl(FSPA fspa, OfficeDrawingsImpl drawings) {
            this.fspa = fspa;
            this.darwings = drawings;
        }


        public byte getHorizontalPositioning() {
            return (byte) getTertiaryPropertyValue(EscherProperties.GROUPSHAPE__POSH, HWPFShape.POSH_ABS);
        }


        public byte getHorizontalRelative() {
            return (byte) getTertiaryPropertyValue(EscherProperties.GROUPSHAPE__POSRELH, HWPFShape.POSRELH_COLUMN);
        }


        public byte[] getPictureData(IControl control) {
            if (blipRecord != null) {
                return blipRecord.getPicturedata();
            }
            EscherContainerRecord shapeDescription = darwings.getEscherShapeRecordContainer(getShapeId());
            if (shapeDescription == null)
                return null;

            EscherOptRecord escherOptRecord = shapeDescription
                    .getChildById(EscherOptRecord.RECORD_ID);
            if (escherOptRecord == null)
                return null;

            EscherSimpleProperty escherProperty = escherOptRecord
                    .lookup(EscherProperties.BLIP__BLIPTODISPLAY);
            if (escherProperty == null)
                return null;

            int bitmapIndex = escherProperty.getPropertyValue();
            blipRecord = darwings.getBitmapRecord(control, bitmapIndex);
            if (blipRecord == null)
                return null;

            return blipRecord.getPicturedata();
        }


        public byte[] getPictureData(IControl control, int index) {
            if (index > 0) {
                blipRecord = darwings.getBitmapRecord(control, index);
                if (blipRecord != null) {
                    return blipRecord.getPicturedata();
                }
            }
            return null;
        }

        public HWPFShape getAutoShape() {
            EscherContainerRecord spContainer = darwings.getEscherShapeRecordContainer(getShapeId());
            if (spContainer != null) {
                return HWPFShapeFactory.createShape(spContainer, null);
            }
            return null;
        }

        public int getRectangleBottom() {
            return fspa.getYaBottom();
        }

        public int getRectangleLeft() {
            return fspa.getXaLeft();
        }

        public int getRectangleRight() {
            return fspa.getXaRight();
        }

        public int getRectangleTop() {
            return fspa.getYaTop();
        }

        public int getShapeId() {
            return fspa.getSpid();
        }

        public int getWrap() {
            return fspa.getWr();
        }

        public boolean isBelowText() {
            return fspa.isFBelowText();
        }

        public boolean isAnchorLock() {
            return fspa.isFAnchorLock();
        }


        public PictureEffectInfo getPictureEffectInfor() {
            EscherContainerRecord shapeDescription = darwings.getEscherShapeRecordContainer(getShapeId());
            if (shapeDescription == null)
                return null;

            EscherOptRecord optRecord = shapeDescription
                    .getChildById(EscherOptRecord.RECORD_ID);

            return PictureEffectInfoFactory.getPictureEffectInfor(optRecord);
        }


        private int getTertiaryPropertyValue(int propertyId, int defaultValue) {
            EscherContainerRecord shapeDescription = darwings.getEscherShapeRecordContainer(getShapeId());
            if (shapeDescription == null)
                return defaultValue;

            EscherTertiaryOptRecord escherTertiaryOptRecord = shapeDescription
                    .getChildById(EscherTertiaryOptRecord.RECORD_ID);
            if (escherTertiaryOptRecord == null)
                return defaultValue;

            EscherSimpleProperty escherProperty = escherTertiaryOptRecord.lookup(propertyId);
            if (escherProperty == null)
                return defaultValue;
            int value = escherProperty.getPropertyValue();

            return value;
        }


        public byte getVerticalPositioning() {
            return (byte) getTertiaryPropertyValue(EscherProperties.GROUPSHAPE__POSV, HWPFShape.POSV_ABS);
        }


        public byte getVerticalRelativeElement() {
            return (byte) getTertiaryPropertyValue(EscherProperties.GROUPSHAPE__POSRELV, HWPFShape.POSRELV_TEXT);
        }


        public String getTempFilePath(IControl control) {
            if (blipRecord == null) {
                getPictureData(control);
            }
            if (blipRecord != null) {
                return blipRecord.getTempFilePath();
            }
            return null;
        }

        @Override
        public String toString() {
            return "OfficeDrawingImpl: " + fspa.toString();
        }
    }
}
