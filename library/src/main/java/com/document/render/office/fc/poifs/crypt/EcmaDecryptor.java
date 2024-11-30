
package com.document.render.office.fc.poifs.crypt;

import com.document.render.office.fc.poifs.filesystem.DirectoryNode;
import com.document.render.office.fc.poifs.filesystem.DocumentInputStream;
import com.document.render.office.fc.util.LittleEndian;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;



public class EcmaDecryptor extends Decryptor {
    private final EncryptionInfo info;
    private byte[] passwordHash;

    public EcmaDecryptor(EncryptionInfo info) {
        this.info = info;
    }

    private byte[] generateKey(int block) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

        sha1.update(passwordHash);
        byte[] blockValue = new byte[4];
        LittleEndian.putInt(blockValue, block);
        byte[] finalHash = sha1.digest(blockValue);

        int requiredKeyLength = info.getHeader().getKeySize() / 8;

        byte[] buff = new byte[64];

        Arrays.fill(buff, (byte) 0x36);

        for (int i = 0; i < finalHash.length; i++) {
            buff[i] = (byte) (buff[i] ^ finalHash[i]);
        }

        sha1.reset();
        byte[] x1 = sha1.digest(buff);

        Arrays.fill(buff, (byte) 0x5c);
        for (int i = 0; i < finalHash.length; i++) {
            buff[i] = (byte) (buff[i] ^ finalHash[i]);
        }

        sha1.reset();
        byte[] x2 = sha1.digest(buff);

        byte[] x3 = new byte[x1.length + x2.length];
        System.arraycopy(x1, 0, x3, 0, x1.length);
        System.arraycopy(x2, 0, x3, x1.length, x2.length);

        return truncateOrPad(x3, requiredKeyLength);
    }

    public boolean verifyPassword(String password) throws GeneralSecurityException {
        passwordHash = hashPassword(info, password);

        Cipher cipher = getCipher();

        byte[] verifier = cipher.doFinal(info.getVerifier().getVerifier());

        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] calcVerifierHash = sha1.digest(verifier);

        byte[] verifierHash = truncateOrPad(cipher.doFinal(info.getVerifier().getVerifierHash()), calcVerifierHash.length);

        return Arrays.equals(calcVerifierHash, verifierHash);
    }


    private byte[] truncateOrPad(byte[] source, int length) {
        byte[] result = new byte[length];
        System.arraycopy(source, 0, result, 0, Math.min(length, source.length));
        if (length > source.length) {
            for (int i = source.length; i < length; i++) {
                result[i] = 0;
            }
        }
        return result;
    }

    private Cipher getCipher() throws GeneralSecurityException {
        byte[] key = generateKey(0);
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        SecretKey skey = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, skey);

        return cipher;
    }

    public InputStream getDataStream(DirectoryNode dir) throws IOException, GeneralSecurityException {
        DocumentInputStream dis = dir.createDocumentInputStream("EncryptedPackage");

        long size = dis.readLong();

        return new CipherInputStream(dis, getCipher());
    }
}
