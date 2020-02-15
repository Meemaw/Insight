import React from 'react';
import { TextArea, Text } from '@blueprintjs/core';

const SetupPanel = () => {
  return (
    <div style={{ background: 'white', padding: 12 }}>
      <Text>
        Copy and paste the script below into the head of every page you wish to
        record.
      </Text>
      <TextArea
        style={{ width: '500px', background: '#F9F9F9' }}
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
    </div>
  );
};

export default SetupPanel;
