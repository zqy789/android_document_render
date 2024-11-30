

package com.document.render.office.fc.hssf.formula;


final class FormulaCellCacheEntrySet {
    private static final FormulaCellCacheEntry[] EMPTY_ARRAY = {};

    private int _size;
    private FormulaCellCacheEntry[] _arr;

    public FormulaCellCacheEntrySet() {
        _arr = EMPTY_ARRAY;
    }

    private static boolean addInternal(CellCacheEntry[] arr, CellCacheEntry cce) {
        int startIx = Math.abs(cce.hashCode() % arr.length);

        for (int i = startIx; i < arr.length; i++) {
            CellCacheEntry item = arr[i];
            if (item == cce) {

                return false;
            }
            if (item == null) {
                arr[i] = cce;
                return true;
            }
        }
        for (int i = 0; i < startIx; i++) {
            CellCacheEntry item = arr[i];
            if (item == cce) {

                return false;
            }
            if (item == null) {
                arr[i] = cce;
                return true;
            }
        }
        throw new IllegalStateException("No empty space found");
    }

    public FormulaCellCacheEntry[] toArray() {
        int nItems = _size;
        if (nItems < 1) {
            return EMPTY_ARRAY;
        }
        FormulaCellCacheEntry[] result = new FormulaCellCacheEntry[nItems];
        int j = 0;
        for (int i = 0; i < _arr.length; i++) {
            FormulaCellCacheEntry cce = _arr[i];
            if (cce != null) {
                result[j++] = cce;
            }
        }
        if (j != nItems) {
            throw new IllegalStateException("size mismatch");
        }
        return result;
    }

    public void add(CellCacheEntry cce) {
        if (_size * 3 >= _arr.length * 2) {

            FormulaCellCacheEntry[] prevArr = _arr;
            FormulaCellCacheEntry[] newArr = new FormulaCellCacheEntry[4 + _arr.length * 3 / 2];
            for (int i = 0; i < prevArr.length; i++) {
                FormulaCellCacheEntry prevCce = _arr[i];
                if (prevCce != null) {
                    addInternal(newArr, prevCce);
                }
            }
            _arr = newArr;
        }
        if (addInternal(_arr, cce)) {
            _size++;
        }
    }

    public boolean remove(CellCacheEntry cce) {
        FormulaCellCacheEntry[] arr = _arr;

        if (_size * 3 < _arr.length && _arr.length > 8) {

            boolean found = false;
            FormulaCellCacheEntry[] prevArr = _arr;
            FormulaCellCacheEntry[] newArr = new FormulaCellCacheEntry[_arr.length / 2];
            for (int i = 0; i < prevArr.length; i++) {
                FormulaCellCacheEntry prevCce = _arr[i];
                if (prevCce != null) {
                    if (prevCce == cce) {
                        found = true;
                        _size--;

                        continue;
                    }
                    addInternal(newArr, prevCce);
                }
            }
            _arr = newArr;
            return found;
        }



        int startIx = Math.abs(cce.hashCode() % arr.length);


        for (int i = startIx; i < arr.length; i++) {
            FormulaCellCacheEntry item = arr[i];
            if (item == cce) {

                arr[i] = null;
                _size--;
                return true;
            }
        }
        for (int i = 0; i < startIx; i++) {
            FormulaCellCacheEntry item = arr[i];
            if (item == cce) {

                arr[i] = null;
                _size--;
                return true;
            }
        }
        return false;
    }
}
