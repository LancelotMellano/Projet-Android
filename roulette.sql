-- phpMyAdmin SQL Dump
-- version 5.2.1deb1
-- https://www.phpmyadmin.net/
--
-- Hôte : localhost:3306
-- Généré le : jeu. 20 fév. 2025 à 10:20
-- Version du serveur : 10.11.6-MariaDB-0+deb12u1
-- Version de PHP : 8.2.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `roulette`
--

-- --------------------------------------------------------

--
-- Structure de la table `historique`
--

CREATE TABLE `historique` (
  `id` int(11) NOT NULL,
  `winning_number` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `historique`
--

INSERT INTO `historique` (`id`, `winning_number`) VALUES
(814, 23),
(815, 15),
(816, 9),
(817, 20),
(818, 0),
(819, 7),
(820, 2),
(821, 32),
(822, 34),
(823, 20);

-- --------------------------------------------------------

--
-- Structure de la table `messages`
--

CREATE TABLE `messages` (
  `id` int(11) NOT NULL,
  `categorie` varchar(50) NOT NULL,
  `cle_message` varchar(50) NOT NULL,
  `valeur_message` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `messages`
--

INSERT INTO `messages` (`id`, `categorie`, `cle_message`, `valeur_message`) VALUES
(37, 'erreurs', 'utilisateur_manquant', 'Erreur : informations utilisateur manquantes.'),
(38, 'erreurs', 'mise_a_jour_solde', 'Erreur de mise à jour du solde.'),
(39, 'erreurs', 'erreur_annulation_mise', 'Erreur : impossible d\'annuler cette mise.'),
(40, 'mises', 'selection_jeton', 'Veuillez d\'abord sélectionner un jeton.'),
(41, 'mises', 'solde_insuffisant_jeton', 'Solde insuffisant pour ce jeton.'),
(42, 'mises', 'solde_insuffisant_mise', 'Solde insuffisant pour cette mise.'),
(43, 'mises', 'aucune_mise_annuler', 'Aucune mise à annuler.'),
(44, 'mises', 'mises_ferment', 'Dépêchez-vous, les mises ferment bientôt !'),
(45, 'mises', 'mises_fermees', 'Les mises sont fermées.'),
(46, 'mises', 'resume_mises', 'Résumée des mises.'),
(47, 'jeu', 'phrase_jeux', 'Les jeux sont faits, rien ne va plus.'),
(48, 'jeu', 'phrase_attention', 'Vous avez atteint un solde insuffisant, veuillez ajouter de l\'argent à nouveau !');

-- --------------------------------------------------------

--
-- Structure de la table `parametres`
--

CREATE TABLE `parametres` (
  `id` int(11) NOT NULL,
  `limite_min` int(11) NOT NULL,
  `limite_max` int(11) NOT NULL,
  `regles` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `parametres`
--

INSERT INTO `parametres` (`id`, `limite_min`, `limite_max`, `regles`) VALUES
(4, 10, 1000, '1. Seules les personnes majeures sont autorisées à jouer. | 2. Le jeu doit rester un loisir, fixez-vous des limites pour éviter toute dépendance. | 3. Toute mise effectuée est définitive et ne peut être remboursée. | 4. Le respect du croupier et des autres joueurs est obligatoire. | 5. Toute suspicion de triche entraînera une enquête et une éventuelle suspension du compte.');

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `user` varchar(50) NOT NULL,
  `mdp` varchar(50) NOT NULL,
  `solde` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`id`, `user`, `mdp`, `solde`) VALUES
(1, 'rt', 'rt', 200),
(2, 'test', 'test', 160),
(4, 'gg', 'gg', 110),
(7, 'marge', 'marge', 410),
(8, 'Math06', 'or2', 500),
(9, 'anne', 'nanouche', 0),
(10, 'mat', 'brg', 200),
(11, 'clem', 'clem', 0),
(12, 'testuser', 'password123', 500),
(13, 'lm', 'lm', 70),
(14, 'titouan', 'titouan', 600),
(15, 'bb', 'bb', 518),
(16, 'toto', 'toto', 245),
(17, 'mathieu', 'brg', 20),
(19, 'mt', 'mt', 300);

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `historique`
--
ALTER TABLE `historique`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `parametres`
--
ALTER TABLE `parametres`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user` (`user`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `historique`
--
ALTER TABLE `historique`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=824;

--
-- AUTO_INCREMENT pour la table `messages`
--
ALTER TABLE `messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=49;

--
-- AUTO_INCREMENT pour la table `parametres`
--
ALTER TABLE `parametres`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
