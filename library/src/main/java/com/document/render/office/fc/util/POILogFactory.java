


package com.document.render.office.fc.util;

import java.util.HashMap;
import java.util.Map;



public class POILogFactory {


    private static Map<String, POILogger> _loggers = new HashMap<String, POILogger>();
    ;


    private static POILogger _nullLogger = new NullLogger();

    private static String _loggerClassName = null;


    private POILogFactory() {
    }



    public static POILogger getLogger(final Class theclass) {
        return getLogger(theclass.getName());
    }



    public static POILogger getLogger(final String cat) {
        POILogger logger = null;






        if (_loggerClassName == null) {
            try {
                _loggerClassName = System.getProperty("org.apache.poi.util.POILogger");
            } catch (Exception e) {
            }



            if (_loggerClassName == null) {
                _loggerClassName = _nullLogger.getClass().getName();
            }
        }



        if (_loggerClassName.equals(_nullLogger.getClass().getName())) {
            return _nullLogger;
        }




        if (_loggers.containsKey(cat)) {
            logger = _loggers.get(cat);
        } else {
            try {
                Class<? extends POILogger> loggerClass =
                        (Class<? extends POILogger>) Class.forName(_loggerClassName);
                logger = loggerClass.newInstance();
                logger.initialize(cat);
            } catch (Exception e) {

                logger = _nullLogger;
            }


            _loggers.put(cat, logger);
        }
        return logger;
    }
}
