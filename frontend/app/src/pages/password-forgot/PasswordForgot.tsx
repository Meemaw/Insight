import React from 'react';
import AuthLayout from 'components/auth/AuthLayout';
import Link from 'next/link';
import Input from 'components/Input';
import usePasswordForgot from './usePasswordForgot';

const PasswordForgot = () => {
  const { handleChange, values, errors, handleSubmit } = usePasswordForgot();

  return (
    <AuthLayout>
      <p style={{ color: '#fff' }}>
        Enter your email below and we&apos;ll send you a link to reset your
        password.
      </p>
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
          Send reset link
        </button>
      </form>

      <Link href="/login">
        <a
          style={{ color: 'white', textDecoration: 'underline', marginTop: 12 }}
        >
          Remember your password?
        </a>
      </Link>
    </AuthLayout>
  );
};

export default PasswordForgot;
