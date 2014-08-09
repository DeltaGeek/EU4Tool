package deltageek.eu4.model;

import deltageek.eu4.util.MapUtilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountryData {
    public final List<Country> countries = new ArrayList<>();

    public CountryData(Path baseDir) throws IOException {
        loadCountries(baseDir);
    }

    public Map<String, Country> getCountriesById(){
        HashMap<String,Country> provincesById = new HashMap<>();
        for(Country c : countries)
            provincesById.put(c.countryCode, c);

        return provincesById;
    }

    private void loadCountries(Path baseDir) throws IOException {
        Map<String, String> countryNamesByCode = loadCountryNames(baseDir);

        Path countryTagFilePath = baseDir.resolve("common").resolve("country_tags").resolve("00_countries.txt");

        try(BufferedReader reader = Files.newBufferedReader(countryTagFilePath, MapUtilities.ISO_CHARSET)){
            String line = reader.readLine();

            while(line != null){
                // Comments start with #
                if(line.startsWith("#") || line.trim().isEmpty()) {
                    line = reader.readLine();
                    continue;
                }

                String[] tokens = line.trim().split("=");

                String countryCode = tokens[0].trim();
                String countryFile = tokens[1].trim().replace("\"", "");
                String countryName = countryNamesByCode.get(countryCode);

                if(countryName == null){
                    // Country name is not localized. Read it from the country file instead
                    Path countryFilePath = baseDir.resolve("common").resolve(countryFile);
                    try (BufferedReader countryFileReader = Files.newBufferedReader(countryFilePath, MapUtilities.ISO_CHARSET)) {
                        line = countryFileReader.readLine();
                        if(line.contains("Please") || !line.contains(":") || line.contains("this File"))
                        {
                            String rawName = new File(countryFile).getName().split("\\.")[0];
                            StringBuilder sb = new StringBuilder(rawName);

                            for(int i=sb.length()-1; i>0; i--){
                                if(Character.isUpperCase(sb.charAt(i)) && sb.charAt(i-1) != ' ')
                                    sb.insert(i, ' ');
                            }

                            countryName = sb.toString();
                        }
                        else
                        {
                            String[] tokens2 = line.split(":");
                            countryName = tokens2[1].trim();
                        }
                    }
                }

                countries.add(new Country(countryCode, countryName, countryFile));

                line = reader.readLine();
            }
        }
    }

    private Map<String, String> loadCountryNames(Path baseDir) throws IOException {
        Path countryTagFilePath = baseDir.resolve("localisation").resolve("countries_l_english.yml");
        Map<String, String> countryNamesByCode = new HashMap<>();

        try(BufferedReader reader = Files.newBufferedReader(countryTagFilePath, MapUtilities.UTF_CHARSET)){
            String line = reader.readLine();

            line = reader.readLine(); // Skip first line

            while(line != null){
                String[] tokens = line.trim().split(": ");

                String countryCode = tokens[0];
                String countryName = tokens[1].replace("\"", "");

                // Skip country adjective definitions
                if(countryCode.endsWith("_ADJ"))
                {
                    line = reader.readLine();
                    continue;
                }

                countryNamesByCode.put(countryCode, countryName);

                line = reader.readLine();
            }
        }

        return countryNamesByCode;
    }
}
