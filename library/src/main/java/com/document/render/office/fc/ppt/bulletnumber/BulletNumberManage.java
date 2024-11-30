
package com.document.render.office.fc.ppt.bulletnumber;

import com.document.render.office.common.bulletnumber.ListKit;
import com.document.render.office.fc.dom4j.Element;
import com.document.render.office.system.IControl;

import java.util.HashMap;
import java.util.Map;


public class BulletNumberManage {
    private static BulletNumberManage kit;

    private Map<Integer, Integer> lvlFmt;

    private Map<Integer, Integer> lvlStartAt;

    private Map<Integer, Integer> lvlNum;

    private Map<Integer, Integer> styleBulletIDs;

    private Map<String, Integer> bulletIDs;


    public BulletNumberManage() {
        lvlFmt = new HashMap<Integer, Integer>();
        lvlStartAt = new HashMap<Integer, Integer>();
        lvlNum = new HashMap<Integer, Integer>();
        styleBulletIDs = new HashMap<Integer, Integer>();
        bulletIDs = new HashMap<String, Integer>();
    }


    public static BulletNumberManage instance() {
        if (kit == null) {
            kit = new BulletNumberManage();
        }
        return kit;
    }


    public int getBulletID(int styleID) {
        Integer id = styleBulletIDs.get(styleID);
        if (id != null) {
            return id;
        }
        return -1;
    }


    public int addBulletNumber(IControl control, int styleID, Element pPr) {
        Integer id = styleBulletIDs.get(styleID);
        if (id != null) {
            return id;
        } else {
            String text = getBulletText(pPr);
            if (text != null) {
                id = bulletIDs.get(text);
                if (id != null) {
                    if (styleID > 0) {
                        styleBulletIDs.put(styleID, id);
                    }
                    return id;
                } else {
                    id = control.getSysKit().getPGBulletText().addBulletText(text);
                    bulletIDs.put(text, id);
                    if (styleID > 0) {
                        styleBulletIDs.put(styleID, id);
                    }
                    return id;
                }
            } else {
                if (pPr != null && pPr.element("buNone") != null) {

                    styleBulletIDs.put(styleID, -2);
                }
            }
        }
        return -1;
    }


    private String getBulletText(Element pPr) {
        if (pPr != null && pPr.element("buNone") == null) {
            int lvl = 0;
            String val = null;
            if (pPr != null && pPr.attribute("lvl") != null) {
                val = pPr.attributeValue("lvl");
                if (val != null && val.length() > 0) {
                    lvl = Integer.parseInt(val);
                }
            }

            Element temp = pPr.element("buAutoNum");
            if (temp != null) {
                int startAt = 1;
                if (temp.attribute("startAt") != null) {
                    val = temp.attributeValue("startAt");
                    if (val != null && val.length() > 0) {
                        startAt = Integer.parseInt(val);
                    }
                }
                return getText(lvl, convertedNumberFormat(temp.attributeValue("type")), startAt);
            } else if ((temp = pPr.element("buBlip")) != null) {

                if (temp.element("blip") != null && temp.element("blip").attributeValue("embed") != null) {
                    char c = 'l';
                    c = converterNumberChar(c);

                    Integer beforFmt = lvlFmt.get(lvl);
                    if (beforFmt == null || beforFmt != c) {
                        if (beforFmt != null && lvl == 0) {
                            lvlFmt.clear();
                            lvlStartAt.clear();
                            lvlNum.clear();
                        }
                        lvlFmt.put(lvl, (int) c);
                    }
                    return String.valueOf(c);
                }
            } else if ((temp = pPr.element("buChar")) != null) {
                if (temp.attribute("char") != null) {
                    val = temp.attributeValue("char");
                    if (val != null && val.length() > 0) {
                        char c = val.charAt(0);
                        c = converterNumberChar(c);

                        Integer beforFmt = lvlFmt.get(lvl);
                        if (beforFmt == null || beforFmt != c) {
                            if (beforFmt != null && lvl == 0) {
                                lvlFmt.clear();
                                lvlStartAt.clear();
                                lvlNum.clear();
                            }
                            lvlFmt.put(lvl, (int) c);
                        }
                        return String.valueOf(c);
                    }
                }
            }
        }
        return null;
    }


    private String getText(int lvl, int type, int start) {
        StringBuffer bulletBuffer = new StringBuffer();
        Integer beforType = lvlFmt.get(lvl);
        if (beforType == null || beforType != type) {
            if (beforType != null && lvl == 0) {
                lvlFmt.clear();
                lvlStartAt.clear();
                lvlNum.clear();
            }
            lvlFmt.put(lvl, type);
            lvlStartAt.put(lvl, start);
            lvlNum.put(lvl, start);
        } else {
            Integer beforStart = lvlStartAt.get(lvl);
            if (beforStart == null || beforStart != start) {
                lvlStartAt.put(lvl, start);
                lvlNum.put(lvl, start);
            } else {
                start = lvlNum.get(lvl) + 1;
                lvlNum.put(lvl, start);
            }
        }
        int numID = type;
        if (numID == 5) {
            numID = 0;
        } else if (numID == 6 || numID == 11) {
            numID = 0;
        } else if (numID == 7 || numID == 12) {
            numID = 1;
        } else if (numID == 8 || numID == 13) {
            numID = 2;
        } else if (numID == 9 || numID == 14) {
            numID = 3;
        } else if (numID == 10 || numID == 15) {
            numID = 4;
        }

        if (type >= 11 && type <= 15) {
            bulletBuffer.append("(");
        }
        bulletBuffer.append(ListKit.instance().getNumberStr(start, numID));
        if (type >= 6 && type <= 15) {
            bulletBuffer.append(")");
        } else if (type != 5) {
            bulletBuffer.append(".");
        }
        return bulletBuffer.toString();
    }


    public int addBulletNumber(IControl control, int lvl, int type, int start, char c) {
        String text = null;





        {
            text = String.valueOf(converterNumberChar(c));
        }
        Integer id = bulletIDs.get(text);
        if (id != null) {
            return id;
        } else {
            id = control.getSysKit().getPGBulletText().addBulletText(text);
            bulletIDs.put(text, id);
            return id;
        }
    }


    private int convertedNumberFormat(String numFormat) {
        if ("arabicPeriod".equalsIgnoreCase(numFormat)) {
            return 0;
        } else if ("romanUcPeriod".equalsIgnoreCase(numFormat)) {
            return 1;
        } else if ("romanLcPeriod".equalsIgnoreCase(numFormat)) {
            return 2;
        } else if ("alphaUcPeriod".equalsIgnoreCase(numFormat)) {
            return 3;
        } else if ("alphaLcPeriod".equalsIgnoreCase(numFormat)) {
            return 4;
        } else if ("arabicPlain".equalsIgnoreCase(numFormat)
                || "circleNumDbPlain".equalsIgnoreCase(numFormat)) {
            return 5;
        } else if ("arabicParenR".equalsIgnoreCase(numFormat)) {
            return 6;
        } else if ("romanUcParenR".equalsIgnoreCase(numFormat)) {
            return 7;
        } else if ("romanLcParenR".equalsIgnoreCase(numFormat)) {
            return 8;
        } else if ("alphaUcParenR".equalsIgnoreCase(numFormat)) {
            return 9;
        } else if ("alphaLcParenR".equalsIgnoreCase(numFormat)) {
            return 10;
        } else if ("arabicParenBoth".equalsIgnoreCase(numFormat)) {
            return 11;
        } else if ("romanUcParentBoth".equalsIgnoreCase(numFormat)) {
            return 12;
        } else if ("romanLcParenBoth".equalsIgnoreCase(numFormat)) {
            return 13;
        } else if ("alphaUcParenBoth".equalsIgnoreCase(numFormat)) {
            return 14;
        } else if ("alphaLcParenBoth".equalsIgnoreCase(numFormat)) {
            return 15;
        } else if ("ea1JpnChsDbPeriod".equalsIgnoreCase(numFormat)) {
            return 39;
        }
        return 0;
    }


    private int convertedNumberFormat(int type) {
        int fmt = 0;
        switch (type) {
            case 0:
                fmt = 4;
                break;
            case 1:
                fmt = 3;
                break;

            case 2:
                fmt = 6;
                break;

            case 3:
                fmt = 0;
                break;

            case 4:
                fmt = 13;
                break;

            case 5:
                fmt = 8;
                break;

            case 6:
                fmt = 2;
                break;

            case 7:
                fmt = 1;
                break;

            case 8:
                fmt = 15;
                break;

            case 9:
                fmt = 10;
                break;

            case 10:
                fmt = 14;
                break;

            case 11:
                fmt = 9;
                break;

            case 12:
                fmt = 11;
                break;

            case 13:
            case 18:
                fmt = 5;
                break;

            case 14:
                fmt = 12;
                break;

            case 15:
                fmt = 7;
                break;

            case 38:
                fmt = 39;
                break;

            default:
                fmt = 0;
        }
        return fmt;
    }


    private char converterNumberChar(int c) {
        if (c == 0x2022 || c == 0x006C || c == 0x0070) {
            c = 0x25CF;
        } else if (c == 0x006E || c == 0x00A7) {
            c = 0x25A0;
        } else if (c == 0x0075) {
            c = 0x25C6;
        } else if (c == 0x00FC) {
            c = 0x221A;
        } else if (c == 0x00D8) {
            c = 0x2605;
        } else if (c != 0x2013) {
            c = 0x25CF;
        }
        return (char) c;
    }


    public void clearData() {
        if (lvlFmt != null) {
            lvlFmt.clear();
        }
        if (lvlStartAt != null) {
            lvlStartAt.clear();
        }
        if (lvlNum != null) {
            lvlNum.clear();
        }
    }


    public void dispose() {
        if (lvlFmt != null) {
            lvlFmt.clear();
            lvlFmt = null;
        }
        if (lvlStartAt != null) {
            lvlStartAt.clear();
            lvlStartAt = null;
        }
        if (lvlNum != null) {
            lvlNum.clear();
            lvlNum = null;
        }
        if (styleBulletIDs != null) {
            styleBulletIDs.clear();
            styleBulletIDs = null;
        }
        if (bulletIDs != null) {
            bulletIDs.clear();
            bulletIDs = null;
        }
        kit = null;
    }
}
