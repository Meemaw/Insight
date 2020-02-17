/* eslint-disable jsx-a11y/label-has-associated-control */
import React from 'react';
import Input from 'components/common/auth/Input';
import Link from 'next/link';
import AuthLayout from 'components/common/auth/Layout';

import useLogin from './useLogin';

const LoginPage = () => {
  const { handleSubmit, handleChange, values, errors, formError } = useLogin();

  return (
    <AuthLayout>
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
          onChange={handleChange}
          value={values.email}
          error={errors.email}
          autoComplete="email"
        />

        <Input
          name="password"
          type="password"
          placeholder="Password"
          onChange={handleChange}
          value={values.password}
          error={errors.password}
          autoComplete="current-passowrd"
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
          Log in
        </button>

        {formError && (
          <div
            style={{
              color: '#f8598b',
              background: 'rgba(255, 255, 255, 0.8)',
              borderRadius: '6px',
              border: '2px solid #fff',
              margin: '1rem 0',
              padding: '0.75rem',
              textAlign: 'center',
            }}
          >
            {formError}
          </div>
        )}
      </form>
      <Link href="/password-forgot">
        <a
          style={{ color: 'white', textDecoration: 'underline', marginTop: 12 }}
        >
          Forgot password?
        </a>
      </Link>
    </AuthLayout>
  );
};

export default LoginPage;
