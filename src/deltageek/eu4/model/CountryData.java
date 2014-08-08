package deltageek.eu4.model;

import deltageek.eu4.util.MapUtilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CountryData {
    public final List<Country> countries = new ArrayList<>();

    public CountryData(Path baseDir) throws IOException {
        Path countryTagFilePath = baseDir.resolve("common").resolve("country_tags").resolve("00_countries.txt");

        try(BufferedReader reader = Files.newBufferedReader(countryTagFilePath, MapUtilities.ISO_CHARSET)){
            String line = reader.readLine();

            while(line != null){
                // Comments start with #
                if(line.isEmpty() || line.startsWith("#")) {
                    line = reader.readLine();
                    continue;
                }

                line.split("");

                line = reader.readLine();
            }
        }
    }

}
