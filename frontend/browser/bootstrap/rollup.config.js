/* eslint-disable @typescript-eslint/no-var-requires */
import path from 'path';

import { terser } from 'rollup-plugin-terser';
import gzipPlugin from 'rollup-plugin-gzip';
import replace from '@rollup/plugin-replace';

const prettier = require('rollup-plugin-prettier');

const input = path.join('src', 'index.js');
const outputBaseName = 'insight.js';

const basePlugins = [terser({ compress: false, mangle: true }), prettier()];

const environments = {
  local: 'https://d1l87tz7sw1x04.cloudfront.net/s/local.insight.js',
  development: 'https://d1l87tz7sw1x04.cloudfront.net/s/development.insight.js',
  production: 'https://d1l87tz7sw1x04.cloudfront.net/s/insight.js',
};

const envConfig = (env) => {
  const baseName = outputBaseName;
  const fileName = `${env}.${baseName}`;
  const trackingScript = environments[env];

  switch (env) {
    case 'production':
      return { trackingScript, fileName: baseName };
    default:
      return { trackingScript, fileName };
  }
};

const config = (env) => {
  const { fileName, trackingScript } = envConfig(env);
  const output = path.join('dist', fileName);

  const plugins = [
    ...basePlugins,
    replace({
      'process.env.TRACKING_SCRIPT': JSON.stringify(trackingScript),
    }),
  ];

  return {
    input,
    plugins: [...plugins, gzipPlugin()],
    output: {
      file: output,
    },
  };
};

export default [
  ...Object.keys(environments).map((env) => config(env)),
  {
    input,
    plugins: [
      ...[
        ...basePlugins,
        replace({
          'process.env.TRACKING_SCRIPT': JSON.stringify(
            environments.development
          ),
        }),
      ],
      {
        name: 'add-script-tag',
        renderChunk: (source, _chunkInfo, _outputOptions) => {
          return `<script>\n${source}</script>`;
        },
      },
    ],
    output: {
      file: path.join('dist', outputBaseName.replace('.js', '.html')),
    },
  },
];
