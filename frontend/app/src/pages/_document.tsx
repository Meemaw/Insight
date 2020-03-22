import React from 'react';
import Document, {
  Html,
  Head,
  Main,
  NextScript,
  DocumentContext,
} from 'next/document';
import { ServerStyleSheet } from 'styled-components';

class InsightDocument extends Document {
  static async getInitialProps(ctx: DocumentContext) {
    const sheet = new ServerStyleSheet();
    const originalRenderPage = ctx.renderPage;

    try {
      ctx.renderPage = () =>
        originalRenderPage({
          enhanceApp: (App) => (props) =>
            sheet.collectStyles(<App {...props} />),
        });

      const initialProps = await Document.getInitialProps(ctx);
      return {
        ...initialProps,
        styles: (
          <>
            {initialProps.styles}
            {sheet.getStyleElement()}
          </>
        ),
      };
    } finally {
      sheet.seal();
    }
  }

  getInsightScript = () => {
    return `((s, t, e) => {
      s._i_debug = !1;
      s._i_host = 'insight.com';
      s._i_org = '<ORG>';
      s._i_ns = 'IS';
      const n = t.createElement(e);
      n.async = true;
      n.crossOrigin = 'anonymous';
      n.src = 'https://d2c0kshu2rj5p.cloudfront.net/s/insight.js';
      const o = t.getElementsByTagName(e)[0];
      o.parentNode.insertBefore(n, o);
    })(window, document, 'script');
    `;
  };

  render() {
    return (
      <Html>
        <Head>
          <style>
            {`
              html, body, #__next {
                height: 100%;
                margin: 0px;
              }
            `}
          </style>
          <link
            href="https://unpkg.com/@blueprintjs/core@3.24.0/lib/css/blueprint.css"
            rel="stylesheet"
          />
          <script
            // eslint-disable-next-line react/no-danger
            dangerouslySetInnerHTML={{ __html: this.getInsightScript() }}
          />
        </Head>

        <body>
          <Main />
          <NextScript />
        </body>
      </Html>
    );
  }
}

export default InsightDocument;
