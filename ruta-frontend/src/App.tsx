import React, { useState } from 'react';
import { Link } from '@fluentui/react';
import GoogleLogin from 'react-google-login';
import { BrowserRouter as Switch, Route } from "react-router-dom";

import { ApiService } from './services/ApiService';
import User from './models/User';

import './App.css';
import { RouteListView } from './route/RouteListView';

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
          <Link href="/">Catálogo de rutas</Link>
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
          {user !== null && <div className="username">
            <p>{user.name}</p>
            <img width="32" height="32" alt="" src={user.picture}/>
          </div>}
        </nav>
      </header>
      <div className="App-main" style={{ backgroundImage: `url(${process.env.PUBLIC_URL + '/background.jpg'})` }}>
        <main className="App-main-main">
          <Switch>
            <Route path="/route/:id">
              <RouteListView apiService={apiService}/>
            </Route>
            <Route exact path="/">
              <RouteListView apiService={apiService}/>
            </Route>
          </Switch>
        </main>
      </div>
      <footer className="App-footer">
        <a href="https://adrianistan.eu">&copy; Adrián Arroyo Calle 2021 - Data from XXX</a>
      </footer>
    </div>
  );
}

export default App;
