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
        initialize();
        System.out.println("[RUNNING]");
        do{
            System.out.println("ENTER YOUR NAME:");
            player.setName(input.nextLine());
        }while(player.getName()==null);
        System.out.println("Welcome, " + player.getName() + "!");
        delay(1);
        while(true){
            mainMenu();
            play();
        }
    }

    private void play() {
        int impatient=0;
        String guess="";
        String answer = game.getCurrentWord();
        do{
            System.out.println("----------");
            gameInfo();
            if(game.outOfAttempts()){
                System.out.println("[Game Over]");
                game.lose();
                player.lose();
                delay(2);
                break;
            }
            System.out.println(game.getPrompt());
            System.out.println("Guess a word (5 letters):");
            guess = input.nextLine().toLowerCase();

            if(guess.equals("cheat")){
                System.out.println(game.getCurrentWord());
            }

            if(guess.equals(answer)){
                game.correctGuess();
                player.win();
                delay(2);
                break;
            }
            else if (guess.length() != 5){
                System.out.println("Please enter a word that is 5 letters long.");
                impatient++;
            } else{
                game.wrongGuess(guess);
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
                delay(5);
                System.out.println("If you actually need help, I'm sorry. Try reading the readme.");
                exit();
            }
            System.out.println("----------");
        }while (true);
    }

    private void gameInfo() {
        System.out.println("Guesses Remaining: " + game.getRemainingAttempts());
    }

    private void initialize(){
        System.out.println("[INITIALIZING]");
        int selection;
        do{
            System.out.println("1 - New Player\n2 - Load Player Data\n3 - Quit");
            selection = input.nextInt();
            input.nextLine();
            if(selection==1){break;} else if (selection==2) {
                loadPlayer();
                break;
            } else if (selection==3) {
                exit();
            }
        }while (selection < 1 || selection > 3);
    }



    private void mainMenu(){
        System.out.println("[MAIN MENU]");
        int selection;
        do{
            System.out.println("1 - Start\n2 - View Player Info\n3 - Save Player Data");
            selection = input.nextInt();
            input.nextLine();
            if(selection==1){
                break;
            } else if (selection==2) {
                System.out.println(player.playerInfo());
                pressEnter();
            } else if (selection==3) {
                GameSaveManager.savePlayer(player);
            }
        }while (true);


    }

    private void loadPlayer() {
        String filename;
        do{
            System.out.println("Notes:\n" +
                    "Do NOT include \".ser\"\n" +
                    "The file name is CASE-SENSITIVE\n" +
                    "Type \"nvm\" to cancel\n" +
                    "Enter the name of the file you would like to load:");
            filename = input.nextLine();
            if(filename.equals("nvm")){
                return;
            }
            if(GameSaveManager.isValidFileName(filename)) {
                player = GameSaveManager.loadPlayer(filename);
                break;
            }
        }while (!GameSaveManager.isValidFileName(filename));

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
