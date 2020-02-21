import React from 'react';
import {
  Classes,
  Intent,
  FormGroup,
  InputGroup,
  Button,
  H3,
} from '@blueprintjs/core';
import AuthLayout from 'modules/auth/components/Layout';
import { PasswordResetRequestBase } from 'api';

import usePasswordReset from './usePasswordReset';

type Props = PasswordResetRequestBase;

const PasswordReset = (props: Props) => {
  const { email } = props;
  const {
    handleSubmit,
    handleChange,
    values,
    errors,
    isSubmitting,
    formError,
  } = usePasswordReset(props);

  return (
    <AuthLayout className={Classes.DARK}>
      <form onSubmit={handleSubmit}>
        <H3 style={{ textAlign: 'center' }}>Insight</H3>

        <p style={{ textAlign: 'center', margin: '32px 0' }}>
          Reset your password.
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
          Reset password and log in
        </Button>

        {formError && <p className="error">{formError}</p>}
      </form>
    </AuthLayout>
  );
};

export default PasswordReset;
