import React from 'react';
import { render, clickElement, blurElement, focusElement } from 'test/utils';
import { wait } from '@testing-library/react';

import GlobalSearch from './GlobalSearch';

describe('<GlobalSearch />', () => {
  it('should display search results via different ways of focus', async () => {
    const { getByPlaceholderText, queryAllByText, getByText } = render(
      <GlobalSearch />
    );

    const searchInput = getByPlaceholderText('Search Insights ...');

    focusElement(searchInput);
    expect(queryAllByText('item').length).toEqual(5);
    blurElement(searchInput);
    await wait(() => {
      expect(queryAllByText('item').length).toEqual(0);
    });

    clickElement(searchInput);
    expect(queryAllByText('item').length).toEqual(5);
    blurElement(searchInput);
    await wait(() => {
      expect(queryAllByText('item').length).toEqual(0);
    });

    clickElement(getByText('/'));
    expect(queryAllByText('item').length).toEqual(5);
    blurElement(searchInput);
    await wait(() => {
      expect(queryAllByText('item').length).toEqual(0);
    });
  });
});
