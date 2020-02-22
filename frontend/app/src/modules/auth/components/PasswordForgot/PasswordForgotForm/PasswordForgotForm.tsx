import React from 'react';
import { Intent, FormGroup, InputGroup, Button, H3 } from '@blueprintjs/core';
import Link from 'next/link';

import { PasswordForgotFormConfig } from '../usePasswordForgot';

type Props = Pick<
  PasswordForgotFormConfig,
  | 'handleChange'
  | 'errors'
  | 'handleSubmit'
  | 'values'
  | 'isSubmitting'
  | 'formError'
>;

const PasswordForgotForm = ({
  handleSubmit,
  errors,
  handleChange,
  values,
  isSubmitting,
  formError,
}: Props) => {
  return (
    <form onSubmit={handleSubmit}>
      <H3 style={{ textAlign: 'center' }}>Insight</H3>

      <p style={{ textAlign: 'center', margin: '32px 0' }}>
        Enter your email below and we&apos;ll send you a link to reset your
        password.
      </p>

      <FormGroup
        className="with-action"
        label="Email"
        helperText={errors.email}
        intent={errors.email ? Intent.DANGER : undefined}
      >
        <div className="action">
          <Link href="/login">
            <a>Remember password?</a>
          </Link>
        </div>

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

      <Button
        type="submit"
        intent={Intent.PRIMARY}
        large
        loading={isSubmitting}
      >
        Send reset link
      </Button>

      {formError && <p className="error">{formError}</p>}
    </form>
  );
};

export default PasswordForgotForm;
