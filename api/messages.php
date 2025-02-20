<?php
// Configuration de la base de données
$host = "localhost"; // Remplace si besoin
$dbname = "roulette";
$username = "root"; // Remplace si besoin
$password = "root"; // Remplace si besoin

// En-têtes pour l'API JSON
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *"); // Permet l'accès depuis n'importe où (modifier si nécessaire)

// Connexion à la base de données
try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Récupérer les paramètres du jeu
    $stmt = $pdo->query("SELECT * FROM parametres");
    $parametres = $stmt->fetch(PDO::FETCH_ASSOC);

    // Récupérer les messages
    $stmt = $pdo->query("SELECT * FROM messages");
    $messages = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $messages[$row['categorie']][$row['cle_message']] = $row['valeur_message'];
    }

    // Création du JSON final
    $data = [
        "parametres" => $parametres,
        "messages" => $messages
    ];

    echo json_encode($data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);

} catch (PDOException $e) {
    echo json_encode(["error" => "Erreur SQL : " . $e->getMessage()]);
}

?>
