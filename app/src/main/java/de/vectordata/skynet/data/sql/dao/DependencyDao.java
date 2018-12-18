package de.vectordata.skynet.data.sql.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import de.vectordata.skynet.data.model.Dependency;

@Dao
public interface DependencyDao {

    @Insert
    void insert(Dependency... dependencies);

}
