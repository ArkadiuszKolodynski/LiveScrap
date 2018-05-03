package pl.tsm.put.poznan.livescrap;

public class Header {
    private int id;
    private String countryCode;
    private String countryName;
    private String leagueName;

    public Header(int id, String countryCode, String countryName, String leagueName) {
        this.id = id;
        if (countryCode != null) {
            this.countryCode = countryCode.toLowerCase();
        } else {
            this.countryCode = "nan";
        }
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
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
