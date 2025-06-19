package dev.ckateptb.webmorph.configuration.rsocket.api;

import io.rsocket.RSocket;

public interface RSocketHolder {
    RSocket getRSocket();

    void setRSocket(RSocket rSocket);
}
