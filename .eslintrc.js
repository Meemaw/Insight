module.exports = {
  parser: '@typescript-eslint/parser',
  extends: [
    'eslint:recommended',
    'airbnb',
    'prettier',
    'plugin:jest/recommended',
    'plugin:lodash/recommended',
    'plugin:@typescript-eslint/recommended',
    'plugin:testing-library/react',
  ],
  plugins: [
    'prettier',
    'jest',
    'import',
    'lodash',
    'testing-library',
    'react-hooks',
  ],
  env: {
    browser: true,
    jest: true,
    node: true,
    es6: true,
  },
  rules: {
    'import/no-unresolved': ['off'],
    'import/no-extraneous-dependencies': ['off'],
    'import/prefer-default-export': ['off'],
    'import/extensions': ['off'],
    'import/order': [
      1,
      {
        'newlines-between': 'always',
        groups: [
          'builtin',
          ['external', 'internal'],
          'parent',
          ['sibling', 'index'],
        ],
      },
    ],

    'lodash/prefer-lodash-method': ['off'],

    'jest/expect-expect': ['off'],

    'react/jsx-props-no-spreading': ['off'],
    'react/jsx-filename-extension': [1, { extensions: ['.tsx'] }],
    'react/jsx-wrap-multilines': ['off'],

    'lines-between-class-members': ['off'],
    'jsx-a11y/anchor-is-valid': ['off'],

    'react-hooks/rules-of-hooks': ['error'],
    'react-hooks/exhaustive-deps': ['warn'],

    '@typescript-eslint/explicit-function-return-type': ['off'],
    '@typescript-eslint/no-unused-vars': [
      'error',
      { argsIgnorePattern: '^_', varsIgnorePattern: '^_' },
    ],
  },
};
