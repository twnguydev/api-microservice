# Calendar Service

## Description

Le **Calendar Service** est une application Spring Boot conçue pour gérer les événements d'un calendrier, permettant aux utilisateurs de créer, visualiser et gérer des événements, ainsi que d'ajouter des participants. Ce service fait partie d'une suite de microservices interconnectés pour une application de gestion d'événements.

## Fonctionnalités

- **Gestion des événements** :
  - Création, mise à jour et suppression d'événements
  - Validation des dates et des heures pour les événements
- **Participants** :
  - Ajout et gestion des participants aux événements
  - Validation des utilisateurs participants
- **Utilisateurs** :
  - Enregistrement et gestion des utilisateurs
  - Authentification et validation des tokens
- **API RESTful** :
  - Endpoints pour interagir avec le service via des requêtes HTTP
  - Utilisation de JSON pour les échanges de données

## Technologies utilisées

- **Java 17** : Langage de programmation principal
- **Spring Boot** : Framework pour créer des applications Java
  - **Spring Data JPA** : Pour l'interaction avec la base de données
  - **Spring Web** : Pour la création d'API RESTful
  - **Spring Security** : Pour l'authentification et l'autorisation
- **Base de données MySQL**
- **Maven** : Outil de gestion de projet et de dépendances

## Prérequis

- **Java 17** ou une version supérieure
- **Maven** pour la gestion du projet

## Installation

1. Clonez le repository :
   ```sh
   git clone https://github.com/twnguydev/api-microservice.git
   cd api-microservice
   ```

2. Compilez et packagez l'application :
    ```sh
    mvn clean install
    ```

3. Démarrez l'application :

    ```sh
    mvn spring-boot:run
    ```

## Utilisation

L'application propose plusieurs endpoints pour gérer les événements, les utilisateurs et les participants. Voici quelques exemples :

1. Créer un utilisateur

Retourne un objet `user` si la création a réussie, sinon, renvoie une erreur si :
- `email` n'est pas au bon format
- `firstname` ou `lastname` n'est pas au bon format

    ```json
    POST /api/auth/token
    Content-Type: application/json

    {
        "firstname": "John",
        "lastname": "Doe",
        "email": "john.doe@example.com"
    }
    ```

2. Obtenir un token d'authentification

Retourne un token d'authentification si l'utilisateur existe, sinon, renvoie une erreur si :
- `email` n'est lié à aucun utilisateur enregistré

    ```json
    POST /api/auth/token
    Content-Type: application/json

    {
        "email": "john.doe@example.com"
    }
    ```

3. Créer un événement

Retourne un objet `event` si la création a réussie, sinon, renvoie une erreur si :
- `participantIds` comporte des IDs n'existant pas parmis les utilisateurs
- `userId` a été aussi renseigné dans `participantIds`
- `startTime` est supérieur à `endTime`
- `startTime` et `endTime` ne sont pas au format **yyyy-MM-dd'T'HH:mm:ss**

    ```json
    POST /api/events
    Authorization: Bearer <token>
    Content-Type: application/json

    {
    "title": "Réunion hebdomadaire",
    "description": "Réunion pour discuter des progrès du projet",
    "startTime": "2024-06-01T10:00:00",
    "endTime": "2024-06-01T11:30:00",
    "location": "Salle de réunion A",
    "userId": 1,
    "groupName": "Équipe de projet",
    "participantIds": [2, 3]
    }
    ````

4. Lister tous les événements

Retourne un tableau d'objets `event`.

    GET /api/events
    Authorization: Bearer <token>
