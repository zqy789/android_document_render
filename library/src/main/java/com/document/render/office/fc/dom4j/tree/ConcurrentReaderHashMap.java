

package com.document.render.office.fc.dom4j.tree;

import java.io.IOException;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;



class ConcurrentReaderHashMap extends AbstractMap implements Map, Cloneable,
        Serializable {





    public static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private static final int MINIMUM_CAPACITY = 4;

    private static final int MAXIMUM_CAPACITY = 1 << 30;

    public static int DEFAULT_INITIAL_CAPACITY = 32;

    protected final BarrierLock barrierLock = new BarrierLock();


    protected transient Object lastWrite;

    protected transient Entry[] table;

    protected transient int count;

    protected int threshold;

    protected float loadFactor;
    protected transient Set keySet = null;
    protected transient Set entrySet = null;
    protected transient Collection values = null;



    public ConcurrentReaderHashMap(int initialCapacity, float loadFactor) {
        if (loadFactor <= 0)
            throw new IllegalArgumentException("Illegal Load factor: "
                    + loadFactor);
        this.loadFactor = loadFactor;

        int cap = p2capacity(initialCapacity);

        table = new Entry[cap];
        threshold = (int) (cap * loadFactor);
    }



    public ConcurrentReaderHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }



    public ConcurrentReaderHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }



    public ConcurrentReaderHashMap(Map t) {
        this(Math.max((int) (t.size() / DEFAULT_LOAD_FACTOR) + 1, 16),
                DEFAULT_LOAD_FACTOR);
        putAll(t);
    }


    private static int hash(Object x) {
        int h = x.hashCode();



        return ((h << 7) - h + (h >>> 9) + (h >>> 17));
    }


    protected final void recordModification(Object x) {
        synchronized (barrierLock) {
            lastWrite = x;
        }
    }


    protected final Entry[] getTableForReading() {
        synchronized (barrierLock) {
            return table;
        }
    }


    private int p2capacity(int initialCapacity) {
        int cap = initialCapacity;


        int result;
        if (cap > MAXIMUM_CAPACITY || cap < 0) {
            result = MAXIMUM_CAPACITY;
        } else {
            result = MINIMUM_CAPACITY;
            while (result < cap)
                result <<= 1;
        }
        return result;
    }


    protected boolean eq(Object x, Object y) {
        return x == y || x.equals(y);
    }



    public synchronized int size() {
        return count;
    }



    public synchronized boolean isEmpty() {
        return count == 0;
    }



    public Object get(Object key) {


        int hash = hash(key);



        Entry[] tab = table;
        int index = hash & (tab.length - 1);
        Entry first = tab[index];
        Entry e = first;

        for (; ; ) {
            if (e == null) {




                Entry[] reread = getTableForReading();
                if (tab == reread && first == tab[index])
                    return null;
                else {

                    tab = reread;
                    e = first = tab[index = hash & (tab.length - 1)];
                }

            } else if (e.hash == hash && eq(key, e.key)) {
                Object value = e.value;
                if (value != null)
                    return value;







                synchronized (this) {
                    tab = table;
                }
                e = first = tab[index = hash & (tab.length - 1)];

            } else
                e = e.next;
        }
    }



    public boolean containsKey(Object key) {
        return get(key) != null;
    }



    public Object put(Object key, Object value) {
        if (value == null)
            throw new NullPointerException();

        int hash = hash(key);
        Entry[] tab = table;
        int index = hash & (tab.length - 1);
        Entry first = tab[index];
        Entry e;

        for (e = first; e != null; e = e.next)
            if (e.hash == hash && eq(key, e.key))
                break;

        synchronized (this) {
            if (tab == table) {
                if (e == null) {

                    if (first == tab[index]) {

                        Entry newEntry = new Entry(hash, key, value, first);
                        tab[index] = newEntry;
                        if (++count >= threshold)
                            rehash();
                        else
                            recordModification(newEntry);
                        return null;
                    }
                } else {
                    Object oldValue = e.value;
                    if (first == tab[index] && oldValue != null) {
                        e.value = value;
                        return oldValue;
                    }
                }
            }


            return sput(key, value, hash);
        }
    }


    protected Object sput(Object key, Object value, int hash) {

        Entry[] tab = table;
        int index = hash & (tab.length - 1);
        Entry first = tab[index];
        Entry e = first;

        for (; ; ) {
            if (e == null) {
                Entry newEntry = new Entry(hash, key, value, first);
                tab[index] = newEntry;
                if (++count >= threshold)
                    rehash();
                else
                    recordModification(newEntry);
                return null;
            } else if (e.hash == hash && eq(key, e.key)) {
                Object oldValue = e.value;
                e.value = value;
                return oldValue;
            } else
                e = e.next;
        }
    }


    protected void rehash() {
        Entry[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity >= MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        int newCapacity = oldCapacity << 1;
        int mask = newCapacity - 1;
        threshold = (int) (newCapacity * loadFactor);

        Entry[] newTable = new Entry[newCapacity];


        for (int i = 0; i < oldCapacity; i++) {


            Entry e = oldTable[i];

            if (e != null) {
                int idx = e.hash & mask;
                Entry next = e.next;


                if (next == null)
                    newTable[idx] = e;

                else {

                    Entry lastRun = e;
                    int lastIdx = idx;
                    for (Entry last = next; last != null; last = last.next) {
                        int k = last.hash & mask;
                        if (k != lastIdx) {
                            lastIdx = k;
                            lastRun = last;
                        }
                    }
                    newTable[lastIdx] = lastRun;


                    for (Entry p = e; p != lastRun; p = p.next) {
                        int k = p.hash & mask;
                        newTable[k] = new Entry(p.hash, p.key, p.value,
                                newTable[k]);
                    }
                }
            }
        }

        table = newTable;
        recordModification(newTable);
    }



    public Object remove(Object key) {


        int hash = hash(key);
        Entry[] tab = table;
        int index = hash & (tab.length - 1);
        Entry first = tab[index];
        Entry e = first;

        for (e = first; e != null; e = e.next)
            if (e.hash == hash && eq(key, e.key))
                break;

        synchronized (this) {
            if (tab == table) {
                if (e == null) {
                    if (first == tab[index])
                        return null;
                } else {
                    Object oldValue = e.value;
                    if (first == tab[index] && oldValue != null) {
                        e.value = null;
                        count--;

                        Entry head = e.next;
                        for (Entry p = first; p != e; p = p.next)
                            head = new Entry(p.hash, p.key, p.value, head);

                        tab[index] = head;
                        recordModification(head);
                        return oldValue;
                    }
                }
            }


            return sremove(key, hash);
        }
    }



    protected Object sremove(Object key, int hash) {
        Entry[] tab = table;
        int index = hash & (tab.length - 1);
        Entry first = tab[index];

        for (Entry e = first; e != null; e = e.next) {
            if (e.hash == hash && eq(key, e.key)) {
                Object oldValue = e.value;
                e.value = null;
                count--;
                Entry head = e.next;
                for (Entry p = first; p != e; p = p.next)
                    head = new Entry(p.hash, p.key, p.value, head);

                tab[index] = head;
                recordModification(head);
                return oldValue;
            }
        }
        return null;
    }



    public boolean containsValue(Object value) {
        if (value == null)
            throw new NullPointerException();

        Entry tab[] = getTableForReading();

        for (int i = 0; i < tab.length; ++i) {
            for (Entry e = tab[i]; e != null; e = e.next)
                if (value.equals(e.value))
                    return true;
        }

        return false;
    }



    public boolean contains(Object value) {
        return containsValue(value);
    }



    public synchronized void putAll(Map t) {
        int n = t.size();
        if (n == 0)
            return;




        while (n >= threshold)
            rehash();

        for (Iterator it = t.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            put(key, value);
        }
    }




    public synchronized void clear() {
        Entry tab[] = table;
        for (int i = 0; i < tab.length; ++i) {



            for (Entry e = tab[i]; e != null; e = e.next)
                e.value = null;

            tab[i] = null;
        }
        count = 0;
        recordModification(tab);
    }



    public synchronized Object clone() {
        try {
            ConcurrentReaderHashMap t = (ConcurrentReaderHashMap) super.clone();

            t.keySet = null;
            t.entrySet = null;
            t.values = null;

            Entry[] tab = table;
            t.table = new Entry[tab.length];
            Entry[] ttab = t.table;

            for (int i = 0; i < tab.length; ++i) {
                Entry first = null;
                for (Entry e = tab[i]; e != null; e = e.next)
                    first = new Entry(e.hash, e.key, e.value, first);
                ttab[i] = first;
            }

            return t;
        } catch (CloneNotSupportedException e) {

            throw new InternalError();
        }
    }



    public Set keySet() {
        Set ks = keySet;
        return (ks != null) ? ks : (keySet = new KeySet());
    }



    public Collection values() {
        Collection vs = values;
        return (vs != null) ? vs : (values = new Values());
    }



    public Set entrySet() {
        Set es = entrySet;
        return (es != null) ? es : (entrySet = new EntrySet());
    }


    protected synchronized boolean findAndRemoveEntry(Map.Entry entry) {
        Object key = entry.getKey();
        Object v = get(key);
        if (v != null && v.equals(entry.getValue())) {
            remove(key);
            return true;
        } else
            return false;
    }


    public Enumeration keys() {
        return new KeyIterator();
    }



    public Enumeration elements() {
        return new ValueIterator();
    }



    private synchronized void writeObject(java.io.ObjectOutputStream s)
            throws IOException {

        s.defaultWriteObject();


        s.writeInt(table.length);


        s.writeInt(count);


        for (int index = table.length - 1; index >= 0; index--) {
            Entry entry = table[index];

            while (entry != null) {
                s.writeObject(entry.key);
                s.writeObject(entry.value);
                entry = entry.next;
            }
        }
    }


    private synchronized void readObject(java.io.ObjectInputStream s)
            throws IOException, ClassNotFoundException {

        s.defaultReadObject();


        int numBuckets = s.readInt();
        table = new Entry[numBuckets];


        int size = s.readInt();


        for (int i = 0; i < size; i++) {
            Object key = s.readObject();
            Object value = s.readObject();
            put(key, value);
        }
    }



    public synchronized int capacity() {
        return table.length;
    }


    public float loadFactor() {
        return loadFactor;
    }


    protected static class BarrierLock implements java.io.Serializable {
    }



    protected static class Entry implements Map.Entry {



        protected final int hash;

        protected final Object key;

        protected final Entry next;

        protected volatile Object value;

        Entry(int hash, Object key, Object value, Entry next) {
            this.hash = hash;
            this.key = key;
            this.next = next;
            this.value = value;
        }



        public Object getKey() {
            return key;
        }


        public Object getValue() {
            return value;
        }



        public Object setValue(Object value) {
            if (value == null)
                throw new NullPointerException();
            Object oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry e = (Map.Entry) o;
            return (key.equals(e.getKey()) && value.equals(e.getValue()));
        }

        public int hashCode() {
            return key.hashCode() ^ value.hashCode();
        }

        public String toString() {
            return key + "=" + value;
        }

    }

    private class KeySet extends AbstractSet {
        public Iterator iterator() {
            return new KeyIterator();
        }

        public int size() {
            return ConcurrentReaderHashMap.this.size();
        }

        public boolean contains(Object o) {
            return ConcurrentReaderHashMap.this.containsKey(o);
        }

        public boolean remove(Object o) {
            return ConcurrentReaderHashMap.this.remove(o) != null;
        }

        public void clear() {
            ConcurrentReaderHashMap.this.clear();
        }
    }

    private class Values extends AbstractCollection {
        public Iterator iterator() {
            return new ValueIterator();
        }

        public int size() {
            return ConcurrentReaderHashMap.this.size();
        }

        public boolean contains(Object o) {
            return ConcurrentReaderHashMap.this.containsValue(o);
        }

        public void clear() {
            ConcurrentReaderHashMap.this.clear();
        }
    }

    private class EntrySet extends AbstractSet {
        public Iterator iterator() {
            return new HashIterator();
        }

        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry entry = (Map.Entry) o;
            Object v = ConcurrentReaderHashMap.this.get(entry.getKey());
            return v != null && v.equals(entry.getValue());
        }

        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            return ConcurrentReaderHashMap.this
                    .findAndRemoveEntry((Map.Entry) o);
        }

        public int size() {
            return ConcurrentReaderHashMap.this.size();
        }

        public void clear() {
            ConcurrentReaderHashMap.this.clear();
        }
    }

    protected class HashIterator implements Iterator, Enumeration {
        protected final Entry[] tab;

        protected int index;

        protected Entry entry = null;

        protected Object currentKey;

        protected Object currentValue;

        protected Entry lastReturned = null;

        protected HashIterator() {
            tab = ConcurrentReaderHashMap.this.getTableForReading();
            index = tab.length - 1;
        }

        public boolean hasMoreElements() {
            return hasNext();
        }

        public Object nextElement() {
            return next();
        }

        public boolean hasNext() {



            for (; ; ) {
                if (entry != null) {
                    Object v = entry.value;
                    if (v != null) {
                        currentKey = entry.key;
                        currentValue = v;
                        return true;
                    } else
                        entry = entry.next;
                }

                while (entry == null && index >= 0)
                    entry = tab[index--];

                if (entry == null) {
                    currentKey = currentValue = null;
                    return false;
                }
            }
        }

        protected Object returnValueOfNext() {
            return entry;
        }

        public Object next() {
            if (currentKey == null && !hasNext())
                throw new NoSuchElementException();

            Object result = returnValueOfNext();
            lastReturned = entry;
            currentKey = currentValue = null;
            entry = entry.next;
            return result;
        }

        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();
            ConcurrentReaderHashMap.this.remove(lastReturned.key);
            lastReturned = null;
        }

    }

    protected class KeyIterator extends HashIterator {
        protected Object returnValueOfNext() {
            return currentKey;
        }
    }

    protected class ValueIterator extends HashIterator {
        protected Object returnValueOfNext() {
            return currentValue;
        }
    }
}
