package deltageek.eu4.model;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Province implements Comparable<Province> {
    public final int id;
    public final String name;
    public final int rgbColor;
    public final ProvinceType provinceType;
    private final Map<PositionType, Point2D> positionData;
    public final Set<Province> adjacentProvinces;
    public final Set<Province> riverCrossings;

    public Province(int id, int color, String name, ProvinceType type){
        this.id = id;
        this.name = name;
        this.rgbColor = color;
        provinceType = type;
        positionData = new HashMap<>();
        adjacentProvinces = new HashSet<>();
        riverCrossings = new HashSet<>();
    }

    public void addPosition(PositionType positionType, Point2D coords){
        positionData.put(positionType, coords);
    }

    public Point2D PositionFor(PositionType positionType){
        return positionData.get(positionType);
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public int compareTo(Province p) {
        return name.compareTo(p.name);
    }
}
