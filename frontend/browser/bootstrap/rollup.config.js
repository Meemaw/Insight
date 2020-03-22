/* eslint-disable @typescript-eslint/no-var-requires */
/* eslint-disable @typescript-eslint/camelcase */
import path from 'path';

import { terser } from 'rollup-plugin-terser';

const prettier = require('rollup-plugin-prettier');

const input = path.join('src', 'index.js');
const output = path.join('dist', 'insight.js');

const plugins = [terser({ compress: false, mangle: true }), prettier()];

export default [
  {
    input,
    plugins,
    output: {
      file: output,
    },
  },
  {
    input,
    plugins: [
      ...plugins,
      {
        name: 'add-script-tag',
        renderChunk: (source, _chunkInfo, _outputOptions) => {
          return `<script>\n${source}</script>`;
        },
      },
    ],
    output: {
      file: output.replace('.js', '.html'),
    },
  },
];
