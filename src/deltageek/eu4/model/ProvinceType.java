package deltageek.eu4.model;

public enum ProvinceType {
    LAND,
    SEA,
    LAKE,
    WASTELAND;

    public static ProvinceType[] getFilterValues(){
        ProvinceType[] filterValues = new ProvinceType[values().length + 1];

        int index = 1;
        for(ProvinceType provinceType: values())
            filterValues[index++] = provinceType;

        return filterValues;
    }
}
