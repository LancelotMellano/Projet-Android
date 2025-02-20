import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;

class Database {

    private final String url = "jdbc:mysql://10.3.122.101:3306/roulette";
    private final String user = "root";
    private final String password = "root";

    // Lire le fichier JSON en une chaîne de caractères
    private String lireFichier(String cheminFichier) throws IOException {
        return new String(Files.readAllBytes(Paths.get(cheminFichier)));
    }

    // Charger et enregistrer les données du JSON dans la base de données
    public void chargerEtEnregistrerDonnees(String cheminFichier) {
        try {
            // Lire le fichier JSON
            String json = lireFichier(cheminFichier);
            JSONObject jsonObj = new JSONObject(json);

            // Connexion à la base de données
            Connection connexion = DriverManager.getConnection(url, user, password);

            // Supprimer les anciennes données avant d'insérer les nouvelles
            supprimerAnciennesDonnees(connexion);

            // Insérer les paramètres du jeu
            if (jsonObj.has("parametres")) {
                JSONObject parametres = jsonObj.getJSONObject("parametres");
                int limiteMin = parametres.getInt("limite_min");
                int limiteMax = parametres.getInt("limite_max");
                String regles = parametres.getString("regles");
                enregistrerParametres(connexion, limiteMin, limiteMax, regles);
            }

            // Insérer les messages
            if (jsonObj.has("messages")) {
                JSONArray messagesArray = jsonObj.getJSONArray("messages");
                for (int i = 0; i < messagesArray.length(); i++) {
                    JSONObject msg = messagesArray.getJSONObject(i);
                    String categorie = msg.getString("categorie");
                    String cle = msg.getString("cle");
                    String valeur = msg.getString("valeur");
                    enregistrerMessage(connexion, categorie, cle, valeur);
                }
            }

            // Fermer la connexion
            connexion.close();

            System.out.println(" Les données ont été enregistrées correctement !");
        } catch (IOException e) {
            System.out.println(" Erreur lors de la lecture du fichier JSON : " + e.getMessage());
        } catch (SQLException e) {
            System.out.println(" Erreur SQL : " + e.getMessage());
        }
    }

    // Supprimer les anciennes données des tables avant d'insérer les nouvelles
    private void supprimerAnciennesDonnees(Connection connexion) throws SQLException {
        try (PreparedStatement suppressionParametres = connexion.prepareStatement("DELETE FROM parametres")) {
            suppressionParametres.executeUpdate();
        }
        try (PreparedStatement suppressionMessages = connexion.prepareStatement("DELETE FROM messages")) {
            suppressionMessages.executeUpdate();
        }
        System.out.println(" Anciennes données supprimées.");
    }

    // Insérer les paramètres du jeu dans la base de données
    private void enregistrerParametres(Connection connexion, int limiteMin, int limiteMax, String regles) throws SQLException {
        String requeteInsertion = "INSERT INTO parametres (limite_min, limite_max, regles) VALUES (?, ?, ?)";
        try (PreparedStatement insertion = connexion.prepareStatement(requeteInsertion)) {
            insertion.setInt(1, limiteMin);
            insertion.setInt(2, limiteMax);
            insertion.setString(3, regles);
            insertion.executeUpdate();
        }
        System.out.println("Paramètres enregistrés : limite_min=" + limiteMin + ", limite_max=" + limiteMax);
    }

    // Insérer un message dans la base de données
    private void enregistrerMessage(Connection connexion, String categorie, String cleMessage, String valeurMessage) throws SQLException {
        String requeteInsertion = "INSERT INTO messages (categorie, cle_message, valeur_message) VALUES (?, ?, ?)";
        try (PreparedStatement insertion = connexion.prepareStatement(requeteInsertion)) {
            insertion.setString(1, categorie);
            insertion.setString(2, cleMessage);
            insertion.setString(3, valeurMessage);
            insertion.executeUpdate();
        }
        System.out.println(" Message enregistré : [" + categorie + "] " + cleMessage + " → " + valeurMessage);
    }


}
