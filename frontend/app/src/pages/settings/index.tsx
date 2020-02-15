import React from 'react';
import Router from 'next/router';
import { NextPageContext } from 'next';
import { isServer } from 'utils/next';

const Settings = () => {
  return <div />;
};

Settings.getInitialProps = (ctx: NextPageContext) => {
  const Location = '/settings/general';
  if (isServer(ctx)) {
    ctx.res.writeHead(302, { Location });
    ctx.res.end();
  } else {
    Router.push(Location);
  }

  return {};
};

export default Settings;
