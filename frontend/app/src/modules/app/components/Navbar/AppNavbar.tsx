import React from 'react';
import { Navbar, NavbarGroup, Alignment, Classes } from '@blueprintjs/core';

import Account from './Account';
import GlobalSearch from './GlobalSearch';
import Navigation from './Navigation';

type Props = {
  pathname: string;
};

const AppNavbar = ({ pathname }: Props) => {
  return (
    <Navbar className={Classes.DARK}>
      <NavbarGroup align={Alignment.LEFT}>
        <GlobalSearch />
      </NavbarGroup>

      <NavbarGroup style={{ marginLeft: 32 }}>
        <Navigation pathname={pathname} />
      </NavbarGroup>

      <NavbarGroup align={Alignment.RIGHT}>
        <Account />
      </NavbarGroup>
    </Navbar>
  );
};

export default React.memo(AppNavbar);
