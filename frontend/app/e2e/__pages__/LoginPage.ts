import { Selector } from 'testcafe';

export class LoginPage {
  public readonly emailInput = Selector('input[placeholder="me@example.com"]');
  public readonly passwordInput = Selector('input[placeholder="Password"]');
  public readonly submitButton = Selector('button').withExactText('Sign in');
  public readonly formError = Selector('p.error');
  public readonly forgotLink = Selector('a').withExactText('Forgot?');

  public email = (email: string, t: TestController) => {
    return t.selectText(this.emailInput).typeText(this.emailInput, email);
  };

  public password = (password: string, t: TestController) => {
    return t
      .selectText(this.passwordInput)
      .typeText(this.passwordInput, password);
  };

  public submit = (t: TestController) => {
    return t.click(this.submitButton);
  };

  public helperText = (text: string) => {
    return Selector('div.bp3-form-helper-text').withExactText(text);
  };

  public formErrorText = (text: string) => {
    return this.formError.withExactText(text);
  };

  public login = (email: string, password: string, t: TestController) => {
    return this.email(email, t)
      .selectText(this.passwordInput)
      .typeText(this.passwordInput, password)
      .click(this.submitButton);
  };
}

export default new LoginPage();
