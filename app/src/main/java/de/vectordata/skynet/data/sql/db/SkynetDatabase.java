package de.vectordata.skynet.data.sql.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import de.vectordata.skynet.data.model.Bio;
import de.vectordata.skynet.data.model.Channel;
import de.vectordata.skynet.data.model.ChannelKey;
import de.vectordata.skynet.data.model.ChannelMessage;
import de.vectordata.skynet.data.model.ChatMessage;
import de.vectordata.skynet.data.model.DaystreamMessage;
import de.vectordata.skynet.data.model.Dependency;
import de.vectordata.skynet.data.model.Device;
import de.vectordata.skynet.data.model.DirectChannelCustomization;
import de.vectordata.skynet.data.model.GroupChannelKeyNotify;
import de.vectordata.skynet.data.model.LoopbackKeyNotify;
import de.vectordata.skynet.data.model.MailAddress;
import de.vectordata.skynet.data.model.Nickname;
import de.vectordata.skynet.data.model.PasswordUpdate;
import de.vectordata.skynet.data.model.ProfileImage;
import de.vectordata.skynet.data.sql.dao.BioDao;
import de.vectordata.skynet.data.sql.dao.ChannelDao;
import de.vectordata.skynet.data.sql.dao.ChannelKeyDao;
import de.vectordata.skynet.data.sql.dao.ChannelMessageDao;
import de.vectordata.skynet.data.sql.dao.ChatMessageDao;
import de.vectordata.skynet.data.sql.dao.DaystreamMessageDao;
import de.vectordata.skynet.data.sql.dao.DependencyDao;
import de.vectordata.skynet.data.sql.dao.DeviceListDao;
import de.vectordata.skynet.data.sql.dao.DirectChannelCustomizationDao;
import de.vectordata.skynet.data.sql.dao.GroupChannelKeyNotifyDao;
import de.vectordata.skynet.data.sql.dao.LoopbackKeyNotifyDao;
import de.vectordata.skynet.data.sql.dao.MailAddressDao;
import de.vectordata.skynet.data.sql.dao.NicknameDao;
import de.vectordata.skynet.data.sql.dao.PasswordUpdateDao;
import de.vectordata.skynet.data.sql.dao.ProfileImageDao;

@Database(entities = {Channel.class, ChannelMessage.class, Dependency.class, ChatMessage.class, DaystreamMessage.class,
        ChannelKey.class, MailAddress.class, PasswordUpdate.class, LoopbackKeyNotify.class, DirectChannelCustomization.class,
        GroupChannelKeyNotify.class, Nickname.class, Bio.class, ProfileImage.class, Device.class
}, version = 1)
public abstract class SkynetDatabase extends RoomDatabase {

    public abstract ChannelDao channelDao();

    public abstract ChannelKeyDao channelKeyDao();

    public abstract ChannelMessageDao channelMessageDao();

    public abstract DependencyDao dependencyDao();

    public abstract ChatMessageDao chatMessageDao();

    public abstract DaystreamMessageDao daystreamMessageDao();

    public abstract MailAddressDao mailAddressDao();

    public abstract PasswordUpdateDao passwordUpdateDao();

    public abstract LoopbackKeyNotifyDao loopbackKeyNotifyDao();

    public abstract DirectChannelCustomizationDao directChannelCustomizationDao();

    public abstract GroupChannelKeyNotifyDao groupChannelKeyNotifyDao();

    public abstract NicknameDao nicknameDao();

    public abstract BioDao bioDao();

    public abstract ProfileImageDao profileImageDao();

    public abstract DeviceListDao deviceListDao();

}
