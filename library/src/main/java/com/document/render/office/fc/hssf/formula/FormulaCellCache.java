

package com.document.render.office.fc.hssf.formula;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


final class FormulaCellCache {

    private final Map<Object, FormulaCellCacheEntry> _formulaEntriesByCell;

    public FormulaCellCache() {

        _formulaEntriesByCell = new HashMap<Object, FormulaCellCacheEntry>();
    }

    public CellCacheEntry[] getCacheEntries() {

        FormulaCellCacheEntry[] result = new FormulaCellCacheEntry[_formulaEntriesByCell.size()];
        _formulaEntriesByCell.values().toArray(result);
        return result;
    }

    public void clear() {
        _formulaEntriesByCell.clear();
    }


    public FormulaCellCacheEntry get(EvaluationCell cell) {
        return _formulaEntriesByCell.get(cell.getIdentityKey());
    }

    public void put(EvaluationCell cell, FormulaCellCacheEntry entry) {
        _formulaEntriesByCell.put(cell.getIdentityKey(), entry);
    }

    public FormulaCellCacheEntry remove(EvaluationCell cell) {
        return _formulaEntriesByCell.remove(cell.getIdentityKey());
    }

    public void applyOperation(IEntryOperation operation) {
        Iterator<FormulaCellCacheEntry> i = _formulaEntriesByCell.values().iterator();
        while (i.hasNext()) {
            operation.processEntry(i.next());
        }
    }

    static interface IEntryOperation {
        void processEntry(FormulaCellCacheEntry entry);
    }
}
