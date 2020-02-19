import { DecoratorFunction } from '@storybook/addons';

import { SetupMocks } from './useSandbox';
import mockDecorator from './mockDecorator';

export type StoryConfiguration<T> = {
  name?: string;
  decorators?: DecoratorFunction[];
  setupMocks?: SetupMocks<T>;
};

const configureStory = <T, C extends StoryConfiguration<T>>({
  setupMocks,
  decorators: passedDecorators = [],
  ...rest
}: C): C => {
  const decorators = setupMocks
    ? [...passedDecorators, mockDecorator<T, unknown>(setupMocks)]
    : passedDecorators;

  return { ...rest, decorators, setupMocks } as C;
};

export default configureStory;
