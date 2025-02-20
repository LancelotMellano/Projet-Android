public class Main {
    public static void main(String[] args) {
        Database db = new Database();
        String cheminFichier = "/nas2/users/etudiant/m/ml306535/Téléchargements/Donnes/donnes.json";
        db.chargerEtEnregistrerDonnees(cheminFichier);
    }
}
