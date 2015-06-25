package org.cri.swarm.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Renaud Bastien <renaudbastien@outlook.com>, Arthur Besnard
 * <arthur.besnard@ovh.fr>
 */
public class Server {

    private final ServerSocketChannel ssc;
    //private
    private final Selector selector;
    private final HashSet<ClientContext> listClient = new HashSet<>();
    private final int port;
    private final Set<SelectionKey> selectedKeys;

    /**
     *
     * @param port
     * @throws IOException
     */
    public Server(int port) throws IOException {
        this.port = port;
        ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_READ);

        selectedKeys = selector.selectedKeys();
    }

    /**
     *
     * @throws IOException
     */
    public void launch() throws IOException {

        ssc.bind(new InetSocketAddress(port));

        while (!Thread.interrupted()) {
            long currentTime = System.currentTimeMillis();
            selector.select();
            System.out.println(selectedKeys);
            processSelectedKeys(currentTime);

            selectedKeys.clear();

        }
        close();
    }

    private void doWrite(SelectionKey key) throws IOException {
        ClientContext context = (ClientContext) key.attachment();
        if (context == null) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, "illegal state for client");
        } else {
            context.write(listClient);
        }

    }

    private void doRead(SelectionKey key) throws IOException {
        ClientContext context = (ClientContext) key.attachment();
        if (context == null) {
            context = new ClientContext(key);
            listClient.add(context);
            ///TODO remove client when inactive
        }
        context.read();

    }

    private void close() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void processSelectedKeys(long currentTime) {

        for (SelectionKey key : selectedKeys) {
            if (key.isValid()) {
                if (key.isAcceptable()) {
                    doAccept(key, currentTime);
                }
                try {
                    if (key.isWritable()) {
                        doWrite(key);
                    }
                    if (key.isReadable()) {
                        doRead(key);
                    }
                } catch (IOException ex) {
                    ClientContext context=(ClientContext) key.attachment();
                    context.close();
                    ///TODO LOg Client disconnect
                }

            }
        }
    }

    private void doAccept(SelectionKey key, long currentTime) throws IOException {
        SocketChannel sc= ssc.accept();
        if (sc==null) {
            return;
        }
        sc.configureBlocking(false);
        SelectionKey clientKey = sc.register(selector, 0);
        clientKey.attach(new ClientContext(key,sc,currentTime));
    }
}
