<?php
require 'db_config.php';

$data = json_decode(file_get_contents("php://input"), true);
$user = $data['user'];

$stmt = $pdo->prepare("SELECT solde FROM users WHERE user = :user");
$stmt->execute(['user' => $user]);
$userData = $stmt->fetch();

if ($userData) {
    echo json_encode([
        'success' => true,
        'solde' => $userData['solde']
    ]);
} else {
    echo json_encode(['success' => false, 'message' => 'Utilisateur introuvable']);
}
?>
