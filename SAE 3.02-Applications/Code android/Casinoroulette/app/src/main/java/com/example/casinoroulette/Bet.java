package com.example.casinoroulette;

public class Bet {
    String key; // Numéro ou couleur
    int value;  // Valeur de la mise

    Bet(String key, int value) {
        this.key = key;
        this.value = value;
    }
}