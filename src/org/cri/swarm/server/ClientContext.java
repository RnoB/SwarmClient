package org.cri.swarm.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Renaud Bastien <renaudbastien@outlook.com>, Arthur Besnard
 * <arthur.besnard@ovh.fr>
 */
public class ClientContext {
    private static int lastId = 0;
    private final int clientId;
    private final SelectionKey key;
    private InetSocketAddress clientAddress;
    private final ByteBuffer codeReadBuffer = ByteBuffer.allocate(Byte.BYTES);
    private int nameSize;
    private final SocketChannel sc;
    private String clientName;
    private double X;
    private double Y;
    private double Z;
    private long lastActionDone;
    private ReadOp currentReadOp;
    private WriteOp currentWriteOp;

    ClientContext(SelectionKey key, SocketChannel sc, long currentTime) {
        this.key = key;
        this.clientId = lastId;
        lastId++;
        this.sc = sc;
        lastActionDone = currentTime;
        key.interestOps(SelectionKey.OP_READ);
    }

    void read() throws IOException {

        if (currentReadOp == null || currentReadOp.isFinished()) {
            sc.read(codeReadBuffer);
            if (!codeReadBuffer.hasRemaining()) {
                codeReadBuffer.flip();
                currentReadOp = Operations.getReadOp(codeReadBuffer.get(), sc, this);
                codeReadBuffer.clear();
            }
        } else {
            currentReadOp.read();
        }

    }

    void write(HashSet<ClientContext> listClient) throws IOException {
        if (currentWriteOp.isFinished()) {
            currentWriteOp = null;
        } else {
            currentWriteOp.write();
        }
        
        
        /*
        ByteBuffer buffW = ByteBuffer.allocate(3 * listClient.size() * Double.BYTES);
        listClient.forEach(context -> {
            buffW.putDouble(context.X);
            buffW.putDouble(context.Y);
            buffW.putDouble(context.Z);
            sc.write(buffW);
        }*/
    }

    void close() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    void setInterestedOp(int op){
        this.key.interestOps(op);
    }
    
    void setWriteOp(WriteOp op){
        this.currentWriteOp = op;
    }
}
