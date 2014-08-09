package deltageek.eu4.ui.countries;

import deltageek.eu4.model.Country;
import deltageek.eu4.model.CountryData;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.stream.Collectors;

public class CountryPane extends JPanel {
    private CountryData countryData;

    private DefaultListModel<Country> countryListModel = new DefaultListModel<>();

    private final JTextField txtCountryFilter;
    private final JList lstCountries;

    public CountryPane(){
        this(null);
    }

    public CountryPane(CountryData data) {
        super(new BorderLayout());

        countryData = data;

        CountryUIHandlers handlers = new CountryUIHandlers(this);

        txtCountryFilter = new JTextField();
        txtCountryFilter.getDocument().addDocumentListener(handlers.getFilterChangedHandler());

        lstCountries = new JList<>(countryListModel);
        lstCountries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstCountries.addListSelectionListener(handlers.getEventSelectionHandler());

        JPanel countryPanel = new JPanel(new BorderLayout());
        countryPanel.add(txtCountryFilter, BorderLayout.NORTH);
        countryPanel.add(new JScrollPane(lstCountries), BorderLayout.CENTER);

        add(countryPanel, BorderLayout.WEST);
    }

    public void refresh() {
        String searchText = txtCountryFilter.getText().toUpperCase();

        if(countryData != null){
            java.util.List<Country> filteredEvents =
                    countryData.countries
                            .stream()
                            .filter(c -> c.countryName.toUpperCase().contains(searchText) || c.countryCode.contains(searchText))
                            .collect(Collectors.toList());

            Collections.sort(filteredEvents);
            setCountries(filteredEvents);
        }
    }

    public void setCountries(java.util.List<Country> events){
        lstCountries.clearSelection();
        countryListModel.removeAllElements();
        events.forEach(countryListModel::addElement);
    }

    public void setData(CountryData data) {
        this.countryData = data;
        refresh();
    }}
