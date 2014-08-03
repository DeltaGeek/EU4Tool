package deltageek.eu4.ui.provinces;

import deltageek.eu4.model.EU4Data;
import deltageek.eu4.model.Province;
import deltageek.eu4.model.ProvinceType;

import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProvinceEventHandlers {
    private ProvinceUI ui;
    private EU4Data data;
    private Province[] emptyProvinceList;

    public ProvinceEventHandlers(ProvinceUI mainUI, EU4Data data){
        this.ui = mainUI;
        this.data = data;

        emptyProvinceList = new Province[0];
    }

    void refresh(ProvinceType filter) {
        ui.clearProvinceSelection();

        List<Province> filteredProvinces =
                data.getProvinces()
                        .stream()
                        .filter(p -> filter == null || p.provinceType == filter)
                        .collect(Collectors.toList());

        Collections.sort(filteredProvinces);
        ui.setProvinces(filteredProvinces);
    }

    public ActionListener getLoadButtonHandler() {
        return e -> {
            try {
                Path loadPath = ui.getLoadPath();
                data.loadFrom(loadPath);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            refresh(null);
            ui.setProvinceFilterEnabled(true);
        };
    }

    public ActionListener getFilterHandler(){
        return e-> refresh(ui.getSelectedProvinceFilter());
    }

    public ListSelectionListener getProvinceSelectionHandler(){
        return e -> {
            if(e.getValueIsAdjusting())
                return;

            Province selectedProvince = ui.getSelectedProvince();

            if(selectedProvince == null)
            {
                ui.setAdjacentProvinces(emptyProvinceList);
                return;
            }

            Province[] adjacentProvinces = selectedProvince.adjacentProvinces.toArray(new Province[0]);
            Arrays.sort(adjacentProvinces, (prov1, prov2) -> prov1.name.compareTo(prov2.name));
            ui.setAdjacentProvinces(adjacentProvinces);
        };
    }

    public MouseListener getAdjacencySelectionHandler(){
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    ui.setSelectedProvince(ui.getSelectedAdjacency());
                }
            }
        };
    }
}
