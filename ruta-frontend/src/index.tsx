import React from 'react';
import ReactDOM from 'react-dom';
import { ThemeProvider, PartialTheme } from '@fluentui/react/lib/Theme';
import './index.css';
import App from './App';
import { BrowserRouter } from 'react-router-dom';
import { initializeIcons } from '@fluentui/font-icons-mdl2';

initializeIcons();

const theme: PartialTheme = {
  palette: {
    themePrimary: '#994b0b',
    themeLighterAlt: '#fbf6f2',
    themeLighter: '#efdbcb',
    themeLight: '#e0bea2',
    themeTertiary: '#c28656',
    themeSecondary: '#a55b1e',
    themeDarkAlt: '#8a430a',
    themeDark: '#743908',
    themeDarker: '#562a06',
    neutralLighterAlt: '#faf9f8',
    neutralLighter: '#f3f2f1',
    neutralLight: '#edebe9',
    neutralQuaternaryAlt: '#e1dfdd',
    neutralQuaternary: '#d0d0d0',
    neutralTertiaryAlt: '#c8c6c4',
    neutralTertiary: '#a19f9d',
    neutralSecondary: '#605e5c',
    neutralPrimaryAlt: '#3b3a39',
    neutralPrimary: '#323130',
    neutralDark: '#201f1e',
    black: '#000000',
    white: '#ffffff',
  }};

ReactDOM.render(
  <React.StrictMode>
    <ThemeProvider theme={theme}>
      <BrowserRouter>
        <App />
      </BrowserRouter>
    </ThemeProvider>
  </React.StrictMode>,
  document.getElementById('root')
);
