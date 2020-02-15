/* eslint-disable react/jsx-wrap-multilines */
import React from 'react';
import { Button, Popover, Intent, InputGroup } from '@blueprintjs/core';

const TeamPanel = () => {
  return (
    <div>
      <Popover
        position="bottom"
        content={
          <div style={{ padding: 12 }}>
            <InputGroup
              placeholder="Email address"
              autoFocus
              style={{ marginBottom: 8 }}
            />

            <Button intent={Intent.PRIMARY}>Send invitation</Button>
            <Button minimal style={{ marginLeft: 8 }}>
              Cancel
            </Button>
          </div>
        }
      >
        <Button
          text="Invite teamate..."
          icon="envelope"
          intent={Intent.PRIMARY}
        />
      </Popover>
    </div>
  );
};

export default TeamPanel;
