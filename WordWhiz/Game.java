import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.*;

public class Game implements Serializable {

    private String currentWord;
    private String prompt;
    private int remainingAttempts;
    private WordBank wordBank;
    private static String playerLastGuess;
    private static StringBuilder containsThese = new StringBuilder();
    private static ArrayList<String> notThese = new ArrayList<>();
    private static boolean tutorial;
    public Game(){
        wordBank = new WordBank();
        currentWord = wordBank.getWord();
        prompt = generatePrompt();
        remainingAttempts = 5;
        playerLastGuess = "";
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
            notThese.add(butNotThese.toString());
            notThese.removeIf(String::isEmpty);
            displayWrongLetters();
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

    public void displayWrongLetters(){
        System.out.println("This word does not contain: " + sortFormat(notThese));
    }

    private String sortFormat(List<String> notThese) {
        List<String> filteredList = new ArrayList<>();

        for(String letter : notThese){
            if(!letter.isEmpty()){
                filteredList.add(letter);
            }
        }
        StringBuilder result= new StringBuilder();
        List<Character> wrongLetters = new ArrayList<>();
        for(String entry : filteredList){
            char[] letters = entry.toCharArray();
            for(char letter : letters){
                if(!wrongLetters.contains(letter)){
                    wrongLetters.add(letter);
                }
            }
        }

        Collections.sort(wrongLetters);

        for(char ch: wrongLetters){
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
        containsThese = null;
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

    public StringBuilder getContainsThese() {
        return containsThese;
    }

    public boolean isValidAnswer(String guess){
        if(!wordBank.isValidWord(guess) || guess.length() != 5){
            System.out.println("[INVALID RESPONSE]");
            return false;
        }
        return true;
    }

    private class WordBank{
        private LinkedList<String> possibleAnswers;
        private List<String> allowedWords;
        private Random random;

        public WordBank(){
            possibleAnswers = new LinkedList<>();
            allowedWords = new ArrayList<>();
            loadWords();
        }

        public String getWord(){
            if(possibleAnswers.isEmpty()){
                loadWords();
            }
            random = new Random();
            int index = random.nextInt(possibleAnswers.size());
            return possibleAnswers.remove(index);
        }

        public boolean isValidWord(String word){
            return allowedWords.contains(word);
        }

        private void loadWords(){
            try{
                System.out.println("[Importing word database]");
                File importedWordList = new File("wordlist.txt");
                File allowedWordList = new File("allowed.txt");
                Scanner reader = new Scanner(importedWordList);
                while(reader.hasNextLine()){
                    String word = reader.nextLine().toLowerCase().trim();
                    possibleAnswers.add(word);
                    allowedWords.add(word);
                }
                reader.close();
                Scanner reader2 = new Scanner( allowedWordList);
                while (reader2.hasNextLine()){
                    String validWord = reader2.nextLine().toLowerCase().trim();
                    allowedWords.add(validWord);
                }
                reader2.close();
                System.out.println("[Import Successful]");
            }
            catch(FileNotFoundException e) {
                System.out.println("Error loading words from file:\n" + e.getMessage());
            }
        }

    }
}
