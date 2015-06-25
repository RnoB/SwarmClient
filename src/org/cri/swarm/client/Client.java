package org.cri.swarm.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Renaud Bastien <renaudbastien@outlook.com>, Arthur Besnard
 * <arthur.besnard@ovh.fr>
 */
public class Client {
    private final SocketChannel sc;
    private static final Charset UTF8_CS = Charset.forName("UTF8");
    private Client(String host, int port) throws IOException {
        sc=SocketChannel.open(new InetSocketAddress(host, port));
    }
    
    public void launch(String name) throws IOException{
        register(name);
        readListIdPlayer();
        while(!Thread.interrupted()){
            sendPosition();
            receivePositions();
        }
    }
    
    private void register(String name) throws IOException{
        ByteBuffer nameBuff = UTF8_CS.encode(name);
        ByteBuffer buff = ByteBuffer.allocate(Byte.BYTES + Integer.BYTES + nameBuff.remaining());
        buff.put((byte)10);
        buff.putInt(nameBuff.remaining());
        buff.put(nameBuff);
        buff.flip();
        sc.write(buff);
    }
    
    
    public static void main(String args[]) {
        String host;
        int port;
        if (args.length < 2) {
            host = "localhost";
            port = 5042;
        } else {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }
        try {
            (new Client(host, port)).launch("Renaud");
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, "Server Connection Time Out", ex);
        }
    }
}
