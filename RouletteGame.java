import java.util.Scanner;
public class RouletteGame {
    public static void main(String[] args) {
        // Initialiser le joueur
        Player player = new Player();
        player.setInitialBalance(500); // Exemple : le joueur commence avec 500

        // Créer une instance de la roulette
        Roulette roulette = new Roulette();

        // Boucle de jeu
        while (true) {
            // Exemple : le joueur place une mise
            //player.placeBet(50, "red", ""); // Mise de 50 sur rouge
            //player.placeBet(20, "number", "17"); // Mise sur le numéro 17
            player.placeBet(30, "dozen", "2"); // Mise sur la deuxième douzaine
            //player.placeBet(40, "column", "3"); // Mise sur la troisième colonne
            // Lancer la roulette
            int result = roulette.spin();
            String resultColor = roulette.getColor(result);

            // Calculer les gains ou les pertes
            int payout = roulette.calculatePayout(player.getCurrentBet(), result, resultColor);
            player.updateBalance(payout);

            // Afficher les résultats
            System.out.println("Numéro tiré : " + result + " (" + resultColor + ")");
            System.out.println("Solde actuel : " + player.getBalance());

            // Vérifier si le joueur veut continuer ou si le solde est à 0
            if (player.getBalance() <= 0) {
                System.out.println("Vous n'avez plus d'argent. Fin de la partie.");
                break;
            }

            System.out.println("Voulez-vous jouer un autre tour ? (oui/non)");
            Scanner scanner = new Scanner(System.in);
            String response = scanner.nextLine();
            if (!response.equalsIgnoreCase("oui")) {
                break;
            }
        }
    }
}