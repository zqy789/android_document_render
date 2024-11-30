

package com.document.render.office.fc.hwpf.model;

import com.document.render.office.fc.hwpf.model.io.HWPFOutputStream;
import com.document.render.office.fc.util.Internal;
import com.document.render.office.fc.util.LittleEndian;
import com.document.render.office.fc.util.POILogFactory;
import com.document.render.office.fc.util.POILogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;



@Internal
public final class ListTables {
    private static final int LIST_DATA_SIZE = 28;
    private static final int LIST_FORMAT_OVERRIDE_SIZE = 16;
    private static POILogger log = POILogFactory.getLogger(ListTables.class);

    ListMap _listMap = new ListMap();
    ArrayList<ListFormatOverride> _overrideList = new ArrayList<ListFormatOverride>();

    public ListTables() {

    }

    public ListTables(byte[] tableStream, int lstOffset, int lfoOffset) {

        int length = LittleEndian.getShort(tableStream, lstOffset);
        lstOffset += LittleEndian.SHORT_SIZE;
        int levelOffset = lstOffset + (length * LIST_DATA_SIZE);

        for (int x = 0; x < length; x++) {
            POIListData lst = new POIListData(tableStream, lstOffset);
            _listMap.put(Integer.valueOf(lst.getLsid()), lst);
            lstOffset += LIST_DATA_SIZE;

            int num = lst.numLevels();
            for (int y = 0; y < num; y++) {
                POIListLevel lvl = new POIListLevel(tableStream, levelOffset);
                lst.setLevel(y, lvl);
                levelOffset += lvl.getSizeInBytes();
            }
        }


        length = LittleEndian.getInt(tableStream, lfoOffset);
        lfoOffset += LittleEndian.INT_SIZE;
        int lfolvlOffset = lfoOffset + (LIST_FORMAT_OVERRIDE_SIZE * length);
        for (int x = 0; x < length; x++) {
            ListFormatOverride lfo = new ListFormatOverride(tableStream, lfoOffset);
            lfoOffset += LIST_FORMAT_OVERRIDE_SIZE;
            int num = lfo.numOverrides();
            for (int y = 0; y < num; y++) {
                while (lfolvlOffset < tableStream.length && tableStream[lfolvlOffset] == -1) {
                    lfolvlOffset++;
                }
                if (lfolvlOffset >= tableStream.length) {
                    continue;
                }
                ListFormatOverrideLevel lfolvl = new ListFormatOverrideLevel(tableStream,
                        lfolvlOffset);
                lfo.setOverride(y, lfolvl);
                lfolvlOffset += lfolvl.getSizeInBytes();
            }
            _overrideList.add(lfo);
        }
    }

    public int addList(POIListData lst, ListFormatOverride override) {
        int lsid = lst.getLsid();
        while (_listMap.get(Integer.valueOf(lsid)) != null) {
            lsid = lst.resetListID();
            override.setLsid(lsid);
        }
        _listMap.put(Integer.valueOf(lsid), lst);
        _overrideList.add(override);
        return lsid;
    }

    public void writeListDataTo(HWPFOutputStream tableStream) throws IOException {
        int listSize = _listMap.size();


        ByteArrayOutputStream levelBuf = new ByteArrayOutputStream();

        byte[] shortHolder = new byte[2];
        LittleEndian.putShort(shortHolder, (short) listSize);
        tableStream.write(shortHolder);

        for (Integer x : _listMap.sortedKeys()) {
            POIListData lst = _listMap.get(x);
            tableStream.write(lst.toByteArray());
            POIListLevel[] lvls = lst.getLevels();
            for (int y = 0; y < lvls.length; y++) {
                levelBuf.write(lvls[y].toByteArray());
            }
        }
        tableStream.write(levelBuf.toByteArray());
    }

    public void writeListOverridesTo(HWPFOutputStream tableStream) throws IOException {


        ByteArrayOutputStream levelBuf = new ByteArrayOutputStream();

        int size = _overrideList.size();

        byte[] intHolder = new byte[4];
        LittleEndian.putInt(intHolder, size);
        tableStream.write(intHolder);

        for (int x = 0; x < size; x++) {
            ListFormatOverride lfo = _overrideList.get(x);
            tableStream.write(lfo.toByteArray());
            ListFormatOverrideLevel[] lfolvls = lfo.getLevelOverrides();
            for (int y = 0; y < lfolvls.length; y++) {
                levelBuf.write(lfolvls[y].toByteArray());
            }
        }
        tableStream.write(levelBuf.toByteArray());

    }

    public ListFormatOverride getOverride(int lfoIndex) {
        if (_overrideList.size() >= lfoIndex) {
            return _overrideList.get(lfoIndex - 1);
        }

        return null;
    }

    public int getOverrideCount() {
        return _overrideList.size();
    }

    public int getOverrideIndexFromListID(int lstid) {
        int returnVal = -1;
        int size = _overrideList.size();
        for (int x = 0; x < size; x++) {
            ListFormatOverride next = _overrideList.get(x);
            if (next.getLsid() == lstid) {

                returnVal = x + 1;
                break;
            }
        }
        if (returnVal == -1) {
            throw new NoSuchElementException("No list found with the specified ID");
        }
        return returnVal;
    }

    public POIListLevel getLevel(int listID, int level) {
        POIListData lst = _listMap.get(Integer.valueOf(listID));
        if (level < lst.numLevels()) {
            POIListLevel lvl = lst.getLevels()[level];
            return lvl;
        }
        log.log(POILogger.WARN, "Requested level " + level
                + " which was greater than the maximum defined (" + lst.numLevels() + ")");
        return null;
    }

    public POIListData getListData(int listID) {
        return _listMap.get(Integer.valueOf(listID));
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        ListTables tables = (ListTables) obj;

        if (_listMap.size() == tables._listMap.size()) {
            Iterator<Integer> it = _listMap.keySet().iterator();
            while (it.hasNext()) {
                Integer key = it.next();
                POIListData lst1 = _listMap.get(key);
                POIListData lst2 = tables._listMap.get(key);
                if (!lst1.equals(lst2)) {
                    return false;
                }
            }
            int size = _overrideList.size();
            if (size == tables._overrideList.size()) {
                for (int x = 0; x < size; x++) {
                    if (!_overrideList.get(x).equals(tables._overrideList.get(x))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static class ListMap implements Map<Integer, POIListData> {
        private ArrayList<Integer> keyList = new ArrayList<Integer>();
        private HashMap<Integer, POIListData> parent = new HashMap<Integer, POIListData>();

        private ListMap() {
        }

        public void clear() {
            keyList.clear();
            parent.clear();
        }

        public boolean containsKey(Object key) {
            return parent.containsKey(key);
        }

        public boolean containsValue(Object value) {
            return parent.containsValue(value);
        }

        public POIListData get(Object key) {
            return parent.get(key);
        }

        public boolean isEmpty() {
            return parent.isEmpty();
        }

        public POIListData put(Integer key, POIListData value) {
            keyList.add(key);
            return parent.put(key, value);
        }

        public void putAll(Map<? extends Integer, ? extends POIListData> map) {
            for (Entry<? extends Integer, ? extends POIListData> entry : map.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }

        public POIListData remove(Object key) {
            keyList.remove(key);
            return parent.remove(key);
        }

        public int size() {
            return parent.size();
        }

        public Set<Entry<Integer, POIListData>> entrySet() {
            throw new IllegalStateException("Use sortedKeys() + get() instead");
        }

        public List<Integer> sortedKeys() {
            return Collections.unmodifiableList(keyList);
        }

        public Set<Integer> keySet() {
            throw new IllegalStateException("Use sortedKeys() instead");
        }

        public Collection<POIListData> values() {
            ArrayList<POIListData> values = new ArrayList<POIListData>();
            for (Integer key : keyList) {
                values.add(parent.get(key));
            }
            return values;
        }
    }
}
