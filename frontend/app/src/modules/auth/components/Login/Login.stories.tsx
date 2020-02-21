import React from 'react';
import fullHeightDecorator from 'storybook/decorators/fullHeightDecorator';
import { SsoApi } from 'api';
import configureStory from 'storybook/utils/configureStory';

import Login from './Login';

export default {
  title: 'Auth|Login',
  decorators: [fullHeightDecorator],
};

export const Base = () => {
  return <Login />;
};
Base.story = configureStory({
  setupMocks: sandbox => {
    return sandbox.stub(SsoApi, 'login').callsFake(_ => {
      const response = { data: true };
      return new Promise(resolve => setTimeout(() => resolve(response), 1000));
    });
  },
});

export const WithError = () => {
  return <Login />;
};
WithError.story = configureStory({
  setupMocks: sandbox => {
    return sandbox.stub(SsoApi, 'login').callsFake(_ => {
      const error = new Error('APIError');
      Object.assign(error, {
        response: {
          json: () => ({ error: { message: 'Something went wrong' } }),
        },
      });

      return new Promise((_resolve, reject) =>
        setTimeout(() => reject(error), 1000)
      );
    });
  },
});
