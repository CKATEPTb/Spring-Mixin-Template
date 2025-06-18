package org.sbooster.webmorph.configuration.rsocket.interfaces;

import io.rsocket.RSocket;

public interface RSocketHolder {
    RSocket getRSocket();

    void setRSocket(RSocket rSocket);
}
