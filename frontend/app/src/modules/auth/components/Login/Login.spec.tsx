import React from 'react';
import {
  waitForElement,
  waitForElementToBeRemoved,
  wait,
} from '@testing-library/react';
import { typeText, clickElement, sandbox, render } from 'test/utils';

import { Base, WithError } from './Login.stories';

describe('<Login />', () => {
  it('Should be able to successfully login', async () => {
    const loginStub = Base.story.setupMocks(sandbox);
    const { getByPlaceholderText, queryByText, getByText, replace } = render(
      <Base />
    );

    const emailInput = getByPlaceholderText('me@example.com');

    typeText(emailInput, 'text');
    await waitForElement(() =>
      queryByText('Please enter a valid email address.')
    );
    await waitForElement(() => queryByText('Required.'));

    const email = 'login@gmail.com';
    typeText(emailInput, email);
    await waitForElementToBeRemoved(() =>
      queryByText('Please enter a valid email address.')
    );
    await waitForElement(() => queryByText('Required.'));

    const passwordInput = getByPlaceholderText('Password');
    typeText(passwordInput, 'abc');
    await waitForElementToBeRemoved(() => queryByText('Required.'));

    await waitForElement(() =>
      queryByText('Password should be at least 8 characters long.')
    );

    const password = 'abcdefgh';
    typeText(passwordInput, password);
    await waitForElementToBeRemoved(() =>
      queryByText('Password should be at least 8 characters long.')
    );

    const submitButton = getByText('Sign in');
    clickElement(submitButton);

    await wait(() => {
      sandbox.assert.calledWithExactly(replace, '/');
      sandbox.assert.calledWithExactly(loginStub, email, password);
    });
  });

  it('Should display error message on server error', async () => {
    const loginStub = WithError.story.setupMocks(sandbox);
    const { getByPlaceholderText, queryByText, getByText } = render(
      <WithError />
    );

    const emailInput = getByPlaceholderText('me@example.com');
    const passwordInput = getByPlaceholderText('Password');

    const email = 'login-error@gmail.com';
    const password = 'abcdefgh';
    typeText(emailInput, email);
    typeText(passwordInput, password);

    const submitButton = getByText('Sign in');
    clickElement(submitButton);

    await waitForElement(() => queryByText('Something went wrong'));

    sandbox.assert.calledWithExactly(loginStub, email, password);
  });
});
