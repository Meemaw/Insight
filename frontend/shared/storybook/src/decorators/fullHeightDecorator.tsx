import React from 'react';
import { StoryFn, StoryContext } from '@storybook/addons';
import { createGlobalStyle } from 'styled-components';

const FullHeightGlobalStyles = createGlobalStyle`
  html,
  body,
  #root {
    height: 100%;
    margin: 0px;
  }
`;

function fullHeightDecorator<T>(storyFn: StoryFn<T>, context: StoryContext) {
  return (
    <>
      <FullHeightGlobalStyles />
      {storyFn(context)}
    </>
  );
}

export default fullHeightDecorator;
