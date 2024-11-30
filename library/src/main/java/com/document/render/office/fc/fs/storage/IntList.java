package com.document.render.office.fc.fs.storage;

import com.document.render.office.fc.fs.filesystem.CFBConstants;


public class IntList {
    private static final int _default_size = 128;
    private int[] _array;
    private int _limit;
    private int fillval = 0;


    public IntList() {
        this(_default_size, 0);
    }



    public IntList(final int initialCapacity, int fillvalue) {
        _array = new int[initialCapacity];
        if (fillval != 0) {
            fillval = fillvalue;
            fillArray(fillval, _array, 0);
        }
        _limit = 0;
    }


    private void fillArray(int val, int[] array, int index) {
        for (int k = index; k < array.length; k++) {
            array[k] = val;
        }
    }




    public boolean add(int value) {
        if (_limit == _array.length) {
            growArray(_limit * 2);
        }
        _array[_limit++] = value;
        return true;
    }




    public int get(int index) {
        if (index >= _limit) {
            return CFBConstants.END_OF_CHAIN;


        }
        return _array[index];
    }




    public int size() {
        return _limit;
    }


    private void growArray(int new_size) {
        int size = (new_size == _array.length) ? new_size + 1 : new_size;
        int[] new_array = new int[size];

        if (fillval != 0) {
            fillArray(fillval, new_array, _array.length);
        }

        System.arraycopy(_array, 0, new_array, 0, _limit);
        _array = new_array;
    }
}

