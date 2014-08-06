package deltageek.eu4.ui.provinces;

import deltageek.eu4.model.Province;

import javax.swing.*;
import java.awt.*;

public class ProvinceListCellRenderer extends DefaultListCellRenderer {
    private ProvincePane ui;

    public ProvinceListCellRenderer(){
        this(null);
    }

    public ProvinceListCellRenderer(ProvincePane provincePane) {
        ui = provincePane;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Province province = (Province) value;
        if(ui == null)
            setText(province.name);
        else{
            Province selectedProvince = ui.getSelectedProvince();
            if(selectedProvince.riverCrossings.contains(value))
                setText(province.name + " (River Crossing)");
            else
                setText(province.name);
        }

        setForeground(ProvinceTypeToColorConverter.getColorForProvinceType(province.provinceType));
        return this;
    }
}
