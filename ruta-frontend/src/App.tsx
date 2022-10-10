import React, { useState } from 'react';
import { Dialog, DialogFooter, Link, PrimaryButton } from '@fluentui/react';
import { Switch, Route, useHistory, useLocation } from "react-router-dom";
import { GoogleLogin } from '@react-oauth/google';

import { ApiService } from './services/ApiService';
import User from './models/User';

import './App.css';
import { RouteListView } from './route/RouteListView';
import RouteMaker from './maker/RouteMaker';
import TrackView from './track/TrackView';
import UploadTrackView from './track/UploadTrackView';
import UserProfile from './profile/UserProfile';

function App() {
  // eslint-disable-next-line
  const [apiService, setApiService] = useState(new ApiService());
  const [user, setUser] = useState<User|null>(null);
  const [showCopyright, setShowCopyright] = useState(false);

  const history = useHistory();
  const location = useLocation();

  const handleLogin = async (googleData: any) => {
    console.log(googleData);
    const jwt = await apiService.getToken(googleData.credential);
    apiService.setJwt(jwt);
    const newUser = await apiService.getMe();
    setUser(newUser);
  }

  return (
    <div className="App">
      <header className="App-header">
        <h1 className="App-header-title">Ruta x ruta x Castilla y León</h1>
        <nav className="App-header-links">
          <Link onClick={() => history.push("/") }>Catálogo de rutas</Link>
          <Link onClick={() => history.push("/upload-track/")}>Sube tu track</Link>
          <Link onClick={() => history.push("/maker/")}>Propón tu ruta</Link>
        </nav>
        <nav className="App-header-user">
          {user === null && 
          <GoogleLogin
            onSuccess={handleLogin}
            onError={() => {
              console.log("Login failed!")
            }}
            />
          }
          {user !== null && <div className="username">
            <p><Link onClick={() => history.push(`/user/${user.id}`)}>{user.name}</Link></p>
            <img width="32" height="32" alt="" src={user.picture}/>
          </div>}
        </nav>
      </header>
      <div className="App-main" style={{ backgroundImage: `url(${process.env.PUBLIC_URL + '/background.jpg'})` }}>
        <main className="App-main-main">
          <Switch>
            <Route exact path="/">
              <RouteListView apiService={apiService}/>
            </Route>
            <Route path="/upload-track/">
              <UploadTrackView apiService={apiService} user={user}/>
            </Route>
            <Route path="/maker/">
              <RouteMaker apiService={apiService} user={user}/>
            </Route>
            <Route path="/route/:id">
              <RouteListView apiService={apiService} key={location.pathname}/>
            </Route>
            <Route path="/track/:id">
              <TrackView apiService={apiService} key={location.pathname}/>
            </Route>
            <Route path="/user/:id">
              <UserProfile apiService={apiService} user={user}/>
            </Route>
          </Switch>
        </main>
      </div>
      <footer className="App-footer">
        <a href="https://adrianistan.eu">&copy; Adrián Arroyo Calle 2021</a> - <Link onClick={() => setShowCopyright(true)}>¿De dónde vienen estos datos?</Link>
        <Dialog hidden={!showCopyright} onDismiss={() => setShowCopyright(false)}>
          <h2>Origen de los datos</h2>
          <p>Ruta x ruta no sería lo mismo si no fuese por las fuentes de datos abiertos de las que bebe:</p>
          <ul>
            <li><a href="https://www.ign.es/web/ign/portal/ide-area-nodo-ide-ign">Mapa de España - Instituto Geográfico Nacional</a></li>
            <li><a href="https://idecyl.jcyl.es/geonetwork/srv/spa/catalog.search#/metadata/SPAGOBCYLITADTSOIOIT">Ortofoto de Castilla y León 2017 - ITaCyl</a></li>
            <li><a href="https://datosabiertos.jcyl.es/web/jcyl/set/es/cultura-ocio/monumentos/1284325843131">Monumentos de Castilla y León - Junta de Castilla y León</a></li>
            <li><a href="https://datosabiertos.jcyl.es/web/jcyl/set/es/turismo/bares/1284211832884">Bares de Castilla y León - Junta de Castilla y León</a></li>
            <li><a href="https://datosabiertos.jcyl.es/web/jcyl/set/es/cultura-ocio/agenda_cultural/1284806871500">Agenda cultural de Castilla y León - Junta de Castilla y León</a></li>
            <li>Icono de la aplicación: <a href="https://www.flaticon.es/icono-gratis/correr-por-senderos_3163782?term=trail&related_id=3163782">Flaticon / photo3idea_studio</a></li>
          </ul>
          <p>Agradecemos enormemente poder disponer de estos datos</p>
          <DialogFooter>
            <PrimaryButton onClick={() => setShowCopyright(false)} text="Entendido"/>
          </DialogFooter>
        </Dialog>
      </footer>
    </div>
  );
}

export default App;
