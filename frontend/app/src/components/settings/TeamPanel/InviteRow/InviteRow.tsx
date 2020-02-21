import React, { useState } from 'react';
import {
  ButtonGroup,
  Button,
  Intent,
  Divider,
  Popover,
  H5,
  Classes,
} from '@blueprintjs/core';
import { TeamInvite, DataResponse } from 'api';
import VerticalAligned from 'shared/components/flex/VerticalAligned';
import { formatDistance } from 'date-fns';
import { successToast, errorToast } from 'components/common/app/Toaster';

import { startCase } from '../SendInviteForm/SendInviteForm';

type Props = {
  invite: TeamInvite;
  onRevoke: (token: string, email: string) => Promise<DataResponse<boolean>>;
  onResend: (email: string) => Promise<DataResponse<boolean>>;
};

const InviteRow = ({
  invite,
  onRevoke: onRevokeProp,
  onResend: onResendProp,
}: Props) => {
  const [revoking, setRevoking] = useState(false);
  const [resending, setResending] = useState(false);

  const onResend = () => {
    setResending(true);
    onResendProp(invite.email)
      .then(() => successToast('Invite successfully resent'))
      .catch(() => errorToast('Something went wrong while resending invite'))
      .finally(() => {
        setResending(false);
      });
  };

  const onRevoke = () => {
    setRevoking(true);
    onRevokeProp(invite.token, invite.email)
      .then(() => successToast('Invite successfully revoked'))
      .catch(() => {
        errorToast('Something went wrong while revoking invite');
        setRevoking(false);
      });
  };

  return (
    <>
      <VerticalAligned>{invite.email}</VerticalAligned>
      <VerticalAligned>{startCase(invite.role)}</VerticalAligned>
      <VerticalAligned>
        {`${formatDistance(invite.createdAt, new Date())} ago`}
      </VerticalAligned>
      <ButtonGroup>
        <Popover
          content={
            <div style={{ padding: 12 }}>
              <H5>Confirm deletion</H5>
              <p>Are you sure you want to revoke this invite?</p>
              <div
                style={{
                  display: 'flex',
                  justifyContent: 'flex-end',
                  marginTop: 15,
                }}
              >
                <Button
                  style={{ marginRight: 10 }}
                  className={Classes.POPOVER_DISMISS}
                >
                  Cancel
                </Button>
                <Button
                  intent={Intent.DANGER}
                  onClick={onRevoke}
                  loading={revoking}
                >
                  Delete
                </Button>
              </div>
            </div>
          }
        >
          <Button text="Revoke" intent={Intent.PRIMARY} minimal />
        </Popover>
        <Divider />
        <Button
          text="Resend"
          intent={Intent.PRIMARY}
          minimal
          loading={resending}
          onClick={onResend}
        />
      </ButtonGroup>
    </>
  );
};

export default React.memo(InviteRow);
