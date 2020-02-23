import React from 'react';
import { Menu, MenuItem, Popover, Button, Classes } from '@blueprintjs/core';
import Link from 'next/link';
import SsoApi from 'api/sso';
import Router from 'next/router';

const Account = () => {
  const onLogoutClick = async () => {
    await SsoApi.logout();
    Router.push('/login');
  };

  const settingsMenu = (
    <Menu>
      <Link href="/settings">
        <MenuItem icon="cog" text="Settings" href="/settings" />
      </Link>
      <MenuItem icon="log-out" text="Log out" onClick={onLogoutClick} />
    </Menu>
  );

  return (
    <Popover content={settingsMenu}>
      <Button className={Classes.MINIMAL} rightIcon="more" />
    </Popover>
  );
};

export default React.memo(Account);
