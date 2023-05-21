import java.util.Scanner;

public class UI {

    private Scanner input;
    private Player player;
    private Game game;
    private String[] previousGuesses;


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
            System.out.println("1 - Start\n2 - View Player Info\n3 - Clear Player Data\n4 - Exit");
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
            } else if (selection==4) {
                exit(player.getGamesPlayed()==0);
            }
        }while (true);


    }

    private void play() {
        int counter=0;
        String guess="";
        String answer = game.getCurrentAnswer();
        System.out.println("If you get stuck at any point, type \"help\" to get a better understanding of the game.");
        previousGuesses = new String[5];
        do{

            if(game.outOfAttempts()){
                System.out.println("[Game Over]");
                System.out.println("The word was: " + game.getCurrentAnswer());
                player.lose();
                delay(2);
                gameOver();
                return;
            }

            do {
                gameInfo();
                guess = input.nextLine().toLowerCase();//user guesses

                if(guess.equals("get")){
                    System.out.println(game.getCurrentAnswer());
                    delay(1);
                    guess = "loser";
                    game.setPlayerLastGuess(guess);
                }

                if(guess.equals("help")){
                    System.out.println();
                    System.out.println("[PREVIOUS GUESSES]");
                    game.help();
                    pressEnter();
                    gameInfo();
                    guess = input.nextLine().toLowerCase();//user guesses
                }

                if(guess.startsWith("set_")){
                    game.setCurrentAnswer(guess);
                }

            }while (!game.isValidAnswer(guess));
            previousGuesses[counter] = guess;
            counter++;
            System.out.println("\nYou guessed " + guess);

            if(guess.equals(answer)){
                player.win();
                delay(2);
                gameOver();
                break;
            }
            else {
                game.check(guess);
                game.setPlayerLastGuess(guess);
                if(!game.outOfAttempts()){
                    delay(2);
                }
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
        game = new Game();
        GameSaveManager.savePlayer(player);
    }

    private void gameInfo() {
        game.displayGameBoard();
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

    private void exit(boolean noob){//if 0 games played, they're a noob

        if(!noob){
            do{
                System.out.println("Would you like to save before exiting?");
                String answer = input.nextLine();
                if (answer.equals("yes")){
                    GameSaveManager.savePlayer(player);
                    break;
                } else if (answer.equals("no")) {
                    break;
                }
            }while (true);
        }

        System.out.println("[EXITING]");
        System.out.println("Thank you for playing!");
        delay(1);
        System.exit(0);

    }
}
