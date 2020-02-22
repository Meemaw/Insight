import React from 'react';
import AuthLayout from 'modules/auth/components/Layout';
import {
  Classes,
  H3,
  H5,
  FormGroup,
  InputGroup,
  Intent,
  Button,
} from '@blueprintjs/core';
import { SignupRequestDTO } from 'api';

import useCompleteSignup from './useSignupComplete';

type Props = SignupRequestDTO;

const SignupComplete = (props: Props) => {
  const { email } = props;
  const {
    handleSubmit,
    handleChange,
    values,
    errors,
    formError,
    isSubmitting,
  } = useCompleteSignup(props);

  return (
    <AuthLayout className={Classes.DARK}>
      <form onSubmit={handleSubmit}>
        <H3 style={{ textAlign: 'center' }}>Insight</H3>
        <H5 style={{ textAlign: 'center' }}>Welcome back!</H5>

        <p style={{ textAlign: 'center', margin: '32px 0' }}>
          Let&apos;s finish creating your account.
        </p>

        <FormGroup label="Email">
          <InputGroup name="email" type="text" value={email} readOnly large />
        </FormGroup>

        <FormGroup
          label="Password"
          helperText={errors.password}
          intent={errors.password ? Intent.DANGER : undefined}
        >
          <InputGroup
            name="password"
            type="password"
            placeholder="Password"
            autoFocus
            intent={errors.password ? Intent.DANGER : undefined}
            onChange={handleChange}
            value={values.password}
            autoComplete="new-passowrd"
            large
          />
        </FormGroup>

        <Button
          type="submit"
          intent={Intent.PRIMARY}
          large
          loading={isSubmitting}
        >
          Create account and log in
        </Button>

        {formError && <p className="error">{formError}</p>}
      </form>
    </AuthLayout>
  );
};

export default SignupComplete;
