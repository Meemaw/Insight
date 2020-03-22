/* eslint-disable @typescript-eslint/camelcase */
import path from 'path';

import { terser } from 'rollup-plugin-terser';
import typescript from '@rollup/plugin-typescript';
import gzipPlugin from 'rollup-plugin-gzip';

const input = path.join('src', 'index.ts');
const output = path.join('dist', 'insight.js');

export default {
  input,
  plugins: [
    typescript(),
    terser({ compress: true, mangle: true }),
    gzipPlugin(),
  ],
  output: {
    file: output,
  },
};
