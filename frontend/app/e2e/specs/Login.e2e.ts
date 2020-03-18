import config from '../config';
import { LoginPage } from '../__pages__';
import { getLocation } from '../utils';

fixture('<LoginPage />').page(`${config.baseURL}/login`);

test('Login page should validate its inputs', async t => {
  await t
    .typeText(LoginPage.emailInput, 'start')
    .expect(LoginPage.helperText('Please enter a valid email address.').visible)
    .ok('Should validate email address: has to be valid')
    .expect(LoginPage.helperText('Required.').visible)
    .ok('Should validate password: it is required')
    .typeText(LoginPage.passwordInput, 'short')
    .expect(
      LoginPage.helperText('Password should be at least 8 characters long.')
        .visible
    )
    .ok('Should validate password: it has to be at least 8 characters long');

  await LoginPage.login('random@gmail.com', 'randomPassword', t)
    .expect(LoginPage.formErrorText('Invalid email or password').visible)
    .ok('Should display error message on invalid credentials');
});

test('Should be able to navigate to password reset page', async t => {
  await t
    .click(LoginPage.forgotLink)
    .expect(getLocation())
    .eql(
      `${config.baseURL}/password-forgot`,
      'Should navigate to /password-forgot'
    );
});
