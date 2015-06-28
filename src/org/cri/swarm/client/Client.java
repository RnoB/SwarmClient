package org.cri.swarm.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;
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
    ArrayList<Byte> codeSend = new ArrayList<>();
    ArrayList<Double> xPlayer = new ArrayList<>();
    ArrayList<Double> yPlayer = new ArrayList<>();
    ArrayList<Double> zPlayer = new ArrayList<>();

    ArrayList<Integer> listId = new ArrayList<>();

    private int nPlayers = 0;
    private int clientId = 0;
    private double X;
    private double Y;
    private double Z;

    private final ByteBuffer codeReadBuffer = ByteBuffer.allocate(Byte.BYTES);
    private final ByteBuffer gameInfoBuffer = ByteBuffer.allocate(Integer.BYTES * 2);
    private final ByteBuffer playerIdBuffer = ByteBuffer.allocate(Integer.BYTES);
    private ByteBuffer listIdBuffer;
    private ByteBuffer listPositionBuffer;
    Random r = new Random();

    private Client(String host, int port) throws IOException {
        sc = SocketChannel.open(new InetSocketAddress(host, port));
    }

    public void launch(String name) throws IOException {
        X = r.nextDouble();
        Y = r.nextDouble();
        Z = r.nextDouble();
        register(name);
        readListIdPlayer();
        while (!Thread.interrupted()) {
            codeWrite();
            send();
            receive();
        }
    }

    private void register(String name) throws IOException {
        ByteBuffer nameBuff = UTF8_CS.encode(name);
        ByteBuffer buff = ByteBuffer.allocate(Byte.BYTES + Integer.BYTES + nameBuff.remaining());
        buff.put((byte) 10);
        buff.putInt(nameBuff.remaining());
        buff.put(nameBuff);
        buff.flip();
        sc.write(buff);
    }

    private void readListIdPlayer() throws IOException {
        sc.read(codeReadBuffer);
        if (!codeReadBuffer.hasRemaining()) {
            codeReadBuffer.flip();
            if (codeReadBuffer.get() == (byte) 11) {
                sc.read(gameInfoBuffer);
                if (!gameInfoBuffer.hasRemaining()) {
                    nPlayers = gameInfoBuffer.getInt();
                    clientId = gameInfoBuffer.getInt();

                }
            }
            listIdBuffer = ByteBuffer.allocate(nPlayers * Integer.BYTES);
            sc.read(listIdBuffer);
            if (!listIdBuffer.hasRemaining()) {
                listIdBuffer.flip();
                for (int i = 0; i < nPlayers; i++) {
                    listId.add(listIdBuffer.getInt());
                }

            }
            gameInfoBuffer.clear();
            listIdBuffer.clear();
            codeReadBuffer.clear();
        }

    }

    private void send() throws IOException {

        for (byte code : codeSend) {
            switch (code) {
                case (byte) 20:
                    ByteBuffer buff = ByteBuffer.allocate(Byte.BYTES + Double.BYTES * 3);
                    buff.put((byte) 20);
                    buff.putDouble(X);
                    buff.putDouble(Y);
                    buff.putDouble(Z);
                    buff.flip();
                    sc.write(buff);

            }
            codeSend.remove(0);
        }
    }

    private void receive() throws IOException {
        sc.read(codeReadBuffer);
        if (!codeReadBuffer.hasRemaining()) {
            codeReadBuffer.flip();
            switch (codeReadBuffer.get()) {
                case (byte) 21:
                    listPositionBuffer = ByteBuffer.allocate(nPlayers * Double.BYTES * 3);
                    sc.read(listPositionBuffer);
                    if (!listPositionBuffer.hasRemaining()) {
                        listPositionBuffer.flip();

                        for (int playerId : listId) {
                            xPlayer.add(listPositionBuffer.getDouble());
                            yPlayer.add(listPositionBuffer.getDouble());
                            zPlayer.add(listPositionBuffer.getDouble());
                        }
                    }
                    listPositionBuffer.clear();
                case (byte) 31:
                    System.exit(0);
                    
                case (byte) 33:
                    sc.read(playerIdBuffer);
                    if (!playerIdBuffer.hasRemaining()) {
                        playerIdBuffer.flip();
                        listId.add(playerIdBuffer.getInt());
                        nPlayers++;
                    }
                    playerIdBuffer.clear();
                    
                case (byte) 35:
                    sc.read(playerIdBuffer);
                    if (!playerIdBuffer.hasRemaining()) {
                        playerIdBuffer.flip();
                        listId.remove(playerIdBuffer.getInt());
                        xPlayer.remove(playerIdBuffer.getInt());
                        yPlayer.remove(playerIdBuffer.getInt());
                        zPlayer.remove(playerIdBuffer.getInt());
                        
                        nPlayers--;
                    }
                    playerIdBuffer.clear();
            }
            listPositionBuffer.clear();
            codeReadBuffer.clear();
        }
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

    private void codeWrite() {
        codeSend.add((byte) 20);

    }

}
