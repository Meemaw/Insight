import React from 'react';
import AppNavbar from 'modules/app/components/Navbar';
import { Classes } from '@blueprintjs/core';

type Props = React.DetailedHTMLProps<
  React.HTMLAttributes<HTMLDivElement>,
  HTMLDivElement
> & {
  pathname: string;
};

const AppLayout = ({ children, pathname, ...rest }: Props) => {
  return (
    <div
      {...rest}
      className={Classes.DARK}
      style={{ height: '100%', display: 'flex', flexDirection: 'column' }}
    >
      <AppNavbar pathname={pathname} />
      {children}
    </div>
  );
};

export default AppLayout;
