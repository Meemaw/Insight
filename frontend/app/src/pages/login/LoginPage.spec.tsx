import React from 'react';
import {
  render,
  waitForElement,
  waitForElementToBeRemoved,
  wait,
} from '@testing-library/react';
import { typeText, clickElement } from 'test/utils';

import { Base } from './LoginPage.stories';

describe('<LoginPage />', () => {
  it('Should correctly validate fields', async () => {
    const { getByPlaceholderText, queryByText, getByText } = render(<Base />);

    const emailInput = getByPlaceholderText('Email');

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

    const consoleSpy = jest.spyOn(console, 'log');
    const submitButton = getByText('Log in');
    clickElement(submitButton);

    await wait(() => {
      expect(consoleSpy).toHaveBeenCalledWith({
        email: 'ematej.snuderl@gmail.com',
        password: 'abcdefgh',
      });
    });
  });
});
