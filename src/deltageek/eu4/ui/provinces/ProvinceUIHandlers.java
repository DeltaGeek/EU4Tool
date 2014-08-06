package deltageek.eu4.ui.provinces;

import deltageek.eu4.model.Province;

import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

public class ProvinceUIHandlers {
    private ProvincePane ui;
    private Province[] emptyProvinceList;

    public ProvinceUIHandlers(ProvincePane provincePane){
        this.ui = provincePane;

        emptyProvinceList = new Province[0];
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
