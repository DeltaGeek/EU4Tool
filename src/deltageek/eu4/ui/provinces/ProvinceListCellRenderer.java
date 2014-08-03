package deltageek.eu4.ui.provinces;

import deltageek.eu4.model.Province;

import javax.swing.*;
import java.awt.*;

public class ProvinceListCellRenderer extends DefaultListCellRenderer {
    private ProvinceUI ui;

    private static final Color CYAN = new Color(10, 166, 250);

    public ProvinceListCellRenderer(){
        this(null);
    }

    public ProvinceListCellRenderer(ProvinceUI provinceUI) {
        ui = provinceUI;
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

        switch(province.provinceType){
            case LAND:
                setForeground(Color.BLACK);
                break;
            case SEA:
                setForeground(Color.BLUE);
                break;
            case LAKE:
                setForeground(CYAN);
                break;
            case WASTELAND:
                setForeground(Color.GRAY);
                break;
            default:
                setForeground(Color.BLACK);
                break;
        }

        return this;
    }
}
