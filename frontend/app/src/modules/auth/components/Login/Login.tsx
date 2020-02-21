import React from 'react';
import {
  Classes,
  Intent,
  FormGroup,
  InputGroup,
  Button,
  Checkbox,
  H3,
  Divider,
} from '@blueprintjs/core';
import Link from 'next/link';

import useLogin from './useLogin';
import { StyledLogin } from './elements';

const Login = () => {
  const {
    handleSubmit,
    handleChange,
    values,
    errors,
    isSubmitting,
    formError,
  } = useLogin();

  return (
    <StyledLogin className={Classes.DARK}>
      <form onSubmit={handleSubmit}>
        <H3 style={{ textAlign: 'center' }}>Insight</H3>

        <div className="sso">
          <Button large>Sign in with Google</Button>
        </div>

        <Divider style={{ marginBottom: 24 }} />

        <FormGroup
          label="Email"
          helperText={errors.email}
          intent={errors.email ? Intent.DANGER : undefined}
        >
          <InputGroup
            name="email"
            type="text"
            placeholder="me@example.com"
            autoFocus
            intent={errors.email ? Intent.DANGER : undefined}
            onChange={handleChange}
            value={values.email}
            autoComplete="email"
            large
          />
        </FormGroup>

        <FormGroup
          className="with-action"
          label="Password"
          helperText={errors.password}
          intent={errors.password ? Intent.DANGER : undefined}
        >
          <InputGroup
            name="password"
            type="password"
            placeholder="Password"
            intent={errors.password ? Intent.DANGER : undefined}
            onChange={handleChange}
            value={values.password}
            autoComplete="current-passowrd"
            large
          />

          <div className="action">
            <Link href="/password-forgot">
              <a>Forgot?</a>
            </Link>
          </div>
        </FormGroup>

        <FormGroup style={{ textAlign: 'center' }}>
          <Checkbox
            name="rememberMe"
            checked={values.rememberMe}
            label="Remember me"
            onChange={handleChange}
          />
        </FormGroup>

        <Button
          type="submit"
          intent={Intent.PRIMARY}
          large
          icon="log-in"
          loading={isSubmitting}
        >
          Sign in
        </Button>

        {formError && <p className="error">{formError}</p>}
      </form>
    </StyledLogin>
  );
};

export default Login;
