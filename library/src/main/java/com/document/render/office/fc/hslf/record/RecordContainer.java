

package com.document.render.office.fc.hslf.record;

import com.document.render.office.fc.util.ArrayUtil;

import java.util.ArrayList;




public abstract class RecordContainer extends Record {
    protected Record[] _children;
    private Boolean changingChildRecordsLock = Boolean.TRUE;


    public static void handleParentAwareRecords(RecordContainer br) {

        for (Record record : br.getChildRecords()) {

            if (record instanceof ParentAwareRecord) {
                ((ParentAwareRecord) record).setParentRecord(br);
            }

            if (record instanceof RecordContainer) {
                handleParentAwareRecords((RecordContainer) record);
            }
        }
    }


    public Record[] getChildRecords() {
        return _children;
    }





    public boolean isAnAtom() {
        return false;
    }


    private int findChildLocation(Record child) {


        synchronized (changingChildRecordsLock) {
            for (int i = 0; i < _children.length; i++) {
                if (_children[i].equals(child)) {
                    return i;
                }
            }
        }
        return -1;
    }


    private void appendChild(Record newChild) {
        synchronized (changingChildRecordsLock) {

            Record[] nc = new Record[(_children.length + 1)];
            System.arraycopy(_children, 0, nc, 0, _children.length);

            nc[_children.length] = newChild;
            _children = nc;
        }
    }


    private void addChildAt(Record newChild, int position) {
        synchronized (changingChildRecordsLock) {

            appendChild(newChild);


            moveChildRecords((_children.length - 1), position, 1);
        }
    }


    private void moveChildRecords(int oldLoc, int newLoc, int number) {
        if (oldLoc == newLoc) {
            return;
        }
        if (number == 0) {
            return;
        }


        if (oldLoc + number > _children.length) {
            throw new IllegalArgumentException("Asked to move more records than there are!");
        }


        ArrayUtil.arrayMoveWithin(_children, oldLoc, newLoc, number);
    }


    public Record findFirstOfType(long type) {
        for (int i = 0; i < _children.length; i++) {
            if (_children[i].getRecordType() == type) {
                return _children[i];
            }
        }
        return null;
    }




    public Record removeChild(Record ch) {
        Record rm = null;
        ArrayList<Record> lst = new ArrayList<Record>();
        for (Record r : _children) {
            if (r != ch) lst.add(r);
            else rm = r;
        }
        _children = lst.toArray(new Record[lst.size()]);
        return rm;
    }


    public void appendChildRecord(Record newChild) {
        synchronized (changingChildRecordsLock) {
            appendChild(newChild);
        }
    }


    public void addChildAfter(Record newChild, Record after) {
        synchronized (changingChildRecordsLock) {

            int loc = findChildLocation(after);
            if (loc == -1) {
                throw new IllegalArgumentException("Asked to add a new child after another record, but that record wasn't one of our children!");
            }


            addChildAt(newChild, loc + 1);
        }
    }


    public void addChildBefore(Record newChild, Record before) {
        synchronized (changingChildRecordsLock) {

            int loc = findChildLocation(before);
            if (loc == -1) {
                throw new IllegalArgumentException("Asked to add a new child before another record, but that record wasn't one of our children!");
            }


            addChildAt(newChild, loc);
        }
    }


    public void moveChildBefore(Record child, Record before) {
        moveChildrenBefore(child, 1, before);
    }


    public void moveChildrenBefore(Record firstChild, int number, Record before) {
        if (number < 1) {
            return;
        }

        synchronized (changingChildRecordsLock) {

            int newLoc = findChildLocation(before);
            if (newLoc == -1) {
                throw new IllegalArgumentException("Asked to move children before another record, but that record wasn't one of our children!");
            }


            int oldLoc = findChildLocation(firstChild);
            if (oldLoc == -1) {
                throw new IllegalArgumentException("Asked to move a record that wasn't a child!");
            }


            moveChildRecords(oldLoc, newLoc, number);
        }
    }


    public void moveChildrenAfter(Record firstChild, int number, Record after) {
        if (number < 1) {
            return;
        }

        synchronized (changingChildRecordsLock) {

            int newLoc = findChildLocation(after);
            if (newLoc == -1) {
                throw new IllegalArgumentException("Asked to move children before another record, but that record wasn't one of our children!");
            }

            newLoc++;


            int oldLoc = findChildLocation(firstChild);
            if (oldLoc == -1) {
                throw new IllegalArgumentException("Asked to move a record that wasn't a child!");
            }


            moveChildRecords(oldLoc, newLoc, number);
        }
    }




    public void setChildRecord(Record[] records) {
        this._children = records;
    }


    public void dispose() {
        if (_children != null) {
            for (Record rec : _children) {
                if (rec != null) {
                    rec.dispose();
                }
            }
            _children = null;
        }
    }


}
