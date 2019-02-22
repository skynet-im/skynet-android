package de.vectordata.skynet.ui.util;

import de.vectordata.skynet.data.Storage;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.MailAddress;
import de.vectordata.skynet.data.model.Nickname;

public class NameUtil {

    public static String getFriendlyName(long counterpartId, Channel accountDataChannel) {
        if (accountDataChannel == null)
            return Long.toHexString(counterpartId);

        Nickname nicknameObj = Storage.getDatabase().nicknameDao().last(accountDataChannel.getChannelId());
        if (nicknameObj != null)
            return nicknameObj.getNickname();

        MailAddress mailAddressObj = Storage.getDatabase().mailAddressDao().last(accountDataChannel.getChannelId());
        if (mailAddressObj != null)
            return mailAddressObj.getMailAddress();

        return Long.toHexString(counterpartId);
    }

}
