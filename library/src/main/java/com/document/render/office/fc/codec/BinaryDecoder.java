

package com.document.render.office.fc.codec;


public interface BinaryDecoder extends Decoder {


    byte[] decode(byte[] source) throws DecoderException;
}  

