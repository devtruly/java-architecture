package com.simpledesign.ndms.common.cdc;

public interface CdcMessageListener {
    void receiveMessage(MessageObject messageObject);
}
