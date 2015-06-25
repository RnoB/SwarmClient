package org.cri.swarm.server;

import java.io.IOException;

interface ReadOp {
    void read() throws IOException;
    boolean isFinished();
}
