# Insurance Claim Processing System

## Description
Système de traitement de réclamations d'assurance de bout en bout utilisant différentes technologies d'API :
- **REST** : Soumission de réclamations, vérification d'identité, éligibilité, documents, expert, compensation, paiement, notifications
- **SOAP** : Validation de police d'assurance
- **gRPC** : Détection de fraude
- **GraphQL** : Suivi des réclamations (Claim Tracking)

## Prérequis
- Java 17
- Maven 3.8+
- NodeJS

## Structure du Projet

```
processing/
├── src/main/java/com/cyterafle/salahsama/claim/processing/
│   ├── entity/              # Entités JPA
│   ├── repository/          # Repositories Spring Data
│   ├── rest/controller/     # Controllers REST
│   ├── soap/                # Service SOAP
│   ├── grpc/                # Service gRPC
│   ├── graphql/             # Controller GraphQL
│   └── orchestration/       # WebSocket pour suivre le workflow
├── src/main/resources/
│   ├── application.properties
│   ├── data.sql             # Données de test
│   ├── xsd/                 # Schémas XSD pour SOAP
│   ├── graphql/             # Schémas GraphQL
│   └── proto/               # Fichiers .proto pour gRPC
└── pom.xml
webapp/ => Front en ReactJS
```

## Lancement

```bash
cd processing
./mvnw spring-boot:run
cd ../webapp
npm install && npm start
```

Ou sous Windows :
```bash
cd processing
mvnw.cmd spring-boot:run
cd ..\webapp
npm install && npm start
```

## URLs Disponibles

| Service | URL | Description |
|---------|-----|-------------|
| Interface | http://localhost:3000 | Front en ReactJS pour l'exécution des endpoints |
| Swagger UI | http://localhost:8081/swagger-ui.html | Documentation REST |
| GraphiQL | http://localhost:8081/graphiql | Interface GraphQL |
| H2 Console | http://localhost:8081/h2-console | Base de données |
| SOAP WSDL | http://localhost:8081/ws/policies.wsdl | WSDL du service SOAP |
| gRPC | localhost:9090 | Port gRPC |

## APIs REST Disponibles

### Claims (Réclamations)
- `POST /api/claims` - Soumettre une réclamation
- `GET /api/claims` - Liste des réclamations
- `GET /api/claims/{id}` - Détails d'une réclamation
- `GET /api/claims/customer/{customerId}` - Réclamations d'un client

### Identity (Vérification d'identité)
- `POST /api/identity/verify` - Vérifier l'identité
- `POST /api/identity/verify-for-claim/{claimId}` - Vérifier pour une réclamation

### Eligibility (Éligibilité)
- `POST /api/eligibility/evaluate/{claimId}` - Évaluer l'éligibilité

### Documents
- `POST /api/documents/request/{claimId}` - Demander des documents
- `POST /api/documents/submit/{claimId}` - Soumettre des documents
- `POST /api/documents/validate/{claimId}` - Valider des documents

### Expert
- `POST /api/expert/assign/{claimId}` - Assigner à un expert
- `POST /api/expert/review/{claimId}` - Décision de l'expert

### Compensation
- `POST /api/compensation/calculate/{claimId}` - Calculer la compensation

### Payment
- `POST /api/payment/authorize/{claimId}` - Autoriser le paiement
- `POST /api/payment/complete/{claimId}` - Compléter le paiement

### Notifications
- `POST /api/notifications/send/{claimId}` - Envoyer une notification
- `GET /api/notifications/history/{claimId}` - Historique des notifications

### Fraud Detection (REST wrapper pour gRPC)
- `POST /api/fraud/analyze` - Analyser une réclamation
- `GET /api/fraud/customer-risk/{customerId}` - Score de risque client

## Service SOAP - Policy Validation

Endpoint : `http://localhost:8081/ws`

Exemple de requête :
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                  xmlns:pol="http://cyterafle.com/claim/soap/policy">
   <soapenv:Header/>
   <soapenv:Body>
      <pol:validatePolicyRequest>
         <pol:policyNumber>POL-AUTO-001</pol:policyNumber>
         <pol:customerId>11111111-1111-1111-1111-111111111111</pol:customerId>
         <pol:claimType>AUTO</pol:claimType>
         <pol:claimedAmount>5000</pol:claimedAmount>
      </pol:validatePolicyRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

## Service GraphQL - Claim Tracking

Endpoint : `http://localhost:8081/graphql`
Interface : `http://localhost:8081/graphiql`

Exemple de requête :
```graphql
query {
  claim(id: "12345678-1234-1234-1234-123456789012") {
    id
    policyNumber
    claimType
    claimedAmount
    status
    history {
      action
      details
      timestamp
    }
  }
}
```

## Workflow de Traitement

1. **Claim Submission** (REST) → Réclamation soumise
2. **Identity Verification** (REST) → Identité vérifiée
3. **Policy Validation** (SOAP) → Police validée
4. **Fraud Detection** (gRPC) → Niveau de risque déterminé
5. **Eligibility Evaluation** (REST) → Éligibilité confirmée
6. **Document Review** (REST) → Documents validés
7. **Expert Assessment** (REST) → Décision de l'expert
8. **Compensation Calculation** (REST) → Montant calculé
9. **Payment Authorization** (REST) → Paiement autorisé
10. **Customer Notification** (REST) → Client notifié
11. **Claim Tracking** (GraphQL) → Suivi disponible

## Données de Test

### Clients
- John Doe (ID: 11111111-...) - Vérifié
- Jane Smith (ID: 22222222-...) - Vérifié
- Pierre Martin (ID: 33333333-...) - Non vérifié
- Marie Dupont (ID: 44444444-...) - Vérifié

### Polices
- POL-AUTO-001 (John Doe) - Auto, 50k€
- POL-HOME-001 (John Doe) - Home, 200k€
- POL-HEALTH-001 (Jane Smith) - Health, 100k€
- POL-AUTO-002 (Pierre Martin) - Auto, 30k€
- POL-TRAVEL-001 (Marie Dupont) - Travel, 10k€

## Connexion H2 Console
- JDBC URL: `jdbc:h2:mem:claimdb`
- User: `sa`
- Password: `password`
