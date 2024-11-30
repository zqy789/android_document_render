

package com.document.render.office.fc.hwpf.model;

import com.document.render.office.fc.util.Internal;
import com.document.render.office.fc.util.POILogFactory;
import com.document.render.office.fc.util.POILogger;

import java.util.Arrays;
import java.util.Comparator;



@Internal
public abstract class PropertyNode<T extends PropertyNode<T>> implements Comparable<T>, Cloneable {

    private final static POILogger _logger = POILogFactory.getLogger(PropertyNode.class);
    protected Object _buf;

    private int _cpStart;

    private int _cpEnd;

    protected PropertyNode(int fcStart, int fcEnd, Object buf) {
        _cpStart = fcStart;
        _cpEnd = fcEnd;
        _buf = buf;

        if (_cpStart < 0) {
            _logger.log(POILogger.WARN, "A property claimed to start before zero, at " + _cpStart
                    + "! Resetting it to zero, and hoping for the best");
            _cpStart = 0;
        }

        if (_cpEnd < _cpStart) {
            _logger.log(POILogger.WARN, "A property claimed to end (" + _cpEnd + ") before start! "
                    + "Resetting end to start, and hoping for the best");
            _cpEnd = _cpStart;
        }
    }


    public int getStart() {
        return _cpStart;
    }

    public void setStart(int start) {
        _cpStart = start;
    }


    public int getEnd() {
        return _cpEnd;
    }

    public void setEnd(int end) {
        _cpEnd = end;
    }


    public void adjustForDelete(int start, int length) {
        int end = start + length;

        if (_cpEnd > start) {


            if (_cpStart < end) {

                _cpEnd = end >= _cpEnd ? start : _cpEnd - length;
                _cpStart = Math.min(start, _cpStart);
            } else {

                _cpEnd -= length;
                _cpStart -= length;
            }
        }
    }

    protected boolean limitsAreEqual(Object o) {
        return ((PropertyNode<?>) o).getStart() == _cpStart
                && ((PropertyNode<?>) o).getEnd() == _cpEnd;

    }

    @Override
    public int hashCode() {
        return this._cpStart * 31 + this._buf.hashCode();
    }

    public boolean equals(Object o) {
        if (limitsAreEqual(o)) {
            Object testBuf = ((PropertyNode<?>) o)._buf;
            if (testBuf instanceof byte[] && _buf instanceof byte[]) {
                return Arrays.equals((byte[]) testBuf, (byte[]) _buf);
            }
            return _buf.equals(testBuf);
        }
        return false;
    }

    public T clone() throws CloneNotSupportedException {
        return (T) super.clone();
    }


    public int compareTo(T o) {
        int cpEnd = o.getEnd();
        if (_cpEnd == cpEnd) {
            return 0;
        } else if (_cpEnd < cpEnd) {
            return -1;
        } else {
            return 1;
        }
    }

    public static final class EndComparator implements Comparator<PropertyNode<?>> {
        public static EndComparator instance = new EndComparator();

        public int compare(PropertyNode<?> o1, PropertyNode<?> o2) {
            int thisVal = o1.getEnd();
            int anotherVal = o2.getEnd();
            return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
        }
    }

    public static final class StartComparator implements Comparator<PropertyNode<?>> {
        public static StartComparator instance = new StartComparator();

        public int compare(PropertyNode<?> o1, PropertyNode<?> o2) {
            int thisVal = o1.getStart();
            int anotherVal = o2.getStart();
            return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
        }
    }
}
