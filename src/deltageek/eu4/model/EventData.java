package deltageek.eu4.model;

import deltageek.eu4.util.MapUtilities;

import java.io.BufferedReader;
import java.io.IOException;
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
                     //parseEventsIn(reader);
                 }
             }));
    }

    private void parseEventsIn(BufferedReader reader) throws IOException {
        String line = reader.readLine();

        while(line != null){
            EventScope scope = null;
            if(line.startsWith("country_event"))
                scope = EventScope.COUNTRY;
            else if(line.startsWith("province_event"))
                scope = EventScope.PROVINCE;

            int id = -1;
            String title = null;
            String description = null;
            EventTrigger condition = null;
            MeanTimeToHappen mtth = null;
            List<EventOption> options = new ArrayList<>();

            line = reader.readLine().trim();
            while(!line.startsWith("}")){
                String[] tokens = line.split(" = ");
                if(tokens[0].equals("id")){
                    id = Integer.parseInt(tokens[1]);
                }
                else if(tokens[0].equals("title")){
                    title = tokens[1].replace("\"", "");
                }
                else if(tokens[0].equals("desc")){
                    description = tokens[1].replace("\"", "");
                }
                else if(tokens[0].equals("picture")){
                    // Ignore this for now
                }
                else if(tokens[0].equals("trigger")){
                    condition = parseTrigger(reader);
                }
                else if(tokens[0].equals("mean_time_to_happen")){
                    mtth = parseMeanTimeToHappen(reader);
                }
                else if(tokens[0].equals("option")){
                    options.add(parseOption(reader));
                }
                else{
                    System.out.println("Unknown event data: " + tokens[0]);
                }
            }

            Event event = new Event(id,
                                    title,
                                    description,
                                    scope,
                                    condition,
                                    mtth,
                                    options);
            events.add(event);

            line = reader.readLine();
        }
    }

    private EventTrigger parseTrigger(BufferedReader reader) throws IOException{ return null; }

    private MeanTimeToHappen parseMeanTimeToHappen(BufferedReader reader) throws IOException {
        return null;
    }

    private EventOption parseOption(BufferedReader reader) throws IOException {
        return null;
    }
}
