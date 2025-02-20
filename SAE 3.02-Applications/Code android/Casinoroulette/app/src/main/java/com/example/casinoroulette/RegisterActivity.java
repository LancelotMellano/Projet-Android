package com.example.casinoroulette;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField;
    private EditText soldeField;
    private Button registerButton;

    private Map<String, Object> gameRules; // Règles du jeu (limites de solde)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        usernameField = findViewById(R.id.username_field);
        passwordField = findViewById(R.id.password_field);
        soldeField = findViewById(R.id.solde_field);
        registerButton = findViewById(R.id.register_button);



        // Charger les règles du jeu avant d'autoriser l'inscription
        loadGameRules();

        registerButton.setOnClickListener(v -> {


            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String soldeStr = soldeField.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || soldeStr.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
                return;
            }

            int solde = Integer.parseInt(soldeStr);

            // Valeurs par défaut si `gameRules` ne s'est pas chargé
            int soldeMin = 10;
            int soldeMax = 1000;

            if (gameRules != null) {
                try {
                    soldeMin = ((Double) gameRules.get("limite_min")).intValue();
                    soldeMax = ((Double) gameRules.get("limite_max")).intValue();
                } catch (Exception e) {
                }
            }

            if (solde < soldeMin || solde > soldeMax) {
                Toast.makeText(RegisterActivity.this, "Le solde doit être entre " + soldeMin + " et " + soldeMax + ".", Toast.LENGTH_SHORT).show();
                return;
            }

            registerUser(username, password, solde);
        });
    }

    private void loadGameRules() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse> call = apiService.getMessages();

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    gameRules = response.body().getParametres();

                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Erreur de chargement des règles.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUser(String username, String password, int solde) {

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        User user = new User(username, password, solde);

        // Vérification JSON avant envoi
        Gson gson = new Gson();
        String jsonUser = gson.toJson(user);

        Call<ApiResponse> call = apiService.registerUser(user);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Erreur lors de l'inscription.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Erreur de connexion : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
