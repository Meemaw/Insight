import React from 'react';
import AuthLayout from 'components/auth/AuthLayout';
import Link from 'next/link';

const InvalidSignUpPage = () => {
  return (
    <AuthLayout>
      <h2>Hmm.</h2>
      <p>It looks like this invite is invalid or has already been accepted.</p>
      <Link href="/login">
        <a
          style={{ color: 'white', textDecoration: 'underline', marginTop: 12 }}
        >
          Log in or reset your password.
        </a>
      </Link>
    </AuthLayout>
  );
};

export default InvalidSignUpPage;
