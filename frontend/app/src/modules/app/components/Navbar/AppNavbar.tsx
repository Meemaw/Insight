import React from 'react';
import { NavbarGroup, Classes } from '@blueprintjs/core';

import Account from './Account';
import Navigation from './Navigation';
import Logo from './Logo';
import { StyledNavbar } from './elements';
import GlobalSearch from './GlobalSearch';

type Props = {
  pathname: string;
};

const AppNavbar = ({ pathname }: Props) => {
  return (
    <StyledNavbar className={Classes.DARK}>
      <NavbarGroup className="left">
        <div className="logo-container">
          <Logo />
        </div>
        <GlobalSearch />
      </NavbarGroup>

      <NavbarGroup className="center">
        <Navigation pathname={pathname} />
      </NavbarGroup>

      <NavbarGroup className="right">
        <Account />
      </NavbarGroup>
    </StyledNavbar>
  );
};

export default React.memo(AppNavbar);
