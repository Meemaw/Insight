import React from 'react';
import FirstComponent from 'components/FirstComponent';
import { NextPageContext } from 'next';
import authMiddleware from 'middleware/auth';
import { UserDTO } from 'api';

type Props = {
  user: UserDTO;
};

const SecondPage = ({ user }: Props) => {
  console.log(user);
  return <FirstComponent />;
};

SecondPage.getInitialProps = async (context: NextPageContext) => {
  const user = await authMiddleware(context);

  return { user };
};

export default SecondPage;
