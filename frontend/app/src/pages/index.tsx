import React from 'react';
import authenticated from 'hoc/auth';
import AppLayout from 'components/common/app/Layout';

const Home = () => {
  return (
    <AppLayout>
      <div>Home</div>
    </AppLayout>
  );
};

export default authenticated(Home);
