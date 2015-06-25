
package org.cri.swarm.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Renaud Bastien <renaudbastien@outlook.com>, Arthur Besnard <arthur.besnard@ovh.fr>
 */
public class SwarmServer {

    private static Server server;
    /*
     * @param args the command line arguments
     */

    public static void main(String args[]) {


        int port;
        if (args.length < 1) {
            port = 5042;
        } else {
            port = Integer.parseInt(args[0]);
        }

        try {
            server = new Server(port);
            server.launch();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
