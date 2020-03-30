import React from 'react';
import { fullHeightDecorator, configureStory } from '@insight/storybook';
import PasswordApi from 'api/password';
import { mockApiError } from 'test/utils/error';

import PasswordReset from './PasswordReset';

export default {
  title: 'Auth|PasswordReset',
  decorators: [fullHeightDecorator],
};

const baseProps = {
  email: 'matthew.brandon@gmail.com',
  token: '123',
  org: 'superOrg',
};

export const Base = () => {
  return <PasswordReset {...baseProps} />;
};
Base.story = configureStory({
  setupMocks: (sandbox) => {
    return sandbox.stub(PasswordApi, 'reset').callsFake((_) => {
      const response = { data: true };
      return new Promise((resolve) => setTimeout(() => resolve(response), 250));
    });
  },
});

export const WithError = () => {
  return <PasswordReset {...baseProps} />;
};
WithError.story = configureStory({
  setupMocks: (sandbox) => {
    return sandbox.stub(PasswordApi, 'reset').callsFake((_) => {
      const error = mockApiError({
        message: 'Something went wrong. Please try again later.',
        statusCode: 500,
        reason: 'Internal Server Error',
      });
      return new Promise((_resolve, reject) =>
        setTimeout(() => reject(error), 250)
      );
    });
  },
});
