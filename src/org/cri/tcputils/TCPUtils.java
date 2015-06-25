/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cri.tcputils;

import java.io.IOException;
import java.nio.channels.Channel;

/**
 *
 * @author renaud
 */
public class TCPUtils {
    
    public static void silentlyClose(Channel channel){
        if (channel !=  null) {
            try {
                channel.close();
            } catch (IOException ex) {
                //Nothing to do
            }
        }
    }
    
}
