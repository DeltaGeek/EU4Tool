package deltageek.eu4.model;

import deltageek.eu4.util.Coordinate;
import deltageek.eu4.util.MapUtilities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapData {
    private int width;
    private int height;
    private int provinceCount;

    private Set<Integer> seaProvinceIds = new HashSet<>();
    private Set<Integer> lakeProvinceIds = new HashSet<>();
    private Set<Integer> coastalProvinceIds = new HashSet<>();
    private Set<Integer> wastelandProvinceIds = new HashSet<>();

    private Map<MapFile, Path> dataFiles = new HashMap<>();

    public final List<Province> provinces = new ArrayList<>();

    public MapData(Path baseDir) throws IOException{
        loadMapData(baseDir);
        loadProvinces(baseDir);
        loadPositions(baseDir);
        loadAdjacencyData(baseDir);
        loadRiverCrossings(baseDir);
    }

    private void addDataFile(String fileId, String fileName) {
        MapFile mapFile = MapFile.forString(fileId);
        Path path = Paths.get("map", fileName);
        dataFiles.put(mapFile, path);
    }

    private Path getFilePath(MapFile fileId, Path baseDir) {
        return baseDir.resolve(dataFiles.get(fileId));
    }

    private ProvinceType provinceTypeFor(int provinceId) {
        if(seaProvinceIds.contains(provinceId))
            return ProvinceType.SEA;
        if(lakeProvinceIds.contains(provinceId))
            return ProvinceType.LAKE;
        if(wastelandProvinceIds.contains(provinceId))
            return ProvinceType.WASTELAND;
        return ProvinceType.LAND;
    }

    public Map<Integer, Province> getProvincesById(){
        HashMap<Integer,Province> provincesById = new HashMap<>();
        for(Province p : provinces)
            provincesById.put(p.id, p);

        return provincesById;
    }

    public Map<Integer, Province> getProvincesByColor(){
        HashMap<Integer,Province> provincesById = new HashMap<>();
        for(Province p : provinces)
            provincesById.put(p.rgbColor, p);

        return provincesById;
    }

    private void loadMapData(Path baseDir) throws IOException {
        Path mapDataPath = baseDir.resolve("map/default.map");

        try(BufferedReader reader = Files.newBufferedReader(mapDataPath, MapUtilities.ISO_CHARSET)){
            String line = reader.readLine();
            int width = Integer.parseInt(line.split(" = ")[1]);
            line = reader.readLine();
            int height = Integer.parseInt(line.split(" = ")[1]);

            reader.readLine();
            line = reader.readLine();
            int provinceCount = Integer.parseInt(line.split(" = ")[1]);

            this.width = width;
            this.height = height;
            this.provinceCount = provinceCount;

            // Load sea province list
            reader.readLine();  // Skip start of sea region
            line = reader.readLine();
            while (line != null && !line.startsWith("}")) {
                for(String provinceId : line.trim().split(" ")) {
                    seaProvinceIds.add(Integer.parseInt(provinceId));
                }
                line = reader.readLine();
            }

            reader.readLine();

            // Load lake province list
            reader.readLine();  // Skip start of lake region
            line = reader.readLine();
            while (line != null && !line.startsWith("}")) {
                for(String provinceId : line.trim().split(" ")) {
                    lakeProvinceIds.add(Integer.parseInt(provinceId));
                }
                line = reader.readLine();
            }

            reader.readLine();

            // Load forced coastal province list
            reader.readLine();  // Skip start of coastal province region
            line = reader.readLine();
            while (line != null && !line.startsWith("}")) {
                for(String provinceId : line.trim().split(" ")) {
                    coastalProvinceIds.add(Integer.parseInt(provinceId));
                }
                line = reader.readLine();
            }

            reader.readLine();

            // Load map file list
            line = reader.readLine();
            while (line != null && !line.startsWith("canal_definition")){
                String[] tokens = line.split(" = ");
                addDataFile(tokens[0], tokens[1].replace("\"", ""));
                line = reader.readLine();
            }
        }

        // Load wasteland province info
        Path climatePath = getFilePath(MapFile.CLIMATE, baseDir);
        try(BufferedReader reader = Files.newBufferedReader(climatePath, MapUtilities.ISO_CHARSET)) {
            String line = reader.readLine();

            while (line != null) {
                // Skip empty lines and comments
                if(line.isEmpty() || !line.startsWith("impassable") ) {
                    line = reader.readLine();
                    continue;
                }

                line = reader.readLine().trim();
                while(!line.startsWith("}")){
                    for(String provinceId : line.split(" ")) {
                        wastelandProvinceIds.add(Integer.parseInt(provinceId));
                    }
                    line = reader.readLine().trim();
                }
            }
        }
    }

    private void loadProvinces(Path baseDir) throws IOException {
        Map<Integer, String> provinceNames = loadProvinceNames(baseDir);

        Path provinceDefinitionsPath = getFilePath(MapFile.DEFINITIONS, baseDir);

        try(BufferedReader reader = Files.newBufferedReader(provinceDefinitionsPath, MapUtilities.ISO_CHARSET)) {
            reader.readLine(); // Skip header

            String line = reader.readLine();

            while(line != null){
                String[] tokens = line.split(";");

                String name = tokens[4];

                if(name.equals("x"))
                    break;

                int id = Integer.parseInt(tokens[0]);
                int r = Integer.parseInt(tokens[1]);
                int g = Integer.parseInt(tokens[2]);
                int b = Integer.parseInt(tokens[3]);
                Color color = new Color(r, g, b);

                String provinceName = provinceNames.get(id);
                if(provinceName == null)
                    provinceName = name;

                ProvinceType provinceType = provinceTypeFor(id);

                provinces.add(new Province(id, color.getRGB(), provinceName, provinceType));
                line = reader.readLine();
            }
        }
    }

    private Map<Integer, String> loadProvinceNames(Path baseDir) throws IOException {
        Path provinceNamesPath = baseDir.resolve("localisation/prov_names_l_english.yml");
        Map<Integer, String> provinceNamesById = new HashMap<>();
        Pattern pattern = Pattern.compile("PROV(\\d+): \"([^\"]+)\"");

        try(BufferedReader reader = Files.newBufferedReader(provinceNamesPath, MapUtilities.UTF_CHARSET)) {
            reader.readLine();

            String line = reader.readLine();

            while (line != null) {
                Matcher matcher = pattern.matcher(line.trim());
                matcher.find();

                int provinceId = Integer.parseInt(matcher.group(1));
                String provinceName = matcher.group(2);
                provinceNamesById.put(provinceId, provinceName);
                line = reader.readLine();
            }
        }

        return provinceNamesById;
    }

    private void loadPositions(Path baseDir) throws IOException {
        Path provinceDefinitionsPath = getFilePath(MapFile.POSITIONS, baseDir);

        Pattern pattern = Pattern.compile("(\\d+)=");
        Map<Integer, Province> provincesById = getProvincesById();
        try(BufferedReader reader = Files.newBufferedReader(provinceDefinitionsPath, MapUtilities.ISO_CHARSET)) {
            String line = "";

            while(line != null) {
                // Lines starting with # are comments, ignore them
                if(line.isEmpty() || line.startsWith("#")) {
                    line = reader.readLine();
                    continue;
                }

                Matcher matcher = pattern.matcher(line.trim());
                if(matcher.matches()){
                    int provinceId = Integer.parseInt(matcher.group(1));
                    reader.readLine(); // Open bracket
                    reader.readLine(); // position=
                    reader.readLine(); // Open bracket

                    String[] tokens = reader.readLine().trim().split(" ");
                    double cityX = Double.parseDouble(tokens[0]);
                    double cityY = height - Double.parseDouble(tokens[1]);
                    double unitX = Double.parseDouble(tokens[2]);
                    double unitY = height - Double.parseDouble(tokens[3]);
                    double textX = Double.parseDouble(tokens[2]);
                    double textY = height - Double.parseDouble(tokens[3]);
                    double tradeX = Double.parseDouble(tokens[2]);
                    double tradeY = height - Double.parseDouble(tokens[3]);
                    double portX = Double.parseDouble(tokens[2]);
                    double portY = height - Double.parseDouble(tokens[3]);
                    double combatX = Double.parseDouble(tokens[2]);
                    double combatY = height - Double.parseDouble(tokens[3]);

                    Province province = provincesById.get(provinceId);
                    province.addPosition(PositionType.CITY, new Point2D.Double(cityX, cityY));
                    province.addPosition(PositionType.UNIT, new Point2D.Double(unitX, unitY));
                    province.addPosition(PositionType.TEXT, new Point2D.Double(textX, textY));
                    province.addPosition(PositionType.TRADE, new Point2D.Double(tradeX, tradeY));
                    province.addPosition(PositionType.PORT, new Point2D.Double(portX, portY));
                    province.addPosition(PositionType.COMBAT, new Point2D.Double(combatX, combatY));

                    // Skip to next section
                    while(line != null && !line.startsWith("#"))
                        line = reader.readLine();
                }
            }
        }
    }

    private void loadAdjacencyData(Path baseDir) throws IOException{
        Map<Integer, Province> provincesById = getProvincesById();

        Path adjacencyDataPath = getFilePath(MapFile.ADJACENCIES, baseDir);
        try(BufferedReader reader = Files.newBufferedReader(adjacencyDataPath, MapUtilities.ISO_CHARSET)) {
            reader.readLine(); // Skip header

            String line = reader.readLine();

            while(line != null){
                String[] tokens = line.trim().split(";");

                if("-1".equals(tokens[0]))
                    break;

                Province fromProvince = provincesById.get(Integer.parseInt(tokens[0]));
                Province toProvince = provincesById.get(Integer.parseInt(tokens[1]));

                fromProvince.adjacentProvinces.add(toProvince);
                toProvince.adjacentProvinces.add(fromProvince);

                line = reader.readLine();
            }
        }

        Path mapImagePath = getFilePath(MapFile.PROVINCES, baseDir);

        try(InputStream stream = new FileInputStream(mapImagePath.toString())){
            BufferedImage mapImage = ImageIO.read(stream);
            int[] rgbValues = mapImage.getRGB(0, 0, width, height, null, 0, width);

            Map<Integer, Province> provincesByColor = getProvincesByColor();

            for(int i=0; i<rgbValues.length; i++){
                int currentPixel = rgbValues[i];

                Province currentProvince = provincesByColor.get(currentPixel);

                int x = i % width;
                int y = i / width;

                if(x > 0){
                    int west = rgbValues[i-1];
                    if(west != currentProvince.rgbColor)
                        currentProvince.adjacentProvinces.add(provincesByColor.get(west));
                }
                if(x < width-1){
                    int east = rgbValues[i+1];
                    if(east != currentProvince.rgbColor)
                        currentProvince.adjacentProvinces.add(provincesByColor.get(east));
                }
                if(y > 0){
                    int north = rgbValues[i-width];
                    if(north != currentProvince.rgbColor)
                        currentProvince.adjacentProvinces.add(provincesByColor.get(north));
                }
                if(y < height-1){
                    int south = rgbValues[i+width];
                    if(south != currentProvince.rgbColor)
                        currentProvince.adjacentProvinces.add(provincesByColor.get(south));
                }
            }
        }
    }

    private void loadRiverCrossings(Path baseDir) throws IOException{
        Path mapImagePath = getFilePath(MapFile.RIVERS, baseDir);

        try(InputStream stream = new FileInputStream(mapImagePath.toString())) {
            BufferedImage mapImage = ImageIO.read(stream);
            int[] rgbValues = mapImage.getRGB(0, 0, width, height, null, 0, width);

            int landRgb = 0xFFFFFFFF;
            int waterRgb = 0xFF7A7A7A; // Seas and lakes

            for(Province province : provinces){
                if(province.provinceType != ProvinceType.LAND)
                    continue;

                Point2D sourcePoint = province.PositionFor(PositionType.UNIT);

                for(Province adjacency : province.adjacentProvinces){
                    if(adjacency.provinceType != ProvinceType.LAND)
                        continue;

                    Point2D destinationPoint = adjacency.PositionFor(PositionType.UNIT);

                    List<Coordinate> pointsBetween = MapUtilities.getPointsBetween(sourcePoint, destinationPoint);
                    for(Coordinate coordinate : pointsBetween) {
                        int rbgIndex = coordinate.x + width * coordinate.y;
                        int coordinateRgb = rgbValues[rbgIndex];
                        if (coordinateRgb != landRgb &&
                                coordinateRgb != waterRgb)
                            adjacency.riverCrossings.add(province);
                    }
                }
            }
        }
    }
}
