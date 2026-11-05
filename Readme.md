# API Backend - Guide Michelin 
## Projet M2 Web Services - Evrard Baptiste

L'application est développée en Spring Boot et utilise les technologies suivantes :

- Java 21
- Spring Boot 3.4.1
- Spring Security, OAuth2, JWT (sécurité)
- H2 (BDD en mémoire)
- OpenAPI (Swagger)
- Apache Lucene (recherche textuelle)
- JUnit 5 et Mockito (tests unitaires)

Mon IntelliJ me proposait d'utiliser la fonctionnalité String Templates, cependant j'ai du ajouter des plugins dans le pom.xml pour que le projet compile bien.

##Lancement de l'application et des tests
Lancer l'application : ./mvnw spring-boot:run
Lancer les tests unitaires : ./mvnw test (24 tests unitaires couvrant les services)

Une fois l'application lancée, l'interface Swagger UI est accessible ici : `http://localhost:8080/swagger-ui.html`

## Les Restaurant et Évaluations

Dans le contexte du sujet, il y a les définitions d'un restaurant et d'une évaluation.

Pour représenter ces définitions, j'ai donc créé un package pour les DTO.

On a donc une classe pour RestaurantDTO et une classe pour EvaluationDTO.

Dans ces classes, j'ai défini les champs comme ce qui est convenu dans le sujet.
Et j'ai également défini les méthodes pour pouvoir transformer une entité Restaurant en RestaurantDTO et vice-versa. De même pour les évaluations.

Je n'ai pas mis d'annotation pour les champs dans ces deux classes car j'ai implémenté des DTO custom :
- CreateRestaurantDTO
- CreateResponseRestaurantDTO
- UpdateRestaurantDTO
- UpdateResponseRestaurantDTO
- CreateEvaluationDTO
- CreateResponseEvaluationDTO
- UpdateEvaluationDTO
- UpdateResponseEvaluationDTO

Dans les DTOs de création (CreateRestaurantDTO et CreateEvaluationDTO), j'ai mis les champs qui sont nécessaires pour créer un restaurant ou une évaluation. 
De plus, c'est donc ici que j'ai ajouté des annotations de validation. Pour les photos et l'image, j'ai décidé de ne pas les ajouter à la création. Donc les champs de l'image et de la liste de photos peuvent être vide à la création. De même pour la liste d'évaluation d'un restaurant qui est vide à la création. 

J'ai donc également des DTO pour les réponses après création (CreateResponseRestaurantDTO et CreateResponseEvaluationDTO) qui renvoie l'entité avec son id et d'autres informations comme l'ensemble des évaluations pour un restaurant ou l'image et la liste de photos pour un restaurant. 

Finallement, il y a des DTO pour les mises à jour (UpdateRestaurantDTO et UpdateEvaluationDTO) qui sont les mêmes que les DTO de création et qui possèdent également les annotations de validation. 
Des DTO de réponse sont renvoyés après les mises à jour.

Pour représenter les Restaurants et Evaluations dans la BDD, il faut des entités.
On a donc une classe RestaurantEntity et une classe EvaluationEntity.
Des vérifications sont également mises en place pour les données des restaurants et des évaluations dans les entités pour respecter le sujet. 

Pour représenter les photos, j'ai fait une table Plats_Photos qui est une table d'association entre les évaluation et les photos et qui contient donc l'id de l'évaluation et le nom de la photo. 

J'ai également fait les repositories des Restaurants et des Évaluations. Ces repositories sont des interfaces qui étendent JpaRepository. J'ai ajouté certaines méthodes dans ces Repositories pour pour effectuer des recherches. Comme par exmple la méthode qui permet de trouver une évaluation par son id et son subEvaluateur :
- Optional<EvaluationEntity> findByIdAndSubEvaluateur(Integer id, String subEvaluateur);

Ensuite j'ai fait les services des Restaurants et des Évaluations. Dans ces services on retrouve les opérations de gestion des restaurants et des évaluations. 
Par exemple pour les restaurants on retrouve les méthodes :
- createRestaurant
- updateRestaurant
Et pareil pour les évaluations, on retrouve les méthodes :
- createEvaluation
- updateEvaluation

Pour certaines fonctions d'EvaluationService, j'avais au début deux méthodes qui faisaient la même chose mais une pour l'admin et une pour le user. J'ai donc tous regroupé en une seule fonction mais qui prend un argument qui dit si l'utilisateur est un admin ou non. Car je n'avais pas encore implémenté le fait de récupérer le rôle de l'utilisateur connecté.

Pour la route qui permet de retourner les évaluations de l'utilisateur connecté, j'ai utilisé cette fois-ci le sub, qui est l'identifiant unique au lieu du name comme lors de l'exercice en cours. Cela empèche donc les homonymes. 

Pour ce qui est de la moyenne, elle est calculée et retournée dans les routes demandées. Et lors de la création, la moyenne vaut -1.

Puis nous avons les controllers pour les Restaurants et les Évaluations. C'est ici que nous avons les routes. 
Pour certaines routes, j'ai fait le choix de ne pas la sécuriser avec l'authentification du role. Comme par exemple les routes pour GET des restaurants, car je me dis que les user peuvent aussi avoir besoin de ces informations. 

## Les images
Nous avons un controller pour les fichiers, FichierController. Comme dans le cours, il permet d'uploader des fichiers sur S3 et de les récupérer cela sans passer par le serveur.
J'ai utilisé les deux buckets mis à notre disposition.
Avec une route PUT on récupère une url pour pouvoir uploader un fichier. Cette url est valable 10 minutes.
Puis on utilise cette url pour faire un PUT avec cette fois-ci l'image dans le body, ce qui va upload l'image sur le S3.
Avec une route GET on récupère l'url pour pouvoir récupérer le fichier et donc l'image d'un plat par exemple.

## Sécurité
Au début je ne mettais que des PreAuthorise("isAuthenticated()") car je n'avais pas encore le concept de mapper le JWT. Puis j'ai implémenter la classe MapperRole qui va chercher les rôles au travers du JWT. J'ai regardé comment était hiérarchisé le JWT grace à jwt.io pour implémenter cette classe. J'ai ensuite fait une classe SecurityConfig qui va gérer la sécurité à l'aide de oauth2 et c'est ici que j'appelle ma méthode pour mapper le JWT.

Ensuite j'ai suivi ce que dit le sujet et j'ai placé des PreAuthorize("hasAuthority("ADMIN")) pour restreindre l'accès à certaines routes.

## Swagger
Par la même occasion, un swagger était demandé. J'avais au départ suivi un tuto sur le web mais cette version était obsolète. J'ai donc trouver une autre manière avec OpenApi et j'ai pu l'implémenter. J'ai donc la classe SwaggerConfig
qui permet d'avoir le swagger. Or je me suis vite rendu compte qu'à cause de l'authentification, le swagger ne permettait plus de tester les routes. J'ai donc ajouter une autre méthode qui permet d'activer un bouton 'Authorize' dans le swagger afin de pouvoir y mettre le token JWT et de pouvoir tester les routes. 

## Index Lucene 
Dans le sujet il y a la fonctionnalité de rechercher une évaluation par mot clé. Comme ce qu'on a vu en cours, j'ai implémenté un IndexService qui permet de gérer l'index lucene. Mais contrairement au cours, cette fois si on peut supprimer ou modifier une évaluation, donc il a fallut implémenter des méthodes qui permettent de mettre à jour ou de supprimer des évaluations dans l'index. 
Pour rechercher une évaluation avec un mot clé il y a la route GET /evaluations/recherche qui prend en paramètre le mot clé recherché et qui renvoie les évaluations qui correspondent au mot clé. Ce mot clé est cherché dans le commentaire dans les évaluations. On pourrait par la suite faire aussi une recherche par note ou par évaluateur. 


## Les exceptions
Comme vu en cours, j'ai impléménté un CustomExceptionHandler.
J'ai également implémenté une classe ErreurExceptionDTO qui permet d'avoir un code et un message lors d'une exception.
Pour cela j'ai 3 classes pour les exceptions :
- BadRequestException
- NotFoundException
- ForbiddenException

J'utilise ces classes quand je dois par exemple trouver une évaluation par son id et son subEvaluateur et que celui ci n'est pas trouvé, alors je lance une ForbiddenException.
J'utilise aussi les classes d'exception de Spring pour la validation des @Valid dans les controllers. J'affiche ces erreurs sous forme de code et message.


## Les tests
J'ai implémenté des tests pour les services des restaurants et des évaluations avec JUnit 5 et Mockito. J'ai essayé de couvrir un maximum de scénarios avec ces tests. 


## Architecture du projet 

```text
src/main/java/M2fst/demo/
├── Config/         # Configurations (Sécurité, Swagger, Mapper JWT)
├── controller/     # Contrôleurs REST (Restaurant, Evaluation, Fichier, Gestion d'erreurs)
├── dto/            # Data Transfer Objects (Requêtes / Réponses typées)
├── entity/         # Entités JPA (RestaurantEntity, EvaluationEntity)
├── exception/      # Exceptions métier personnalisées (BadRequest, NotFound, Forbidden)
├── repository/     # Repositories Spring Data JPA & S3Repository
└── service/        # Logique métier (Restaurant, Evaluation, Indexation Lucene)
```









