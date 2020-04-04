import { EventData } from 'backend/types';

import { BaseTransport, Status, GlobalObject } from './base';

/** `fetch` based transport */
export class FetchTranport implements BaseTransport {
  public send = (url: string, body: string) => {
    return fetch(url, {
      body,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
    });
  };

  public sendEvents = (url: string, data: EventData) => {
    return this.send(url, JSON.stringify(data)).then((response) => {
      return { status: Status.fromHttpCode(response.status) };
    });
  };

  public static isSupported = (global: GlobalObject) => {
    if (!('fetch' in global)) {
      return false;
    }

    try {
      // eslint-disable-next-line no-new
      new Headers();
      // eslint-disable-next-line no-new
      new Request('');
      // eslint-disable-next-line no-new
      new Response();
      return true;
    } catch (e) {
      return false;
    }
  };
}
