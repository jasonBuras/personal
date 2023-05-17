import java.io.*;
import java.util.Scanner;

public class GameSaveManager {



    public static void savePlayer(Player player){
        String filename = "playerdata.ser";
        try (FileOutputStream fileOut = new FileOutputStream(filename);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)){
            objectOut.writeObject(player);
            System.out.println("[Player Data Saved Successfully]");
        }catch (IOException e){
            System.out.println("[Error saving game]:\n[" + e.getMessage() +"]");
        }
    }

    public static Player loadPlayer(){
        Player player = null;
        try (FileInputStream fileIn = new FileInputStream("playerdata.ser");
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)){
            player = (Player) objectIn.readObject();
            if(player.getGamesPlayed() > 0) {
                System.out.println("[Player Data loaded successfully]\n" +
                        "[PLAYER]:\n" +
                        "-------------------------\n" +
                        "Name:  |" + player.getName() + "|\n" +
                        "Wins:  |" + player.getWins() + "|\n" +
                        "Win %: |" + player.winPercentage() + "|\n" +
                        "-------------------------");
            }else{
                System.out.println("[NO PLAYER STATS DETECTED]");
            }
            return player;
        }catch (IOException | ClassNotFoundException e){
            System.out.println("[No player data detected]\n[" + e.getMessage()+"]");

        }
        return null;

    }

    public static boolean isValidFileName(String s){
        Character[] invalidChars = {'"', '*', '<', '>', '?', '|'};
        if(s == null || s.isEmpty() || s.length() > 255 || s.equals("nvm")){
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            for(int j=0;j<invalidChars.length;j++){
                if(c==invalidChars[j]){
                    return false;
                }
            }
        }
        return true;
    }
}
