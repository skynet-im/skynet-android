package de.vectordata.skynet.net.listener;

import de.vectordata.skynet.net.model.RestoreSessionError;

public interface AuthenticationListener {

    void onAuthFailed(RestoreSessionError error);

}
