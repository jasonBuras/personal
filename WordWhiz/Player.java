import java.io.Serializable;

public class Player implements Serializable {
    /*
    TODO: Consider adding additional stats.
    correct letter %
     */
    private String name;
    private int wins;
    private int gamesPlayed;
    public Player(){
        this.name="";
        this.wins=0;
        this.gamesPlayed=0;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public int getWins(){
        return this.wins;
    }

    public int getGamesPlayed(){
        return this.gamesPlayed;
    }

    public void win(){
        System.out.println("Congrats " + this.name + ", you won!");
        this.wins++;
        this.gamesPlayed++;
    }

    public void lose(){
        System.out.println("Better luck next time, " + this.name);
        this.gamesPlayed++;
    }

    public String winPercentage(){
        String result;
        if(this.gamesPlayed == 0){
            return null;
        }
        double winPercentage = (double) this.wins/this.gamesPlayed * 100;
        result = winPercentage + "%";
        return result;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setGamesPlayed(int gamesPlayed){
        this.gamesPlayed = gamesPlayed;
    }

    public String playerInfo(){
        return "[" + this.name + "]\n" +
                "[" + this.wins + "]" + "WINS\n" +
                "[" + this.gamesPlayed + "] GAMES PLAYED\n" +
                "[" + this.winPercentage() + "] WIN %\n";
    }
}
