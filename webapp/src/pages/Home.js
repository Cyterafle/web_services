import { useState } from 'react';
import App from './App';
import EndpointsTesting from './EndpointsTesting';

export default function Home() {
  const [currentPage, setCurrentPage] = useState('dashboard'); // dashboard, testing

  return (
    <div>
      {/* Navigation */}
      <nav className='navbar navbar-expand-lg navbar-dark bg-dark sticky-top'>
        <div className='container-fluid'>
          <a 
            className='navbar-brand fw-bold' 
            href='#'
            onClick={(e) => {
              e.preventDefault();
              setCurrentPage('dashboard');
            }}
          >
            🏢 Insurance Claim Processing
          </a>
          <button className='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav'>
            <span className='navbar-toggler-icon'></span>
          </button>
          <div className='collapse navbar-collapse' id='navbarNav'>
            <ul className='navbar-nav ms-auto'>
              <li className='nav-item'>
                <button 
                  className={`nav-link btn btn-link ${currentPage === 'dashboard' ? 'active' : ''}`}
                  onClick={() => setCurrentPage('dashboard')}
                >
                  📋 Tableau de bord
                </button>
              </li>
              <li className='nav-item'>
                <button 
                  className={`nav-link btn btn-link ${currentPage === 'testing' ? 'active' : ''}`}
                  onClick={() => setCurrentPage('testing')}
                >
                  🧪 Testeur d'Endpoints
                </button>
              </li>
            </ul>
          </div>
        </div>
      </nav>

      {/* Pages */}
      <main>
        {currentPage === 'dashboard' && <App />}
        {currentPage === 'testing' && <EndpointsTesting />}
      </main>
    </div>
  );
}
