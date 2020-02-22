import React from 'react';
import SsoApi from 'api/sso';
import Router from 'next/router';
import {
  Navbar,
  NavbarGroup,
  Button,
  Classes,
  Alignment,
  Menu,
  MenuItem,
  Popover,
} from '@blueprintjs/core';

const AppNavbar = () => {
  const onLogoutClick = async () => {
    await SsoApi.logout();
    Router.push('/login');
  };

  const onSettingsClick = () => {
    Router.push('/settings');
  };

  const onHomeClick = () => {
    Router.push('/');
  };

  const settingsMenu = (
    <Menu>
      <MenuItem icon="cog" text="Settings" onClick={onSettingsClick} />
      <MenuItem icon="log-out" text="Log out" onClick={onLogoutClick} />
    </Menu>
  );

  return (
    <Navbar>
      <NavbarGroup align={Alignment.LEFT}>
        <Navbar.Heading style={{ cursor: 'pointer' }} onClick={onHomeClick}>
          Insight
        </Navbar.Heading>
      </NavbarGroup>

      <NavbarGroup align={Alignment.RIGHT}>
        <Popover content={settingsMenu}>
          <Button className={Classes.MINIMAL} rightIcon="more" />
        </Popover>
      </NavbarGroup>
    </Navbar>
  );
};

export default AppNavbar;
