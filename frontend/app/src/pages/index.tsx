import React from 'react';
import authenticated from 'hoc/auth';
import AppLayout from 'components/AppLayout';

const Home = () => {
  return (
    <AppLayout>
      <div>Home</div>
    </AppLayout>
  );
};

export default authenticated(Home);
