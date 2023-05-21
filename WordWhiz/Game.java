import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class Game implements Serializable {

    private WordBank wordBank = new WordBank();
    private String currentAnswer;
    private int remainingAttempts;
    private GameBoard gameBoard;
    private static String playerLastGuess;
    private String prompt;
    private ArrayList<Character> doesntExist;//holds letters that do not exist in currentAnswer
    private ArrayList<Character> wrongSpot;//holds letters that exist in currentAnswer, but in the wrong spot
    private GameData gameData;


    public Game(){
        currentAnswer = wordBank.getWord();
        gameBoard = new GameBoard();
        remainingAttempts = 5;
        playerLastGuess = "";
        prompt = "_____";
        doesntExist = new ArrayList<>();
        gameData = new GameData();
        wrongSpot = new ArrayList<>();

    }

    public void check(String guess){
        boolean wrong = false;
        StringBuilder tempPrompt= new StringBuilder(currentAnswer.length());

        for(int x=0;x<currentAnswer.length();x++){
            tempPrompt.append("_");
        }

        for (int i = 0; i < guess.length(); i++) {
            char guessedLetter = guess.charAt(i);
            char answerLetter = currentAnswer.charAt(i);
            String guessedLetterString = String.valueOf(guessedLetter);

            if(guessedLetter == answerLetter && prompt.charAt(i) == '_'){
                gameBoard.updateBoard(guessedLetter,1);
                updatePrompt(guessedLetter, i);
                tempPrompt.setCharAt(i,Character.toUpperCase(guessedLetter));
            } else if (prompt.charAt(i) != '_') {
                tempPrompt.setCharAt(i,Character.toUpperCase(guessedLetter));
            } else if (currentAnswer.contains(guessedLetterString)) {
                if(!wrongSpot.contains(guessedLetter)){
                    tempPrompt.setCharAt(i, Character.toLowerCase(guessedLetter));
                    wrongSpot.add(guessedLetter);
                }
                gameBoard.updateBoard(guessedLetter, 2);
                wrong = true;
            } else {
                if(!doesntExist.contains(guessedLetter)){
                    doesntExist.add(guessedLetter);
                }
                gameBoard.updateBoard(guessedLetter,3);
                wrong= true;
            }
        }
        gameData.addGuess(guess,tempPrompt.toString());
        Collections.sort(wrongSpot);
        Collections.sort(doesntExist);
        if(wrong){
            remainingAttempts--;
            System.out.println("You guessed: [" + guess + "] which was incorrect.");
        }
    }

    public void help(){
        if(gameData.guessEntries.isEmpty()){
            System.out.println("You have no previous guesses");
            return;
        }
        gameData.displayData();
    }

    public void updatePrompt(char guessedLetter, int index){
        StringBuilder updatedPrompt = new StringBuilder(prompt);

        for (int i = 0; i < currentAnswer.length(); i++) {
            if(currentAnswer.charAt(i) == guessedLetter && i == index && updatedPrompt.charAt(i) == '_'){
                    updatedPrompt.setCharAt(i, guessedLetter);
            }
        }

        prompt = updatedPrompt.toString();
    }

    public boolean outOfAttempts(){
        return remainingAttempts==0;
    }

    public void setCurrentAnswer(String userInput){
        if(userInput.startsWith("set_")){
            String answer = userInput.substring(4);
            this.currentAnswer = answer;
            System.out.println("Answer changed to " + answer);
        }
    }

    public String getCurrentAnswer(){
        return currentAnswer;
    }


    public int getRemainingAttempts(){
        return remainingAttempts;
    }


    public String getPlayerLastGuess() {
        return playerLastGuess;
    }

    public void setPlayerLastGuess(String guess) {
        playerLastGuess = guess;
    }


    public boolean isValidAnswer(String guess){
        if(!wordBank.isValidWord(guess) || guess.length() != 5){
            System.out.println("[INVALID RESPONSE]");
            return false;
        }
        return true;
    }

    public void displayGameBoard(){
        gameBoard.display();
        System.out.println("[WordWhiz]");
        System.out.println("Guesses Remaining [" + remainingAttempts + "]");
        if(playerLastGuess.length() == 5) {
            System.out.println("Previous Word Guessed: " + playerLastGuess);
            if(!wrongSpot.isEmpty()) {
                System.out.println("Contains: " + wrongSpot + " but in a different location ");
            }
            if(!doesntExist.isEmpty()){
                System.out.println("Removed From Board: " + doesntExist);
            }
        }
        System.out.println("Guess a word (" + currentAnswer.length() + " letters) or type \"help\" for help:");
        System.out.println(getPrompt());
    }

    public String getPrompt(){
        return prompt;
    }//end Game

    private class GameBoard{
        private String[] alphabet;

        public GameBoard(){
            alphabet = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                    "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        }
        public void display(){
            System.out.println("-------------------------");
            for (int i = 0; i < 13; i++) {
                System.out.print(alphabet[i] + " ");
            }System.out.println();
            for (int i = 13; i < alphabet.length; i++) {
                System.out.print(alphabet[i] + " ");
            }
            System.out.println();
            System.out.println("-------------------------");

        }

        private void updateBoard(char guessedLetter, int condtion){
            if(condtion==1){//correct letter, correct spot
                colorLetter(guessedLetter,"green");
            } else if (condtion==2) {//correct letter, wrong spot
                colorLetter(guessedLetter, "yellow");
            } else if (condtion==3) {//wrong letter, doesn't exist in word
                removeLetter(guessedLetter);
            } else{
                System.out.println("wait what..?");
            }
        }

        public void colorLetter(char letter, String color){
            letter = Character.toUpperCase(letter);
            String ansiColor = "";
            switch (color){
                case "green":
                    ansiColor = "\u001B[32m"; // Green ANSI escape code
                    break;
                case "yellow":
                    ansiColor = "\u001B[33m"; // Yellow ANSI escape code
                    break;
                default:
                    break;
            }
            String coloredLetter = ansiColor + letter + "\u001B[0m";
            for (int i = 0; i < alphabet.length; i++) {
                if(alphabet[i].contains(String.valueOf(letter))){
                    alphabet[i] = coloredLetter;
                    break;
                }
            }
        }

        public void removeLetter(char letter){
            letter = Character.toUpperCase(letter);
            for (int i = 0; i < alphabet.length; i++) {
                if(alphabet[i].contains(String.valueOf(letter))){
                    alphabet[i] = " ";
                    break;
                }
            }
        }
    }//end GameBoard

    private static class GameData{
        private List<GuessEntry> guessEntries;

        public GameData(){
            guessEntries = new ArrayList<>();
        }

        public void addGuess(String guess, String prompt){
            if(!prompt.contains("_")){//meaning no letters were in the correct spot
                prompt = "None of these letters exist in the word.";
            }
            guessEntries.add(new GuessEntry(guess, prompt));
        }

        public void displayData(){
            int counter = 1;
            System.out.println("----------");
            System.out.println("[UPPER CASE] Letters are in the correct position.\n"+
                               "[LOWER CASE] letters are in the word, but a different location");
            for(GuessEntry entry : guessEntries){
                System.out.println("[Guess " + counter + "]");
                System.out.println(entry.guess);
                System.out.println(entry.prompt);
                System.out.println("----------");
                counter++;
            }
        }

        private class GuessEntry{
            private String guess;
            private String prompt;

            public GuessEntry(String guess, String prompt){
                this.guess=guess;
                this.prompt=prompt;
            }
        }
    }

    private class WordBank{
        private LinkedList<String> possibleAnswers;
        private List<String> allowedWords;
        private Random random;

        public WordBank(){
            possibleAnswers = new LinkedList<>();
            allowedWords = new ArrayList<>();
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
