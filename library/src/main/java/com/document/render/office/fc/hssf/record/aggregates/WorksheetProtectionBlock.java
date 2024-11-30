

package com.document.render.office.fc.hssf.record.aggregates;


import com.document.render.office.fc.hssf.model.RecordStream;
import com.document.render.office.fc.hssf.record.ObjectProtectRecord;
import com.document.render.office.fc.hssf.record.PasswordRecord;
import com.document.render.office.fc.hssf.record.ProtectRecord;
import com.document.render.office.fc.hssf.record.Record;
import com.document.render.office.fc.hssf.record.RecordFormatException;
import com.document.render.office.fc.hssf.record.ScenarioProtectRecord;



public final class WorksheetProtectionBlock extends RecordAggregate {


    private ProtectRecord _protectRecord;
    private ObjectProtectRecord _objectProtectRecord;
    private ScenarioProtectRecord _scenarioProtectRecord;
    private PasswordRecord _passwordRecord;


    public WorksheetProtectionBlock() {

    }


    public static boolean isComponentRecord(int sid) {
        switch (sid) {
            case ProtectRecord.sid:
            case ObjectProtectRecord.sid:
            case ScenarioProtectRecord.sid:
            case PasswordRecord.sid:
                return true;
        }
        return false;
    }

    private static void visitIfPresent(Record r, RecordVisitor rv) {
        if (r != null) {
            rv.visitRecord(r);
        }
    }


    private static ObjectProtectRecord createObjectProtect() {
        ObjectProtectRecord retval = new ObjectProtectRecord();
        retval.setProtect(false);
        return retval;
    }


    private static ScenarioProtectRecord createScenarioProtect() {
        ScenarioProtectRecord retval = new ScenarioProtectRecord();
        retval.setProtect(false);
        return retval;
    }


    private static PasswordRecord createPassword() {
        return new PasswordRecord(0x0000);
    }

    private boolean readARecord(RecordStream rs) {
        switch (rs.peekNextSid()) {
            case ProtectRecord.sid:
                checkNotPresent(_protectRecord);
                _protectRecord = (ProtectRecord) rs.getNext();
                break;
            case ObjectProtectRecord.sid:
                checkNotPresent(_objectProtectRecord);
                _objectProtectRecord = (ObjectProtectRecord) rs.getNext();
                break;
            case ScenarioProtectRecord.sid:
                checkNotPresent(_scenarioProtectRecord);
                _scenarioProtectRecord = (ScenarioProtectRecord) rs.getNext();
                break;
            case PasswordRecord.sid:
                checkNotPresent(_passwordRecord);
                _passwordRecord = (PasswordRecord) rs.getNext();
                break;
            default:

                return false;
        }
        return true;
    }

    private void checkNotPresent(Record rec) {
        if (rec != null) {
            throw new RecordFormatException("Duplicate PageSettingsBlock record (sid=0x"
                    + Integer.toHexString(rec.getSid()) + ")");
        }
    }

    public void visitContainedRecords(RecordVisitor rv) {


        visitIfPresent(_protectRecord, rv);
        visitIfPresent(_objectProtectRecord, rv);
        visitIfPresent(_scenarioProtectRecord, rv);
        visitIfPresent(_passwordRecord, rv);
    }

    public PasswordRecord getPasswordRecord() {
        return _passwordRecord;
    }

    public ScenarioProtectRecord getHCenter() {
        return _scenarioProtectRecord;
    }


    public void addRecords(RecordStream rs) {
        while (true) {
            if (!readARecord(rs)) {
                break;
            }
        }
    }


    private ProtectRecord getProtect() {
        if (_protectRecord == null) {
            _protectRecord = new ProtectRecord(false);
        }
        return _protectRecord;
    }


    private PasswordRecord getPassword() {
        if (_passwordRecord == null) {
            _passwordRecord = createPassword();
        }
        return _passwordRecord;
    }


    public void protectSheet(String password, boolean shouldProtectObjects,
                             boolean shouldProtectScenarios) {
        if (password == null) {
            _passwordRecord = null;
            _protectRecord = null;
            _objectProtectRecord = null;
            _scenarioProtectRecord = null;
            return;
        }

        ProtectRecord prec = getProtect();
        PasswordRecord pass = getPassword();
        prec.setProtect(true);
        pass.setPassword(PasswordRecord.hashPassword(password));
        if (_objectProtectRecord == null && shouldProtectObjects) {
            ObjectProtectRecord rec = createObjectProtect();
            rec.setProtect(true);
            _objectProtectRecord = rec;
        }
        if (_scenarioProtectRecord == null && shouldProtectScenarios) {
            ScenarioProtectRecord srec = createScenarioProtect();
            srec.setProtect(true);
            _scenarioProtectRecord = srec;
        }
    }

    public boolean isSheetProtected() {
        return _protectRecord != null && _protectRecord.getProtect();
    }

    public boolean isObjectProtected() {
        return _objectProtectRecord != null && _objectProtectRecord.getProtect();
    }

    public boolean isScenarioProtected() {
        return _scenarioProtectRecord != null && _scenarioProtectRecord.getProtect();
    }

    public int getPasswordHash() {
        if (_passwordRecord == null) {
            return 0;
        }
        return _passwordRecord.getPassword();
    }
}
