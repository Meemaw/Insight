import React from 'react';
import fullHeightDecorator from 'storybook/decorators/fullHeightDecorator';
import configureStory from 'storybook/utils/configureStory';
import SignupApi from 'api/signup';
import { mockApiError } from 'test/utils/error';

import SignupComplete from './SignupComplete';

export default {
  title: 'Auth|SignupComplete',
  decorators: [fullHeightDecorator],
};

const baseProps = {
  email: 'matthew.brandon@gmail.com',
  token: '123',
  org: 'superOrg',
};

export const Base = () => {
  return <SignupComplete {...baseProps} />;
};
Base.story = configureStory({
  setupMocks: sandbox => {
    return sandbox.stub(SignupApi, 'complete').callsFake(_ => {
      const response = { data: true };
      return new Promise(resolve => setTimeout(() => resolve(response), 250));
    });
  },
});

export const WithError = () => {
  return <SignupComplete {...baseProps} />;
};
WithError.story = configureStory({
  setupMocks: sandbox => {
    return sandbox.stub(SignupApi, 'complete').callsFake(_ => {
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
