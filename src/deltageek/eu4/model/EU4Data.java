package deltageek.eu4.model;

import deltageek.eu4.util.Coordinate;
import deltageek.eu4.util.MapUtilities;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EU4Data {
    private final static String ISO_CHARSET = "ISO-8859-1";
    private final static String UTF_CHARSET = "UTF-8";

    private MapData mapData;
    private List<Province> provinces;

    public List<Province> getProvinces() {
        return provinces;
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

    public void loadFrom(Path baseDir) throws IOException {
        provinces = new ArrayList<>();

        mapData = loadMapData(baseDir);
        loadProvinces(baseDir);
        loadPositions(baseDir);
        loadAdjacencyData(baseDir);
        loadRiverCrossings(baseDir);
    }

    private MapData loadMapData(Path baseDir) throws IOException {
        Path mapdataPath = baseDir.resolve("map/default.map");

        Scanner scanner = new Scanner(mapdataPath, ISO_CHARSET);

        String line = scanner.nextLine();
        int width = Integer.parseInt(line.split(" = ")[1]);
        line = scanner.nextLine();
        int height = Integer.parseInt(line.split(" = ")[1]);

        scanner.nextLine();
        line = scanner.nextLine();
        int provinceCount = Integer.parseInt(line.split(" = ")[1]);

        MapData mapData = new MapData(width, height, provinceCount);

        // Load sea province list
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            line = scanner.nextLine().trim();

            if(line.startsWith("}"))
                break;

            for(String provinceId : line.split(" ")) {
                mapData.addSeaProvince(Integer.parseInt(provinceId));
            }
        }

        // Load lake province list
        scanner.nextLine();
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            line = scanner.nextLine().trim();

            if(line.startsWith("}"))
                break;

            for(String provinceId : line.split(" ")) {
                mapData.addLakeProvince(Integer.parseInt(provinceId));
            }
        }

        // Load forced coastal province list
        scanner.nextLine();
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            line = scanner.nextLine().trim();

            if(line.startsWith("}"))
                break;

            for(String provinceId : line.split(" ")) {
                mapData.addCoastalProvince(Integer.parseInt(provinceId));
            }
        }

        // Load map file list
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            line = scanner.nextLine().trim();

            if(line.isEmpty() || line.startsWith("canal_definition"))
                break;

            String[] tokens = line.split(" = ");
            mapData.addDataFile(tokens[0], tokens[1].replace("\"", ""));
        }

        // Load wasteland province info
        Path climatePath = mapData.getFilePath(MapFile.CLIMATE, baseDir);
        scanner = new Scanner(climatePath, ISO_CHARSET);

        while (scanner.hasNextLine()) {
            line = scanner.nextLine().trim();

            // Skip empty lines and comments
            if(line.isEmpty() || line.startsWith("#") )
                continue;

            String[] tokens = line.split(" = ");
            if(tokens[0].equals("impassable")){
                line = scanner.nextLine().trim();
                while(!line.startsWith("}")){
                    for(String provinceId : line.split(" ")) {
                        mapData.addWastelandProvince(Integer.parseInt(provinceId));
                    }
                    line = scanner.nextLine().trim();
                }
            }
            else{
                while(!line.startsWith("}"))
                    line = scanner.nextLine().trim();
            }
        }

        return mapData;
    }

    private void loadProvinces(Path baseDir) throws IOException {
        Map<Integer, String> provinceNames = loadProvinceNames(baseDir);

        Path provinceDefinitionsPath = mapData.getFilePath(MapFile.DEFINITIONS, baseDir);

        Scanner scanner = new Scanner(provinceDefinitionsPath, ISO_CHARSET);
        String line;

        scanner.nextLine(); // Ignore header line
        while(scanner.hasNextLine()){
            line = scanner.nextLine();
            String[] tokens = line.split(";");

            String name = tokens[4];

            if(name.equals("x"))
                continue;

            int id = Integer.parseInt(tokens[0]);
            int r = Integer.parseInt(tokens[1]);
            int g = Integer.parseInt(tokens[2]);
            int b = Integer.parseInt(tokens[3]);
            Color color = new Color(r, g, b);

            String provinceName = provinceNames.get(id);
            if(provinceName == null)
                provinceName = name;

            ProvinceType provinceType = mapData.provinceTypeFor(id);

            provinces.add(new Province(id, color.getRGB(), provinceName, provinceType));
        }
    }

    private Map<Integer, String> loadProvinceNames(Path baseDir) throws IOException {
        Path provinceNamesPath = baseDir.resolve("localisation/prov_names_l_english.yml");
        Map<Integer, String> provinceNamesById = new HashMap<>();

        Scanner scanner = new Scanner(provinceNamesPath, UTF_CHARSET);
        String line;

        scanner.nextLine(); // Ignore header line
        Pattern pattern = Pattern.compile("PROV(\\d+): \"([^\"]+)\"");

        while (scanner.hasNextLine()) {
            line = scanner.nextLine().trim();
            Matcher matcher = pattern.matcher(line);
            matcher.find();

            int provinceId = Integer.parseInt(matcher.group(1));
            String provinceName = matcher.group(2);
            provinceNamesById.put(provinceId, provinceName);
        }

        return provinceNamesById;
    }

    private void loadPositions(Path baseDir) throws IOException {
        Path provinceDefinitionsPath = mapData.getFilePath(MapFile.POSITIONS, baseDir);

        Scanner scanner = new Scanner(provinceDefinitionsPath, ISO_CHARSET);

        Pattern pattern = Pattern.compile("(\\d+)=");
        Map<Integer, Province> provincesById = getProvincesById();

        while(scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            // Lines starting with # are comments, ignore them
            if(line.startsWith("#"))
                continue;

            Matcher matcher = pattern.matcher(line);
            if(matcher.matches()){
                int provinceId = Integer.parseInt(matcher.group(1));
                scanner.nextLine(); // Open bracket
                scanner.nextLine(); // position=
                scanner.nextLine(); // Open bracket

                String[] tokens = scanner.nextLine().trim().split("\\s");
                double cityX = Double.parseDouble(tokens[0]);
                double cityY = mapData.height - Double.parseDouble(tokens[1]);
                double unitX = Double.parseDouble(tokens[2]);
                double unitY = mapData.height - Double.parseDouble(tokens[3]);
                double textX = Double.parseDouble(tokens[2]);
                double textY = mapData.height - Double.parseDouble(tokens[3]);
                double tradeX = Double.parseDouble(tokens[2]);
                double tradeY = mapData.height - Double.parseDouble(tokens[3]);
                double portX = Double.parseDouble(tokens[2]);
                double portY = mapData.height - Double.parseDouble(tokens[3]);
                double combatX = Double.parseDouble(tokens[2]);
                double combatY = mapData.height - Double.parseDouble(tokens[3]);

                Province province = provincesById.get(provinceId);
                province.addPosition(PositionType.CITY, new Point2D.Double(cityX, cityY));
                province.addPosition(PositionType.UNIT, new Point2D.Double(unitX, unitY));
                province.addPosition(PositionType.TEXT, new Point2D.Double(textX, textY));
                province.addPosition(PositionType.TRADE, new Point2D.Double(tradeX, tradeY));
                province.addPosition(PositionType.PORT, new Point2D.Double(portX, portY));
                province.addPosition(PositionType.COMBAT, new Point2D.Double(combatX, combatY));

                // Skip to next section
                while(!line.startsWith("#") && scanner.hasNextLine())
                    line = scanner.nextLine();
            }
        }
    }

    private void loadAdjacencyData(Path baseDir) throws IOException{
        Map<Integer, Province> provincesById = getProvincesById();

        Path adjacencyDataPath = mapData.getFilePath(MapFile.ADJACENCIES, baseDir);
        Scanner scanner = new Scanner(adjacencyDataPath, ISO_CHARSET);

        scanner.nextLine(); // Skip header
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            String[] tokens = line.split(";");

            if("-1".equals(tokens[0]))
                break;

            Province fromProvince = provincesById.get(Integer.parseInt(tokens[0]));
            Province toProvince = provincesById.get(Integer.parseInt(tokens[1]));

            fromProvince.adjacentProvinces.add(toProvince);
            toProvince.adjacentProvinces.add(fromProvince);
        }

        Path mapImagePath = mapData.getFilePath(MapFile.PROVINCES, baseDir);

        BufferedImage mapImage = ImageIO.read(new File(mapImagePath.toString()));
        int[] rgbValues = mapImage.getRGB(0,0,mapData.width, mapData.height, null, 0, mapData.width);

        Map<Integer, Province> provincesByColor = getProvincesByColor();

        for(int i=0; i<rgbValues.length; i++){
            int currentPixel = rgbValues[i];

            Province currentProvince = provincesByColor.get(currentPixel);

            int x = i % mapData.width;
            int y = i / mapData.width;

            if(x > 0){
                int west = rgbValues[i-1];
                if(west != currentProvince.rgbColor)
                    currentProvince.adjacentProvinces.add(provincesByColor.get(west));
            }
            if(x < mapData.width-1){
                int east = rgbValues[i+1];
                if(east != currentProvince.rgbColor)
                    currentProvince.adjacentProvinces.add(provincesByColor.get(east));
            }
            if(y > 0){
                int north = rgbValues[i-mapData.width];
                if(north != currentProvince.rgbColor)
                    currentProvince.adjacentProvinces.add(provincesByColor.get(north));
            }
            if(y < mapData.height-1){
                int south = rgbValues[i+mapData.width];
                if(south != currentProvince.rgbColor)
                    currentProvince.adjacentProvinces.add(provincesByColor.get(south));
            }
        }
    }

    private void loadRiverCrossings(Path baseDir) throws IOException{
        Path mapImagePath = mapData.getFilePath(MapFile.RIVERS, baseDir);

        BufferedImage mapImage = ImageIO.read(new File(mapImagePath.toString()));
        int[] rgbValues = mapImage.getRGB(0,0,mapData.width, mapData.height, null, 0, mapData.width);

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
                    int rbgIndex = coordinate.x + mapData.width * coordinate.y;
                    int coordinateRgb = rgbValues[rbgIndex];
                    if (coordinateRgb != landRgb &&
                            coordinateRgb != waterRgb)
                        adjacency.riverCrossings.add(province);
                }
            }
        }
    }
}
