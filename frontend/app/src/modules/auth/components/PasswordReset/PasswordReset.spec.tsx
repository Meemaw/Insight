import React from 'react';
import {
  waitForElement,
  waitForElementToBeRemoved,
  wait,
} from '@testing-library/react';
import { typeText, clickElement, sandbox, render } from 'test/utils';

import { Base, WithError } from './PasswordReset.stories';

describe('<PasswordForgot />', () => {
  it('Should be able to successfully initiate password reset flow', async () => {
    const resetStub = Base.story.setupMocks(sandbox);
    const {
      queryByText,
      getByText,
      getByPlaceholderText,
      replace,
      queryByDisplayValue,
    } = render(<Base />);

    expect(
      queryByDisplayValue('matthew.brandon@gmail.com')
    ).toBeInTheDocument();

    const passwordInput = getByPlaceholderText('Password');

    typeText(passwordInput, 'aba');
    await waitForElement(() =>
      queryByText('Password should be at least 8 characters long.')
    );

    const password = 'abcdefgh';
    typeText(passwordInput, password);
    await waitForElementToBeRemoved(() =>
      queryByText('Password should be at least 8 characters long.')
    );

    const submitButton = getByText('Reset password and log in');
    clickElement(submitButton);

    await wait(() => {
      sandbox.assert.calledWithExactly(replace, '/');
      sandbox.assert.calledWithExactly(resetStub, {
        email: 'matthew.brandon@gmail.com',
        token: '123',
        org: 'superOrg',
        password,
      });
    });
  });

  it('Should display error message on server error', async () => {
    const resetStub = WithError.story.setupMocks(sandbox);
    const { getByPlaceholderText, queryByText, getByText } = render(
      <WithError />
    );
    const passwordInput = getByPlaceholderText('Password');

    const password = 'abcdefgh';
    typeText(passwordInput, password);

    const submitButton = getByText('Reset password and log in');
    clickElement(submitButton);

    await waitForElement(() =>
      queryByText('Something went wrong. Please try again later.')
    );

    sandbox.assert.calledWithExactly(resetStub, {
      email: 'matthew.brandon@gmail.com',
      token: '123',
      org: 'superOrg',
      password,
    });
  });
});
