package com.example.casinoroulette;

import java.util.List;
import java.util.Map;

public class ApiResponse {
    private boolean success;
    private String message;
    private int solde;
    private Map<String, Object> parametres; // Stocke limite_min, limite_max, etc.
    private Map<String, Map<String, String>> messages; // Stocke erreurs, mises, jeu
    private List<Integer> history; // Stocke les 10 derniers numéros de la roulette

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getSolde() {
        return solde;
    }

    public Map<String, Object> getParametres() {
        return parametres;
    }

    public Map<String, Map<String, String>> getMessages() {
        return messages;
    }

    public List<Integer> getHistory() {
        return history;
    }



}
