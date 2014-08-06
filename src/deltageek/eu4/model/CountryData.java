package deltageek.eu4.model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CountryData {
    public final List<Country> countries = new ArrayList<>();

    public CountryData(Path baseDir) throws IOException {
    }

}
