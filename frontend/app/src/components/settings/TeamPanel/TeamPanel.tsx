import React from 'react';
import useInvites from 'hooks/useInvites';
import InviteApi from 'api/invite';
import SpacedBetween from 'shared/components/flex/SpacedBetween';
import { H3 } from '@blueprintjs/core';

import SendInvite from './SendInvite';
import PendingInvitesList from './PendingInvitesList';

const TeamPanel = () => {
  const { invites, deleteInvite, createInvite } = useInvites();

  return (
    <>
      <SpacedBetween>
        <H3>Team Settings</H3>
        <SendInvite createInvite={createInvite} />
      </SpacedBetween>
      <PendingInvitesList
        invites={invites}
        deleteInvite={deleteInvite}
        resendInvite={InviteApi.resend}
      />
    </>
  );
};

export default TeamPanel;
