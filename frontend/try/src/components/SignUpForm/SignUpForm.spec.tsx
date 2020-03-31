import React from 'react';
import { render, clickElement, typeText, sandbox } from 'test/utils';
import { Base } from './SignUpForm.stories';
import { waitFor } from '@testing-library/react';

jest.mock('react-virtualized', () => {
  return {
    __esModule: true,
    AutoSizer: ({ children }: any) => {
      return children({ width: 500, height: 500 });
    },
  };
});

describe('<SignUpForm />', () => {
  test('User can signup in normal flow', async () => {
    const onSubmit = sandbox.stub().resolves(undefined);

    const {
      getByPlaceholderText,
      getByText,
      findByText,
      findAllByText,
      container,
    } = render(<Base onSubmit={onSubmit} />);
    const submitButton = getByText('Get started');
    const firstNameInput = getByPlaceholderText('First name');
    const lastNameInput = getByPlaceholderText('Last name');
    const companyInput = getByPlaceholderText('Company');
    const emailInput = getByPlaceholderText('Email');
    const passwordInput = getByPlaceholderText('Password');

    clickElement(submitButton);
    expect((await findAllByText('Required')).length).toEqual(5);

    typeText(firstNameInput, 'Joe');
    typeText(lastNameInput, 'Makarena');
    typeText(companyInput, 'Insight');
    typeText(emailInput, 'random');
    typeText(passwordInput, 'short');

    clickElement(submitButton);
    await findByText('Invalid email address');
    await findByText('Password must be at least 8 characters long');

    typeText(emailInput, 'user@example.com');
    typeText(passwordInput, 'veryHardPassword');

    clickElement(submitButton);

    await waitFor(() => {
      sandbox.assert.calledWithExactly(onSubmit, {
        firstName: 'Joe',
        lastName: 'Makarena',
        company: 'Insight',
        email: 'user@example.com',
        password: 'veryHardPassword',
      });
    });

    // can also include phone nume
    const phoneNumberInput = getByPlaceholderText('Phone number');
    typeText(phoneNumberInput, '51222333');

    clickElement(submitButton);
    await waitFor(() => {
      sandbox.assert.calledWithExactly(onSubmit, {
        firstName: 'Joe',
        lastName: 'Makarena',
        company: 'Insight',
        email: 'user@example.com',
        password: 'veryHardPassword',
        phoneNumber: '+151222333',
      });
    });
  });
});
