package deltageek.eu4.ui.events;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;

public class EventUIHandlers {
    private final EventPane ui;

    public EventUIHandlers(EventPane eventPane) {
        ui = eventPane;
    }

    public DocumentListener getFilterChangedHandler() {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                ui.refresh();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                ui.refresh();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        };
    }

    public ListSelectionListener getEventSelectionHandler() {
        return e -> {

        };
    }
}
