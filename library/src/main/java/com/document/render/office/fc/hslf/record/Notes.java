

package com.document.render.office.fc.hslf.record;



public final class Notes extends SheetContainer {
    private static long _type = 1008l;
    private byte[] _header;
    
    private NotesAtom notesAtom;
    private PPDrawing ppDrawing;
    private ColorSchemeAtom _colorScheme;

    
    protected Notes(byte[] source, int start, int len) {
        
        _header = new byte[8];
        System.arraycopy(source, start, _header, 0, 8);

        
        _children = Record.findChildRecords(source, start + 8, len - 8);

        
        for (int i = 0; i < _children.length; i++) {
            if (_children[i] instanceof NotesAtom) {
                notesAtom = (NotesAtom) _children[i];
                
            }
            if (_children[i] instanceof PPDrawing) {
                ppDrawing = (PPDrawing) _children[i];
            }
            if (ppDrawing != null && _children[i] instanceof ColorSchemeAtom) {
                _colorScheme = (ColorSchemeAtom) _children[i];
            }
        }
    }

    
    public NotesAtom getNotesAtom() {
        return notesAtom;
    }

    
    public PPDrawing getPPDrawing() {
        return ppDrawing;
    }

    
    public long getRecordType() {
        return _type;
    }

    public ColorSchemeAtom getColorScheme() {
        return _colorScheme;
    }

    
    public void dispose() {
        super.dispose();
        if (notesAtom != null) {
            notesAtom.dispose();
            notesAtom = null;
        }
        if (ppDrawing != null) {
            ppDrawing.dispose();
            ppDrawing = null;
        }
        if (_colorScheme != null) {
            _colorScheme.dispose();
            _colorScheme = null;
        }
    }
}
