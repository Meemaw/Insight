import React from 'react';
import { action } from '@storybook/addon-actions';
import { DataResponse, TeamInvite } from 'api';

import SendInviteForm from './SendInviteForm';

export default {
  title: 'Settings|Team/SendInviteForm',
};

export const Base = () => {
  return (
    <SendInviteForm
      createInvite={() => Promise.resolve({} as DataResponse<TeamInvite>)}
      onCancel={action('onCancel')}
    />
  );
};
