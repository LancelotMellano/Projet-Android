class Player {
    private int balance;
    private Bet currentBet;

    public void setInitialBalance(int amount) {
        if (amount >= 1 && amount <= 1000) {
            this.balance = amount; // Définir le solde initial
        } else {
            throw new IllegalArgumentException("Le solde initial doit être entre 1 et 1000.");
        }
    }

    public int getBalance() {
        return balance; // Retourne le solde actuel
    }

    public void placeBet(int amount, String type, String detail) {
        if (amount > 0 && amount <= balance) {
            this.currentBet = new Bet(amount, type, detail); // Enregistre la mise actuelle
        } else {
            throw new IllegalArgumentException("Mise invalide ou solde insuffisant.");
        }
    }

    public Bet getCurrentBet() {
        return currentBet; // Retourne la mise actuelle
    }

    public void updateBalance(int amount) {
        this.balance += amount; // Met à jour le solde en fonction des gains ou pertes
    }
}