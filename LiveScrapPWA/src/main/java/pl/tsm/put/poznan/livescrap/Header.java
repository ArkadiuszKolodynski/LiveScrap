package pl.tsm.put.poznan.livescrap;

public class Header {
    private int id;
    private String countryName;
    private String leagueName;

    public Header(int id, String countryName, String leagueName) {
        this.id = id;
        this.countryName = countryName;
        this.leagueName = leagueName;
    }

    public int getId() {
        return id;
    }
    
    public String getIdAsString() {
        return Integer.toString(id);
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }
    
}
