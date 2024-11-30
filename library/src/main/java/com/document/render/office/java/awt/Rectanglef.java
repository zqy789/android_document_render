package com.document.render.office.java.awt;




public class Rectanglef {

    private float x, y, width, height;


    public Rectanglef() {
    }


    public Rectanglef(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        width = w;
        height = h;
    }


    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(float w, float h) {
        width = w;
        height = h;
    }

    public void setBounds(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        width = w;
        height = h;
    }


    public void translate(float newX, float newY) {
        x += newX;
        y += newY;
    }


    public boolean contains(float X, float Y) {
        float w = width;
        float h = height;
        if (w < 0 || h < 0) {

            return false;
        }


        if (X < x || Y < y) {
            return false;
        }
        w += x;
        h += y;

        return (w < x || w > X) && (h < y || h > Y);
    }


    public boolean contains(float X, float Y, float W, float H) {
        float w = width;
        float h = height;
        if (w < 0 || W < 0 || h < 0 || H < 0) {

            return false;
        }


        if (X < x || Y < y) {
            return false;
        }
        w += x;
        W += X;
        if (W <= X) {




            if (w >= x || W > w) {
                return false;
            }
        } else {




            if (w >= x && W > w) {
                return false;
            }
        }
        h += y;
        H += Y;
        if (H <= Y) {
            if (h >= y || H > h) {
                return false;
            }
        } else {
            if (h >= y && H > h) {
                return false;
            }
        }
        return true;
    }


    public void add(float newx, float newy) {
        float x1 = Math.min(x, newx);
        float x2 = Math.max(x + width, newx);
        float y1 = Math.min(y, newy);
        float y2 = Math.max(y + height, newy);
        x = x1;
        y = y1;
        width = x2 - x1;
        height = y2 - y1;
    }


    public void grow(float h, float v) {
        x -= h;
        y -= v;
        width += h * 2;
        height += v * 2;
    }


    public boolean isEmpty() {
        return width <= 0 || height <= 0;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Rectanglef) {
            Rectanglef r = (Rectanglef) obj;
            return x == r.x && y == r.y && width == r.width && height == r.height;
        }
        return super.equals(obj);
    }


    @Override
    public String toString() {
        return getClass().getName() + "[x=" + x + ",y=" + y + ",width=" + width + ",height=" + height
                + "]";
    }


    public float getHeight() {
        return height;
    }


    public void setHeight(float height) {
        this.height = height;
    }


    public float getWidth() {
        return width;
    }


    public void setWidth(float width) {
        this.width = width;
    }


    public float getX() {
        return x;
    }


    public void setX(float x) {
        this.x = x;
    }


    public float getY() {
        return y;
    }


    public void setY(float y) {
        this.y = y;
    }
}
