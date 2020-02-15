import React from 'react';
import AuthLayout from 'components/auth/AuthLayout';
import Input from 'components/Input';

const SignupPage = () => {
  return (
    <AuthLayout>
      <h3>Start your free trial now.</h3>
      <p>You&apos;re minutes away from insights.</p>

      <form
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
          autoComplete="email"
        />

        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            marginTop: 12,
          }}
        >
          <Input name="firstName" type="text" placeholder="First name" />
          <Input name="lastName" type="text" placeholder="Last name" />
        </div>

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
          Get started
        </button>
      </form>
    </AuthLayout>
  );
};

export default SignupPage;
