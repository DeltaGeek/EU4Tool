package deltageek.eu4.model;

public class Country implements Comparable<Country>{
    public final String name;

    public Country(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Country c) {
        return name.compareTo(c.name);
    }
}
