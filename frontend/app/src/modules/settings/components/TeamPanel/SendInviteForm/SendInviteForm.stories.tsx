import React from 'react';
import SendInviteForm from './SendInviteForm';

export default {
  title: 'Settings|Team/SendInviteForm',
};

export const Base = () => {
  return (
    <SendInviteForm
      createInvite={() => Promise.resolve({} as any)}
      onCancel={console.log}
    />
  );
};
