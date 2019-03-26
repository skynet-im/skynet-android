package de.vectordata.skynet.data.file;

import android.content.Context;
import android.util.Log;

import org.nustaq.serialization.FSTConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Stores an object of type <T> on the internal memory while also
 * caching values to ensure less file system access
 *
 * @param <T> The object type
 */
public class ObjectCache<T> {

    public static final String TAG = "ObjectCache";

    private static FSTConfiguration configuration = FSTConfiguration.createAndroidDefaultConfiguration();

    private T cache;
    private File file;

    public ObjectCache(Context context, String name) {
        file = new File(context.getFilesDir(), name);
    }

    public void set(T object) {
        cache = object;
        save();
    }

    public T get() {
        if (cache == null) load();
        return cache;
    }

    public void clear() {
        cache = null;
        file.delete();
    }

    private void load() {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            inputStream.read(data);
            inputStream.close();
            cache = (T) configuration.asObject(data);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read ObjectCache", e);
        }
    }

    private void save() {
        byte[] data = configuration.asByteArray(cache);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(data);
            outputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to save ObjectCache", e);
        }
    }

}
