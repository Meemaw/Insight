const path = require('path');

module.exports = {
  stories: ['../src/**/*.stories.tsx'],
  addons: [
    {
      name: '@storybook/preset-typescript',
      options: {
        options: {
          tsLoaderOptions: {
            configFile: path.resolve(__dirname, './tsconfig.storybook.json'),
          },
        },
      },
    },
    '@storybook/addon-actions/register',
    '@storybook/addon-knobs/register',
    '@storybook/addon-viewport/register',
  ],
};
