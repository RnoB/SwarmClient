/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cri.swarm.server;

import java.io.IOException;

/**
 *
 * @author renaud
 */
interface WriteOp {
    void write() throws IOException;
}
