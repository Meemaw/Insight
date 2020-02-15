import React from 'react';
import AuthLayout from 'components/auth/AuthLayout';
import { NextPageContext } from 'next';
import Router from 'next/router';
import { verifySignup, SignupRequestDTO } from 'api';
import Input from 'components/Input';
import useCompleteSignup from './useCompleteSignUp';

type Props = {
  signupRequest: SignupRequestDTO;
};

const CompleteSignupPage = ({ signupRequest }: Props) => {
  const { handleSubmit, handleChange, values, errors } = useCompleteSignup(
    signupRequest
  );

  return (
    <AuthLayout>
      <h2>Welcome back!</h2>
      <p>Let&apos;s finish creating your account.</p>

      <form
        onSubmit={handleSubmit}
        style={{
          maxWidth: '480px',
          display: 'flex',
          flexDirection: 'column',
          width: '100%',
        }}
      >
        <Input
          name="email"
          type="text"
          placeholder="Email"
          value={signupRequest.email}
          autoComplete="email"
          disabled
        />

        <Input
          name="password"
          type="password"
          placeholder="Password"
          onChange={handleChange}
          value={values.password}
          error={errors.password}
          autoComplete="off"
        />

        <button
          type="submit"
          style={{
            marginTop: '12px',
            lineHeight: 4,
            background: '#0D283F',
            color: 'white',
            border: 0,
            borderRadius: '4px',
            width: '100%',
            textTransform: 'uppercase',
          }}
        >
          Create account
        </button>
      </form>
    </AuthLayout>
  );
};

CompleteSignupPage.getInitialProps = async (ctx: NextPageContext) => {
  const { email, orgId, token } = ctx.query;

  const invalidSignupRedirect = () => {
    const Location = '/invalid-sign-up';
    if (ctx.res) {
      ctx.res.writeHead(302, { Location });
      ctx.res.end();
    } else {
      Router.push(Location);
    }
  };

  if (!email || !orgId || !token) {
    invalidSignupRedirect();
  }

  const response = await verifySignup({
    email: email as string,
    token: token as string,
    org: orgId as string,
  });

  if (response.data === false) {
    invalidSignupRedirect();
  }

  return { signupRequest: { email, token, org: orgId } };
};

export default CompleteSignupPage;
