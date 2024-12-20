class Bet {
    private int amount;
    private String type; // Exemple : "rouge", "noir", "pair", "impair", "number", etc.
    private String detail; // Détail supplémentaire pour certains types de mises

    public Bet(int amount, String type, String detail) {
        this.amount = amount; // Montant de la mise
        this.type = type; // Type de mise
        this.detail = detail; // Détail supplémentaire
    }

    public int getAmount() {
        return amount; // Retourne le montant de la mise
    }

    public String getType() {
        return type; // Retourne le type de mise
    }

    public String getDetail() {
        return detail; // Retourne les détails de la mise
    }
}
