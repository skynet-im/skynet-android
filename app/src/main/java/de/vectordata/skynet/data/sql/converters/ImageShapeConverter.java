package de.vectordata.skynet.data.sql.converters;

import androidx.room.TypeConverter;

import de.vectordata.skynet.net.packet.model.ImageShape;

public class ImageShapeConverter {

    @TypeConverter
    public static ImageShape toImageShape(int format) {
        return ImageShape.values()[format];
    }

    @TypeConverter
    public static int toInteger(ImageShape imageShape) {
        return imageShape.ordinal();
    }

}
