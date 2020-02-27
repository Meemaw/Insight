import React from 'react';
import { TextArea, H3, Divider, Colors } from '@blueprintjs/core';
import styled from 'styled-components';
import SpacedBetween from 'shared/components/flex/SpacedBetween';
import Link from 'next/link';

const StyledSetupPanel = styled.div`
  padding: 32px;
  margin: 0 auto;
  max-width: 800px;
  width: 100%;
  background: ${Colors.DARK_GRAY5};

  textarea {
    width: 100%;
  }
`;

const SetupPanel = () => {
  return (
    <StyledSetupPanel>
      <SpacedBetween>
        <H3>Your recording snippet</H3>
        <div style={{ display: 'flex' }}>
          <Link href="/todo">Setup guide</Link>
          <Divider style={{ height: 12 }} />
          <Link href="/todo">Developer API</Link>
        </div>
      </SpacedBetween>
      <Divider />
      <p>
        Copy and paste the script below into the head of every page you wish to
        record.
      </p>
      <TextArea
        style={{ marginTop: 12 }}
        growVertically
        value={`<script>
((s, t, e) => {
  s._i_debug = !1;
  s._i_host = 'insight.com';
  s._i_org = '<ORG>';
  s._i_ns = 'IS';
  const n = t.createElement(e);
  n.async = true;
  n.crossOrigin = 'anonymous';
  n.src = 'https://d2c0kshu2rj5p.cloudfront.net/s/insight.js';
  const o = t.getElementsByTagName(e)[0];
  o.parentNode.insertBefore(n, o);
})(window, document, 'script');
</script>
`}
      />
    </StyledSetupPanel>
  );
};

export default SetupPanel;
