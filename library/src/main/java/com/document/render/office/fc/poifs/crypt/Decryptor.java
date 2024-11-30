
package com.document.render.office.fc.poifs.crypt;

import com.document.render.office.fc.EncryptedDocumentException;
import com.document.render.office.fc.poifs.filesystem.DirectoryNode;
import com.document.render.office.fc.poifs.filesystem.NPOIFSFileSystem;
import com.document.render.office.fc.poifs.filesystem.POIFSFileSystem;
import com.document.render.office.fc.util.LittleEndian;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public abstract class Decryptor {
    public static final String DEFAULT_PASSWORD = "VelvetSweatshop";

    public static Decryptor getInstance(EncryptionInfo info) {
        int major = info.getVersionMajor();
        int minor = info.getVersionMinor();

        if (major == 4 && minor == 4)
            return new AgileDecryptor(info);
        else if (minor == 2 && (major == 3 || major == 4))
            return new EcmaDecryptor(info);
        else
            throw new EncryptedDocumentException("Cannot process encrypted office files!");
    }

    protected static int getBlockSize(int algorithm) {
        switch (algorithm) {
            case EncryptionHeader.ALGORITHM_AES_128:
                return 16;
            case EncryptionHeader.ALGORITHM_AES_192:
                return 24;
            case EncryptionHeader.ALGORITHM_AES_256:
                return 32;
        }
        throw new EncryptedDocumentException("Cannot process encrypted office files!");
    }

    public abstract InputStream getDataStream(DirectoryNode dir)
            throws IOException, GeneralSecurityException;

    public abstract boolean verifyPassword(String password)
            throws GeneralSecurityException;

    public InputStream getDataStream(NPOIFSFileSystem fs) throws IOException, GeneralSecurityException {
        return getDataStream(fs.getRoot());
    }

    public InputStream getDataStream(POIFSFileSystem fs) throws IOException, GeneralSecurityException {
        return getDataStream(fs.getRoot());
    }

    protected byte[] hashPassword(EncryptionInfo info,
                                  String password) throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] bytes;
        try {
            bytes = password.getBytes("UTF-16LE");
        } catch (UnsupportedEncodingException e) {
            throw new EncryptedDocumentException("Cannot process encrypted office files!");
        }

        sha1.update(info.getVerifier().getSalt());
        byte[] hash = sha1.digest(bytes);
        byte[] iterator = new byte[4];

        for (int i = 0; i < info.getVerifier().getSpinCount(); i++) {
            sha1.reset();
            LittleEndian.putInt(iterator, i);
            sha1.update(iterator);
            hash = sha1.digest(hash);
        }

        return hash;
    }
}
