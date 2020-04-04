import { EventData } from 'backend/types';

import { BaseTransport, Status, TransportResponse } from './base';

/** `XHR` based transport */
export class XHRTransport implements BaseTransport {
  public send = (url: string, body: string) => {
    return new Promise<TransportResponse>((resolve, reject) => {
      const request = new XMLHttpRequest();

      request.onreadystatechange = () => {
        if (request.readyState !== 4) {
          return;
        }

        if (request.status === 200) {
          resolve({ status: Status.fromHttpCode(request.status) });
        }

        reject(request);
      };

      request.open('POST', url);
      request.send(body);
    });
  };

  public sendEvents = (url: string, data: EventData) => {
    return this.send(url, JSON.stringify(data));
  };
}
