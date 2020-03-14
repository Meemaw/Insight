/* eslint-disable @typescript-eslint/no-empty-function */
/* eslint-disable lodash/prefer-noop */
import '@testing-library/jest-dom/extend-expect';

// https://github.com/mui-org/material-ui/issues/15726#issuecomment-493124813
// addapted Tippy.js setup https://github.com/atomiks/tippy.js-react/blob/master/test/setup.js
window.document.createRange = () =>
  (({
    setStart: () => {},
    setEnd: () => {},
    commonAncestorContainer: window.document.createElement('div'),
  } as unknown) as Range);
