

package com.document.render.office.common.bulletnumber;

import com.document.render.office.constant.wp.WPViewConstant;
import com.document.render.office.simpletext.view.DocAttr;



public class ListKit {


    private static final char[] ENGLISH_LETTERS = {'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};


    private static final String[] ROMAN_LETTERS = {"m", "cm", "d", "cd", "c", "xc", "l", "xl", "x",
            "ix", "v", "iv", "i"};
    private static final int[] ROMAN_VALUES = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};


    private static final char[] CN_SIMPLIFIED = {'零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖',};
    private static final char[] CN_SIMPLIFIED_SERIES = {'拾', '佰', '仟', '萬'};


    private static final char[] CN_THOUSAND = {'〇', '一', '二', '三', '四', '五', '六', '七', '八', '九', '十'};
    private static final char[] CN_THOUSAND_SERIES = {'十', '百', '千', '万'};



    private static final String[] TRADITIONAL = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};

    private static final String[] ZODIAC = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};


    private static ListKit kit = new ListKit();


    public static ListKit instance() {
        return kit;
    }


    public String getNumberStr(int num, int style) {
        switch (style) {
            case 1:
                return getRoman(num).toUpperCase();

            case 2:
                return getRoman(num);

            case 3:
                return getLetters(num).toUpperCase();

            case 4:
                return getLetters(num);

            case 5:
                return getOrdinal(num);

            case 6:
                return getCardinalText(num);

            case 22:
                return (num < 10 ? "0" : "") + String.valueOf(num);

            case 30:
                return num <= 10 ? TRADITIONAL[num - 1] : String.valueOf(num);

            case 31:
                return num <= 12 ? ZODIAC[num - 1] : String.valueOf(num);

            case 38:
                return getChineseLegalSimplified(num);

            case 39:
                return getChineseCountingThousand(num);

            case 0:
            default:
                return String.valueOf(num);
        }
    }


    public String getLetters(int number) {
        final int base = 26;
        if (number <= 0 || number > 780) {
            return String.valueOf(ENGLISH_LETTERS[0]);
        }
        if (number <= base) {
            return String.valueOf(ENGLISH_LETTERS[number - 1]);
        }

        StringBuilder sb = new StringBuilder();
        int t = number / base;
        int mod = number % base;
        mod = mod == 0 ? 26 : mod;
        for (int i = 0; i < t; i++) {
            sb.append(ENGLISH_LETTERS[mod - 1]);
        }
        return sb.toString();
    }


    public String getRoman(int number) {
        if (number <= 0) {
            return ROMAN_LETTERS[ROMAN_LETTERS.length - 1];
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ROMAN_LETTERS.length; i++) {
            String letter = ROMAN_LETTERS[i];
            int value = ROMAN_VALUES[i];
            while (number >= value) {
                number -= value;
                sb.append(letter);
            }
        }
        return sb.toString();
    }

    /**
     * @return
     */
    public String getChineseLegalSimplified(int number) {
        if (number <= 0 || number > 99999) {
            return String.valueOf(CN_SIMPLIFIED[0]);
        }
        if (number <= 9) {
            return String.valueOf(CN_SIMPLIFIED[number]);
        }
        StringBuilder sb = new StringBuilder();
        String numStr = String.valueOf(number);
        int len = numStr.length();
        boolean isAddZero = false;
        for (int i = 0; i < len; i++) {
            int t = numStr.charAt(i) - 0x30;
            if (t > 0) {
                sb.append(CN_SIMPLIFIED[t]);
                if (len - i - 2 >= 0) {
                    sb.append(CN_SIMPLIFIED_SERIES[len - i - 2]);
                }
                isAddZero = true;
            } else if (isAddZero && i != len - 1) {
                sb.append(CN_SIMPLIFIED[0]);
                isAddZero = false;
            }
        }

        if (sb.charAt(sb.length() - 1) == CN_SIMPLIFIED[0]) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    }

    /**
     * @param number
     * @return
     */
    public String getChineseCountingThousand(int number) {
        if (number <= 0 || number > 99999) {
            return String.valueOf(CN_THOUSAND[0]);
        }
        if (number <= 9) {
            return String.valueOf(CN_THOUSAND[number]);
        }
        StringBuilder sb = new StringBuilder();
        String numStr = String.valueOf(number);
        int len = numStr.length();
        boolean isAddZero = false;
        for (int i = 0; i < len; i++) {
            int t = numStr.charAt(i) - 0x30;
            if (t > 0) {
                sb.append(CN_THOUSAND[t]);
                if (len - i - 2 >= 0) {
                    sb.append(CN_THOUSAND_SERIES[len - i - 2]);
                }
                isAddZero = true;
            } else if (isAddZero && i != len - 1) {
                sb.append(CN_THOUSAND[0]);
                isAddZero = false;
            }
        }

        if (sb.charAt(sb.length() - 1) == CN_THOUSAND[0]) {
            sb.deleteCharAt(sb.length() - 1);
        }

        if (number > 10 && number < 20) {
            if (sb.charAt(0) == CN_THOUSAND[1]) {
                sb.deleteCharAt(0);
            }
        }

        return sb.toString();
    }

    /**
     * @param number
     * @return
     */
    public String getOrdinal(int number) {
        int t = number % 10;
        String suff = "";
        if (t == 1) {
            suff = "st";
        } else if (t == 2) {
            suff = "nd";
        } else if (t == 3) {
            suff = "rd";
        } else {
            suff = "th";
        }
        return String.valueOf(number) + suff;
    }


    /**
     * @param listLevel
     * @return
     */
    public String getBulletText(ListData listData, ListLevel listLevel, DocAttr docAttr, int currentLevel) {
        if (listLevel.getNumberText() == null) {
            return "";
        }
        StringBuffer bulletBuffer = new StringBuffer();
        char[] xst = listLevel.getNumberText();
        for (char ch : xst) {
            if (ch >= 0 && ch < 9) {
                ListLevel numLevel = listData.getLevel(ch);
                int num = numLevel.getStartAt() +
                        (docAttr.rootType == WPViewConstant.NORMAL_ROOT ?
                                numLevel.getNormalParaCount() : numLevel.getParaCount());

                if (ch < currentLevel && num > numLevel.getStartAt()) {
                    num--;
                }
                bulletBuffer.append(getNumberStr(num, numLevel.getNumberFormat()));
            } else {
                bulletBuffer.append(ch);
            }
        }
        byte follow = listLevel.getFollowChar();
        switch (follow) {
            /*case 0:
                bulletBuffer.append( "\t" );
                break;*/
            case 1:
                bulletBuffer.append(" ");
                break;
            default:
                break;
        }
        return bulletBuffer.toString();
    }

    public String getCardinalText(int num) {
        String numberStr = String.valueOf(num);
        String lStr = numberStr;
        String lStrRev = reverseString(lStr);
        String[] a = new String[5];
        switch (lStrRev.length() % 3) {
            case 1:
                lStrRev = lStrRev + "00";
                break;
            case 2:
                lStrRev = lStrRev + "0";
                break;
            default:
                break;
        }
        String StrInt = "";
        for (int i = 0; i <= lStrRev.length() / 3 - 1; i++)
        {
            a[i] = reverseString(lStrRev.substring(3 * i, 3 * i + 3));
            if (!a[i].equals("000"))
            {
                if (i != 0) {
                    StrInt = w3(a[i]) + " " + dw(String.valueOf(i)) + " "
                            + StrInt;




                } else {
                    StrInt = w3(a[i]);
                }
            } else {
                StrInt = w3(a[i]) + StrInt;
            }
        }
        return toUpperCaseFirstOne(StrInt);
    }


    private String reverseString(String str) {
        int lenInt = str.length();
        String[] z = new String[str.length()];
        for (int i = 0; i < lenInt; i++) {
            z[i] = str.substring(i, i + 1);
        }
        str = "";
        for (int i = lenInt - 1; i >= 0; i--) {
            str = str + z[i];
        }
        return str;
    }

    private String zr4(String y) {
        String[] z = new String[10];
        z[0] = "";
        z[1] = "one";
        z[2] = "two";
        z[3] = "three";
        z[4] = "four";
        z[5] = "five";
        z[6] = "six";
        z[7] = "seven";
        z[8] = "eight";
        z[9] = "nine";
        return z[Integer.parseInt(y.substring(0, 1))];
    }

    private String zr3(String y) {
        String[] z = new String[10];
        z[0] = "";
        z[1] = "one";
        z[2] = "two";
        z[3] = "three";
        z[4] = "four";
        z[5] = "five";
        z[6] = "six";
        z[7] = "seven";
        z[8] = "eight";
        z[9] = "nine";
        return z[Integer.parseInt(y.substring(2, 3))];
    }

    private String zr2(String y) {
        String[] z = new String[20];
        z[10] = "ten";
        z[11] = "eleven";
        z[12] = "twelve";
        z[13] = "thirteen";
        z[14] = "fourteen";
        z[15] = "fifteen";
        z[16] = "sixteen";
        z[17] = "seventeen";
        z[18] = "eighteen";
        z[19] = "nineteen";
        return z[Integer.parseInt(y.substring(1, 3))];
    }

    private String zr1(String y) {
        String[] z = new String[10];
        z[1] = "ten";
        z[2] = "twenty";
        z[3] = "thirty";
        z[4] = "forty";
        z[5] = "fifty";
        z[6] = "sixty";
        z[7] = "seventy";
        z[8] = "eighty";
        z[9] = "ninety";
        return z[Integer.parseInt(y.substring(1, 2))];
    }

    private String dw(String y) {
        String[] z = new String[5];
        z[0] = "";
        z[1] = "thousand";
        z[2] = "million";
        z[3] = "billion";
        return z[Integer.parseInt(y)];
    }


    private String w2(String y) {
        String tempstr;
        if (y.substring(1, 2).equals("0"))
        {
            tempstr = zr3(y);
        } else if (y.substring(1, 2).equals("1"))
        {
            tempstr = zr2(y);
        } else {
            if (y.substring(2, 3).equals("0"))
            {
                tempstr = zr1(y);
            } else {
                tempstr = zr1(y) + "-" + zr3(y);
            }
        }
        return tempstr;
    }

    private String w3(String y) {
        String tempstr;
        if (y.substring(0, 1).equals("0"))
        {
            tempstr = w2(y);
        } else {
            if (y.substring(1, 3).equals("00"))
            {
                tempstr = zr4(y) + " " + "hundred";
            } else {
                tempstr = zr4(y) + " " + "hundred" + " " + w2(y);
            }
        }
        return tempstr;
    }

    public String toUpperCaseFirstOne(String s) {
        if (s.equals("")) {
            return String.valueOf(0);
        } else {
            return (new StringBuilder())
                    .append(Character.toUpperCase(s.charAt(0)))
                    .append(s.substring(1)).toString();
        }
    }
}
