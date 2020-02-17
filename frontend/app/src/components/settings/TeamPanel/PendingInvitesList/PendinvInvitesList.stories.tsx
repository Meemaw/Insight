import React, { useState } from 'react';
import { DataResponse } from 'api';
import PendingInvitesList from './PendingInvitesList';

export default {
  title: 'Settings|Team/PendingInvitesList',
};

type Options = {
  rejecting?: boolean;
};

const usePenvingInvitesList = ({ rejecting = false }: Options = {}) => {
  const [invites, setInvites] = useState(() => [
    {
      creator: 'creator-1@gmail.com',
      token: 'token1',
      email: 'user-1@gmail.com',
      org: 'Org123',
      role: 'ADMIN',
      createdAt: Date.now() - 1000 * 60 * 60,
    } as const,
    {
      creator: 'creator-2@gmail.com',
      token: 'token2',
      email: 'user-2@gmail.com',
      org: 'Org12345',
      role: 'STANDARD',
      createdAt: Date.now(),
    } as const,
  ]);

  const resendInvite = (_emai: string) => {
    return new Promise<DataResponse<boolean>>((resolve, reject) => {
      setTimeout(rejecting ? reject : resolve, 1000);
    });
  };

  const deleteInvite = (token: string, _email: string) => {
    return new Promise<DataResponse<boolean>>((resolve, reject) => {
      setTimeout(() => {
        if (rejecting) {
          reject();
        } else {
          setInvites(prev => prev.filter(i => i.token !== token));
          resolve({ data: true });
        }
      }, 1000);
    });
  };

  return { invites, deleteInvite, resendInvite };
};

export const Base = () => {
  const props = usePenvingInvitesList();
  return <PendingInvitesList {...props} />;
};

export const Rejecting = () => {
  const props = usePenvingInvitesList({ rejecting: true });
  return <PendingInvitesList {...props} />;
};
