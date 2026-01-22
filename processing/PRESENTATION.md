# 🎓 Guide de Présentation - Système de Traitement de Réclamations d'Assurance

## 📚 C'est quoi ce projet ?

Ce projet simule le système informatique d'une **compagnie d'assurance** qui traite les réclamations des clients.

### Scénario concret :
> Imagine : Tu as un accident de voiture. Tu appelles ton assurance pour demander un remboursement.
> L'assurance doit :
> 1. Vérifier ton identité (c'est bien toi ?)
> 2. Vérifier ta police d'assurance (tu es bien couvert ?)
> 3. Vérifier que ce n'est pas une fraude
> 4. Calculer combien te rembourser
> 5. Te payer

**Ce projet automatise tout ça avec différentes technologies d'API !**

---

## 🔧 Les 4 types d'API utilisées

### 1. REST (le plus courant)
- **C'est quoi ?** : Des URLs simples comme sur un site web
- **Exemple** : `GET /api/claims` = "donne-moi toutes les réclamations"
- **Utilisé pour** : Soumission de réclamations, notifications, paiements

### 2. SOAP (ancien mais encore utilisé dans les banques/assurances)
- **C'est quoi ?** : Messages XML très structurés
- **Exemple** : Validation de police d'assurance
- **Utilisé pour** : Communication avec systèmes legacy (anciens systèmes)

### 3. gRPC (moderne et rapide)
- **C'est quoi ?** : Communication binaire ultra-rapide entre serveurs
- **Exemple** : Détection de fraude
- **Utilisé pour** : Services qui ont besoin de haute performance

### 4. GraphQL (flexible)
- **C'est quoi ?** : Tu demandes exactement les données que tu veux
- **Exemple** : Tracking de réclamation avec historique
- **Utilisé pour** : Interfaces où on veut choisir les champs retournés

---

## 🔄 C'est quoi Flowable/BPMN ?

**Flowable** = Un moteur de workflow (comme un chef d'orchestre)

**BPMN** = Business Process Model and Notation (un langage pour dessiner des processus)

### En gros :
Au lieu d'écrire du code comme :
```java
if (identityVerified) {
    if (policyValid) {
        if (fraudRisk != HIGH) {
            // etc...
        }
    }
}
```

On dessine un schéma avec des boîtes et des flèches, et Flowable l'exécute automatiquement !

### Les "Gates" (portes de décision) :

| Type | Symbole | Description | Exemple |
|------|---------|-------------|---------|
| **XOR** | ◇ | Choix exclusif (OU) | "Identité vérifiée ? → Oui OU Non" |
| **AND** | + | Parallèle (ET) | "Faire validation police ET détection fraude en même temps" |
| **OR** | ○ | Conditionnel | "Si montant > 5000€ → revue docs, Si risque moyen → expert" |

---

## 🏗️ Les "Pools" (participants)

Un Pool = Un acteur dans le processus

| Pool | Qui c'est ? | Ce qu'il fait |
|------|-------------|---------------|
| **Customer** | Le client | Soumet sa réclamation, reçoit la réponse |
| **Insurance System** | Notre système | Traite la réclamation |
| **Payment Partner** | Partenaire bancaire | Effectue le paiement |

---

# 🎬 PLAN DE PRÉSENTATION DÉTAILLÉ

## ⏱️ Durée totale : ~25 minutes

---

## 📍 Étape 0 : Démarrer l'application (2 min)

### Ce qu'il faut faire :
```bash
cd processing
mvn spring-boot:run
```

### Ce qu'il faut dire :
> "Je lance l'application Spring Boot. Elle démarre sur le port 8081 avec un serveur gRPC sur le port 9090."

### Attendre de voir :
```
Started ProcessingApplication in X seconds
```

---

## 📍 Étape 1 : Montrer la documentation des APIs (3 min)

### 1.1 Swagger UI (REST)
- **Ouvrir** : http://localhost:8081/swagger-ui.html

### Ce qu'il faut dire :
> "Voici Swagger UI, c'est la documentation interactive de notre API REST. 
> Elle est générée automatiquement grâce à OpenAPI/Swagger.
> On peut voir tous les endpoints disponibles et les tester directement."

### 1.2 WSDL (SOAP)
- **Ouvrir** : http://localhost:8081/ws/policies.wsdl

### Ce qu'il faut dire :
> "Et voici le WSDL de notre service SOAP pour la validation de police.
> C'est un contrat XML qui décrit les opérations disponibles."

### 1.3 GraphiQL (GraphQL)
- **Ouvrir** : http://localhost:8081/graphiql

### Ce qu'il faut dire :
> "Enfin, GraphiQL pour tester nos requêtes GraphQL.
> On peut explorer le schéma sur la droite et écrire des requêtes sur la gauche."

---

## 📍 Étape 2 : Voir les données de test (2 min)

### Dans Swagger UI :

1. **Cliquer sur** : `Claims` → `GET /api/claims` → `Try it out` → `Execute`

### Ce qu'il faut dire :
> "On a 3 réclamations pré-chargées en base de données pour les tests.
> On voit leurs IDs, montants, types (AUTO, HEALTH), et statuts."

2. **Cliquer sur** : `Customers` → `GET /api/customers` → `Try it out` → `Execute`

### Ce qu'il faut dire :
> "Et 4 clients, dont certains sont vérifiés et d'autres non.
> Ces données sont chargées automatiquement au démarrage depuis le fichier data.sql."

---

## 📍 Étape 3 : Démarrer un Workflow Flowable (5 min)

### 3.1 Lancer le workflow
Dans Swagger UI :
1. Chercher **"Workflow Management"**
2. Cliquer sur `POST /api/workflow/start/{claimId}`
3. Cliquer `Try it out`
4. Entrer : `12345678-1234-1234-1234-123456789012`
5. Cliquer `Execute`

### Ce qu'il faut dire :
> "Je démarre le workflow Flowable pour traiter cette réclamation.
> Le moteur va exécuter automatiquement toutes les étapes définies dans notre fichier BPMN."

### 3.2 Regarder les logs
**Dans le terminal**, vous verrez :
```
=== IDENTITY VERIFICATION (REST) ===
Processing claim: 12345678-1234-1234-1234-123456789012
Identity verified: true

=== POLICY VALIDATION (SOAP) ===
Policy Number: POL-AUTO-001
Policy valid: false - Policy is inactive or expired

=== CUSTOMER NOTIFICATION (REST) ===
Notification sent: Your claim has been rejected.
```

### Ce qu'il faut dire :
> "On voit dans les logs que le workflow s'exécute :
> 1. D'abord la vérification d'identité via REST → OK
> 2. Puis la validation de police via SOAP → Échouée (police expirée)
> 3. Donc le client est notifié du rejet.
> 
> Le workflow s'est arrêté car la police n'est pas valide.
> C'est le XOR Gateway qui a dirigé vers le chemin de rejet."

### 3.3 Vérifier le statut
Dans Swagger UI :
1. `GET /api/workflow/status/{claimId}`
2. Entrer le même ID
3. `Execute`

### Ce qu'il faut dire :
> "Le statut montre COMPLETED. Le processus s'est terminé (par un rejet dans ce cas)."

---

## 📍 Étape 4 : Démontrer chaque API individuellement (8 min)

### 4.1 REST - Vérification d'identité

Dans Swagger UI → `Identity Verification` → `POST /api/identity/verify`

Body :
```json
{
  "customerId": "11111111-1111-1111-1111-111111111111",
  "documentType": "PASSPORT",
  "documentNumber": "AB123456"
}
```

### Ce qu'il faut dire :
> "Voici un appel REST classique. On envoie un JSON, on reçoit un JSON.
> C'est le format le plus répandu aujourd'hui pour les APIs web."

---

### 4.2 SOAP - Validation de police

**Option A : Via curl dans le terminal**
```bash
curl -X POST "http://localhost:8081/ws" \
  -H "Content-Type: text/xml" \
  -d '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" 
       xmlns:pol="http://cyterafle.com/claim/soap/policy">
    <soapenv:Body>
      <pol:validatePolicyRequest>
        <pol:policyNumber>POL-HEALTH-001</pol:policyNumber>
        <pol:customerId>22222222-2222-2222-2222-222222222222</pol:customerId>
        <pol:claimType>HEALTH</pol:claimType>
        <pol:claimedAmount>1000</pol:claimedAmount>
      </pol:validatePolicyRequest>
    </soapenv:Body>
  </soapenv:Envelope>'
```

### Ce qu'il faut dire :
> "SOAP utilise des messages XML structurés avec des namespaces.
> C'est plus verbeux que REST, mais très strict et validé par le schéma XSD.
> On l'utilise souvent pour communiquer avec des systèmes bancaires ou legacy."

---

### 4.3 gRPC - Détection de fraude

Dans Swagger UI → `Fraud Detection` → `POST /api/fraud/analyze`

Body :
```json
{
  "claimId": "12345678-1234-1234-1234-123456789012",
  "customerId": "11111111-1111-1111-1111-111111111111",
  "claimedAmount": 5000,
  "claimType": "AUTO"
}
```

### Ce qu'il faut dire :
> "Ici on utilise un wrapper REST autour de notre service gRPC.
> gRPC utilise Protocol Buffers (binaire) et HTTP/2, c'est beaucoup plus rapide que REST.
> Le fichier .proto définit le contrat du service.
> On l'utilise pour les services internes à haute performance."

---

### 4.4 GraphQL - Tracking de réclamation

Dans **http://localhost:8081/graphiql**, exécuter :

```graphql
query {
  claim(id: "12345678-1234-1234-1234-123456789012") {
    id
    policyNumber
    claimType
    claimedAmount
    status
    customer {
      name
      surname
      mail
    }
    history {
      action
      details
      timestamp
    }
  }
}
```

### Ce qu'il faut dire :
> "Avec GraphQL, on demande exactement les champs qu'on veut.
> Contrairement à REST où on reçoit toujours les mêmes données,
> ici on peut demander seulement le nom du client, ou tout son historique.
> C'est très flexible pour les applications frontend."

---

## 📍 Étape 5 : Expliquer l'architecture BPMN (3 min)

### Ouvrir le fichier BPMN
Dans VS Code : `src/main/resources/processes/insuranceClaimProcess.bpmn20.xml`

### Ce qu'il faut dire :
> "Voici notre fichier BPMN. Il définit tout le processus métier.
> 
> **Les Service Tasks** appellent nos différents services :
> - identityVerificationDelegate → REST
> - policyValidationDelegate → SOAP
> - fraudDetectionDelegate → gRPC
> 
> **Les Gateways** gèrent les décisions :
> - XOR (exclusive) : un seul chemin possible
> - AND (parallel) : plusieurs chemins en même temps
> - OR (inclusive) : un ou plusieurs chemins selon conditions
> 
> **L'avantage** : On peut modifier le processus sans toucher au code Java !"

---

## 📍 Étape 6 : Lancer les tests (2 min)

### Dans un nouveau terminal :
```bash
cd processing
mvn test
```

### Ce qu'il faut dire :
> "On a 28 tests unitaires et d'intégration qui vérifient :
> - Les endpoints REST
> - Le service SOAP
> - Les requêtes GraphQL
> - Les repositories JPA
> - Le workflow Flowable
> 
> Tous les tests passent !"

---

## 📍 Étape 7 : Conclusion (1 min)

### Ce qu'il faut dire :
> "En résumé, ce projet démontre :
> 
> ✅ **4 types d'API** : REST, SOAP, gRPC, GraphQL
> ✅ **11 services** interconnectés
> ✅ **Workflow BPMN** avec Flowable
> ✅ **3 types de Gateways** : XOR, AND, OR
> ✅ **3 Pools** : Customer, Insurance System, Payment Partner
> ✅ **Documentation** : Swagger, WSDL, GraphQL Schema
> ✅ **Tests** : 28 tests automatisés
> 
> Merci pour votre attention !"

---

## 📌 Aide-mémoire des URLs

| Quoi | URL |
|------|-----|
| Swagger UI | http://localhost:8081/swagger-ui.html |
| GraphiQL | http://localhost:8081/graphiql |
| WSDL SOAP | http://localhost:8081/ws/policies.wsdl |
| H2 Console | http://localhost:8081/h2-console |

## 📌 ClaimIDs de test

| ClaimId | Description |
|---------|-------------|
| `12345678-1234-1234-1234-123456789012` | John Doe, AUTO, 5000€ |
| `23456789-2345-2345-2345-234567890123` | Jane Smith, HEALTH, 2500€ |
| `34567890-3456-3456-3456-345678901234` | Pierre Martin, AUTO, 15000€ |

---

## ❓ Questions potentielles du jury

### "Pourquoi utiliser plusieurs types d'API ?"
> "Chaque type a ses avantages :
> - REST : Simple, universel, bon pour le web
> - SOAP : Standards entreprise, transactions sécurisées
> - gRPC : Performance, communication entre microservices
> - GraphQL : Flexibilité pour les clients frontend"

### "C'est quoi l'avantage de Flowable ?"
> "Au lieu de coder en dur le processus, on le dessine.
> On peut le modifier sans recompiler, et Flowable gère automatiquement
> la persistance, les reprises après erreur, et le monitoring."

### "Comment fonctionne le AND Gateway ?"
> "Il lance plusieurs tâches en parallèle et attend qu'elles soient toutes terminées.
> Dans notre cas, validation de police ET détection de fraude en même temps."
