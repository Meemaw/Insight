import React from 'react';
import Link from 'next/link';
import { Icon } from '@blueprintjs/core';

const Logo = () => {
  return (
    <Link href="/">
      <a style={{ color: 'inherit' }}>
        <Icon icon="offline" iconSize={24} />
      </a>
    </Link>
  );
};

export default Logo;
