
package com.document.render.office.fc.ss.format;


import com.document.render.office.fc.ss.format.CellFormatPart.PartHandler;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.util.BitSet;
import java.util.Collections;
import java.util.Formatter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;


public class CellNumberFormatter extends CellFormatter {
    private static final CellFormatter SIMPLE_INT = new CellNumberFormatter(
            "#");
    private static final CellFormatter SIMPLE_FLOAT = new CellNumberFormatter(
            "#.#");
    static final CellFormatter SIMPLE_NUMBER = new CellFormatter("General") {
        public void formatValue(StringBuffer toAppendTo, Object value) {
            if (value == null)
                return;
            if (value instanceof Number) {
                Number num = (Number) value;
                if (num.doubleValue() % 1.0 == 0)
                    SIMPLE_INT.formatValue(toAppendTo, value);
                else
                    SIMPLE_FLOAT.formatValue(toAppendTo, value);
            } else {
                CellTextFormatter.SIMPLE_TEXT.formatValue(toAppendTo, value);
            }
        }

        public void simpleValue(StringBuffer toAppendTo, Object value) {
            formatValue(toAppendTo, value);
        }
    };
    private final String desc;
    private final List<Special> specials;
    private String printfFmt;
    private double scale;
    private Special decimalPoint;
    private Special slash;
    private Special exponent;
    private Special numerator;
    private Special afterInteger;
    private Special afterFractional;
    private boolean integerCommas;
    private List<Special> integerSpecials;
    private List<Special> fractionalSpecials;
    private List<Special> numeratorSpecials;
    private List<Special> denominatorSpecials;
    private List<Special> exponentSpecials;
    private List<Special> exponentDigitSpecials;
    private int maxDenominator;
    private String numeratorFmt;
    private String denominatorFmt;
    private boolean improperFraction;
    private DecimalFormat decimalFmt;


    public CellNumberFormatter(String format) {
        super(format);

        scale = 1;

        specials = new LinkedList<Special>();

        NumPartHandler partHandler = new NumPartHandler();
        StringBuffer descBuf = CellFormatPart.parseFormat(format,
                CellFormatType.NUMBER, partHandler);


        if ((decimalPoint != null || exponent != null) && slash != null) {
            slash = null;
            numerator = null;
        }

        interpretCommas(descBuf);

        int precision;
        int fractionPartWidth = 0;
        if (decimalPoint == null) {
            precision = 0;
        } else {
            precision = interpretPrecision();
            fractionPartWidth = 1 + precision;
            if (precision == 0) {


                specials.remove(decimalPoint);
                decimalPoint = null;
            }
        }

        if (precision == 0)
            fractionalSpecials = Collections.emptyList();
        else
            fractionalSpecials = specials.subList(specials.indexOf(
                    decimalPoint) + 1, fractionalEnd());
        if (exponent == null)
            exponentSpecials = Collections.emptyList();
        else {
            int exponentPos = specials.indexOf(exponent);
            exponentSpecials = specialsFor(exponentPos, 2);
            exponentDigitSpecials = specialsFor(exponentPos + 2);
        }

        if (slash == null) {
            numeratorSpecials = Collections.emptyList();
            denominatorSpecials = Collections.emptyList();
        } else {
            if (numerator == null)
                numeratorSpecials = Collections.emptyList();
            else
                numeratorSpecials = specialsFor(specials.indexOf(numerator));

            denominatorSpecials = specialsFor(specials.indexOf(slash) + 1);
            if (denominatorSpecials.isEmpty()) {

                numeratorSpecials = Collections.emptyList();
            } else {
                maxDenominator = maxValue(denominatorSpecials);
                numeratorFmt = singleNumberFormat(numeratorSpecials);
                denominatorFmt = singleNumberFormat(denominatorSpecials);
            }
        }

        integerSpecials = specials.subList(0, integerEnd());

        if (exponent == null) {
            StringBuffer fmtBuf = new StringBuffer("%");

            int integerPartWidth = calculateIntegerPartWidth();
            int totalWidth = integerPartWidth + fractionPartWidth;

            fmtBuf.append('0').append(totalWidth).append('.').append(precision);

            fmtBuf.append("f");
            printfFmt = fmtBuf.toString();
        } else {
            StringBuffer fmtBuf = new StringBuffer();
            boolean first = true;
            List<Special> specialList = integerSpecials;
            if (integerSpecials.size() == 1) {

                fmtBuf.append("0");
                first = false;
            } else
                for (Special s : specialList) {
                    if (isDigitFmt(s)) {
                        fmtBuf.append(first ? '#' : '0');
                        first = false;
                    }
                }
            if (fractionalSpecials.size() > 0) {
                fmtBuf.append('.');
                for (Special s : fractionalSpecials) {
                    if (isDigitFmt(s)) {
                        if (!first)
                            fmtBuf.append('0');
                        first = false;
                    }
                }
            }
            fmtBuf.append('E');
            placeZeros(fmtBuf, exponentSpecials.subList(2,
                    exponentSpecials.size()));
            decimalFmt = new DecimalFormat(fmtBuf.toString());
        }

        if (exponent != null)
            scale =
                    1;

        desc = descBuf.toString();
    }

    private static void placeZeros(StringBuffer sb, List<Special> specials) {
        for (Special s : specials) {
            if (isDigitFmt(s))
                sb.append('0');
        }
    }

    private static Special firstDigit(List<Special> specials) {
        for (Special s : specials) {
            if (isDigitFmt(s))
                return s;
        }
        return null;
    }

    static StringMod insertMod(Special special, CharSequence toAdd, int where) {
        return new StringMod(special, toAdd, where);
    }

    static StringMod deleteMod(Special start, boolean startInclusive,
                               Special end, boolean endInclusive) {

        return new StringMod(start, startInclusive, end, endInclusive);
    }

    static StringMod replaceMod(Special start, boolean startInclusive,
                                Special end, boolean endInclusive, char withChar) {

        return new StringMod(start, startInclusive, end, endInclusive,
                withChar);
    }

    private static String singleNumberFormat(List<Special> numSpecials) {
        return "%0" + numSpecials.size() + "d";
    }

    private static int maxValue(List<Special> s) {
        return (int) Math.round(Math.pow(10, s.size()) - 1);
    }

    private static boolean isDigitFmt(Special s) {
        return s.ch == '0' || s.ch == '?' || s.ch == '#';
    }

    private static boolean hasChar(char ch, List<Special>... numSpecials) {
        for (List<Special> specials : numSpecials) {
            for (Special s : specials) {
                if (s.ch == ch) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasOnly(char ch, List<Special>... numSpecials) {
        for (List<Special> specials : numSpecials) {
            for (Special s : specials) {
                if (s.ch != ch) {
                    return false;
                }
            }
        }
        return true;
    }

    private List<Special> specialsFor(int pos, int takeFirst) {
        if (pos >= specials.size())
            return Collections.emptyList();
        ListIterator<Special> it = specials.listIterator(pos + takeFirst);
        Special last = it.next();
        int end = pos + takeFirst;
        while (it.hasNext()) {
            Special s = it.next();
            if (!isDigitFmt(s) || s.pos - last.pos > 1)
                break;
            end++;
            last = s;
        }
        return specials.subList(pos, end + 1);
    }

    private List<Special> specialsFor(int pos) {
        return specialsFor(pos, 0);
    }

    private Special previousNumber() {
        ListIterator<Special> it = specials.listIterator(specials.size());
        while (it.hasPrevious()) {
            Special s = it.previous();
            if (isDigitFmt(s)) {
                Special numStart = s;
                Special last = s;
                while (it.hasPrevious()) {
                    s = it.previous();
                    if (last.pos - s.pos > 1)
                        break;
                    if (isDigitFmt(s))
                        numStart = s;
                    else
                        break;
                    last = s;
                }
                return numStart;
            }
        }
        return null;
    }

    private int calculateIntegerPartWidth() {
        ListIterator<Special> it = specials.listIterator();
        int digitCount = 0;
        while (it.hasNext()) {
            Special s = it.next();

            if (s == afterInteger)
                break;
            else if (isDigitFmt(s))
                digitCount++;
        }
        return digitCount;
    }

    private int interpretPrecision() {
        if (decimalPoint == null) {
            return -1;
        } else {
            int precision = 0;
            ListIterator<Special> it = specials.listIterator(specials.indexOf(
                    decimalPoint));
            if (it.hasNext())
                it.next();
            while (it.hasNext()) {
                Special s = it.next();
                if (isDigitFmt(s))
                    precision++;
                else
                    break;
            }
            return precision;
        }
    }

    private void interpretCommas(StringBuffer sb) {

        ListIterator<Special> it = specials.listIterator(integerEnd());

        boolean stillScaling = true;
        integerCommas = false;
        while (it.hasPrevious()) {
            Special s = it.previous();
            if (s.ch != ',') {
                stillScaling = false;
            } else {
                if (stillScaling) {
                    scale /= 1000;
                } else {
                    integerCommas = true;
                }
            }
        }

        if (decimalPoint != null) {
            it = specials.listIterator(fractionalEnd());
            while (it.hasPrevious()) {
                Special s = it.previous();
                if (s.ch != ',') {
                    break;
                } else {
                    scale /= 1000;
                }
            }
        }


        it = specials.listIterator();
        int removed = 0;
        while (it.hasNext()) {
            Special s = it.next();
            s.pos -= removed;
            if (s.ch == ',') {
                removed++;
                it.remove();
                sb.deleteCharAt(s.pos);
            }
        }
    }

    private int integerEnd() {
        if (decimalPoint != null)
            afterInteger = decimalPoint;
        else if (exponent != null)
            afterInteger = exponent;
        else if (numerator != null)
            afterInteger = numerator;
        else
            afterInteger = null;
        return afterInteger == null ? specials.size() : specials.indexOf(
                afterInteger);
    }

    private int fractionalEnd() {
        int end;
        if (exponent != null)
            afterFractional = exponent;
        else if (numerator != null)
            afterInteger = numerator;
        else
            afterFractional = null;
        end = afterFractional == null ? specials.size() : specials.indexOf(
                afterFractional);
        return end;
    }


    public void formatValue(StringBuffer toAppendTo, Object valueObject) {
        double value = ((Number) valueObject).doubleValue();
        value *= scale;


        boolean negative = value < 0;
        if (negative)
            value = -value;


        double fractional = 0;
        if (slash != null) {
            if (improperFraction) {
                fractional = value;
                value = 0;
            } else {
                fractional = value % 1.0;

                value = (long) value;
            }
        }

        Set<StringMod> mods = new TreeSet<StringMod>();
        StringBuffer output = new StringBuffer(desc);

        if (exponent != null) {
            writeScientific(value, output, mods);
        } else if (improperFraction) {
            writeFraction(value, null, fractional, output, mods);
        } else {
            StringBuffer result = new StringBuffer();
            Formatter f = new Formatter(result);
            f.format(LOCALE, printfFmt, value);

            if (numerator == null) {
                writeFractional(result, output);
                writeInteger(result, output, integerSpecials, mods,
                        integerCommas);
            } else {
                writeFraction(value, result, fractional, output, mods);
            }
        }


        ListIterator<Special> it = specials.listIterator();
        Iterator<StringMod> changes = mods.iterator();
        StringMod nextChange = (changes.hasNext() ? changes.next() : null);
        int adjust = 0;
        BitSet deletedChars = new BitSet();
        while (it.hasNext()) {
            Special s = it.next();
            int adjustedPos = s.pos + adjust;
            if (!deletedChars.get(s.pos) && output.charAt(adjustedPos) == '#') {
                output.deleteCharAt(adjustedPos);
                adjust--;
                deletedChars.set(s.pos);
            }
            while (nextChange != null && s == nextChange.special) {
                int lenBefore = output.length();
                int modPos = s.pos + adjust;
                int posTweak = 0;
                switch (nextChange.op) {
                    case StringMod.AFTER:

                        if (nextChange.toAdd.equals(",") && deletedChars.get(s.pos))
                            break;
                        posTweak = 1;

                    case StringMod.BEFORE:
                        output.insert(modPos + posTweak, nextChange.toAdd);
                        break;

                    case StringMod.REPLACE:
                        int delPos =
                                s.pos;
                        if (!nextChange.startInclusive) {
                            delPos++;
                            modPos++;
                        }


                        while (deletedChars.get(delPos)) {
                            delPos++;
                            modPos++;
                        }

                        int delEndPos =
                                nextChange.end.pos;
                        if (nextChange.endInclusive)
                            delEndPos++;

                        int modEndPos =
                                delEndPos + adjust;

                        if (modPos < modEndPos) {
                            if (nextChange.toAdd == "")
                                output.delete(modPos, modEndPos);
                            else {
                                char fillCh = nextChange.toAdd.charAt(0);
                                for (int i = modPos; i < modEndPos; i++)
                                    output.setCharAt(i, fillCh);
                            }
                            deletedChars.set(delPos, delEndPos);
                        }
                        break;

                    default:
                        throw new IllegalStateException(
                                "Unknown op: " + nextChange.op);
                }
                adjust += output.length() - lenBefore;

                if (changes.hasNext())
                    nextChange = changes.next();
                else
                    nextChange = null;
            }
        }


        if (negative)
            toAppendTo.append('-');
        toAppendTo.append(output);
    }

    private void writeScientific(double value, StringBuffer output,
                                 Set<StringMod> mods) {

        StringBuffer result = new StringBuffer();
        FieldPosition fractionPos = new FieldPosition(
                DecimalFormat.FRACTION_FIELD);
        decimalFmt.format(value, result, fractionPos);
        writeInteger(result, output, integerSpecials, mods, integerCommas);
        writeFractional(result, output);




        int ePos = fractionPos.getEndIndex();
        int signPos = ePos + 1;
        char expSignRes = result.charAt(signPos);
        if (expSignRes != '-') {

            expSignRes = '+';


            result.insert(signPos, '+');
        }


        ListIterator<Special> it = exponentSpecials.listIterator(1);
        Special expSign = it.next();
        char expSignFmt = expSign.ch;



        if (expSignRes == '-' || expSignFmt == '+')
            mods.add(replaceMod(expSign, true, expSign, true, expSignRes));
        else
            mods.add(deleteMod(expSign, true, expSign, true));

        StringBuffer exponentNum = new StringBuffer(result.substring(
                signPos + 1));
        writeInteger(exponentNum, output, exponentDigitSpecials, mods, false);
    }

    private void writeFraction(double value, StringBuffer result,
                               double fractional, StringBuffer output, Set<StringMod> mods) {



        if (!improperFraction) {


            if (fractional == 0 && !hasChar('0', numeratorSpecials)) {
                writeInteger(result, output, integerSpecials, mods, false);

                Special start = integerSpecials.get(integerSpecials.size() - 1);
                Special end = denominatorSpecials.get(
                        denominatorSpecials.size() - 1);
                if (hasChar('?', integerSpecials, numeratorSpecials,
                        denominatorSpecials)) {

                    mods.add(replaceMod(start, false, end, true, ' '));
                } else {

                    mods.add(deleteMod(start, false, end, true));
                }


                return;
            } else {

                boolean allZero = (value == 0 && fractional == 0);
                boolean willShowFraction = fractional != 0 || hasChar('0',
                        numeratorSpecials);
                boolean removeBecauseZero = allZero && (hasOnly('#',
                        integerSpecials) || !hasChar('0', numeratorSpecials));
                boolean removeBecauseFraction =
                        !allZero && value == 0 && willShowFraction && !hasChar(
                                '0', integerSpecials);
                if (removeBecauseZero || removeBecauseFraction) {
                    Special start = integerSpecials.get(
                            integerSpecials.size() - 1);
                    if (hasChar('?', integerSpecials, numeratorSpecials)) {
                        mods.add(replaceMod(start, true, numerator, false,
                                ' '));
                    } else {
                        mods.add(deleteMod(start, true, numerator, false));
                    }
                } else {

                    writeInteger(result, output, integerSpecials, mods, false);
                }
            }
        }


        try {
            int n;
            int d;

            if (fractional == 0 || (improperFraction && fractional % 1 == 0)) {

                n = (int) Math.round(fractional);
                d = 1;
            } else {
                Fraction frac = new Fraction(fractional, maxDenominator);
                n = frac.getNumerator();
                d = frac.getDenominator();
            }
            if (improperFraction)
                n += Math.round(value * d);
            writeSingleInteger(numeratorFmt, n, output, numeratorSpecials,
                    mods);
            writeSingleInteger(denominatorFmt, d, output, denominatorSpecials,
                    mods);
        } catch (RuntimeException ignored) {
            ignored.printStackTrace();
        }
    }

    private void writeSingleInteger(String fmt, int num, StringBuffer output,
                                    List<Special> numSpecials, Set<StringMod> mods) {

        StringBuffer sb = new StringBuffer();
        Formatter formatter = new Formatter(sb);
        formatter.format(LOCALE, fmt, num);
        writeInteger(sb, output, numSpecials, mods, false);
    }

    private void writeInteger(StringBuffer result, StringBuffer output,
                              List<Special> numSpecials, Set<StringMod> mods,
                              boolean showCommas) {

        int pos = result.indexOf(".") - 1;
        if (pos < 0) {
            if (exponent != null && numSpecials == integerSpecials)
                pos = result.indexOf("E") - 1;
            else
                pos = result.length() - 1;
        }

        int strip;
        for (strip = 0; strip < pos; strip++) {
            char resultCh = result.charAt(strip);
            if (resultCh != '0' && resultCh != ',')
                break;
        }

        ListIterator<Special> it = numSpecials.listIterator(numSpecials.size());
        boolean followWithComma = false;
        Special lastOutputIntegerDigit = null;
        int digit = 0;
        while (it.hasPrevious()) {
            char resultCh;
            if (pos >= 0)
                resultCh = result.charAt(pos);
            else {

                resultCh = '0';
            }
            Special s = it.previous();
            followWithComma = showCommas && digit > 0 && digit % 3 == 0;
            boolean zeroStrip = false;
            if (resultCh != '0' || s.ch == '0' || s.ch == '?' || pos >= strip) {
                zeroStrip = s.ch == '?' && pos < strip;
                output.setCharAt(s.pos, (zeroStrip ? ' ' : resultCh));
                lastOutputIntegerDigit = s;
            }
            if (followWithComma) {
                mods.add(insertMod(s, zeroStrip ? " " : ",", StringMod.AFTER));
                followWithComma = false;
            }
            digit++;
            --pos;
        }
        StringBuffer extraLeadingDigits = new StringBuffer();
        if (pos >= 0) {

            ++pos;
            extraLeadingDigits = new StringBuffer(result.substring(0, pos));
            if (showCommas) {
                while (pos > 0) {
                    if (digit > 0 && digit % 3 == 0)
                        extraLeadingDigits.insert(pos, ',');
                    digit++;
                    --pos;
                }
            }
            mods.add(insertMod(lastOutputIntegerDigit, extraLeadingDigits,
                    StringMod.BEFORE));
        }
    }

    private void writeFractional(StringBuffer result, StringBuffer output) {
        int digit;
        int strip;
        ListIterator<Special> it;
        if (fractionalSpecials.size() > 0) {
            digit = result.indexOf(".") + 1;
            if (exponent != null)
                strip = result.indexOf("e") - 1;
            else
                strip = result.length() - 1;
            while (strip > digit && result.charAt(strip) == '0')
                strip--;
            it = fractionalSpecials.listIterator();
            while (it.hasNext()) {
                Special s = it.next();
                char resultCh = result.charAt(digit);
                if (resultCh != '0' || s.ch == '0' || digit < strip)
                    output.setCharAt(s.pos, resultCh);
                else if (s.ch == '?') {

                    output.setCharAt(s.pos, ' ');
                }
                digit++;
            }
        }
    }


    public void simpleValue(StringBuffer toAppendTo, Object value) {
        SIMPLE_NUMBER.formatValue(toAppendTo, value);
    }


    static class Special {
        final char ch;
        int pos;

        Special(char ch, int pos) {
            this.ch = ch;
            this.pos = pos;
        }

        @Override
        public String toString() {
            return "'" + ch + "' @ " + pos;
        }
    }


    static class StringMod implements Comparable<StringMod> {
        public static final int BEFORE = 1;
        public static final int AFTER = 2;
        public static final int REPLACE = 3;
        final Special special;
        final int op;
        CharSequence toAdd;
        Special end;
        boolean startInclusive;
        boolean endInclusive;

        private StringMod(Special special, CharSequence toAdd, int op) {
            this.special = special;
            this.toAdd = toAdd;
            this.op = op;
        }

        public StringMod(Special start, boolean startInclusive, Special end,
                         boolean endInclusive, char toAdd) {
            this(start, startInclusive, end, endInclusive);
            this.toAdd = toAdd + "";
        }

        public StringMod(Special start, boolean startInclusive, Special end,
                         boolean endInclusive) {
            special = start;
            this.startInclusive = startInclusive;
            this.end = end;
            this.endInclusive = endInclusive;
            op = REPLACE;
            toAdd = "";
        }

        public int compareTo(StringMod that) {
            int diff = special.pos - that.special.pos;
            if (diff != 0)
                return diff;
            else
                return op - that.op;
        }

        @Override
        public boolean equals(Object that) {
            try {
                return compareTo((StringMod) that) == 0;
            } catch (RuntimeException ignored) {

                return false;
            }
        }

        @Override
        public int hashCode() {
            return special.hashCode() + op;
        }
    }


    private static class Fraction {

        private final int denominator;


        private final int numerator;


        private Fraction(double value, double epsilon, int maxDenominator, int maxIterations) {
            long overflow = Integer.MAX_VALUE;
            double r0 = value;
            long a0 = (long) Math.floor(r0);
            if (a0 > overflow) {
                throw new IllegalArgumentException("Overflow trying to convert " + value + " to fraction (" + a0 + "/" + 1l + ")");
            }



            if (Math.abs(a0 - value) < epsilon) {
                this.numerator = (int) a0;
                this.denominator = 1;
                return;
            }

            long p0 = 1;
            long q0 = 0;
            long p1 = a0;
            long q1 = 1;

            long p2;
            long q2;

            int n = 0;
            boolean stop = false;
            do {
                ++n;
                double r1 = 1.0 / (r0 - a0);
                long a1 = (long) Math.floor(r1);
                p2 = (a1 * p1) + p0;
                q2 = (a1 * q1) + q0;
                if ((p2 > overflow) || (q2 > overflow)) {
                    throw new RuntimeException("Overflow trying to convert " + value + " to fraction (" + p2 + "/" + q2 + ")");
                }

                double convergent = (double) p2 / (double) q2;
                if (n < maxIterations && Math.abs(convergent - value) > epsilon && q2 < maxDenominator) {
                    p0 = p1;
                    p1 = p2;
                    q0 = q1;
                    q1 = q2;
                    a0 = a1;
                    r0 = r1;
                } else {
                    stop = true;
                }
            } while (!stop);

            if (n >= maxIterations) {
                throw new RuntimeException("Unable to convert " + value + " to fraction after " + maxIterations + " iterations");
            }

            if (q2 < maxDenominator) {
                this.numerator = (int) p2;
                this.denominator = (int) q2;
            } else {
                this.numerator = (int) p1;
                this.denominator = (int) q1;
            }

        }


        public Fraction(double value, int maxDenominator) {
            this(value, 0, maxDenominator, 100);
        }


        public int getDenominator() {
            return denominator;
        }


        public int getNumerator() {
            return numerator;
        }

    }

    private class NumPartHandler implements PartHandler {
        private char insertSignForExponent;

        public String handlePart(Matcher m, String part, CellFormatType type,
                                 StringBuffer desc) {
            int pos = desc.length();
            char firstCh = part.charAt(0);
            switch (firstCh) {
                case 'e':
                case 'E':



                    if (exponent == null && specials.size() > 0) {
                        specials.add(exponent = new Special('.', pos));
                        insertSignForExponent = part.charAt(1);
                        return part.substring(0, 1);
                    }
                    break;

                case '0':
                case '?':
                case '#':
                    if (insertSignForExponent != '\0') {
                        specials.add(new Special(insertSignForExponent, pos));
                        desc.append(insertSignForExponent);
                        insertSignForExponent = '\0';
                        pos++;
                    }
                    for (int i = 0; i < part.length(); i++) {
                        char ch = part.charAt(i);
                        specials.add(new Special(ch, pos + i));
                    }
                    break;

                case '.':
                    if (decimalPoint == null && specials.size() > 0)
                        specials.add(decimalPoint = new Special('.', pos));
                    break;

                case '/':

                    if (slash == null && specials.size() > 0) {
                        numerator = previousNumber();


                        if (numerator == firstDigit(specials))
                            improperFraction = true;
                        specials.add(slash = new Special('.', pos));
                    }
                    break;

                case '%':

                    scale *= 100;
                    break;

                default:
                    return null;
            }
            return part;
        }
    }

}
