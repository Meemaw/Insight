import React, { useState, useCallback } from 'react';
import { Popover, Button, Intent } from '@blueprintjs/core';
import * as api from 'api';

import SendInviteForm from '../SendInviteForm';

type Props = {
  createInvite: typeof api.createInvite;
};

const SendInvite = ({ createInvite }: Props) => {
  const [sendingInvite, setSendingInvite] = useState(false);
  const [isOpen, setIsOpen] = useState(false);

  const togleIsOpen = useCallback(() => {
    setIsOpen(prev => !prev);
  }, []);

  const onCancel = useCallback(() => {
    setIsOpen(false);
  }, []);

  const onCreateInvite = useCallback(
    (role: api.UserRole, email: string) => {
      onCancel();
      setSendingInvite(true);
      return createInvite(role, email).finally(() => {
        setSendingInvite(false);
      });
    },
    [createInvite, onCancel]
  );

  return (
    <Popover
      isOpen={isOpen}
      position="bottom"
      onInteraction={open => {
        setIsOpen(open);
      }}
      content={
        <SendInviteForm createInvite={onCreateInvite} onCancel={onCancel} />
      }
    >
      <Button
        text="Invite teamate ..."
        icon="envelope"
        intent={Intent.PRIMARY}
        onClick={togleIsOpen}
        loading={sendingInvite}
      />
    </Popover>
  );
};

export default React.memo(SendInvite);
