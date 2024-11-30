
package com.document.render.office.thirdpart.emf.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class RoutedInputStream extends DecodingInputStream {

    private static final int UNROUTED = 0;
    private static final int ROUTEFOUND = 1;
    private static final int ROUTEINFORM = 2;
    private static final int ROUTED = 3;
    private static final int CLOSING = 4;
    private static final int CLOSED = 5;
    private InputStream in;
    private Map routes, listeners;
    private byte[] buffer;


    private int sob, eob;


    private int index;


    private int state;


    private byte[] start;


    public RoutedInputStream(InputStream input) {
        super();
        in = input;
        routes = new HashMap();
        listeners = new HashMap();

        buffer = new byte[20];
        sob = -1;
        eob = 0;
        index = 0;
        state = UNROUTED;
    }


    private static boolean equals(byte[] cmp, byte[] buf, int index) {
        for (int i = cmp.length - 1; i > 0; i--) {
            int j = (index + buf.length + i) % buf.length;
            if (buf[j] != cmp[i]) {
                return false;
            }
        }

        return buf[(index + buf.length) % buf.length] == cmp[0];
    }


    public int read() throws IOException {

        int result;

        NEWSTATE:
        while (true) {
            switch (state) {
                default:
                case UNROUTED:

                    int b = -1;
                    while (sob != eob) {
                        if (sob < 0) {
                            sob = 0;
                        }


                        b = in.read();
                        if (b < 0) {

                            state = CLOSING;
                            continue NEWSTATE;
                        }


                        buffer[eob] = (byte) b;
                        eob = (eob + 1) % buffer.length;


                        for (Iterator i = routes.keySet().iterator(); i.hasNext(); ) {
                            start = (byte[]) i.next();
                            index = (eob + buffer.length - start.length)
                                    % buffer.length;
                            if (equals(start, buffer, index)) {
                                state = ROUTEFOUND;
                                continue NEWSTATE;
                            }
                        }

                    }






                    result = buffer[sob];
                    sob = (sob + 1) % buffer.length;
                    return result;

                case ROUTEFOUND:



                    if (sob == index) {
                        state = ROUTEINFORM;
                        continue NEWSTATE;
                    }
                    result = buffer[sob];
                    sob = (sob + 1) % buffer.length;
                    return result;

                case ROUTEINFORM:

                    state = ROUTED;
                    Route route = new Route(start, (byte[]) routes.get(start));


                    ((RouteListener) listeners.get(start)).routeFound(route);


                    state = UNROUTED;
                    if (sob == eob) {

                        sob = -1;
                        eob = 0;
                        continue NEWSTATE;
                    }






                    result = buffer[sob];
                    sob = (sob + 1) % buffer.length;
                    return result;

                case ROUTED:



                    if (sob == eob) {
                        result = in.read();
                        if (result < 0) {
                            state = CLOSED;
                            continue NEWSTATE;
                        }
                    } else {
                        result = buffer[sob];
                        sob = (sob + 1) % buffer.length;
                    }
                    return result;

                case CLOSING:


                    if (sob == eob) {
                        state = CLOSED;
                        continue NEWSTATE;
                    }
                    result = buffer[sob];
                    sob = (sob + 1) % buffer.length;
                    return result;

                case CLOSED:

                    return -1;
            }
        }
    }


    public void addRoute(String start, String end, RouteListener listener) {
        addRoute(start.getBytes(), (end == null) ? null : end.getBytes(),
                listener);
    }


    public void addRoute(byte[] start, byte[] end, RouteListener listener) {
        for (Iterator i = routes.keySet().iterator(); i.hasNext(); ) {
            String key = new String((byte[]) i.next());
            String name = new String(start);
            if (key.startsWith(name) || name.startsWith(key)) {
                throw new IllegalArgumentException("Route '" + name
                        + "' cannot be added since it overlaps with '" + key
                        + "'.");
            }
        }

        routes.put(start, end);
        listeners.put(start, listener);


        if (start.length > buffer.length - 1) {
            byte[] tmp = new byte[start.length + 1];
            System.arraycopy(buffer, 0, tmp, 0, buffer.length);
            buffer = tmp;
        }
    }


    public class Route extends InputStream {

        private byte[] start, end;

        private byte[] buffer;

        private int index;

        private boolean closed;


        public Route(byte[] start, byte[] end) {
            this.start = start;
            this.end = end;
            if (end != null) {
                buffer = new byte[end.length];
            }
            index = 0;
            closed = false;
        }


        public int read() throws IOException {
            if (closed) {
                return -1;
            }

            int b = RoutedInputStream.this.read();
            if (b < 0) {
                closed = true;
                return b;
            }

            if (end == null) {
                return b;
            }

            buffer[index] = (byte) b;
            index = (index + 1) % buffer.length;

            closed = RoutedInputStream.equals(end, buffer, index);

            return b;
        }


        public void close() throws IOException {
            while (read() >= 0) {
                continue;
            }
            closed = true;
        }


        public byte[] getStart() {
            return start;
        }


        public byte[] getEnd() {
            return end;
        }
    }
}
