# Documentation des 10 nouveaux tests

## 1. constructorWithInsufficientWords()

### Emplacement

- **Nom de la méthode** : `WordEncoder(String wordFile)`
- **Classe** : `WordEncoder`
- **Fichier** : `src/main/java/org/cryptomator/ui/recoverykey/WordEncoder.java`

### Emplacement du Test

- **Classe de Test** : `WordEncoderTest`
- **Fichier de Test** : `src/test/java/org/cryptomator/ui/recoverykey/WordEncoderTest.java`

### Explication du Choix de cette Méthode

Le test `constructorWithInsufficientWords()` est essentiel pour vérifier que le constructeur de la classe `WordEncoder` réagit correctement lorsqu'un fichier de mots contenant un nombre insuffisant de mots est fourni. Cela garantit que l'application lève une exception appropriée (`IllegalArgumentException`) avec un message clair en cas de données insuffisantes, ce qui est crucial pour éviter des comportements imprévisibles dans le système et garantir que les fichiers de mots utilisés répondent aux exigences minimales.


## 2. testSearchError()

### Emplacement

- **Nom de la méthode** : `SearchError()`
- **Classe** : `ErrorController`
- **Fichier** : `src/main/java/org/cryptomator/ui/error/ErrorController.java`

### Emplacement du Test

- **Classe de Test** : `ErrorControllerTest`
- **Fichier de Test** : `src/test/java/org/cryptomator/ui/error/ErrorControllerTest.java`

### Explication du Choix de cette Méthode

La méthode searchError() est essentielle car elle permet à l'utilisateur de rechercher des solutions d'erreurs via un lien généré dynamiquement à partir du code d'erreur. Tester cette méthode garantit que le formatage de l'URL est correct et que l'application ouvre correctement le navigateur pour la recherche. Cela assure que la recherche d'erreurs fonctionne sans problème et que l'utilisateur peut accéder aux solutions pertinentes.


## 3. testReportError()

### Emplacement

- **Nom de la méthode** : `reportError()`
- **Classe** : `ErrorController`
- **Fichier** : `src/main/java/org/cryptomator/ui/error/ErrorController.java`

### Emplacement du Test

- **Classe de Test** : `ErrorControllerTest`
- **Fichier de Test** : `src/test/java/org/cryptomator/ui/error/ErrorControllerTest.java`

### Explication du Choix de cette Méthode

La méthode reportError() est essentielle car elle génère un rapport d'erreur formaté et ouvre l'URL correspondante dans le navigateur pour permettre à l'utilisateur de signaler un problème. Tester cette méthode garantit que l'URL est correctement construite avec les informations du système d'exploitation et de la version de l'application, qu'elle contient les détails de l'erreur et respecte le format attendu pour l'intégration avec GitHub. Cela permet également de vérifier que l'intégration avec JavaFX via HostServices.showDocument() fonctionne correctement, en s'assurant que le navigateur s'ouvre bien avec l'URL correcte. Ce test confirme ainsi que la fonctionnalité de rapport d'erreurs est correctement implémentée, offrant une expérience utilisateur fluide lors du signalement de problèmes.


## 4. testNewMasterkeyFileWithPassphrase_InvalidRecoveryKey()

### Emplacement

- **Nom de la méthode** : `newMasterkeyFileWithPassphrase()`
- **Classe** : `RecoveryKeyFactory`
- **Fichier** : `src/main/java/org/cryptomator/ui/recoverykey/RecoveryKeyFactory.java`

### Emplacement du Test

- **Classe de Test** : `RecoveryKeyFactoryTest`
- **Fichier de Test** : `src/test/java/org/cryptomator/ui/recoverykey/RecoveryKeyFactoryTest.java`

### Explication du Choix de cette Méthode

Cette méthode est testée pour s'assurer qu'une exception est levée lorsqu'une recovery key invalide est fournie. Il est important de garantir que l'application réagit correctement aux entrées invalides et qu'elle gère les erreurs de manière sécurisée, sans compromettre la sécurité ou l'intégrité des données.


## 5. testNewMasterkeyFileWithPassphrase_PersistFails()

### Emplacement

- **Nom de la méthode** : `newMasterkeyFileWithPassphrase()`
- **Classe** : `RecoveryKeyFactory`
- **Fichier** : `src/main/java/org/cryptomator/ui/recoverykey/RecoveryKeyFactory.java`

### Emplacement du Test

- **Classe de Test** : `RecoveryKeyFactoryTest`
- **Fichier de Test** : `src/test/java/org/cryptomator/ui/recoverykey/RecoveryKeyFactoryTest.java`

### Explication du Choix de cette Méthode

Cette méthode est testée pour vérifier que l'application gère correctement les erreurs de persistance, comme un échec d'écriture dans le système de fichiers. Le test garantit qu'une exception de type `IOException` est levée lorsqu'une erreur survient lors de la sauvegarde d'une nouvelle master key, ce qui est crucial pour éviter la corruption de données ou l'inaccessibilité des données utilisateur.

