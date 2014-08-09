package deltageek.eu4.model;

public class Country implements Comparable<Country>{

    public final String countryCode;
    public final String filename;
    public final String countryName;

    public Country(String code, String name, String filename) {
        countryCode = code;
        countryName = name;
        this.filename = filename;
    }

    @Override
    public String toString(){
        return String.format("%s (%s)", countryName, countryCode);
    }

    @Override
    public int compareTo(Country c) {
        return countryCode.compareTo(c.countryCode);
    }
}
