import { withStyle } from 'baseui';

import FlexColumn from '../FlexColumn';

const VerticalAligned = withStyle(FlexColumn, {
  justifyContent: 'center',
});

export default VerticalAligned;
