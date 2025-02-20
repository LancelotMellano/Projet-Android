<?php
require 'db_config.php'; // Connexion à la BDD

if ($_SERVER["REQUEST_METHOD"] == "GET") {
    try {
        // Récupérer les 10 derniers numéros gagnants
        $stmt = $pdo->query("SELECT winning_number FROM historique ORDER BY id DESC LIMIT 10");
        $history = $stmt->fetchAll(PDO::FETCH_COLUMN); // Récupère seulement la colonne "winning_number"

        echo json_encode(["success" => true, "history" => $history]);
    } catch (PDOException $e) {
        echo json_encode(["success" => false, "message" => "Erreur SQL : " . $e->getMessage()]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Méthode non autorisée"]);
}
?>
