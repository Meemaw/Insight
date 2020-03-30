import React from 'react';
import { fullHeightDecorator, configureStory } from '@insight/storybook';
import SsoApi from 'api/sso';

import Login from './Login';

export default {
  title: 'Auth|Login',
  decorators: [fullHeightDecorator],
};

export const Base = () => {
  return <Login dest={encodeURIComponent('/')} />;
};
Base.story = configureStory({
  setupMocks: (sandbox) => {
    return sandbox.stub(SsoApi, 'login').callsFake((_) => {
      const response = { data: true };
      return new Promise((resolve) => setTimeout(() => resolve(response), 250));
    });
  },
});

export const CustomDest = () => {
  return <Login dest={encodeURIComponent('/settings/general')} />;
};
CustomDest.story = configureStory({
  setupMocks: (sandbox) => {
    return sandbox.stub(SsoApi, 'login').callsFake((_) => {
      const response = { data: true };
      return new Promise((resolve) => setTimeout(() => resolve(response), 250));
    });
  },
});

export const WithError = () => {
  return <Login dest={encodeURIComponent('/')} />;
};
WithError.story = configureStory({
  setupMocks: (sandbox) => {
    return sandbox.stub(SsoApi, 'login').callsFake((_) => {
      const error = new Error('APIError');
      Object.assign(error, {
        response: {
          json: () => ({ error: { message: 'Something went wrong' } }),
        },
      });

      return new Promise((_resolve, reject) =>
        setTimeout(() => reject(error), 250)
      );
    });
  },
});
