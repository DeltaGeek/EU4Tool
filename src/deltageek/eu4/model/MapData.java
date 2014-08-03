package deltageek.eu4.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapData {
    public final int width;
    public final int height;
    public final int provinceCount;

    private Set<Integer> seaProvinces = new HashSet<>();
    private Set<Integer> lakeProvinces = new HashSet<>();
    private Set<Integer> coastalProvinces = new HashSet<>();
    private Set<Integer> wastelandProvinces = new HashSet();

    private Map<MapFile, Path> dataFiles = new HashMap<>();

    public MapData(int width, int height, int provinceCount) {
        this.width = width;
        this.height= height;
        this.provinceCount = provinceCount;
    }

    public void addSeaProvince(int provinceId) {
        seaProvinces.add(provinceId);
    }

    public void addLakeProvince(int provinceId) {
        lakeProvinces.add(provinceId);
    }

    public void addCoastalProvince(int provinceId) {
        coastalProvinces.add(provinceId);
    }

    public void addWastelandProvince(int provinceId){
        wastelandProvinces.add(provinceId);
    }

    public void addDataFile(String fileId, String fileName) {
        MapFile mapFile = MapFile.forString(fileId);
        Path path = Paths.get("map", fileName);
        dataFiles.put(mapFile, path);
    }

    public Path getFilePath(MapFile fileId, Path baseDir) {
        return baseDir.resolve(dataFiles.get(fileId));
    }

    public ProvinceType provinceTypeFor(int provinceId) {
        if(seaProvinces.contains(provinceId))
            return ProvinceType.SEA;
        if(lakeProvinces.contains(provinceId))
            return ProvinceType.LAKE;
        if(wastelandProvinces.contains(provinceId))
            return ProvinceType.WASTELAND;
        return ProvinceType.LAND;
    }
}
