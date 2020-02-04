import React from 'react';
import { render } from '@testing-library/react';

import { Base } from './FirstComponent.stories';

describe('<FirstComponent />', () => {
  it('should do that', () => {
    const { queryByText } = render(<Base />);
    expect(queryByText('Hello world')).toBeInTheDocument();
  });
});
