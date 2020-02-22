import {
  getByPlaceholderText,
  getByText,
  queryByText,
} from '@testing-library/testcafe';

import config from '../config';

fixture('<LoginPage />').page(`${config.baseURL}/login`);

test('Displays error message on invalid credentials', async t => {
  const emailInput = getByPlaceholderText('me@example.com');
  const passwordInput = getByPlaceholderText('Password');
  const submitButton = getByText('Sign in');

  await t
    .typeText(emailInput, 'test-email@gmail.com')
    .typeText(passwordInput, 'test-password')
    .click(submitButton)
    .wait(100000)
    .expect(queryByText('Invalid email or password').visible)
    .ok('Should display error message');
});
