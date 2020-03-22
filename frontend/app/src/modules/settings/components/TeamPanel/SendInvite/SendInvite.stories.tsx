import React from 'react';

import SendInvite from './SendInvite';

export default {
  title: 'Settings|Team/SendInvite',
};

export const Success = () => {
  return (
    <SendInvite
      createInvite={() => new Promise((resolve) => setTimeout(resolve, 1000))}
    />
  );
};

export const Error = () => {
  return (
    <SendInvite
      createInvite={() => {
        return new Promise((_resolve, reject) => {
          setTimeout(() => reject(), 1000);
        });
      }}
    />
  );
};
