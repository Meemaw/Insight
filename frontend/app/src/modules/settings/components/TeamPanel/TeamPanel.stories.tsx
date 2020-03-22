import React from 'react';
import configureStory from 'storybook/utils/configureStory';
import InviteApi from 'api/invite';
import uuid from 'uuid';

import TeamPanel from './TeamPanel';

export default {
  title: 'Settings|Team/Panel',
};

export const Base = () => {
  return <TeamPanel />;
};

Base.story = configureStory({
  setupMocks: (sandbox) => {
    return {
      resend: sandbox.stub(InviteApi, 'resend').resolves({ data: true }),
      delete: sandbox.stub(InviteApi, 'delete').resolves({ data: true }),
      create: sandbox.stub(InviteApi, 'create').callsFake((role, email) => {
        return Promise.resolve({
          data: {
            role,
            email,
            token: uuid.v4(),
            org: 'org123',
            creator: 'creator@gmail.com',
            createdAt: Date.now(),
          },
        });
      }),
    };
  },
});
