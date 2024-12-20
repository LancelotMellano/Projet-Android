class Roulette {
    public int spin() {
        return (int) (Math.random() * 37); // Numéro entre 0 et 36
    }

    public String getColor(int number) {
        if (number == 0) return "vert"; // Le 0 est vert
        if ((number >= 1 && number <= 10) || (number >= 19 && number <= 28)) {
            return (number % 2 == 0) ? "noir" : "rouge"; // Noir si pair, rouge sinon
        } else {
            return (number % 2 == 0) ? "rouge" : "noir"; // Rouge si pair, noir sinon
        }
    }

    public boolean isEven(int number) {
        return number != 0 && number % 2 == 0; // Vérifie si le numéro est pair
    }

    public boolean isOdd(int number) {
        return number != 0 && number % 2 != 0; // Vérifie si le numéro est impair
    }

    public int getDozen(int number) {
        if (number >= 1 && number <= 12) return 1; // Première douzaine
        if (number >= 13 && number <= 24) return 2; // Deuxième douzaine
        if (number >= 25 && number <= 36) return 3; // Troisième douzaine
        return 0; // Pour le 0
    }

    public int getColumn(int number) {
        if (number == 0) return 0; // Le 0 n'appartient à aucune colonne
        return (number - 1) % 3 + 1; // Colonne 1, 2 ou 3
    }

    public int calculatePayout(Bet bet, int result, String resultColor) {
        String type = bet.getType();
        int amount = bet.getAmount();

        switch (type.toLowerCase()) {
            case "red":
                return resultColor.equals("rouge") ? amount * 2 : -amount; // Gain pour rouge
            case "black":
                return resultColor.equals("noir") ? amount * 2 : -amount; // Gain pour noir
            case "even":
                return isEven(result) ? amount * 2 : -amount; // Gain pour pair
            case "odd":
                return isOdd(result) ? amount * 2 : -amount; // Gain pour impair
            case "number":
                return Integer.parseInt(bet.getDetail()) == result ? amount * 35 : -amount; // Gain pour un numéro exact
            case "dozen":
                return Integer.parseInt(bet.getDetail()) == getDozen(result) ? amount * 3 : -amount; // Gain pour une douzaine
            case "column":
                return Integer.parseInt(bet.getDetail()) == getColumn(result) ? amount * 3 : -amount; // Gain pour une colonne
            default:
                return -amount; // Mise perdue
        }
    }
}