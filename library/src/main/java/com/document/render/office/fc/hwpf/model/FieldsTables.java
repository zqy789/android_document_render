

package com.document.render.office.fc.hwpf.model;

import com.document.render.office.fc.hwpf.model.io.HWPFOutputStream;
import com.document.render.office.fc.util.Internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



@Internal
public class FieldsTables {

    @Deprecated
    public static final int PLCFFLDATN = 0;

    @Deprecated
    public static final int PLCFFLDEDN = 1;

    @Deprecated
    public static final int PLCFFLDFTN = 2;

    @Deprecated
    public static final int PLCFFLDHDR = 3;

    @Deprecated
    public static final int PLCFFLDHDRTXBX = 4;

    @Deprecated
    public static final int PLCFFLDMOM = 5;

    @Deprecated
    public static final int PLCFFLDTXBX = 6;

    private static final int FLD_SIZE = 2;
    private Map<FieldsDocumentPart, PlexOfCps> _tables;

    public FieldsTables(byte[] tableStream, FileInformationBlock fib) {
        _tables = new HashMap<FieldsDocumentPart, PlexOfCps>(FieldsDocumentPart.values().length);

        for (FieldsDocumentPart part : FieldsDocumentPart.values()) {
            final PlexOfCps plexOfCps = readPLCF(tableStream, fib, part);
            _tables.put(part, plexOfCps);
        }
    }

    private static ArrayList<PlexOfField> toArrayList(PlexOfCps plexOfCps) {
        if (plexOfCps == null)
            return new ArrayList<PlexOfField>();

        ArrayList<PlexOfField> fields = new ArrayList<PlexOfField>(plexOfCps.length());
        for (int i = 0; i < plexOfCps.length(); i++) {
            GenericPropertyNode propNode = plexOfCps.getProperty(i);
            PlexOfField plex = new PlexOfField(propNode);
            fields.add(plex);
        }

        return fields;
    }

    public ArrayList<PlexOfField> getFieldsPLCF(FieldsDocumentPart part) {
        return toArrayList(_tables.get(part));
    }

    @Deprecated
    public ArrayList<PlexOfField> getFieldsPLCF(int partIndex) {
        return getFieldsPLCF(FieldsDocumentPart.values()[partIndex]);
    }

    private PlexOfCps readPLCF(byte[] tableStream, FileInformationBlock fib,
                               FieldsDocumentPart documentPart) {
        int start = fib.getFieldsPlcfOffset(documentPart);
        int length = fib.getFieldsPlcfLength(documentPart);

        if (start <= 0 || length <= 0)
            return null;

        return new PlexOfCps(tableStream, start, length, FLD_SIZE);
    }

    private int savePlex(FileInformationBlock fib, FieldsDocumentPart part, PlexOfCps plexOfCps,
                         HWPFOutputStream outputStream) throws IOException {
        if (plexOfCps == null || plexOfCps.length() == 0) {
            fib.setFieldsPlcfOffset(part, outputStream.getOffset());
            fib.setFieldsPlcfLength(part, 0);
            return 0;
        }

        byte[] data = plexOfCps.toByteArray();

        int start = outputStream.getOffset();
        int length = data.length;

        outputStream.write(data);

        fib.setFieldsPlcfOffset(part, start);
        fib.setFieldsPlcfLength(part, length);

        return length;
    }

    public void write(FileInformationBlock fib, HWPFOutputStream tableStream) throws IOException {
        for (FieldsDocumentPart part : FieldsDocumentPart.values()) {
            PlexOfCps plexOfCps = _tables.get(part);
            savePlex(fib, part, plexOfCps, tableStream);
        }
    }

}
