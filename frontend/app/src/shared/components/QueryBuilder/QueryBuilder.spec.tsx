import React from 'react';
import { render } from 'test/utils';
import userEvent from '@testing-library/user-event';
import { fireEvent } from '@testing-library/react';

import { Default } from './QueryBuilder.stories';

describe('<QueryBuilder />', () => {
  it('Should work for click filters', async () => {
    const {
      getByText,
      getAllByText,
      getByPlaceholderText,
      getAllByPlaceholderText,
      container,
    } = render(<Default />);

    fireEvent.click(getByText('Anything'));
    fireEvent.click(getByText('Text is exactly'));

    const input = getByPlaceholderText('Type something') as HTMLInputElement;
    userEvent.type(input, 'my-query');
    expect(input.value).toEqual('my-query');

    // add another input
    fireEvent.click(getByText('or'));
    expect(getAllByPlaceholderText('Type something').length).toEqual(2);

    // remove first input
    const deleteInputIcon = container.querySelector<SVGElement>(
      'span[role="button"] svg[title="Delete"]'
    ) as SVGElement;
    fireEvent.click(deleteInputIcon);
    expect(getAllByPlaceholderText('Type something').length).toEqual(1);

    // Add another filter
    const addFilterIcon = container.querySelector<SVGElement>(
      'button svg[title="Plus"]'
    ) as SVGElement;
    fireEvent.click(addFilterIcon);
    expect(getAllByText('Clicked').length).toEqual(2);

    // Remove a filter
    let deleteFilterIcon = container.querySelector<SVGElement>(
      'button svg[title="Delete"]'
    ) as SVGElement;
    fireEvent.click(deleteFilterIcon);
    expect(getAllByText('Clicked').length).toEqual(1);

    deleteFilterIcon = container.querySelector<SVGElement>(
      'button svg[title="Delete"]'
    ) as SVGElement;
    fireEvent.click(deleteFilterIcon);
    expect(getAllByText('Clicked').length).toEqual(1);
  });
});
