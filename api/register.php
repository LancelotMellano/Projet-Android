<?php
require 'db_config.php';

// Récupérer les données envoyées via POST
$data = json_decode(file_get_contents("php://input"), true);

if (!isset($data['user']) || !isset($data['mdp']) || !isset($data['solde'])) {
    echo json_encode(["success" => false, "message" => "Paramètres manquants."]);
    exit();
}

$user = $data['user'];
$mdp = $data['mdp'];
$solde = (int) $data['solde']; // Convertir le solde en entier

try {
    // Récupérer les limites min et max de solde
    $stmt = $pdo->prepare("SELECT limite_min, limite_max FROM parametres LIMIT 1");
    $stmt->execute();
    $limits = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($limits) {
        $soldeMin = (int) $limits['limite_min'];
        $soldeMax = (int) $limits['limite_max'];
    } else {
        $soldeMin = 10;
        $soldeMax = 1000;
    }

    // Vérifier si le solde est valide
    if ($solde < $soldeMin || $solde > $soldeMax) {
        echo json_encode(["success" => false, "message" => "Le solde doit être compris entre $soldeMin et $soldeMax."]);
        exit();
    }

    // Vérifier si l'utilisateur existe déjà
    $stmt = $pdo->prepare("SELECT * FROM users WHERE user = :user");
    $stmt->execute(['user' => $user]);
    $existingUser = $stmt->fetch();

    if ($existingUser) {
        echo json_encode(["success" => false, "message" => "Le nom d'utilisateur existe déjà."]);
        exit();
    }

    // Insérer le nouvel utilisateur avec le solde choisi
    $stmt = $pdo->prepare("INSERT INTO users (user, mdp, solde) VALUES (:user, :mdp, :solde)");
    $stmt->execute([
        'user' => $user,
        'mdp' => $mdp,
        'solde' => $solde
    ]);

    echo json_encode(["success" => true, "message" => "Compte créé avec succès !"]);
} catch (PDOException $e) {
    echo json_encode(["success" => false, "message" => "Erreur SQL : " . $e->getMessage()]);
}
?>
