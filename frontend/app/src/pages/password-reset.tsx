import React from 'react';
import { NextPageContext } from 'next';
import PasswordResetApi, { PasswordResetRequestBase } from 'api/password';
import PasswordReset from 'modules/auth/components/PasswordReset';

type NonExistingPasswordResetRequest = {
  exists: false;
};

type ExistingPasswordResetRequest = PasswordResetRequestBase & {
  exists: true;
};

type Props = ExistingPasswordResetRequest | NonExistingPasswordResetRequest;

const passwordResetRequest = (
  props: Props
): props is ExistingPasswordResetRequest => {
  return props.exists === true;
};

const PasswordResetPage = (props: Props) => {
  if (!passwordResetRequest(props)) {
    return <div>Does not exist</div>;
  }

  const { email, token, org } = props;
  return <PasswordReset email={email} token={token} org={org} />;
};

PasswordResetPage.getInitialProps = async (ctx: NextPageContext) => {
  const { email, orgId: org, token } = ctx.query;

  const response = await PasswordResetApi.resetExists({
    email: email as string,
    token: token as string,
    org: org as string,
  });

  if (response.data === false) {
    return { exists: false };
  }

  return { exists: true, email, token, org };
};

export default PasswordResetPage;
