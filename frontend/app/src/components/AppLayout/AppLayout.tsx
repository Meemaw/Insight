import React from 'react';
import AppNavbar from 'components/AppNavbar';

type Props = {
  children: React.ReactNode;
};

const AppLayout = ({ children }: Props) => {
  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <AppNavbar />

      {children}
    </div>
  );
};

export default AppLayout;
