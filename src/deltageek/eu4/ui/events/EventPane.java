package deltageek.eu4.ui.events;

import deltageek.eu4.model.Event;
import deltageek.eu4.model.EventData;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.stream.Collectors;

public class EventPane extends JPanel {
    private EventData eventData;

    private DefaultListModel<Event> eventListModel = new DefaultListModel<>();

    private final JTextField txtEventFilter;
    private final JList<Event> lstEvents;

    public EventPane(){
        this(null);
    }

    public EventPane(EventData data){
        super(new BorderLayout());

        eventData = data;

        EventUIHandlers handlers = new EventUIHandlers(this);

        txtEventFilter = new JTextField();
        txtEventFilter.getDocument().addDocumentListener(handlers.getFilterChangedHandler());

        lstEvents = new JList<>(eventListModel);
        lstEvents.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstEvents.addListSelectionListener(handlers.getEventSelectionHandler());

//        lstAdjacent = new JList<>();
//        lstAdjacent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        lstAdjacent.addMouseListener(handlers.getAdjacencySelectionHandler());

        JPanel eventPanel = new JPanel(new BorderLayout());
        eventPanel.add(txtEventFilter, BorderLayout.NORTH);
        eventPanel.add(new JScrollPane(lstEvents), BorderLayout.CENTER);

//        JScrollPane adjacencyScroll = new JScrollPane(lstAdjacent);
//        JPanel adjacencyPanel = new JPanel(new BorderLayout());
//        JLabel adjacencyHeader = new JLabel("Adjacent Provinces");
//        adjacencyHeader.setHorizontalAlignment(SwingConstants.CENTER);
//        adjacencyPanel.add(adjacencyHeader, BorderLayout.NORTH);
//        adjacencyPanel.add(adjacencyScroll, BorderLayout.CENTER);

        add(eventPanel, BorderLayout.WEST);
//        add(adjacencyPanel, BorderLayout.CENTER);
    }

    public void refresh() {
        String searchText = txtEventFilter.getText();

        if(eventData != null){
            java.util.List<Event> filteredEvents =
                    eventData.events
                           .stream()
                           .filter(e -> e.title.contains(searchText))
                           .collect(Collectors.toList());

            Collections.sort(filteredEvents);
            setEvents(filteredEvents);
        }
    }

    public void setEvents(java.util.List<Event> events){
        lstEvents.clearSelection();
        eventListModel.removeAllElements();
        events.forEach(eventListModel::addElement);
    }

    public void setData(EventData data) {
        this.eventData = data;
        refresh();
    }
}
