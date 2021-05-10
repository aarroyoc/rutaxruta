import React, { useState } from 'react';
import { Link } from '@fluentui/react';
import './App.css';

import { RouteView } from "./route/RouteView"
import GoogleLogin from 'react-google-login';
import { ApiService } from './services/ApiService';
import User from './models/User';

function App() {

  const apiService = new ApiService();
  const [user, setUser] = useState<User|null>(null);

  const handleLogin = async (googleData: any) => {
    const jwt = await apiService.getToken(googleData.tokenId);
    apiService.setJwt(jwt);
    const newUser = await apiService.getMe();
    setUser(newUser);
  }

  return (
    <div className="App">
      <header className="App-header">
        <h1>Ruta x ruta x Castilla y León</h1>
        <nav className="App-header-links">
          <Link href="">Catálogo de rutas</Link>
          <Link href="">Viajes de los usuarios</Link>
          <Link href="">Crea tu ruta</Link>
        </nav>
        <nav>
          {user === null && 
          <GoogleLogin
            clientId={process.env.REACT_APP_GOOGLE_CLIENT_ID || ""}
            buttonText="Iniciar sesión"
            onSuccess={handleLogin}
            onFailure={handleLogin}
            cookiePolicy={"single_host_origin"}
            />
          }
          {user !== null && <div>
            <p>{user.name}</p>
            <img src={user.picture}/>
          </div>}
        </nav>
      </header>
      <div className="App-main" style={{ backgroundImage: `url(${process.env.PUBLIC_URL + '/background.jpg'})` }}>
        <main className="App-main-main">
          <RouteView id="608598a7250a8361c418d003"/>
        </main>
      </div>
      <footer className="App-footer">
        <a href="https://adrianistan.eu">&copy; Adrián Arroyo Calle 2021</a>
      </footer>
    </div>
  );
}

export default App;
