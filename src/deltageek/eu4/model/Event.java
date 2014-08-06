package deltageek.eu4.model;

import java.util.ArrayList;
import java.util.List;

public class Event implements Comparable<Event> {
    public final int id;
    public final String title;
    public final String description;
    public final EventCondition condition;
    public final MeanTimeToHappen meanTimeToHappen;
    public final List<EventOption> options = new ArrayList<>();

    public Event(int id, String title, String description, EventCondition condition, MeanTimeToHappen meanTimeToHappen){
        this.id = id;
        this.title = title;
        this.description = description;
        this.condition = condition;
        this.meanTimeToHappen = meanTimeToHappen;
    }

    @Override
    public int compareTo(Event e) { return title.compareTo(e.title); }
}
