import EndpointTester from '../components/EndpointTester';

export default function EndpointsTesting() {
  // Données de test basées sur data.sql
  const testData = {
    customerId: '11111111-1111-1111-1111-111111111111',
    customerName: 'John Doe',
    customerEmail: 'john.doe@example.com',
    claimId: '12345678-1234-1234-1234-123456789012',
    policyNumber: 'POL-AUTO-001',
    policyId: 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'
  };

  return (
    <div className='container-fluid py-5'>
      <div className='d-flex justify-content-between align-items-center mb-5'>
        <div>
          <h1 className='mb-3'>🧪 Testeur d'Endpoints</h1>
          <p className='text-muted lead'>
            Testez chaque endpoint du système de traitement de réclamations avec des données de test
          </p>
        </div>
        <a href='/' className='btn btn-secondary'>← Retour au tableau de bord</a>
      </div>

      {/* 1. Claim Submission */}
      <EndpointTester
        title="1. Claim Submission (REST)"
        endpoint="/api/claims"
        method="POST"
        testData={{
          customerId: testData.customerId,
          policyNumber: testData.policyNumber,
          claimType: "accident",
          claimedAmount: 5000.00,
          description: "Car accident - rear bumper damage"
        }}
        description="Soumettre une nouvelle réclamation"
      />

      {/* 2. Identity Verification */}
      <EndpointTester
        title="2. Identity Verification (REST)"
        endpoint="/api/identity/verify-for-claim/12345678-1234-1234-1234-123456789012"
        method="POST"
        testData={{
          claimId: testData.claimId,
          customerId: testData.customerId,
          firstName: "John",
          lastName: "Doe",
          email: testData.customerEmail,
          phone: "+33612345678"
        }}
        description="Vérifier l'identité d'un client pour une réclamation"
      />

      {/* 3. Policy Validation (SOAP) */}
      <EndpointTester
        title="3. Policy Validation (SOAP)"
        endpoint="/ws"
        method="POST"
        isXml={true}
        testData={`<?xml version="1.0" encoding="UTF-8"?>
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
</soapenv:Envelope>`}
        description="Valider une police d'assurance via SOAP (format XML)"
      />

      {/* 4. Fraud Detection (gRPC via REST) */}
      <EndpointTester
        title="4. Fraud Detection (gRPC)"
        endpoint="/api/fraud/analyze"
        method="POST"
        testData={{
          claimId: testData.claimId,
          customerId: testData.customerId,
          policyNumber: testData.policyNumber,
          claimType: "accident",
          claimedAmount: 5000.00,
          description: "Car accident - rear bumper damage"
        }}
        description="Analyser le risque de fraude d'une réclamation"
      />

      {/* 5. Eligibility Evaluation */}
      <EndpointTester
        title="5. Eligibility Evaluation (REST)"
        endpoint="/api/eligibility/evaluate/12345678-1234-1234-1234-123456789012"
        method="POST"
        testData={{
          claimId: testData.claimId,
          customerId: testData.customerId,
          policyNumber: testData.policyNumber,
          claimType: "accident",
          claimedAmount: 5000.00
        }}
        description="Évaluer l'éligibilité d'une réclamation"
      />

      {/* 6. Document Review */}
      <EndpointTester
        title="6. Document Review (REST)"
        endpoint="/api/documents/validate/12345678-1234-1234-1234-123456789012"
        method="POST"
        testData={{
          claimId: testData.claimId,
          documents: [
            {
              type: "POLICE",
              name: "policy_scan.pdf",
              verified: true
            },
            {
              type: "DAMAGE_REPORT",
              name: "damage_report.pdf",
              verified: true
            }
          ],
          notes: "Documents validés et conformes"
        }}
        description="Valider les documents d'une réclamation"
      />

      {/* 7. Expert Assessment */}
      <EndpointTester
        title="7. Expert Assessment (REST)"
        endpoint="/api/expert/review/12345678-1234-1234-1234-123456789012"
        method="POST"
        testData={{
          claimId: testData.claimId,
          expertDecision: "APPROVED",
          approvedAmount: 4500.00,
          expertise: "Damage assessment completed. Vehicle repair estimate: 4500€",
          comments: "All documents verified. No fraud indicators detected."
        }}
        description="Décision d'un expert pour une réclamation"
      />

      {/* 8. Compensation Calculation */}
      <EndpointTester
        title="8. Compensation Calculation (REST)"
        endpoint="/api/compensation/calculate/12345678-1234-1234-1234-123456789012"
        method="POST"
        testData={{
          claimId: testData.claimId,
          baseAmount: 4500.00,
          deductible: 250.00,
          finalAmount: 4250.00,
          calculationDetails: "Deductible applied: -250€"
        }}
        description="Calculer le montant de compensation pour une réclamation"
      />

      {/* 9. Payment Authorization */}
      <EndpointTester
        title="9. Payment Authorization (REST)"
        endpoint="/api/payment/authorize/12345678-1234-1234-1234-123456789012"
        method="POST"
        testData={{
          claimId: testData.claimId,
          customerId: testData.customerId,
          amount: 4250.00,
          paymentMethod: "BANK_TRANSFER",
          bankDetails: {
            accountHolder: "John Doe",
            iban: "FR1420041010050500013M02606"
          }
        }}
        description="Autoriser le paiement d'une réclamation"
      />

      {/* 10. Customer Notification */}
      <EndpointTester
        title="10. Customer Notification (REST)"
        endpoint="/api/notifications/send/12345678-1234-1234-1234-123456789012"
        method="POST"
        testData={{
          claimId: testData.claimId,
          customerId: testData.customerId,
          notificationType: "CLAIM_APPROVED",
          message: "Votre réclamation a été approuvée. Montant autorisé: 4250€",
          channel: "EMAIL",
          recipient: testData.customerEmail
        }}
        description="Envoyer une notification au client"
      />

      {/* 11. Claim Tracking (GraphQL) */}
      <EndpointTester
        title="11. Claim Tracking (GraphQL)"
        endpoint="/graphql"
        method="POST"
        testData={{
          query: `query {
  claim(id: "12345678-1234-1234-1234-123456789012") {
    id
    policyNumber
    claimType
    claimedAmount
    status
    fraudRiskLevel
    approvedAmount
    createdAt
    history {
      action
      details
      performedBy
      timestamp
    }
  }
}`
        }}
        description="Suivre une réclamation avec GraphQL"
      />
    </div>
  );
}
