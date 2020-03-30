import React from 'react';
import { fullHeightDecorator, configureStory } from '@insight/storybook';
import PasswordApi from 'api/password';
import { mockApiError } from 'test/utils/error';

import PasswordForgot from './PasswordForgot';

export default {
  title: 'Auth|PasswordForgot',
  decorators: [fullHeightDecorator],
};

export const Base = () => {
  return <PasswordForgot />;
};
Base.story = configureStory({
  setupMocks: (sandbox) => {
    return sandbox.stub(PasswordApi, 'forgot').callsFake((_) => {
      const response = { data: true };
      return new Promise((resolve) => setTimeout(() => resolve(response), 200));
    });
  },
});

export const WithError = () => {
  return <PasswordForgot />;
};
WithError.story = configureStory({
  setupMocks: (sandbox) => {
    return sandbox.stub(PasswordApi, 'forgot').callsFake((_) => {
      const error = mockApiError({
        message: 'Something went wrong. Please try again later.',
        statusCode: 500,
        reason: 'Internal Server Error',
      });
      return new Promise((_resolve, reject) =>
        setTimeout(() => reject(error), 200)
      );
    });
  },
});
