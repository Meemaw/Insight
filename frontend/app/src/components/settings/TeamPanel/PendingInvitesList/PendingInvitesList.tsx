import React from 'react';
import { InviteApi, TeamInvite } from 'api';

import InviteRow from '../InviteRow';

import { StyledPendingInviteList } from './elements';

type Props = {
  invites: TeamInvite[];
  deleteInvite: typeof InviteApi.delete;
  resendInvite: typeof InviteApi.resend;
};

const PendingInvitesList = ({ invites, deleteInvite, resendInvite }: Props) => {
  return (
    <StyledPendingInviteList>
      <li>
        <div>Pending invites</div>
        <div>Role</div>
        <div>Invited</div>
        <div />
      </li>

      {invites.map(teamInvite => {
        return (
          <li key={teamInvite.token}>
            <InviteRow
              invite={teamInvite}
              onRevoke={deleteInvite}
              onResend={resendInvite}
            />
          </li>
        );
      })}
    </StyledPendingInviteList>
  );
};

export default React.memo(PendingInvitesList);
