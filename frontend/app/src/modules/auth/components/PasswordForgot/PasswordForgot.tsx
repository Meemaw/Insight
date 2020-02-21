import React from 'react';
import { Classes, H3, H5 } from '@blueprintjs/core';

import usePasswordForgot from './usePasswordForgot';
import { StyledForgotPassword } from './elements';
import PasswordForgotForm from './PasswordForgotForm';

const PasswordForgot = () => {
  const {
    handleSubmit,
    handleChange,
    values,
    errors,
    isSubmitting,
    formError,
    checkYourInbox,
  } = usePasswordForgot();

  return (
    <StyledForgotPassword className={Classes.DARK}>
      {checkYourInbox ? (
        <div style={{ width: '25%' }}>
          <H3 style={{ textAlign: 'center' }}>Insight</H3>
          <H5 style={{ textAlign: 'center' }}>Check your inbox!</H5>

          <p style={{ textAlign: 'center', margin: '32px 0' }}>
            If your email address is associated with an Insight account, you
            will be receiving a password reset request shortly.
          </p>
        </div>
      ) : (
        <PasswordForgotForm
          handleSubmit={handleSubmit}
          handleChange={handleChange}
          values={values}
          errors={errors}
          isSubmitting={isSubmitting}
          formError={formError}
        />
      )}
    </StyledForgotPassword>
  );
};

export default PasswordForgot;
