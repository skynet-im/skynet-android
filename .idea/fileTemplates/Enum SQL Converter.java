#set($varName = $NAME.substring(0, 1).toLowerCase() + $NAME.substring(1)) 

#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end
#parse("File Header.java")

import androidx.room.TypeConverter;

public class ${NAME}Converter {

    @TypeConverter
    public static ${NAME} to${NAME}(int format) {
        return ${NAME}.values()[format];
    }

    @TypeConverter
    public static int toInteger(${NAME} ${varName}) {
        return ${varName}.ordinal();
    }

}
