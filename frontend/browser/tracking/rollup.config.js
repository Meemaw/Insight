/* eslint-disable @typescript-eslint/camelcase */
import path from 'path';

import { terser } from 'rollup-plugin-terser';
import typescript from '@rollup/plugin-typescript';
import gzipPlugin from 'rollup-plugin-gzip';
import replace from '@rollup/plugin-replace';

const input = path.join('src', 'index.ts');

const environments = ['development', 'production'];

const config = (env) => {
  const baseName = 'insight.js';
  const fileName = env === 'production' ? baseName : `${env}.${baseName}`;

  const output = path.join('dist', fileName);

  return {
    input,
    plugins: [
      typescript(),
      replace({ 'process.env.NODE_ENV': JSON.stringify(env) }),
      terser({
        output: { comments: false },
        compress: {
          keep_infinity: true,
          pure_getters: true,
          passes: 10,
        },
        ecma: 5,
        warnings: true,
      }),

      gzipPlugin(),
    ],
    output: { file: output },
  };
};

export default environments.map((env) => config(env));
