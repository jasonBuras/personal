import java.util.Scanner;

public class UI {

    private Scanner input;
    private Player player;
    private Game game;


    public UI(){
        input = new Scanner(System.in);
        player = new Player();
        game = new Game();
    }

    public void run(){
        //load player data
        System.out.println("[INITIALIZING]");
        if(player.getGamesPlayed() < 1) {
            resetPlayer(true);
            loadPlayer();//if player has played before, load playerdata; else player remains null, so a new player is created below
        }
        delay(2);
        do{
            if(player.getName()==null){
                newPlayer();
            }else{break;}
        }while(player.getName()==null);
        System.out.println("Welcome, " + player.getName() + "!");
        delay(1);
        while(true){
            mainMenu();
            play();
        }
    }

    private void mainMenu(){
        System.out.println("[MAIN MENU]");
        int selection;
        do{
            System.out.println("1 - Start\n2 - View Player Info\n3 - Clear Player Data");
            selection = input.nextInt();
            input.nextLine();
            System.out.println();
            if(selection==1){
                break;
            } else if (selection==2) {
                System.out.println(player.playerInfo());
                pressEnter();
            } else if (selection==3) {
                do{
                    System.out.println("Are you sure?\n" +
                            "1 - yes (clears data)\n" +
                            "2 - no (return)\n" +
                            "Note: If you'd like to keep your data, simply go to your directory and copy [playerdata.ser] to a different location or rename it before clearing your data.");
                    selection = input.nextInt();
                    input.nextLine();
                    if(selection==1){
                        resetPlayer(false);
                        break;
                    } else if (selection==2) {
                        break;
                    }
                }while (true);
            }
        }while (true);


    }

    private void play() {
        int impatient=0;
        String guess="";
        String answer = game.getCurrentWord();
        System.out.println("If you get stuck at any point, type \"help\" to get a better understanding of the game.");
        do{
            System.out.println("----------");
            gameInfo();
            if(game.outOfAttempts()){
                System.out.println("[Game Over]");
                game.lose();
                player.lose();
                gameOver();
                delay(2);
                break;
            }

            do {

                System.out.println(game.getPrompt());
                System.out.println("Guess a word (5 letters) or type \"help\" for help:");
                guess = input.nextLine().toLowerCase();
                if(!game.isValidAnswer(guess)){
                    System.out.println("Try Again");
                    delay(1);
                }
            }while (!game.isValidAnswer(guess));

            if(guess.equals("cheats")){
                System.out.println(game.getCurrentWord());
                guess = "loser";
                game.setPlayerLastGuess(guess);
            }

            System.out.println("\nYou guessed " + guess);

            if(guess.equals(answer)){
                game.correctGuess();
                player.win();
                gameOver();
                delay(2);
                break;
            }
            else if(guess.equals("help")){
                System.out.println("\n....................");
                game.displayWrongLetters();
                System.out.println("[PROMPT]:\n" + game.getPrompt() + "\n----------");
                System.out.println("[CONTAINS THESE LETTERS]:\n" + game.getContainsThese() + "\n----------");
                System.out.println("The letters at the top are letters you entered but do not appear in the answer\n" +
                        "\tIf a letter appears in the prompt, then you entered it in the correct location\n" +
                        "\tThe letters under \"CONTAINS THESE LETTERS\" are letters that appear in the word, but you may have in the wrong place.\n" +
                        "\t*- indicates that letter is in the word, but you entered it in the wrong spot");
                System.out.println("\n....................");
                pressEnter();
            }
            else if (guess.length() != 5 && !guess.equals("help")){
                System.out.println("Please enter a word that is 5 letters long.");
                delay(2);
                impatient++;
            }
            else{
                game.wrongGuess(guess);
                game.setPlayerLastGuess(guess);
                if(!game.outOfAttempts()){
                    delay(2);
                }
            }

            if(impatient == 2){
                System.out.println("FIVE (5) LETTERS");
                delay(1);
            } else if (impatient==3) {
                System.out.println("LAST CHANCE BUDDY");
                delay(2);
            } else if (impatient > 3) {
                System.out.println("You don't deserve to use this program and now I'm just gonna waste your time");
                delay(impatient);
                System.out.println("If you actually need help, I'm sorry. Try reading the readme or typing help.");
                exit();
            }
            System.out.println("----------");
        }while (true);
    }

    private void newPlayer() {
        System.out.println("[CREATING NEW PLAYER]");
        System.out.println("ENTER YOUR NAME:");
        player.setName(input.nextLine());
        GameSaveManager.savePlayer(player);
        System.out.println();
    }

    private void resetPlayer(boolean workaround) {//workaround is temporary until I think of a better solution
        /*
        workaround:
        true - [WORKAROUND] bypasses saving the player. This is used to reset a player that has created a Player object but hasn't played a game.
        false - [DEFAULT BEHAVIOUR] save the player (for resetting and overwriting the old player when resetting)
         This is mainly a QOL feature because the console displays what's going on in the background
         */
        player.setWins(0);
        player.setGamesPlayed(0);
        player.setName(null);
        if(!workaround){
            GameSaveManager.savePlayer(player);
            newPlayer();
        }

    }

    private void gameOver() {
        GameSaveManager.savePlayer(player);
    }

    private void gameInfo() {
        System.out.println("Guesses Remaining: " + game.getRemainingAttempts());
    }

    private void loadPlayer() {
        Player loadedPlayer = GameSaveManager.loadPlayer();
        if(loadedPlayer != null){
            player = loadedPlayer;
        }
    }

    public void pressEnter() {
        System.out.println("Press [ENTER] to continue");
        input.nextLine();
    }

    private static void delay(int seconds){//Intentional delay in the code. This helps leave text on the screen long enough to read.
        long mil= (long) (seconds* 1000L);
        try {
            Thread.sleep(mil);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private void exit(){
        System.out.println("[EXITING]");
        System.out.println("Thank you for playing!");
        delay(1);
        System.exit(0);

    }
}
