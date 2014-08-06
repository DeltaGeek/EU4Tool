package deltageek.eu4.app;

import deltageek.eu4.model.CountryData;
import deltageek.eu4.model.EventData;
import deltageek.eu4.model.MapData;
import deltageek.eu4.ui.countries.CountryPane;
import deltageek.eu4.ui.events.EventPane;
import deltageek.eu4.ui.provinces.ProvincePane;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EU4Main {
    private final CountryPane countryPane;
    private final ProvincePane provincePane;
    private final EventPane eventPane;

    public static void main(final String[] args) {
        EU4Main mainApp = new EU4Main();
        mainApp.buildAndShowGuiFor();
    }

    public EU4Main(){
        countryPane = new CountryPane();
        provincePane = new ProvincePane();
        eventPane = new EventPane();
    }

    private void buildAndShowGuiFor() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setPreferredSize(new Dimension(600, 800));

            final JTextField txtLoadDir = new JTextField("D:\\Steam\\steamapps\\common\\Europa Universalis IV");
            JButton btnLoad;

            btnLoad = new JButton("Load Data");
            btnLoad.addActionListener(e -> {
                try {
                    Path loadPath = Paths.get(txtLoadDir.getText());

                    MapData mapData = new MapData(loadPath);
                    CountryData countryData = new CountryData(loadPath);
                    EventData eventData = new EventData(loadPath);

                    countryPane.setData(countryData);
                    provincePane.setData(mapData);
                    eventPane.setData(eventData);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                provincePane.refresh();
                provincePane.setProvinceFilterEnabled(true);
            });

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Countries", countryPane);
            tabs.addTab("Provinces", provincePane);
            tabs.addTab("Events", eventPane);

            f.setLayout(new BorderLayout());

            JPanel textPanel = new JPanel();
            textPanel.add(new JLabel("EU 4 Base Directory"));
            textPanel.add(txtLoadDir);
            textPanel.add(btnLoad);

            f.add(textPanel, BorderLayout.NORTH);
            f.add(tabs, BorderLayout.CENTER);

            f.pack();
            f.setVisible(true);
        });
    }
}
