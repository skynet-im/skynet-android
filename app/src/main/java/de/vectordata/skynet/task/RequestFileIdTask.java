package de.vectordata.skynet.task;

import de.vectordata.skynet.net.SkynetContext;
import de.vectordata.skynet.net.packet.P30FileUpload;
import de.vectordata.skynet.net.packet.P31FileUploadResponse;
import de.vectordata.skynet.task.model.TaskWithResult;

public class RequestFileIdTask extends TaskWithResult<Long> {

    @Override
    public void onExecute() {
        SkynetContext.getCurrent().getNetworkManager().sendPacket(new P30FileUpload())
                .waitForPacket(P31FileUploadResponse.class, packet -> {
                    setResult(packet.fileId);
                    success();
                });
    }

}