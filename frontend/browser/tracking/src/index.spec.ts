/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable no-console */
/* eslint-disable @typescript-eslint/camelcase */
/* eslint-disable no-underscore-dangle */
import path from 'path';
import { createServer, Server } from 'http';
import fs from 'fs';

import playwright from 'playwright';
import { yearFromNow } from 'time';

const SERVE_PORT = 5000;
const I_ORG = 'test-1';
const I_HOST = `localhost:${SERVE_PORT}`;

// TODO: resuse shared types
type PageResponse = {
  data: { uid: string; sessionId: string; pageId: string };
};

const parsePageResponse = (response: playwright.Response) => {
  return response.body().then<PageResponse>((b) => JSON.parse(String(b)));
};

// TODO: make tests much better & extend to other browsers
describe('tracking', () => {
  let server: Server;

  beforeAll(() => {
    jest.setTimeout(30000);
    const pagePath = path.join(process.cwd(), 'templates', 'index.html');
    const pageContents = String(fs.readFileSync(pagePath));
    server = createServer((_req, res) => {
      res.write(pageContents);
      res.end();
    }).listen(SERVE_PORT, () => console.log('Server up'));
  });

  afterAll(() => {
    server.close();
  });

  it('starts sending events to beacon API', async () => {
    const browser = await playwright.chromium.launch();
    const context = await browser.newContext();
    const page = await context.newPage();

    await page.goto(`http://${I_HOST}`);
    await page.evaluate(
      ({ orgID, host }) => {
        window._i_org = orgID;
        window._i_host = host;
      },
      { orgID: I_ORG, host: I_HOST }
    );

    const insightScript = path.join(process.cwd(), 'dist', 'local.insight.js');
    await page.addScriptTag({ path: insightScript });

    const response = await page.waitForResponse(
      async (resp: playwright.Response) => {
        const request = resp.request();
        const headers = request.headers() as Record<string, string>;
        return (
          resp.url() === 'http://localhost:8082/v1/sessions' &&
          resp.status() === 200 &&
          request.method() === 'POST' &&
          headers['content-type'] === 'application/json' &&
          request.resourceType() === 'fetch'
        );
      }
    );

    const {
      data: { sessionId, uid },
    } = await parsePageResponse(response);

    const storageKey = '_is_uid';
    const encodedIdentity = `${I_HOST}#${I_ORG}#${uid}:${sessionId}/${yearFromNow()}`;

    const { cookie, localStorage } = await page.evaluate(() => {
      return {
        cookie: document.cookie,
        localStorage: JSON.stringify(window.localStorage),
      };
    });

    expect(cookie).toEqual(`${storageKey}=${encodedIdentity}`);
    expect(localStorage).toEqual(
      JSON.stringify({ [storageKey]: encodedIdentity })
    );

    await browser.close();
  });
});
