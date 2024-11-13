# Documentation des Changements sur la GitHub Action

[Voir le fichier de configuration GitHub Action](.github/workflows/test.yml)

## Description des Changements

La GitHub Action a été modifiée pour exécuter des builds parallèles avec cinq configurations différentes de la JVM, en utilisant une matrice (`matrix`) dans le workflow. Chaque configuration utilise un flag spécifique de la JVM afin de tester l’application dans diverses conditions. Cette approche permet de collecter des données sur la couverture de code pour chaque configuration, afin de mieux comprendre comment différents paramètres JVM affectent les tests, la performance et l’observabilité de l’application.

### Structure de la Matrice

La matrice de configurations (`matrix.jvm_opts`) définit cinq configurations, chacune ayant un flag JVM unique. Pour chaque build, un taux de couverture est calculé et enregistré, permettant une analyse approfondie des effets de chaque flag sur la couverture de code et la stabilité des tests.

### Liste des Flags Utilisés et Justification (Si vous lisez vraiment cette partie)


1. **`-Xmx512m`** :
   - **Description** : Limite la taille maximale de la heap (mémoire allouée aux objets Java) à 512 Mo.
   - **Impact sur la performance** : En limitant la mémoire, on peut évaluer comment l’application se comporte sous contrainte de mémoire. Cela aide à identifier les problèmes potentiels de fuite de mémoire et à tester l’efficacité du garbage collector.
   - **Impact sur la qualité** : Permet de vérifier si les tests passent avec des ressources limitées, par exemple, un Windows Xp, ce qui peut révéler des faiblesses ou inefficacités dans le code.
   - **Observabilité** : Donne un aperçu de l'utilisation de la mémoire et peut aider à identifier des scénarios de surcharge de mémoire.

2. **`-XX:+UnlockDiagnosticVMOptions`** :
   - **Description** : Active les options de diagnostic avancées pour déboguer et affiner la JVM.
   - **Impact sur la performance** : Permet de tester avec plus de données de diagnostic activées, ce qui peut affecter légèrement les performances en raison de la collecte de données supplémentaires.
   - **Impact sur la qualité** : Offre des informations précieuses pour diagnostiquer les erreurs potentielles, ce qui aide à améliorer la fiabilité du code.
   - **Observabilité** : Améliore l’observabilité en fournissant des informations de diagnostic détaillées, facilitant le suivi de l’état et des problèmes potentiels de la JVM.

3. **`-XX:+PrintGCDetails`** :
   - **Description** : Affiche des détails sur la collecte des déchets pour le garbage collector.
   - **Impact sur la performance** : Aide à identifier les cycles de garbage collection et à évaluer si le GC impacte les performances de l’application.
   - **Impact sur la qualité** : Permet de repérer d’éventuelles anomalies dans la gestion de la mémoire en affichant les cycles de collecte.
   - **Observabilité** : Donne des informations précieuses sur les cycles de GC, permettant une meilleure compréhension de la gestion de la mémoire par la JVM, si le lecteur arrive à comprendre l'information retourné.

4. **`-XX:+UseCompressedOops`** :
   - **Description** : Active la compression des pointeurs d’objets (Ordinary Object Pointers) pour optimiser la consommation de mémoire dans les environnements 64 bits.
   - **Impact sur la performance** : Améliore l’efficacité de l’utilisation de la mémoire, ce qui peut optimiser les performances des applications consommant beaucoup d’objets.
   - **Impact sur la qualité** : Assure que la compression des pointeurs ne cause pas de problème de compatibilité ou de corruption de données.
   - **Observabilité** : Non directement observable, mais contribue indirectement à la réduction de l'empreinte mémoire.

5. **`-XX:MaxMetaspaceSize=256m`** :
   - **Description** : Limite la taille maximale de la Metaspace, qui stocke les métadonnées des classes chargées par la JVM.
   - **Impact sur la performance** : En contrôlant l’espace alloué pour la Metaspace, ce flag peut prévenir les fuites de mémoire liées à un chargement excessif de classes.
   - **Impact sur la qualité** : Assure que l’application fonctionne correctement avec une Metaspace limitée, ce qui peut aider à identifier des problèmes de gestion de classe.
   - **Observabilité** : Aide à comprendre l'usage de la Metaspace et à identifier les problèmes de chargement de classe si la limite est atteinte.

