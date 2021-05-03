import React from 'react';
import { Link, PrimaryButton } from '@fluentui/react';
import './App.css';

import { RouteView } from "./route/RouteView"

function App() {
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
          <PrimaryButton text="Iniciar sesión" onClick={() => alert("Click")}/>
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
