

package com.document.render.office.fc.hpsf;


public abstract class VariantTypeException extends HPSFException {

    private Object value;

    private long variantType;



    public VariantTypeException(final long variantType, final Object value,
                                final String msg) {
        super(msg);
        this.variantType = variantType;
        this.value = value;
    }



    public long getVariantType() {
        return variantType;
    }



    public Object getValue() {
        return value;
    }

}
