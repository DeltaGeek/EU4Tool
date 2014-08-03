package deltageek.eu4.model;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum MapFile {
    ADJACENCIES("adjacencies"),
    DEFINITIONS("definitions"),
    PROVINCES("provinces"),
    POSITIONS("positions"),
    RIVERS("rivers"),
    TERRAIN("terrain_definition"),
    CLIMATE("climate");

    private static final Map<String, MapFile> enumValues = new HashMap<>();

    static {
        for(MapFile mapFile : EnumSet.allOf(MapFile.class)){
            enumValues.put(mapFile.value, mapFile);
        }
    }

    public final String value;

    private MapFile(String value){
        this.value = value;
    }

    public static MapFile forString(String value){
        return enumValues.get(value);
    }
}
