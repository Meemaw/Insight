import React from 'react';
import { Icon, InputGroup } from '@blueprintjs/core';
import Link from 'next/link';

const GlobalSearch = () => {
  return (
    <>
      <div style={{ margin: '0 15px 0 0' }}>
        <Link href="/">
          <a style={{ color: 'inherit' }}>
            <Icon icon="offline" iconSize={24} />
          </a>
        </Link>
      </div>
      <InputGroup
        placeholder="Search Insights ..."
        round
        leftIcon="search"
        style={{ width: '300px' }}
      />
    </>
  );
};

export default GlobalSearch;
