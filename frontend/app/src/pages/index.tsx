import React from 'react';
import authenticated from 'modules/auth/hoc/authenticated';

const Home = () => {
  return <div>Hello world</div>;
};

export default authenticated(Home);
