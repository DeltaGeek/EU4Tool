package deltageek.eu4.model;

import deltageek.eu4.util.MapUtilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static deltageek.eu4.util.DeltaUtilities.unchecked;

public class EventData {

    public List<Event> events = new ArrayList<>();

    public EventData(Path baseDir) throws IOException {
        loadEvents(baseDir);
    }

    private void loadEvents(Path baseDir) throws IOException{
        Path eventDirPath = baseDir.resolve("events");

        Files.list(eventDirPath)
             .forEach(unchecked(f -> {
                 try(BufferedReader reader = Files.newBufferedReader(f, MapUtilities.ISO_CHARSET)){
                     parseEventsIn(reader);
                 }
             }));
    }

    private void parseEventsIn(Reader reader) {

    }
}
