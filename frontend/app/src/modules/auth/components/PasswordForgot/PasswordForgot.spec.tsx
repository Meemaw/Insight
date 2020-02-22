import React from 'react';
import {
  waitForElement,
  waitForElementToBeRemoved,
} from '@testing-library/react';
import { typeText, clickElement, sandbox, render } from 'test/utils';

import { Base, WithError } from './PasswordForgot.stories';

describe('<PasswordForgot />', () => {
  it('Should be able to successfully initiate password reset flow', async () => {
    const forgotApiStub = Base.story.setupMocks(sandbox);
    const { getByPlaceholderText, queryByText, getByText } = render(<Base />);

    const emailInput = getByPlaceholderText('me@example.com');

    typeText(emailInput, 'text');
    await waitForElement(() =>
      queryByText('Please enter a valid email address.')
    );

    const email = 'forgot@gmail.com';
    typeText(emailInput, email);
    await waitForElementToBeRemoved(() =>
      queryByText('Please enter a valid email address.')
    );

    const submitButton = getByText('Send reset link');
    clickElement(submitButton);

    await waitForElement(() => queryByText('Check your inbox!'));

    sandbox.assert.calledWithExactly(forgotApiStub, email);
  });

  it('Should display error message on server error', async () => {
    const forgotApiStub = WithError.story.setupMocks(sandbox);
    const { getByPlaceholderText, queryByText, getByText } = render(
      <WithError />
    );

    const emailInput = getByPlaceholderText('me@example.com');
    const email = 'forgot@gmail.com';
    typeText(emailInput, email);

    const submitButton = getByText('Send reset link');
    clickElement(submitButton);

    await waitForElement(() =>
      queryByText('Something went wrong. Please try again later.')
    );

    sandbox.assert.calledWithExactly(forgotApiStub, email);
  });
});
