package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import de.vectordata.skynet.data.model.Device;

@Dao
public interface DeviceListDao {

    @Insert
    void insert(Device... devices);

    @Query("DELETE FROM deviceList")
    void clear();

}
