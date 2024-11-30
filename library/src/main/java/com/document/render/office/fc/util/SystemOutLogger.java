

package com.document.render.office.fc.util;



public class SystemOutLogger extends POILogger {
    private String _cat;

    public void initialize(final String cat) {
        this._cat = cat;
    }



    public void log(final int level, final Object obj1) {
        log(level, obj1, null);
    }


    public void log(final int level, final Object obj1,
                    final Throwable exception) {
        if (check(level)) {
            System.out.println("[" + _cat + "] " + obj1);
            if (exception != null) {
                exception.printStackTrace(System.out);
            }
        }
    }


    public boolean check(final int level) {
        int currentLevel;
        try {
            currentLevel = Integer.parseInt(System.getProperty("poi.log.level", WARN + ""));
        } catch (SecurityException e) {
            currentLevel = POILogger.DEBUG;
        }

        if (level >= currentLevel) {
            return true;
        }
        return false;
    }


}

