<?php
require 'db_config.php'; // Connexion à la BDD

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $winning_number = $_POST["winning_number"];

    try {
        // Insérer le numéro dans la table
        $stmt = $pdo->prepare("INSERT INTO historique (winning_number) VALUES (:winning_number)");
        $stmt->bindParam(":winning_number", $winning_number, PDO::PARAM_INT);
        $stmt->execute();

        // Vérifier le nombre d'entrées et supprimer l'ancien si > 10
        $pdo->query("DELETE FROM historique WHERE id NOT IN (
                        SELECT id FROM (SELECT id FROM historique ORDER BY id DESC LIMIT 10) AS temp
                    )");

        echo json_encode(["success" => true, "message" => "Numéro ajouté avec succès"]);
    } catch (PDOException $e) {
        echo json_encode(["success" => false, "message" => "Erreur SQL : " . $e->getMessage()]);
    }
} else {
    echo json_encode(["success" => false, "message" => "Méthode non autorisée"]);
}
?>
