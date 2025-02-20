<?php
require 'db_config.php';

// Récupérer les données JSON
$data = json_decode(file_get_contents("php://input"), true);

// Log des données reçues pour débogage
file_put_contents("php://stderr", "Données reçues : " . print_r($data, true) . "\n", FILE_APPEND);

// Vérifiez si les paramètres sont présents
if (!isset($data['user']) || !isset($data['mdp']) || !isset($data['change'])) {
    echo json_encode(['success' => false, 'message' => 'Paramètres manquants.']);
    exit();
}

$user = $data['user'];
$mdp = $data['mdp'];
$change = (int)$data['change'];

// Log de la requête SQL
file_put_contents("php://stderr", "Requête SQL : SELECT * FROM users WHERE user = $user AND mdp = $mdp \n", FILE_APPEND);

// Vérifiez les informations d'identification de l'utilisateur
$stmt = $pdo->prepare("SELECT * FROM users WHERE user = :user AND mdp = :mdp");
$stmt->execute(['user' => $user, 'mdp' => $mdp]);
$userData = $stmt->fetch();

if ($userData) {
    // Mettre à jour le solde
    $newSolde = $userData['solde'] + $change;

    // Log de la mise à jour
    file_put_contents("php://stderr", "Nouvelle solde : $newSolde pour utilisateur : $user\n", FILE_APPEND);

    $updateStmt = $pdo->prepare("UPDATE users SET solde = :solde WHERE user = :user");
    $updateStmt->execute(['solde' => $newSolde, 'user' => $user]);

    echo json_encode(['success' => true, 'message' => 'Solde mis à jour avec succès.', 'new_solde' => $newSolde]);
} else {
    // Log en cas d'échec
    file_put_contents("php://stderr", "Utilisateur non trouvé ou mot de passe incorrect : $user\n", FILE_APPEND);

    echo json_encode(['success' => false, 'message' => 'Utilisateur non trouvé ou mot de passe incorrect.']);
}
?>
