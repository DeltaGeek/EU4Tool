package deltageek.eu4.app;

import deltageek.eu4.model.EU4Data;
import deltageek.eu4.ui.provinces.ProvinceUI;

import javax.swing.*;
import java.awt.*;

public class EU4Main {
    public static void main(final String[] args) {
        EU4Data data = new EU4Data();
        buildAndShowGuiFor(data);
    }

    private static void buildAndShowGuiFor(final EU4Data data) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setPreferredSize(new Dimension(600, 800));

            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Province Info", new ProvinceUI(data));

            f.add(tabs);
            f.pack();
            f.setVisible(true);
        });
    }
}
