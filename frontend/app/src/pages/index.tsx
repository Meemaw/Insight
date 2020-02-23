import React from 'react';
import authenticated from 'modules/auth/hoc/authenticated';
import AppLayout from 'modules/app/components/Layout';
import { BaseRouter } from 'next/dist/next-server/lib/router/router';

type Props = {
  url: BaseRouter;
};

const Home = ({ url }: Props) => {
  return (
    <AppLayout pathname={url.pathname}>
      <div>Home</div>
    </AppLayout>
  );
};

export default authenticated(Home);
