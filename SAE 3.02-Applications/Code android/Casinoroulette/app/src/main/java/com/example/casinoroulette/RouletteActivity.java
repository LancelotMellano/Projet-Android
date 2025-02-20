package com.example.casinoroulette;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.view.animation.DecelerateInterpolator;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import android.view.LayoutInflater;


public class RouletteActivity extends AppCompatActivity {

    private int selectedBetValue = 0; // Valeur actuelle du jeton sélectionné
    private String currentUser;      // Nom d'utilisateur reçu dynamiquement
    private String currentPassword;  // Mot de passe reçu dynamiquement
    private TextView soldeTextView;  // TextView pour afficher le solde
    private int currentSolde = 0;    // Solde actuel de l'utilisateur
    private CountDownTimer timer;
    private TextView timerText;     // Pour afficher le timer
    private ImageView rouletteWheel; // Roulette animée

    // Structure pour stocker les mises
    private HashMap<String, Integer> placedBets = new HashMap<>();
    private static final String RED = "Rouge";
    private static final String BLACK = "Noir";

    private static final String EVEN = "Pair";
    private static final String ODD = "Impair";

    private Button selectedJetonButton; // Référence au bouton du jeton sélectionné
    // Stocke la dernière mise et sa couleur pour l’annulation
    private String lastBetKey = null;




    private Stack<Bet> betHistory = new Stack<>();

    private ConstraintLayout rulesContainer;
    private TextView rulesText;
    private boolean isRulesVisible = false;
    private Map<String, Object> gameRules;



    private Map<String, Map<String, String>> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roulette);

        // Configuration de la barre d'action
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialisation des vues
        timerText = findViewById(R.id.timer_text);
        rouletteWheel = findViewById(R.id.roulette_wheel);
        soldeTextView = findViewById(R.id.solde_text);
        rulesContainer = findViewById(R.id.rules_container);
        rulesText = findViewById(R.id.rules_text);
        rulesContainer.setVisibility(View.GONE);

        // Vérification des informations utilisateur
        currentUser = getIntent().getStringExtra("username");
        currentPassword = getIntent().getStringExtra("password");
        if (currentUser == null || currentPassword == null) {
            Toast.makeText(this, messages.get("erreurs").get("utilisateur_manquant"), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Chargement des données
        loadInitialSolde();
        loadHistory();
        loadMessagesFromAPI();

        // Configuration des boutons de mise
        setupJetonButtons();
        setupBetButtons();
        setupSpecialBetButtons();
        setupDozenAndColumnButtons();

        // Gestion des règles du jeu
        Button rulesButton = findViewById(R.id.rules_button);
        rulesButton.setOnClickListener(v -> toggleRules());

        // Démarrer le compte à rebours
        startBettingTimer();
    }

    private void setupJetonButtons() {
        Button jeton10 = findViewById(R.id.jeton_10);
        jeton10.setOnClickListener(v -> selectBetValue(10, jeton10));

        Button jeton50 = findViewById(R.id.jeton_50);
        jeton50.setOnClickListener(v -> selectBetValue(50, jeton50));

        Button jeton100 = findViewById(R.id.jeton_100);
        jeton100.setOnClickListener(v -> selectBetValue(100, jeton100));
    }

    private void setupBetButtons() {
        for (int i = 0; i <= 36; i++) {
            int resId = getResources().getIdentifier("bet_" + i, "id", getPackageName());
            Button betButton = findViewById(resId);
            int finalI = i;
            betButton.setOnClickListener(v -> placeBet(String.valueOf(finalI)));
        }
    }

    private void setupSpecialBetButtons() {
        Button betRed = findViewById(R.id.bet_red);
        betRed.setOnClickListener(v -> placeColorBet(RED));

        Button betBlack = findViewById(R.id.bet_black);
        betBlack.setOnClickListener(v -> placeColorBet(BLACK));

        Button betEven = findViewById(R.id.bet_even);
        betEven.setOnClickListener(v -> placeParityBet(EVEN));

        Button betOdd = findViewById(R.id.bet_odd);
        betOdd.setOnClickListener(v -> placeParityBet(ODD));

        Button undoBet = findViewById(R.id.undo_bet_button);
        undoBet.setOnClickListener(v -> undoLastBet());
    }

    private void setupDozenAndColumnButtons() {
        Button betFirst12 = findViewById(R.id.bet_first_12);
        betFirst12.setOnClickListener(v -> placeDozenBet("1e12", 1, 12));

        Button betSecond12 = findViewById(R.id.bet_second_12);
        betSecond12.setOnClickListener(v -> placeDozenBet("2e12", 13, 24));

        Button betThird12 = findViewById(R.id.bet_third_12);
        betThird12.setOnClickListener(v -> placeDozenBet("3e12", 25, 36));

        Button betColumn1 = findViewById(R.id.bet_2to1_1);
        betColumn1.setOnClickListener(v -> placeColumnBet("2to1_1", generateColumn(1)));

        Button betColumn2 = findViewById(R.id.bet_2to1_2);
        betColumn2.setOnClickListener(v -> placeColumnBet("2to1_2", generateColumn(2)));

        Button betColumn3 = findViewById(R.id.bet_2to1_3);
        betColumn3.setOnClickListener(v -> placeColumnBet("2to1_3", generateColumn(3)));
    }

    private int[] generateColumn(int start) {
        int[] column = new int[12];
        for (int i = 0; i < 12; i++) {
            column[i] = start + (i * 3);
        }
        return column;
    }
    private void startBettingTimer() {
        timer = new CountDownTimer(50000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                lockBets();
                startRouletteAnimation();
            }
        }.start();
    }

    private void selectBetValue(int value, Button selectedButton) {
        if (value > currentSolde) {
            showCustomToast(messages.get("mises").get("solde_insuffisant_jeton"), R.drawable.ic_warning);
            return;
        }

        // Si on reclique sur le même jeton, le désélectionner
        if (selectedJetonButton == selectedButton) {
            selectedBetValue = 0;
            selectedJetonButton.setBackgroundColor(getResources().getColor(R.color.default_bet));
            selectedJetonButton = null;
            return;
        }

        // Réinitialiser tous les jetons à leur couleur par défaut
        resetJetonStyles();

        // Définir la couleur du jeton sélectionné
        if (value == 10) {
            selectedButton.setBackgroundColor(getResources().getColor(R.color.jeton_10)); // Jaune
        } else if (value == 50) {
            selectedButton.setBackgroundColor(getResources().getColor(R.color.jeton_50)); // Rose
        } else if (value == 100) {
            selectedButton.setBackgroundColor(getResources().getColor(R.color.jeton_100)); // Bleu
        }

        // Mettre à jour le jeton sélectionné
        selectedBetValue = value;
        selectedJetonButton = selectedButton;
    }

    private void resetJetonStyles() {
        Button jeton10 = findViewById(R.id.jeton_10);
        Button jeton50 = findViewById(R.id.jeton_50);
        Button jeton100 = findViewById(R.id.jeton_100);

        int defaultColor = getResources().getColor(R.color.default_bet);
        jeton10.setBackgroundColor(defaultColor);
        jeton50.setBackgroundColor(defaultColor);
        jeton100.setBackgroundColor(defaultColor);
    }

    private void placeBet(String bet) {
        if (selectedBetValue == 0) {
            showCustomToast(messages.get("mises").get("selection_jeton"), R.drawable.ic_warning);
            return;
        }

        if (selectedBetValue > currentSolde) {
            showCustomToast(messages.get("mises").get("solde_insuffisant_mise"), R.drawable.ic_warning);
            return;
        }

        // Ajouter la mise
        int currentBet = placedBets.getOrDefault(bet, 0);
        placedBets.put(bet, currentBet + selectedBetValue);

        // Sauvegarder l'historique pour annulation
        betHistory.push(new Bet(bet, selectedBetValue));
        updateBetButtonAppearance(bet, placedBets.get(bet));
        updateUserBalanceAndShowMessage(-selectedBetValue, "Mise de " + selectedBetValue + "€ sur " + bet + ".");
    }
    private void undoLastBet() {
        if (betHistory.isEmpty()) {
            showCustomToast(messages.get("mises").get("aucune_mise_annuler"), R.drawable.ic_warning);
            return;
        }

        Bet lastBet = betHistory.pop(); // Dernière mise
        int currentBet = placedBets.getOrDefault(lastBet.key, 0);

        if (currentBet < lastBet.value) {
            showCustomToast(messages.get("erreurs").get("erreur_annulation_mise"), R.drawable.ic_warning);
            return;
        }

        placedBets.put(lastBet.key, currentBet - lastBet.value);
        if (placedBets.get(lastBet.key) == 0) placedBets.remove(lastBet.key);

        updateBetButtonAppearance(lastBet.key, placedBets.getOrDefault(lastBet.key, 0));
        updateUserBalance(lastBet.value);
        showCustomToast("Mise annulée sur : " + lastBet.key + " (" + lastBet.value + "€)", R.drawable.ic_undo);
    }


    private void lockBets() {
        // Afficher le message "Les mises sont fermées"
        showCustomToast(messages.get("mises").get("mises_fermees"), R.drawable.ic_lock);

        // Désactiver l'interaction avec les boutons de mise sans modifier leur apparence
        for (Button button : getAllBetButtons()) {
            button.setClickable(false); // Empêche l'interaction sans désactiver l'affichage
        }
    }

    private Button[] getAllBetButtons() {
        List<Button> betButtons = new ArrayList<>();

        // Ajouter les boutons de 0 à 36
        for (int i = 0; i <= 36; i++) {
            int resId = getResources().getIdentifier("bet_" + i, "id", getPackageName());
            betButtons.add(findViewById(resId));
        }

        // Ajouter les autres mises
        betButtons.add(findViewById(R.id.bet_red));
        betButtons.add(findViewById(R.id.bet_black));

        betButtons.add(findViewById(R.id.bet_even));
        betButtons.add(findViewById(R.id.bet_odd));

        betButtons.add(findViewById(R.id.bet_first_12));
        betButtons.add(findViewById(R.id.bet_second_12));
        betButtons.add(findViewById(R.id.bet_third_12));

        betButtons.add(findViewById(R.id.bet_2to1_1));
        betButtons.add(findViewById(R.id.bet_2to1_2));
        betButtons.add(findViewById(R.id.bet_2to1_3));

        return betButtons.toArray(new Button[0]);
    }


    private void placeDozenBet(String betKey, int min, int max) {
        placeGeneralBet(betKey);
    }

    private void placeColumnBet(String betKey, int[] columnNumbers) {
        placeGeneralBet(betKey);
    }

    private void placeParityBet(String betKey) {
        placeGeneralBet(betKey);
    }

    private void placeColorBet(String betKey) {
        placeGeneralBet(betKey);
    }

    // Méthode générale pour gérer toutes les mises groupées
    private void placeGeneralBet(String betKey) {
        if (!isBetValid()) return;

        // Ajouter la mise et l'historique
        placedBets.put(betKey, placedBets.getOrDefault(betKey, 0) + selectedBetValue);
        betHistory.push(new Bet(betKey, selectedBetValue));

        // Mettre à jour l'affichage et le solde
        updateBetButtonAppearance(betKey, placedBets.get(betKey));
        updateUserBalanceAndShowMessage(-selectedBetValue, "Mise de " + selectedBetValue + "€ sur " + betKey + ".");
    }

    // Vérifie si la mise est valide
    private boolean isBetValid() {
        if (selectedBetValue == 0) {
            showCustomToast("Veuillez d'abord sélectionner un jeton.", R.drawable.ic_warning);
            return false;
        }
        if (selectedBetValue > currentSolde) {
            showCustomToast("Solde insuffisant pour cette mise.", R.drawable.ic_warning);
            return false;
        }
        return true;
    }



    private void startRouletteAnimation() {
        rouletteWheel.setVisibility(View.VISIBLE);

        // Faire une seule animation avec un décélérateur
        rouletteWheel.animate()
                .rotationBy(2880) // Faire 8 tours rapides
                .setDuration(8000) // Durée totale de 8 secondes pour ralentir progressivement
                .setInterpolator(new DecelerateInterpolator()) // Ajouter un effet de ralentissement fluide
                .withEndAction(() -> {
                    // Garder la roulette immobile pendant 3 secondes
                    rouletteWheel.postDelayed(() -> {
                        rouletteWheel.setVisibility(View.GONE); // Cacher la roulette
                        int winningNumber = determineWinningNumber();
                        displayWinningNumber(winningNumber);

                        // Redémarrer le jeu
                        startBettingTimer();
                        resetBets();
                    }, 3000); // Attendre 3 secondes avant d'exécuter l'action suivante
                })
                .start();
    }


    private void updateUserBalanceAndShowMessage(int change, String baseMessage) {
        Log.d("DEBUG_SOLDE", "Mise à jour solde : " + currentSolde + " | Changement : " + change);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse> call = apiService.updateSolde(new User(currentUser, currentPassword, currentSolde, change));

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        currentSolde += change;
                        soldeTextView.setText(currentSolde + "$");
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(RouletteActivity.this, "Erreur de mise à jour du solde : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int determineWinningNumber() {
        return (int) (Math.random() * 37); // Génère un nombre entre 0 et 36
    }

    private boolean isRed(int number) {
        int[] redNumbers = {1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36};
        for (int red : redNumbers) {
            if (number == red) return true;
        }
        return false;
    }

    public void showCustomToast(String message, int iconResId) {
        // Obtenir le LayoutInflater
        LayoutInflater inflater = getLayoutInflater();

        // Charger le layout personnalisé
        View layout = inflater.inflate(R.layout.custom_toast, findViewById(android.R.id.content), false);

        // Définir le message du Toast
        TextView text = layout.findViewById(R.id.toast_message);
        text.setText(message);

        // Définir l'icône (optionnel)
        ImageView icon = layout.findViewById(R.id.toast_icon);
        icon.setImageResource(iconResId);

        // Créer et afficher le Toast
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private void displayWinningNumber(int winningNumber) {
        // 1⃣ Affichage du numéro gagnant
        Toast.makeText(this, "Le numéro gagnant est : " + winningNumber, Toast.LENGTH_LONG).show();

        // 2 Détermination des caractéristiques du numéro gagnant
        String color;
        if (winningNumber == 0) {
            color = "aucune couleur"; // Le 0 n'est ni Rouge ni Noir
        } else if (isRed(winningNumber)) {
            color = "Rouge";
        } else {
            color = "Noir";
        }

        // 3 Calcul du total des mises placées
        int totalBets = 0;
        for (int betAmount : placedBets.values()) {
            totalBets += betAmount;
        }

        // 4⃣ Calcul des gains
        int winnings = 0;

        // Gains sur les numéros directs
        winnings += placedBets.getOrDefault(String.valueOf(winningNumber), 0) * 35;

        // Gains sur Rouge / Noir (le 0 ne compte pas)
        if (winningNumber != 0) {
            winnings += placedBets.getOrDefault(color, 0) * 2;
        }

        // Gains sur Pair / Impair (le 0 ne compte pas)
        if (winningNumber != 0) {
            if (winningNumber % 2 == 0) {
                winnings += placedBets.getOrDefault(EVEN, 0) * 2;
            } else {
                winnings += placedBets.getOrDefault(ODD, 0) * 2;
            }
        }

        // Gains sur les douzaines
        if (winningNumber >= 1 && winningNumber <= 12) {
            winnings += placedBets.getOrDefault("1er12", 0) * 3;
        } else if (winningNumber >= 13 && winningNumber <= 24) {
            winnings += placedBets.getOrDefault("2e12", 0) * 3;
        } else if (winningNumber >= 25 && winningNumber <= 36) {
            winnings += placedBets.getOrDefault("3e12", 0) * 3;
        }

        // Gains sur les colonnes (2 to 1)
        int[][] columnBets = {
                {1, 4, 7, 10, 13, 16, 19, 22, 25, 28, 31, 34}, // Colonne 1
                {2, 5, 8, 11, 14, 17, 20, 23, 26, 29, 32, 35}, // Colonne 2
                {3, 6, 9, 12, 15, 18, 21, 24, 27, 30, 33, 36}  // Colonne 3
        };

        if (contains(columnBets[0], winningNumber)) {
            winnings += placedBets.getOrDefault("2to1_1", 0) * 3;
        } else if (contains(columnBets[1], winningNumber)) {
            winnings += placedBets.getOrDefault("2to1_2", 0) * 3;
        } else if (contains(columnBets[2], winningNumber)) {
            winnings += placedBets.getOrDefault("2to1_3", 0) * 3;
        }

        // 5 Calcul du gain net et affichage du résultat
        int netResult = winnings - totalBets;
        String resultMessage;

        if (netResult > 0) {
            resultMessage = "Félicitations ! Vous avez gagné " + netResult + "€.";
            showCustomToast(resultMessage, R.drawable.ic_win);
        } else if (netResult < 0) {
            resultMessage = "Dommage ! Vous avez perdu " + Math.abs(netResult) + "€.";
            showCustomToast(resultMessage, R.drawable.ic_loss);
        } else {
            resultMessage = "Vous n'avez rien gagné ni perdu cette fois.";
            showCustomToast(resultMessage, R.drawable.ic_neutral);
        }

        // 6 Envoi du numéro gagnant à l'API et mise à jour de l'historique
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse> call = apiService.updateHistory(winningNumber);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    loadHistory(); // Rafraîchir l'historique
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(RouletteActivity.this, "Erreur mise à jour historique", Toast.LENGTH_SHORT).show();
            }
        });

        // 7 Mettre à jour le solde et réinitialiser les mises
        updateUserBalance(winnings);
        resetBets();
    }

    // Fonction utilitaire pour vérifier si un nombre est dans un tableau
    private boolean contains(int[] array, int value) {
        for (int num : array) {
            if (num == value) {
                return true;
            }
        }
        return false;
    }

    private void resetBets() {
        for (String betKey : placedBets.keySet()) {
            updateBetButtonAppearance(betKey, 0);
        }

        placedBets.clear();
        lastBetKey = null;

        for (Button button : getAllBetButtons()) {
            button.setClickable(true);
        }
    }
    private void updateBetButtonAppearance(String betKey, int betValue) {
        Button betButton = getBetButton(betKey);
        if (betButton != null) {
            if (betValue > 0) {
                // Appliquer une couleur en fonction du montant misé
                if (betValue >= 100) {
                    betButton.setBackgroundColor(getResources().getColor(R.color.jeton_100));
                } else if (betValue >= 50) {
                    betButton.setBackgroundColor(getResources().getColor(R.color.jeton_50));
                } else {
                    betButton.setBackgroundColor(getResources().getColor(R.color.jeton_10));
                }
            } else {
                // Rétablir la couleur d'origine si aucune mise
                betButton.setBackgroundColor(getOriginalColor(betKey));
            }
        }
    }

    private Button getBetButton(String betKey) {
        switch (betKey) {
            case "Rouge":
                return findViewById(R.id.bet_red);
            case "Noir":
                return findViewById(R.id.bet_black);
            case "Pair":
                return findViewById(R.id.bet_even);
            case "Impair":
                return findViewById(R.id.bet_odd);
            case "1e12":
                return findViewById(R.id.bet_first_12);
            case "2e12":
                return findViewById(R.id.bet_second_12);
            case "3e12":
                return findViewById(R.id.bet_third_12);
            case "2to1_1":
                return findViewById(R.id.bet_2to1_1);
            case "2to1_2":
                return findViewById(R.id.bet_2to1_2);
            case "2to1_3":
                return findViewById(R.id.bet_2to1_3);
            default:
                // Vérifier si c'est un numéro (0 à 36)
                int resID = getResources().getIdentifier("bet_" + betKey, "id", getPackageName());
                return findViewById(resID);
        }
    }

    private int getOriginalColor(String betKey) {
        try {
            // Si la mise est un numéro individuel
            int number = Integer.parseInt(betKey);
            if (number == 0) {
                return getResources().getColor(R.color.green); // Vert pour le 0
            } else if (isRed(number)) {
                return getResources().getColor(R.color.red); // Rouge
            } else {
                return getResources().getColor(R.color.black); // Noir
            }
        } catch (NumberFormatException ignored) {
            // Si ce n'est pas un nombre, on traite les autres mises spéciales
        }

        // Gestion des couleurs Rouge/Noir
        if (betKey.equals(RED)) {
            return getResources().getColor(R.color.red);
        }
        if (betKey.equals(BLACK)) {
            return getResources().getColor(R.color.black);
        }

        // Gestion des mises Pair/Impair
        if (betKey.equals(EVEN) || betKey.equals(ODD)) {
            return getResources().getColor(R.color.white); // Blanc pour Pair/Impair
        }

        // Gestion des Douzaines
        if (betKey.equals("1e12") || betKey.equals("2e12") || betKey.equals("3e12")) {
            return getResources().getColor(R.color.white);
        }

        //  Gestion des colonnes (2to1)
        if (betKey.equals("2to1_1") || betKey.equals("2to1_2") || betKey.equals("2to1_3")) {
            return getResources().getColor(R.color.white);
        }

        // Couleur par défaut (mises non reconnues)
        return getResources().getColor(R.color.default_bet);
    }

    private void updateUserBalance(int change) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse> call = apiService.updateSolde(new User(currentUser, currentPassword, currentSolde, change));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        currentSolde += change;
                        soldeTextView.setText(currentSolde + "$");
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(RouletteActivity.this, "Erreur de mise à jour du solde : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadInitialSolde() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse> call = apiService.loginUser(new User(currentUser, currentPassword));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentSolde = response.body().getSolde();
                    soldeTextView.setText(currentSolde + "$");
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(RouletteActivity.this, "Erreur de chargement du solde : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadMessagesFromAPI() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse> call = apiService.getMessages();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Stocke les données reçues
                    messages = response.body().getMessages();
                    gameRules = response.body().getParametres(); // Récupère les paramètres JSON
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(RouletteActivity.this, "Erreur API : " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void toggleRules() {
        if (isRulesVisible) {
            rulesContainer.setVisibility(View.GONE);
        } else {
            if (gameRules != null && gameRules.containsKey("regles")) {
                String reglesText = (String) gameRules.get("regles");
                rulesText.setText(reglesText.replace(" | ", "\n"));
            } else {
                rulesText.setText("Erreur de chargement des règles.");
            }
            rulesContainer.setVisibility(View.VISIBLE);
        }
        isRulesVisible = !isRulesVisible;
    }

    private void loadHistory() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse> call = apiService.getHistory();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        updateHistoryDisplay(response.body().getHistory());
                    }
                }
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(RouletteActivity.this, "Erreur chargement historique", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateHistoryDisplay(List<Integer> historyNumbers) {
        LinearLayout historyContainer = findViewById(R.id.history_container);
        historyContainer.removeAllViews(); // Nettoyer l'historique avant d'ajouter les nouveaux numéros

        for (int number : historyNumbers) {
            TextView numberView = new TextView(this);
            numberView.setText(String.valueOf(number));
            numberView.setTextColor(Color.WHITE);
            numberView.setTextSize(18);
            numberView.setTypeface(Typeface.DEFAULT_BOLD);
            numberView.setGravity(Gravity.CENTER);
            numberView.setPadding(20, 10, 20, 10);

            // Appliquer la couleur de fond arrondie
            if (number == 0) {
                numberView.setBackground(ContextCompat.getDrawable(this, R.drawable.history_green));
            } else if (isRed(number)) {
                numberView.setBackground(ContextCompat.getDrawable(this, R.drawable.history_red));
            } else {
                numberView.setBackground(ContextCompat.getDrawable(this, R.drawable.history_black));
            }

            // Ajouter une marge en bas pour donner plus d’espace sous le texte
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 0, 10, 30);// Ajout d'une marge en bas
            numberView.setLayoutParams(params);

            // Ajouter le TextView à l'affichage
            historyContainer.addView(numberView);
        }

        // Forcer le rafraîchissement de l'affichage
        historyContainer.invalidate();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}