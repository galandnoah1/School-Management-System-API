# School Management System

Backend REST pour la gestion complète d'un établissement scolaire : classes, élèves, enseignants, matières, notes, bulletins, frais de scolarité et suivi des paiements.

## Sommaire

- [Stack technique](#stack-technique)
- [Prérequis](#prérequis)
- [Configuration](#configuration)
- [Lancement du projet](#lancement-du-projet)
- [Documentation API](#documentation-api)
- [Modules fonctionnels](#modules-fonctionnels)
- [Règles métier](#règles-métier)
- [Structure du projet](#structure-du-projet)
- [Gestion des erreurs](#gestion-des-erreurs)

## Stack technique

| Composant | Technologie |
|---|---|
| Langage | Java 17 |
| Framework | Spring Boot 3|
| Persistance | Spring Data JPA / Hibernate |
| Base de données | MySQL |
| Documentation API | springdoc-openapi (Swagger UI) |
| Génération de code | Lombok |
| Build | Maven |

## Prérequis

- JDK 17 ou supérieur
- Maven 3.8+
- MySQL 8+

## Configuration

Renseigner les propriétés de connexion à la base de données dans `src/main/resources/application.properties` (ou `application.yml`) :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/school_management_system?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=<votre_utilisateur>
spring.datasource.password=<votre_mot_de_passe>

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> `allowPublicKeyRetrieval=true` et `useSSL=false` sont recommandés pour un environnement de développement local avec MySQL 8+ (authentification `caching_sha2_password`). À ajuster pour un environnement de production.

## Lancement du projet

```bash
mvn clean install
mvn spring-boot:run
```

L'application démarre par défaut sur `http://localhost:8084`.

## Documentation API

La documentation interactive Swagger est disponible une fois l'application lancée :

```
http://localhost:8084/swagger-ui.html
```

## Modules fonctionnels

### Gestion académique

- **Classroom** — gestion des classes (section, niveau, spécialité, LV2, répartition). Le nom de la classe est généré automatiquement à partir de ces attributs.
- **Student** — gestion des élèves, avec génération automatique d'un matricule unique (format `MD` + année + numéro aléatoire).
- **Subject** / **SubjectByClassroom** — gestion des matières et de leur affectation à une classe avec un coefficient.
- **Teacher** / **Affectation** — gestion des enseignants et de leurs affectations (classe + matière).

### Évaluations et bulletins

- **Note** — saisie des notes des élèves par évaluation (CC1 à CC6), avec enregistrement individuel ou en masse (`bulk`).
- **NoteLine** — ligne de matière au sein d'un bulletin, alimentée automatiquement à la saisie des notes.
- **ReportCard** — bulletin trimestriel d'un élève. La génération calcule automatiquement les moyennes par matière, la moyenne trimestrielle, le classement et les statistiques de classe.
- **PrintHistory** — suivi des générations, impressions et téléchargements de bulletins.

### Gestion financière

- **SchoolFee** — grilles de frais de scolarité (inscription, tranche 1, tranche 2), applicables à une ou plusieurs classes.
- **Payment** — enregistrement des paiements des élèves, avec calcul automatique du statut (avance / complet) et de l'éligibilité à l'impression des bulletins.

## Règles métier

### Génération du nom de classe

Le nom d'une `Classroom` est composé automatiquement à partir de `level`, `speciality`, `lv2` et `repartition`, en ignorant les champs non renseignés.

### Génération du matricule élève

Format : `MD` + année courante + nombre aléatoire entre 1000 et 3000, avec vérification d'unicité.

### Saisie des notes et construction des bulletins

Chaque évaluation (`Evaluation`) détermine le trimestre et la position de la note dans la ligne de bulletin :

| Évaluation | Trimestre | Position |
|---|---|---|
| CC1 | T1 | note1 |
| CC2 | T1 | note2 |
| CC3 | T2 | note1 |
| CC4 | T2 | note2 |
| CC5 | T3 | note1 |
| CC6 | T3 | note2 |

L'enregistrement d'une note crée ou met à jour automatiquement le bulletin (`ReportCard`) et la ligne de matière (`NoteLine`) correspondants.

### Génération des bulletins

La génération se fait par classe et par trimestre. Pour chaque élève :

- `average1` / `average2` : moyennes pondérées par coefficient du premier et du second contrôle continu du trimestre
- `average` : moyenne des deux
- `appreciation` : Non acquis (< 10), En cours d'acquisition (< 14), Acquis (< 17), Excellent (≤ 20)
- `ranking` : classement dans la classe (rangs distincts, sans gestion d'ex æquo)
- `overallaverage`, `firstaverage`, `lastaverage` : moyenne de la classe, moyenne du premier et du dernier de la classe, répercutées sur le bulletin de chaque élève

Une régénération recalcule et écrase les valeurs existantes plutôt que de créer un doublon.

### Suivi des impressions et téléchargements

- Une génération de bulletins pour une classe/trimestre produit une seule entrée d'historique, dont le compteur est mis à jour (et non dupliqué) à chaque régénération.
- Chaque impression ou téléchargement d'un bulletin individuel incrémente un compteur associé à ce bulletin plutôt que de créer une nouvelle entrée à chaque fois.

### Frais de scolarité et paiements

- Une grille de frais (`SchoolFee`) définit les montants d'inscription et des deux tranches, ainsi que leurs délais.
- Une classe ne peut appartenir qu'à une seule grille de frais à la fois.
- L'inscription doit être payée intégralement en une seule fois.
- Les tranches acceptent des paiements partiels (avances), sans jamais dépasser le montant requis.
- Le statut de chaque paiement (`ADVANCE` / `COMPLETED`) est calculé automatiquement selon le cumul des paiements du même type.
- L'impression du bulletin du trimestre 1 nécessite l'inscription et la tranche 1 complètes. Les trimestres 2 et 3 nécessitent la tranche 2 complète.

## Structure du projet

```
src/main/java/com/truhoster/school_management_system/
├── classroom/
├── student/
├── subject/
├── teacher/
├── note/
├── reportcard/
├── printhistory/
├── schoolfee/
├── payment/
└── common/
    └── exception/
```

Chaque module suit la même organisation : `entity`, `dto` (`*Request` / `*Response`), `mapper`, `repository`, `service`, `controller`.

## Gestion des erreurs

Les erreurs sont centralisées dans un `GlobalExceptionHandler` :

| Exception | Code HTTP | Cas d'usage |
|---|---|---|
| `EntityNotFoundException` | 404 | Ressource introuvable |
| `MethodArgumentNotValidException` | 400 | Échec de validation des champs (`@Valid`) |
| `IllegalStateException` | 409 | Conflit métier (doublon, dépassement de montant, etc.) |
| `Exception` | 500 | Erreur interne inattendue |

Chaque réponse d'erreur contient un message explicite sous la forme `{"error": "..."}`.
