import React from 'react';
import fullHeightDecorator from 'storybook/decorators/fullHeightDecorator';
import LoginPage from './LoginPage';

export default {
  title: 'Auth|LoginPage',
  decorators: [fullHeightDecorator],
};

export const Base = () => {
  return <LoginPage />;
};
