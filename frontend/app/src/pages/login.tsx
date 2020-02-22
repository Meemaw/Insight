import React from 'react';
import Login from 'modules/auth/components/Login';
import { NextPageContext } from 'next';

type Props = {
  dest: string;
};

const LoginPage = ({ dest }: Props) => {
  return <Login dest={dest} />;
};

LoginPage.getInitialProps = (ctx: NextPageContext) => {
  const { dest } = ctx.query;
  return { dest };
};

export default LoginPage;
