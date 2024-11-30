

package com.document.render.office.fc.poifs.nio;

import com.document.render.office.fc.util.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;



public class FileBackedDataSource extends DataSource {
    private FileChannel channel;

    public FileBackedDataSource(File file) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.toString());
        }
        this.channel = (new RandomAccessFile(file, "r")).getChannel();
    }

    public FileBackedDataSource(FileChannel channel) {
        this.channel = channel;
    }

    public ByteBuffer read(int length, long position) throws IOException {
        if (position >= size()) {
            throw new IllegalArgumentException("Position " + position + " past the end of the file");
        }


        channel.position(position);
        ByteBuffer dst = ByteBuffer.allocate(length);
        int worked = IOUtils.readFully(channel, dst);


        if (worked == -1) {
            throw new IllegalArgumentException("Position " + position + " past the end of the file");
        }


        dst.position(0);


        return dst;
    }

    public void write(ByteBuffer src, long position) throws IOException {
        channel.write(src, position);
    }

    public void copyTo(OutputStream stream) throws IOException {

        WritableByteChannel out = Channels.newChannel(stream);

        channel.transferTo(0, channel.size(), out);
    }

    public long size() throws IOException {
        return channel.size();
    }

    public void close() throws IOException {
        channel.close();
    }
}
