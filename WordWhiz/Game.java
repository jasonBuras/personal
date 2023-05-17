import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class Game implements Serializable {

    private String currentWord;
    private String prompt;
    private int remainingAttempts;
    private WordBank wordBank;
    private String playerLastGuess;
    private ArrayList<String> notThese;
    private static boolean tutorial;
    public Game(){
        wordBank = new WordBank();
        currentWord = wordBank.getWord();
        prompt = generatePrompt();
        remainingAttempts = 5;
        playerLastGuess = "";
        notThese = new ArrayList<>();
    }

    /**
     * This generates the blank prompt the player is presented with at the beginning of the game.
     * It starts of as <br>"_____" and will update via {@link #updatePrompt(String guess)} method
     * @return
     */
    private String generatePrompt(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < currentWord.length(); i++) {
            sb.append("_");
        }
        return sb.toString();
    }

    public void updatePrompt(String guess){
        boolean triggered=false;
        boolean displayWrongLetters = false;
        StringBuilder containsThese = new StringBuilder();
        StringBuilder butNotThese = new StringBuilder();
        StringBuilder sb = new StringBuilder(prompt);
        for (int i = 0; i < currentWord.length(); i++) {
            if(currentWord.charAt(i) == guess.charAt(i)){
                sb.setCharAt(i,guess.charAt(i));
            }else if(currentWord.contains(String.valueOf(guess.charAt(i)))){
                sb.setCharAt(i,'*');
                containsThese.append("[").append(guess.charAt(i)).append("]");
                triggered=true;
            }
            else{
                char temp = guess.charAt(i);
                if(butNotThese.indexOf(String.valueOf(temp)) == -1){
                    butNotThese.append(temp);
                }
                displayWrongLetters=true;
                /*
                TODO: organize butNotThese into alphabetical order, add { } to each character and add it to an array
                 */

            }
        }//end for loop
        if(displayWrongLetters){
            notThese.add(sortFormat(butNotThese.toString()));
            System.out.println("This word does not contain: " + sortFormat(notThese.toString()));
        }

        if(triggered){
            if(tutorial){
                System.out.println("* - indicates the letter you typed in that location exists in the word, but in a different location.");
                tutorial=false;
            }
            System.out.println("This word contains: " + containsThese + " but in a different location");
        }
        prompt = sb.toString();
    }

    private String sortFormat(String butNotThese) {
        StringBuilder result= new StringBuilder();
        char[] wrong = butNotThese.toCharArray();
        Arrays.sort(wrong);
        for(char ch : wrong){
            result.append("{").append(ch).append("}");
        }
        return result.toString();
    }

    /*
    TODO: formatList
     */

    public boolean outOfAttempts(){
        return remainingAttempts==0;
    }

    public void correctGuess() {
        System.out.println("You guessed correctly! The word was " + currentWord);
        reset();
    }

    public void lose() {
        System.out.println("You are out of guesses. The word was: " + currentWord);
        reset();

    }

    public void wrongGuess(String guess){
        remainingAttempts--;
        if(!outOfAttempts()){
            updatePrompt(guess);
            System.out.println(prompt);
            System.out.println(guess);
        }
    }

    public String getCurrentWord(){
        return currentWord;
    }

    public String getPrompt(){
        return prompt;
    }

    public int getRemainingAttempts(){
        return remainingAttempts;
    }

    private void reset(){
        currentWord = wordBank.getWord();
        prompt = generatePrompt();
        remainingAttempts = 5;
        playerLastGuess = "";
        notThese = new ArrayList<>();
        tutorial=true;
    }

    public String getPlayerLastGuess() {
        return playerLastGuess;
    }

    public void setPlayerLastGuess(String playerLastGuess) {
        this.playerLastGuess = playerLastGuess;
    }

    public ArrayList<String> getNotThese() {
        return notThese;
    }
    private class WordBank{
        private LinkedList<String> words;
        private Random random;

        public WordBank(){
            words = new LinkedList<>();
        }

        public String getWord(){
            if(words.isEmpty()){
                loadWords();
            }
            random = new Random();
            int index = random.nextInt(words.size());
            return words.remove(index);
        }

        private void loadWords(){
            try{
                System.out.println("[Importing word database]");
                File importedWordList = new File("wordlist.txt");
                /*
                TODO: UPDATE wordlist.txt to exclude words with repeated letters
                 */
                Scanner reader = new Scanner(importedWordList);
                while(reader.hasNextLine()){
                    String word = reader.nextLine().toLowerCase().trim();
                    words.add(word);
                }
                reader.close();
                System.out.println("[Import Successful]");
            }
            catch(FileNotFoundException e) {
                System.out.println("Error loading words from file:\n" + e.getMessage());
            }
        }

    }
}
