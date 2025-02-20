package com.example.casinoroulette;

public class User {
    private String user;  // Nom d'utilisateur
    private String mdp;   // Mot de passe
    private int solde;    // Solde initial de l'utilisateur
    private int change;   // Changement de solde (valeur de mise)

    // Constructeur pour la connexion (utilisé dans loginUser)
    public User(String user, String mdp) {
        this.user = user;
        this.mdp = mdp;
    }

    // Constructeur pour l'inscription avec solde initial
    public User(String user, String mdp, int solde) {
        this.user = user;
        this.mdp = mdp;
        this.solde = solde;
    }

    // Constructeur pour la mise à jour du solde
    public User(String user, String mdp, int solde, int change) {
        this.user = user;
        this.mdp = mdp;
        this.solde = solde;
        this.change = change;
    }

}
