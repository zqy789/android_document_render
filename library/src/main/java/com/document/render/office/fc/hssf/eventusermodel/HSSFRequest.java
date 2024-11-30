

package com.document.render.office.fc.hssf.eventusermodel;

import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.RecordFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class HSSFRequest {
    private final Map<Short, List<HSSFListener>> _records;


    public HSSFRequest() {
        _records = new HashMap<Short, List<HSSFListener>>(50);
    }


    public void addListener(HSSFListener lsnr, short sid) {
        List<HSSFListener> list = _records.get(Short.valueOf(sid));

        if (list == null) {
            list = new ArrayList<HSSFListener>(1);
            _records.put(Short.valueOf(sid), list);
        }
        list.add(lsnr);
    }


    public void addListenerForAllRecords(HSSFListener lsnr) {
        short[] rectypes = RecordFactory.getAllKnownRecordSIDs();

        for (int k = 0; k < rectypes.length; k++) {
            addListener(lsnr, rectypes[k]);
        }
    }


    protected short processRecord(Record rec) throws HSSFUserException {
        Object obj = _records.get(Short.valueOf(rec.getSid()));
        short userCode = 0;

        if (obj != null) {
            List listeners = (List) obj;

            for (int k = 0; k < listeners.size(); k++) {
                Object listenObj = listeners.get(k);
                if (listenObj instanceof AbortableHSSFListener) {
                    AbortableHSSFListener listener = (AbortableHSSFListener) listenObj;
                    userCode = listener.abortableProcessRecord(rec);
                    if (userCode != 0)
                        break;
                } else {
                    HSSFListener listener = (HSSFListener) listenObj;
                    listener.processRecord(rec);
                }
            }
        }
        return userCode;
    }
}
