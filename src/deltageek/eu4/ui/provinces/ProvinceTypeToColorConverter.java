package deltageek.eu4.ui.provinces;

import deltageek.eu4.model.ProvinceType;

import java.awt.*;

public class ProvinceTypeToColorConverter {
    private static final Color CYAN = new Color(10, 166, 250);

    public static Color getColorForProvinceType(ProvinceType provinceType){
        if(provinceType == null)
            return Color.BLACK;

        switch(provinceType){
            case LAND:
                return Color.BLACK;
            case SEA:
                return Color.BLUE;
            case LAKE:
                return CYAN;
            case WASTELAND:
                return Color.GRAY;
            default:
                return Color.BLACK;
        }
    }
}
