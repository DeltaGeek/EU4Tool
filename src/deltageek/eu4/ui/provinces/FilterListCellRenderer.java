package deltageek.eu4.ui.provinces;

import deltageek.eu4.model.ProvinceType;

import javax.swing.*;
import java.awt.*;

public class FilterListCellRenderer extends DefaultListCellRenderer {

    private static final Color CYAN = new Color(10, 166, 250);

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        ProvinceType provinceType = (ProvinceType) value;

        setForeground(ProvinceTypeToColorConverter.getColorForProvinceType(provinceType));
        return this;
    }
}
