

package com.document.render.office.fc.util;

import java.util.ArrayList;
import java.util.List;


public abstract class POILogger {

    public static int DEBUG = 1;
    public static int INFO = 3;
    public static int WARN = 5;
    public static int ERROR = 7;
    public static int FATAL = 9;


    POILogger() {

    }

    abstract public void initialize(String cat);


    abstract public void log(int level, Object obj1);


    abstract public void log(int level, Object obj1,
                             final Throwable exception);



    abstract public boolean check(int level);


    public void log(int level, Object obj1, Object obj2) {
        if (check(level)) {
            log(level, new StringBuffer(32).append(obj1).append(obj2));
        }
    }


    public void log(int level, Object obj1, Object obj2,
                    Object obj3) {


        if (check(level)) {
            log(level,
                    new StringBuffer(48).append(obj1).append(obj2)
                            .append(obj3));
        }
    }


    public void log(int level, Object obj1, Object obj2,
                    Object obj3, Object obj4) {


        if (check(level)) {
            log(level,
                    new StringBuffer(64).append(obj1).append(obj2)
                            .append(obj3).append(obj4));
        }
    }


    public void log(int level, Object obj1, Object obj2,
                    Object obj3, Object obj4, Object obj5) {


        if (check(level)) {
            log(level,
                    new StringBuffer(80).append(obj1).append(obj2)
                            .append(obj3).append(obj4).append(obj5));
        }
    }


    public void log(int level, Object obj1, Object obj2,
                    Object obj3, Object obj4, Object obj5,
                    Object obj6) {


        if (check(level)) {
            log(level,
                    new StringBuffer(96).append(obj1).append(obj2)
                            .append(obj3).append(obj4).append(obj5).append(obj6));
        }
    }


    public void log(int level, Object obj1, Object obj2,
                    Object obj3, Object obj4, Object obj5,
                    Object obj6, Object obj7) {


        if (check(level)) {
            log(level,
                    new StringBuffer(112).append(obj1).append(obj2)
                            .append(obj3).append(obj4).append(obj5).append(obj6)
                            .append(obj7));
        }
    }


    public void log(int level, Object obj1, Object obj2,
                    Object obj3, Object obj4, Object obj5,
                    Object obj6, Object obj7, Object obj8) {


        if (check(level)) {
            log(level,
                    new StringBuffer(128).append(obj1).append(obj2)
                            .append(obj3).append(obj4).append(obj5).append(obj6)
                            .append(obj7).append(obj8));
        }
    }


    public void log(int level, final Throwable exception) {
        log(level, null, exception);
    }


    public void log(int level, Object obj1, Object obj2,
                    final Throwable exception) {


        if (check(level)) {
            log(level, new StringBuffer(32).append(obj1).append(obj2),
                    exception);
        }
    }


    public void log(int level, Object obj1, Object obj2,
                    Object obj3, final Throwable exception) {


        if (check(level)) {
            log(level, new StringBuffer(48).append(obj1).append(obj2)
                    .append(obj3), exception);
        }
    }


    public void log(int level, Object obj1, Object obj2,
                    Object obj3, Object obj4,
                    final Throwable exception) {


        if (check(level)) {
            log(level, new StringBuffer(64).append(obj1).append(obj2)
                    .append(obj3).append(obj4), exception);
        }
    }


    public void log(int level, Object obj1, Object obj2,
                    Object obj3, Object obj4, Object obj5,
                    final Throwable exception) {


        if (check(level)) {
            log(level, new StringBuffer(80).append(obj1).append(obj2)
                    .append(obj3).append(obj4).append(obj5), exception);
        }
    }


    public void log(int level, Object obj1, Object obj2,
                    Object obj3, Object obj4, Object obj5,
                    Object obj6, final Throwable exception) {


        if (check(level)) {
            log(level, new StringBuffer(96).append(obj1)
                    .append(obj2).append(obj3).append(obj4).append(obj5)
                    .append(obj6), exception);
        }
    }


    public void log(int level, Object obj1, Object obj2,
                    Object obj3, Object obj4, Object obj5,
                    Object obj6, Object obj7,
                    final Throwable exception) {


        if (check(level)) {
            log(level, new StringBuffer(112).append(obj1).append(obj2)
                    .append(obj3).append(obj4).append(obj5).append(obj6)
                    .append(obj7), exception);
        }
    }


    public void log(int level, Object obj1, Object obj2,
                    Object obj3, Object obj4, Object obj5,
                    Object obj6, Object obj7, Object obj8,
                    final Throwable exception) {


        if (check(level)) {
            log(level, new StringBuffer(128).append(obj1).append(obj2)
                    .append(obj3).append(obj4).append(obj5).append(obj6)
                    .append(obj7).append(obj8), exception);
        }
    }


    public void logFormatted(int level, String message,
                             Object obj1) {
        commonLogFormatted(level, message, new Object[]
                {
                        obj1
                });
    }


    public void logFormatted(int level, String message,
                             Object obj1, Object obj2) {
        commonLogFormatted(level, message, new Object[]
                {
                        obj1, obj2
                });
    }


    public void logFormatted(int level, String message,
                             Object obj1, Object obj2,
                             Object obj3) {
        commonLogFormatted(level, message, new Object[]
                {
                        obj1, obj2, obj3
                });
    }


    public void logFormatted(int level, String message,
                             Object obj1, Object obj2,
                             Object obj3, Object obj4) {
        commonLogFormatted(level, message, new Object[]
                {
                        obj1, obj2, obj3, obj4
                });
    }

    private void commonLogFormatted(int level, String message,
                                    Object[] unflatParams) {


        if (check(level)) {
            Object[] params = flattenArrays(unflatParams);

            if (params[params.length - 1] instanceof Throwable) {
                log(level, StringUtil.format(message, params),
                        (Throwable) params[params.length - 1]);
            } else {
                log(level, StringUtil.format(message, params));
            }
        }
    }


    private Object[] flattenArrays(Object[] objects) {
        List<Object> results = new ArrayList<Object>();

        for (int i = 0; i < objects.length; i++) {
            results.addAll(objectToObjectArray(objects[i]));
        }
        return results.toArray(new Object[results.size()]);
    }

    private List<Object> objectToObjectArray(Object object) {
        List<Object> results = new ArrayList<Object>();

        if (object instanceof byte[]) {
            byte[] array = (byte[]) object;

            for (int j = 0; j < array.length; j++) {
                results.add(Byte.valueOf(array[j]));
            }
        }
        if (object instanceof char[]) {
            char[] array = (char[]) object;

            for (int j = 0; j < array.length; j++) {
                results.add(Character.valueOf(array[j]));
            }
        } else if (object instanceof short[]) {
            short[] array = (short[]) object;

            for (int j = 0; j < array.length; j++) {
                results.add(Short.valueOf(array[j]));
            }
        } else if (object instanceof int[]) {
            int[] array = (int[]) object;

            for (int j = 0; j < array.length; j++) {
                results.add(Integer.valueOf(array[j]));
            }
        } else if (object instanceof long[]) {
            long[] array = (long[]) object;

            for (int j = 0; j < array.length; j++) {
                results.add(Long.valueOf(array[j]));
            }
        } else if (object instanceof float[]) {
            float[] array = (float[]) object;

            for (int j = 0; j < array.length; j++) {
                results.add(new Float(array[j]));
            }
        } else if (object instanceof double[]) {
            double[] array = (double[]) object;

            for (int j = 0; j < array.length; j++) {
                results.add(new Double(array[j]));
            }
        } else if (object instanceof Object[]) {
            Object[] array = (Object[]) object;

            for (int j = 0; j < array.length; j++) {
                results.add(array[j]);
            }
        } else {
            results.add(object);
        }
        return results;
    }
}
