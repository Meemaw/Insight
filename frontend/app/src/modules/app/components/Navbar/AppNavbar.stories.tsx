import React from 'react';

import AppNavbar from './AppNavbar';

export default {
  title: 'App|Navbar',
};

export const Home = () => {
  return <AppNavbar pathname="/" />;
};

export const Insights = () => {
  return <AppNavbar pathname="/insights" />;
};
