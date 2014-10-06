package deltageek.eu4.ui.provinces;

import deltageek.eu4.model.MapData;
import deltageek.eu4.model.Province;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class ProvinceUIHandlers {
    private MapData mapData;
    private ProvincePane ui;
    private Province[] emptyProvinceList;

    public ProvinceUIHandlers(ProvincePane provincePane, MapData mapData){
        this.ui = provincePane;
        this.mapData = mapData;

        emptyProvinceList = new Province[0];
    }

    public void setMapData(MapData mapData){
        this.mapData = mapData;
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

    public ActionListener getExportHandler() {
        return e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showSaveDialog(ui);

            if (result != JFileChooser.APPROVE_OPTION)
                return;

            ui.setExportButtonEnabled(false);

            File selectedFile = fileChooser.getSelectedFile();
            Path exportPath = Paths.get(selectedFile.getPath());
            ExportToModWorker worker = new ExportToModWorker(mapData, ui, exportPath);
            worker.execute();
        };
    }
}
