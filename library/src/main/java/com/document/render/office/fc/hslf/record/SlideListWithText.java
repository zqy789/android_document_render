

package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.LittleEndian;

import java.util.Vector;





public final class SlideListWithText extends RecordContainer {


    public static final int SLIDES = 0;

    public static final int MASTER = 1;

    public static final int NOTES = 2;
    private static long _type = 4080;
    private byte[] _header;
    private SlideAtomsSet[] slideAtomsSets;


    protected SlideListWithText(byte[] source, int start, int len) {

        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);


        _children = Record.findChildRecords(source, start + 8, len - 8);




        Vector<SlideAtomsSet> sets = new Vector<SlideAtomsSet>();
        for (int i = 0; i < _children.length; i++) {
            if (_children[i] instanceof SlidePersistAtom) {

                int endPos = i + 1;
                while (endPos < _children.length
                        && !(_children[endPos] instanceof SlidePersistAtom)) {
                    endPos += 1;
                }

                int clen = endPos - i - 1;
                boolean emptySet = false;
                if (clen == 0) {
                    emptySet = true;
                }



                Record[] spaChildren = new Record[clen];
                System.arraycopy(_children, i + 1, spaChildren, 0, clen);
                SlideAtomsSet set = new SlideAtomsSet((SlidePersistAtom) _children[i], spaChildren);
                sets.add(set);


                i += clen;
            }
        }


        slideAtomsSets = sets.toArray(new SlideAtomsSet[sets.size()]);
    }


    public SlideListWithText() {
        _header = new byte[8];
        LittleEndian.putUShort(_header, 0, 15);
        LittleEndian.putUShort(_header, 2, (int) _type);
        LittleEndian.putInt(_header, 4, 0);


        _children = new Record[0];
        slideAtomsSets = new SlideAtomsSet[0];
    }


    public void addSlidePersistAtom(SlidePersistAtom spa) {

        appendChildRecord(spa);

        SlideAtomsSet newSAS = new SlideAtomsSet(spa, new Record[0]);


        SlideAtomsSet[] sas = new SlideAtomsSet[slideAtomsSets.length + 1];
        System.arraycopy(slideAtomsSets, 0, sas, 0, slideAtomsSets.length);
        sas[sas.length - 1] = newSAS;
        slideAtomsSets = sas;
    }

    public int getInstance() {
        return LittleEndian.getShort(_header, 0) >> 4;
    }

    public void setInstance(int inst) {
        LittleEndian.putShort(_header, (short) ((inst << 4) | 0xF));
    }


    public SlideAtomsSet[] getSlideAtomsSets() {
        return slideAtomsSets;
    }


    public void setSlideAtomsSets(SlideAtomsSet[] sas) {
        slideAtomsSets = sas;
    }


    public long getRecordType() {
        return _type;
    }


    public void dispose() {
        _header = null;
        if (slideAtomsSets != null) {
            for (SlideAtomsSet sas : slideAtomsSets) {
                sas.dispose();
            }
            slideAtomsSets = null;
        }
    }


    public class SlideAtomsSet {
        private SlidePersistAtom slidePersistAtom;
        private Record[] slideRecords;


        public SlideAtomsSet(SlidePersistAtom s, Record[] r) {
            slidePersistAtom = s;
            slideRecords = r;
        }


        public SlidePersistAtom getSlidePersistAtom() {
            return slidePersistAtom;
        }


        public Record[] getSlideRecords() {
            return slideRecords;
        }


        public void dispose() {
            if (slidePersistAtom != null) {
                slidePersistAtom.dispose();
            }
            if (slideRecords != null) {
                for (Record rec : slideRecords) {
                    rec.dispose();
                }
                slideRecords = null;
            }
        }
    }
}
