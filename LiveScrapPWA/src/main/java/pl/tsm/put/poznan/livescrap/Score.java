package pl.tsm.put.poznan.livescrap;

public class Score {
    private int id;
    private String min;
    private String team1;
    private String team2;
    private String score;

    public Score(int id, String min, String team1, String team2, String score) {
        this.id = id;
        this.min = min;
        this.team1 = team1;
        this.team2 = team2;
        this.score = score;
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

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
    
    
}
