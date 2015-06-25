/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cri.swarm.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 *
 * @author renaud
 */
public class Operations {

    private static final Charset UTF8_CS = Charset.forName("UTF8");

    static ReadOp getReadOp(byte code, SocketChannel sc, ClientContext context) {

        switch (code) {
            case (byte) 10:
                return new ReadOp() {
                    private final ByteBuffer readSizeBuffer = ByteBuffer.allocate(Integer.BYTES);
                    private ByteBuffer nameReadBuffer;
                    private int nameSize = -1;
                    private String clientName;
                    private boolean isFinished = false;

                    @Override
                    public void read() throws IOException {
                        sc.read(readSizeBuffer);
                        if (!readSizeBuffer.hasRemaining() && nameSize < 0) {
                            readSizeBuffer.flip();
                            nameSize = readSizeBuffer.getInt();
                            readSizeBuffer.clear();
                            nameReadBuffer = ByteBuffer.allocate(nameSize);
                        } else {
                            sc.read(nameReadBuffer);
                            if (!nameReadBuffer.hasRemaining()) {
                                nameReadBuffer.flip();
                                clientName = UTF8_CS.decode(nameReadBuffer).toString();
                                isFinished = true;
                            }
                        }
                    }

                    @Override
                    public boolean isFinished() {
                        return isFinished;
                    }
                };

        }
    }

    static WriteOp getWriteOp(byte get, SocketChannel sc, ClientContext aThis) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
