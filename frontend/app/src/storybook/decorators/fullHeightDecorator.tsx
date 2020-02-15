import React from 'react';
import { StoryFn, StoryContext } from '@storybook/addons';

function fullHeightDecorator<T>(storyFn: StoryFn<T>, context: StoryContext) {
  return (
    <>
      <style jsx global>
        {`
          html,
          body,
          #root {
            height: 100%;
            margin: 0px;
          }
        `}
      </style>
      {storyFn(context)}
    </>
  );
}

export default fullHeightDecorator;
