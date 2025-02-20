<?php
require 'db_config.php';

// Log pour afficher les données reçues dans les logs Apache
file_put_contents("php://stderr", "Données reçues : " . print_r($data, true) . "\n", FILE_APPEND);


// Vérifier si les données ont été envoyées via POST
$data = json_decode(file_get_contents("php://input"), true);

// Vérification des paramètres
if (!isset($data['user']) || !isset($data['mdp'])) {
    echo json_encode(["success" => false, "message" => "Paramètres manquants."]);
    exit();
}

$user = $data['user'];
$mdp = $data['mdp'];

try {
    // Préparer la requête pour vérifier les identifiants
    $stmt = $pdo->prepare("SELECT * FROM users WHERE user = :user AND mdp = :mdp");
    $stmt->execute(['user' => $user, 'mdp' => $mdp]);
    $userData = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($userData) {
        // Connexion réussie
        echo json_encode([
            "success" => true,
            "message" => "Connexion réussie",
            "solde" => $userData['solde'] // Inclure le solde si nécessaire
        ]);
    } else {
        // Identifiants incorrects
        echo json_encode(["success" => false, "message" => "Nom d'utilisateur ou mot de passe incorrect"]);
    }
} catch (PDOException $e) {
    // Gestion des erreurs SQL
    echo json_encode(["success" => false, "message" => "Erreur SQL : " . $e->getMessage()]);
}
?>

