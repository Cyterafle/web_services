
import { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router';
const socket = new WebSocket("ws://localhost:8081/websocket-raw")

// Connection opened
socket.addEventListener("open", event => {
  socket.send("Connection established")
});

// Listen for messages
socket.addEventListener("message", event => {
  console.log("Message from server ", event.data)
});


function App() {
  const navigate = useNavigate();
  const [customers, setCustomers] = useState([]);
  const [policies, setPolicies] = useState([]);
  const [claims, setClaims] = useState([]);
  const [showWorkflow, setShowWorkflow] = useState(false);
  const [workflowStepStatuses, setWorkflowStepStatuses] = useState({});
  const [workflowError, setWorkflowError] = useState(null);
  const [formData, setFormData] = useState({
    customerId: '',
    policyNumber: '',
    claimType: '',
    claimedAmount: '',
    description: ''
  });

  useEffect(() => {
    axios.get("http://localhost:8081/api/customers")
        .then(response => {
          console.log(response.data)
            setCustomers(response.data)
        })
        .catch(error => {
            console.error(error)
        })
        .finally(() => {
            console.log('Customer Request finished')
        })
    axios.get("http://localhost:8081/api/policies")
      .then(response => {
            console.log(response.data)
            setPolicies(response.data)
          })
          .catch(error => {
              console.error(error)
          })
          .finally(() => {
              console.log('Policies Request finished')
          })
    axios.get("http://localhost:8081/api/claims")
      .then(response => {
            console.log(response.data)
            setClaims(response.data)
          })
          .catch(error => {
              console.error(error)
          })
          .finally(() => {
              console.log('Claims Request finished')
          })
  }, [])

  const getStatusBadge = (status) => {
    const badgeClass = {
      'SUBMITTED': 'bg-warning',
      'IDENTITY_VERIFIED': 'bg-info',
      'POLICY_VALIDATED': 'bg-info',
      'FRAUD_CHECK_LOW': 'bg-success',
      'FRAUD_CHECK_MEDIUM': 'bg-warning',
      'FRAUD_CHECK_HIGH': 'bg-danger',
      'APPROVED': 'bg-success',
      'REJECTED': 'bg-danger',
      'EXPERT_REVIEW': 'bg-secondary'
    };
    return badgeClass[status] || 'bg-secondary';
  };

  return (
    <div className='container mt-5'>

      <div className='card shadow-sm mt-5'>
        <div className='card-header bg-secondary text-white'>
          <h4 className='mb-0'>Réclamations Existantes ({claims.length})</h4>
        </div>
        <div className='card-body'>
          {claims.length === 0 ? (
            <p className='text-muted'>Aucune réclamation trouvée</p>
          ) : (
            <div className='table-responsive'>
              <table className='table table-hover'>
                <thead className='table-light'>
                  <tr>
                    <th>ID</th>
                    <th>Client</th>
                    <th>Police</th>
                    <th>Type</th>
                    <th>Montant</th>
                    <th>Statut</th>
                    <th>Description</th>
                  </tr>
                </thead>
                <tbody>
                  {claims.map(claim => (
                    <tr key={claim.id}>
                      <td className='fw-bold text-truncate' style={{maxWidth: '150px'}}>{claim.id}</td>
                      <td>{claim.customer?.name} {claim.customer?.surname}</td>
                      <td>{claim.policyNumber}</td>
                      <td>{claim.claimType}</td>
                      <td className='fw-bold'>{claim.claimedAmount}€</td>
                      <td>
                        <span className={`badge ${getStatusBadge(claim.status)}`}>
                          {claim.status}
                        </span>
                      </td>
                      <td className='text-truncate' style={{maxWidth: '200px'}} title={claim.description}>
                        {claim.description}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
      <a href='/testing' className='btn btn-info mt-3'>🧪 Tester les endpoints</a>
    </div>
  );
}


export default App;
