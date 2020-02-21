import React from 'react';
import {
  waitForElement,
  waitForElementToBeRemoved,
  wait,
} from '@testing-library/react';
import { typeText, clickElement, sandbox, render } from 'test/utils';

import { Base } from './Login.stories';

describe('<Login />', () => {
  it('Should correctly validate fields', async () => {
    Base.story.setupMocks(sandbox);
    const { getByPlaceholderText, queryByText, getByText, replace } = render(
      <Base />
    );

    const emailInput = getByPlaceholderText('me@example.com');

    typeText(emailInput, 'text');
    await waitForElement(() =>
      queryByText('Please enter a valid email address.')
    );
    await waitForElement(() => queryByText('Required.'));

    typeText(emailInput, 'ematej.snuderl@gmail.com');
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

    typeText(passwordInput, 'abcdefgh');
    await waitForElementToBeRemoved(() =>
      queryByText('Password should be at least 8 characters long.')
    );

    const submitButton = getByText('Sign in');
    clickElement(submitButton);

    await wait(() => {
      sandbox.assert.calledWithExactly(replace, '/');
    });
  });
});
