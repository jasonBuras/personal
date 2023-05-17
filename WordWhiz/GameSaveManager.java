import java.io.*;
import java.util.Scanner;

public class GameSaveManager {



    public static void savePlayer(Player player){
        Scanner gameSaver = new Scanner(System.in);
        String filename;
        do {
            System.out.println("Please enter a name to save your file ('nvm' to cancel):");
            filename=gameSaver.nextLine();
            if (filename.equals("nvm")) {
                return;
            }

        }while (!isValidFileName(filename));
        try (FileOutputStream fileOut = new FileOutputStream(filename + ".ser");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)){
            objectOut.writeObject(player);
            System.out.println("[Game saved successfully]");
        }catch (IOException e){
            System.out.println("Error saving game: " + e.getMessage());
        }
    }

    public static Player loadPlayer(String filename){
        Player player = null;
        try (FileInputStream fileIn = new FileInputStream(filename + ".ser");
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)){
            player = (Player) objectIn.readObject();
            System.out.println("[Game loaded successfully]");
        }catch (IOException | ClassNotFoundException e){
            System.out.println("Error loading game state: " + e.getMessage());
        }
        return player;
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
