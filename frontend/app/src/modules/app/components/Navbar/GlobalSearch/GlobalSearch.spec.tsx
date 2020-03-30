import React from 'react';
import {
  render,
  clickElement,
  blurElement,
  focusElement,
  pressEscape,
} from 'test/utils';
import { waitFor } from '@testing-library/react';

import GlobalSearch from './GlobalSearch';

describe('<GlobalSearch />', () => {
  it('should display search results via different ways of focus', async () => {
    const { getByPlaceholderText, queryAllByText, getByText } = render(
      <GlobalSearch />
    );

    const searchInput = getByPlaceholderText('Search Insights ...');

    // open via focus & close via blur
    focusElement(searchInput);
    expect(queryAllByText('item').length).toEqual(5);
    blurElement(searchInput);
    await waitFor(() => {
      expect(queryAllByText('item').length).toEqual(0);
    });

    // open via click & close via blur
    clickElement(searchInput);
    expect(queryAllByText('item').length).toEqual(5);
    blurElement(searchInput);
    await waitFor(() => {
      expect(queryAllByText('item').length).toEqual(0);
    });

    // open via slash & close via blur
    clickElement(getByText('/'));
    expect(queryAllByText('item').length).toEqual(5);
    blurElement(searchInput);
    await waitFor(() => {
      expect(queryAllByText('item').length).toEqual(0);
    });

    // open via click & close with escape press
    clickElement(searchInput);
    expect(queryAllByText('item').length).toEqual(5);
    pressEscape(searchInput);
    await waitFor(() => {
      expect(queryAllByText('item').length).toEqual(0);
    });
  });
});
