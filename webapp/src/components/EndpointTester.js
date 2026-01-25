import { useState } from 'react';
import axios from 'axios';

export default function EndpointTester({title, endpoint, method = 'POST', testData, description, isXml = false}) {
  const [data, setData] = useState(
    isXml ? testData : JSON.stringify(testData, null, 2)
  );
  const [response, setResponse] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleChange = (e) => {
    setData(e.target.value);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setResponse(null);

    try {
      let requestData = data;
      let headers = {};

      if (!isXml) {
        // JSON validation
        try {
          requestData = JSON.parse(data);
        } catch {
          setError('JSON invalide');
          setLoading(false);
          return;
        }
        headers = { 'Content-Type': 'application/json' };
      } else {
        // XML - send as string with proper header
        headers = { 'Content-Type': 'text/xml; charset=utf-8' };
      }

      const url = `http://localhost:8081${endpoint}`;
      
      let result;
      if (method === 'GET') {
        result = await axios.get(url, { data: requestData, headers });
      } else if (method === 'POST') {
        result = await axios.post(url, requestData, { headers });
      } else if (method === 'PUT') {
        result = await axios.put(url, requestData, { headers });
      }

      setResponse({
        status: result.status,
        data: result.data
      });
    } catch (err) {
      setError(err.response?.data || err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className='card mb-4 shadow-sm'>
      <div className='card-header bg-info text-white'>
        <div className='d-flex justify-content-between align-items-center'>
          <div>
            <h5 className='mb-1'>{title}</h5>
            <small className='text-light'>{method} {endpoint}{isXml ? ' (XML)' : ''}</small>
          </div>
          <span className='badge bg-light text-dark'>{method}</span>
        </div>
      </div>
      <div className='card-body'>
        {description && <p className='text-muted mb-3'>{description}</p>}
        
        <form onSubmit={handleSubmit}>
          <div className='mb-3'>
            <label className='form-label fw-bold'>Données ({isXml ? 'XML' : 'JSON'})</label>
            <textarea 
              className="form-control font-monospace" 
              placeholder="Entrez les données..."
              rows="8"
              value={data}
              onChange={handleChange}
              style={{fontSize: '0.9rem'}}
            ></textarea>
          </div>

          <div className='d-flex gap-2 mb-3'>
            <button 
              type='submit' 
              className='btn btn-primary'
              disabled={loading}
            >
              {loading ? 'Envoi...' : 'Envoyer'}
            </button>
            <button 
              type='reset' 
              className='btn btn-outline-secondary'
              onClick={() => setData(
                isXml ? testData : JSON.stringify(testData, null, 2)
              )}
            >
              Réinitialiser
            </button>
          </div>

          {error && (
            <div className='alert alert-danger'>
              <strong>Erreur :</strong>
              <pre className='mt-2 text-break' style={{fontSize: '0.85rem', maxHeight: '300px', overflow: 'auto'}}>
                {typeof error === 'string' ? error : JSON.stringify(error, null, 2)}
              </pre>
            </div>
          )}

          {response && (
            <div className='alert alert-success'>
              <strong>Réponse (Status: {response.status}) :</strong>
              <pre className='mt-2 bg-light p-2 text-break' style={{fontSize: '0.85rem', maxHeight: '400px', overflow: 'auto'}}>
                {typeof response.data === 'string' ? response.data : JSON.stringify(response.data, null, 2)}
              </pre>
            </div>
          )}
        </form>
      </div>
    </div>
  );
}