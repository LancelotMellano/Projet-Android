<?php
header("Content-Type: application/json");

// Configuration de la base de données
$host = "localhost"; // Adresse du serveur MySQL
$port = 3306;        // Port MySQL
$username = "root";  // Nom d'utilisateur MySQL
$password = "root";  // Mot de passe MySQL
$database = "roulette"; // Nom de votre base de données
$socket = "/run/mysqld/mysqld.sock"; // Chemin du socket MySQL

try {
    // Création de la connexion PDO avec le socket MySQL
    $pdo = new PDO("mysql:unix_socket=$socket;dbname=$database;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    // Gestion des erreurs de connexion
    echo json_encode([
        "success" => false,
        "message" => "Erreur de connexion à la base de données : " . $e->getMessage()
    ]);
    exit();
}
?>
