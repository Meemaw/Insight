import React from 'react';
import { useStyletron } from 'baseui';

const Home = () => {
  const [css] = useStyletron();
  return (
    <>
      <div className={css({ fontSize: '20px' })}>Hello world</div>
    </>
  );
};

export default Home;
