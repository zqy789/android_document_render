

package com.document.render.office.fc.hwpf.model;

import com.document.render.office.fc.hwpf.usermodel.Shape;
import com.document.render.office.fc.util.Internal;

import java.util.ArrayList;
import java.util.List;


@Internal
@Deprecated
public final class ShapesTable {
    private List<Shape> _shapes;
    private List<Shape> _shapesVisibili;

    public ShapesTable(byte[] tblStream, FileInformationBlock fib) {
        PlexOfCps binTable = new PlexOfCps(tblStream, fib.getFcPlcspaMom(), fib.getLcbPlcspaMom(),
                26);

        _shapes = new ArrayList<Shape>();
        _shapesVisibili = new ArrayList<Shape>();

        for (int i = 0; i < binTable.length(); i++) {
            GenericPropertyNode nodo = binTable.getProperty(i);

            Shape sh = new Shape(nodo);
            _shapes.add(sh);
            if (sh.isWithinDocument())
                _shapesVisibili.add(sh);
        }
    }

    public List<Shape> getAllShapes() {
        return _shapes;
    }

    public List<Shape> getVisibleShapes() {
        return _shapesVisibili;
    }
}
