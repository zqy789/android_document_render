
package com.document.render.office.fc.hwpf.usermodel;

import com.document.render.office.fc.hwpf.model.FieldDescriptor;
import com.document.render.office.fc.hwpf.model.FieldsDocumentPart;
import com.document.render.office.fc.hwpf.model.FieldsTables;
import com.document.render.office.fc.hwpf.model.PlexOfField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class FieldsImpl implements Fields {
    private Map<FieldsDocumentPart, Map<Integer, FieldImpl>> _fieldsByOffset;
    private PlexOfFieldComparator comparator = new PlexOfFieldComparator();

    public FieldsImpl(FieldsTables fieldsTables) {
        _fieldsByOffset = new HashMap<FieldsDocumentPart, Map<Integer, FieldImpl>>(
                FieldsDocumentPart.values().length);

        for (FieldsDocumentPart part : FieldsDocumentPart.values()) {
            List<PlexOfField> plexOfCps = fieldsTables.getFieldsPLCF(part);
            _fieldsByOffset.put(part, parseFieldStructure(plexOfCps));
        }
    }


    private static <T> int binarySearch(List<PlexOfField> list,
                                        int startIndex, int endIndex, int requiredStartOffset) {
        checkIndexForBinarySearch(list.size(), startIndex, endIndex);

        int low = startIndex, mid = -1, high = endIndex - 1, result = 0;
        while (low <= high) {
            mid = (low + high) >>> 1;
            int midStart = list.get(mid).getFcStart();

            if (midStart == requiredStartOffset) {
                return mid;
            } else if (midStart < requiredStartOffset) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        if (mid < 0) {
            int insertPoint = endIndex;
            for (int index = startIndex; index < endIndex; index++) {
                if (requiredStartOffset < list.get(index).getFcStart()) {
                    insertPoint = index;
                }
            }
            return -insertPoint - 1;
        }
        return -mid - (result >= 0 ? 1 : 2);
    }

    private static void checkIndexForBinarySearch(int length, int start,
                                                  int end) {
        if (start > end) {
            throw new IllegalArgumentException();
        }
        if (length < end || 0 > start) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public Collection<Field> getFields(FieldsDocumentPart part) {
        Map<Integer, FieldImpl> map = _fieldsByOffset.get(part);
        if (map == null || map.isEmpty())
            return Collections.emptySet();

        return Collections.<Field>unmodifiableCollection(map.values());
    }

    public FieldImpl getFieldByStartOffset(FieldsDocumentPart documentPart,
                                           int offset) {
        Map<Integer, FieldImpl> map = _fieldsByOffset.get(documentPart);
        if (map == null || map.isEmpty())
            return null;

        return map.get(Integer.valueOf(offset));
    }

    private Map<Integer, FieldImpl> parseFieldStructure(
            List<PlexOfField> plexOfFields) {
        if (plexOfFields == null || plexOfFields.isEmpty())
            return new HashMap<Integer, FieldImpl>();

        Collections.sort(plexOfFields, comparator);
        List<FieldImpl> fields = new ArrayList<FieldImpl>(
                plexOfFields.size() / 3 + 1);
        parseFieldStructureImpl(plexOfFields, 0, plexOfFields.size(), fields);

        HashMap<Integer, FieldImpl> result = new HashMap<Integer, FieldImpl>(
                fields.size());
        for (FieldImpl field : fields) {
            result.put(Integer.valueOf(field.getFieldStartOffset()), field);
        }
        return result;
    }

    private void parseFieldStructureImpl(List<PlexOfField> plexOfFields,
                                         int startOffsetInclusive, int endOffsetExclusive,
                                         List<FieldImpl> result) {
        int next = startOffsetInclusive;
        while (next < endOffsetExclusive) {
            PlexOfField startPlexOfField = plexOfFields.get(next);
            if (startPlexOfField.getFld().getBoundaryType() != FieldDescriptor.FIELD_BEGIN_MARK) {

                next++;
                continue;
            }


            int nextNodePositionInList = binarySearch(plexOfFields, next + 1,
                    endOffsetExclusive, startPlexOfField.getFcEnd());
            if (nextNodePositionInList < 0) {

                next++;
                continue;
            }
            PlexOfField nextPlexOfField = plexOfFields
                    .get(nextNodePositionInList);

            switch (nextPlexOfField.getFld().getBoundaryType()) {
                case FieldDescriptor.FIELD_SEPARATOR_MARK: {
                    PlexOfField separatorPlexOfField = nextPlexOfField;

                    int endNodePositionInList = binarySearch(plexOfFields,
                            nextNodePositionInList, endOffsetExclusive,
                            separatorPlexOfField.getFcEnd());
                    if (endNodePositionInList < 0) {

                        next++;
                        continue;
                    }
                    PlexOfField endPlexOfField = plexOfFields
                            .get(endNodePositionInList);

                    if (endPlexOfField.getFld().getBoundaryType() != FieldDescriptor.FIELD_END_MARK) {

                        next++;
                        continue;
                    }

                    FieldImpl field = new FieldImpl(startPlexOfField,
                            separatorPlexOfField, endPlexOfField);
                    result.add(field);


                    if (startPlexOfField.getFcStart() + 1 < separatorPlexOfField
                            .getFcStart() - 1) {
                        parseFieldStructureImpl(plexOfFields, next + 1,
                                nextNodePositionInList, result);
                    }
                    if (separatorPlexOfField.getFcStart() + 1 < endPlexOfField
                            .getFcStart() - 1) {
                        parseFieldStructureImpl(plexOfFields,
                                nextNodePositionInList + 1, endNodePositionInList,
                                result);
                    }

                    next = endNodePositionInList + 1;

                    break;
                }
                case FieldDescriptor.FIELD_END_MARK: {

                    FieldImpl field = new FieldImpl(startPlexOfField, null,
                            nextPlexOfField);
                    result.add(field);


                    if (startPlexOfField.getFcStart() + 1 < nextPlexOfField
                            .getFcStart() - 1) {
                        parseFieldStructureImpl(plexOfFields, next + 1,
                                nextNodePositionInList, result);
                    }

                    next = nextNodePositionInList + 1;
                    break;
                }
                case FieldDescriptor.FIELD_BEGIN_MARK:
                default: {

                    next++;
                    continue;
                }
            }
        }
    }

    private static final class PlexOfFieldComparator implements
            Comparator<PlexOfField> {
        public int compare(PlexOfField o1, PlexOfField o2) {
            int thisVal = o1.getFcStart();
            int anotherVal = o2.getFcStart();
            return thisVal < anotherVal ? -1 : thisVal == anotherVal ? 0 : 1;
        }
    }

}
