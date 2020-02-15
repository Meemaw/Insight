import React from 'react';

type Props = {
  children: React.ReactNode;
};

const AuthLayout = ({ children }: Props) => {
  return (
    <div
      style={{
        height: '100%',
        background:
          'linear-gradient(to bottom right, #973999, #f8598b, #f7bf00)',
      }}
    >
      <div
        style={{
          display: 'flex',
          justifyContent: 'center',
          flexDirection: 'column',
          alignItems: 'center',
          height: '100%',
        }}
      >
        {children}
      </div>
    </div>
  );
};

export default AuthLayout;
