import React from 'react';
import Link from 'next/link';

const FirstComponent = () => {
  return (
    <div>
      <Link href="/">
        <a>home</a>
      </Link>
      <Link href="/second">
        <a>second</a>
      </Link>
    </div>
  );
};

export default FirstComponent;
