import { Outlet, useLocation } from "react-router";

export default function Layout(){
    const location = useLocation();

    const navItems = [
        { path: '/', label: '📋 Tableau de bord', icon: '📋' },
        { path: '/testing', label: '🧪 Testeur d\'Endpoints', icon: '🧪' }
    ];

    return(
        <>
            <nav className='navbar navbar-expand-lg navbar-dark bg-dark sticky-top'>
                <div className='container-fluid'>
                    <a className='navbar-brand fw-bold' href='/'>
                        🏢 Insurance Claim Processing
                    </a>
                    <button className='navbar-toggler' type='button' data-bs-toggle='collapse' data-bs-target='#navbarNav'>
                        <span className='navbar-toggler-icon'></span>
                    </button>
                    <div className='collapse navbar-collapse' id='navbarNav'>
                        <ul className='navbar-nav ms-auto'>
                            {navItems.map(item => (
                                <li className='nav-item' key={item.path}>
                                    <a 
                                        className={`nav-link ${location.pathname === item.path ? 'active' : ''}`}
                                        href={item.path}
                                    >
                                        {item.label}
                                    </a>
                                </li>
                            ))}
                        </ul>
                    </div>
                </div>
            </nav>
            <Outlet/>
        </>
    )
}