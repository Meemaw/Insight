import { NextPageContext } from 'next';
import Router from 'next/router';
import SignupApi from 'api/signup';

// eslint-disable-next-line lodash/prefer-constant
const SignupCompletePage = () => null;

SignupCompletePage.getInitialProps = async (ctx: NextPageContext) => {
  const { token } = ctx.query;

  const invalidSignupRedirect = () => {
    const Location = '/invalid-sign-up';
    if (ctx.res) {
      ctx.res.writeHead(302, { Location });
      ctx.res.end();
    } else {
      Router.push(Location);
    }
  };

  if (!token) {
    invalidSignupRedirect();
  }

  const response = await SignupApi.verify(token as string);
  if (response.data === false) {
    invalidSignupRedirect();
  } else {
    const Location = '/';
    const resp = await SignupApi.complete(token as string);
    console.log(resp.headers);
    if (ctx.res) {
      ctx.res.writeHead(302, { Location });
      ctx.res.end();
    } else {
      Router.push(Location);
    }
  }
};

export default SignupCompletePage;
