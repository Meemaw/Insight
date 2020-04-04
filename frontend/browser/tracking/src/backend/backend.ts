/* eslint-disable no-console */
import { BrowserEvent } from 'event';

import { PageDTO } from './types';
import { BeaconTransport } from './transports/beacon';
import { BaseTransport, getGlobalObject } from './transports/base';
import { FetchTranport } from './transports/fetch';
import { XHRTransport } from './transports/xhr';

class Backend {
  private readonly transport: BaseTransport;
  private readonly beaconUrl: string;
  private readonly pageUrl: string;

  private beaconSeq: number;

  constructor(baseURL: string) {
    this.beaconUrl = `${baseURL}/v1/beacon`;
    this.pageUrl = `${baseURL}/v1/page`;
    this.beaconSeq = 0;

    const globalObject = getGlobalObject();
    if (BeaconTransport.isSupported(globalObject)) {
      this.transport = new BeaconTransport();
      if (process.env.NODE_ENV !== 'production') {
        console.debug('BeaconTransport enabled');
      }
    } else if (FetchTranport.isSupported(globalObject)) {
      this.transport = new FetchTranport();
      if (process.env.NODE_ENV !== 'production') {
        console.debug('FetchTransport enabled');
      }
    } else {
      this.transport = new XHRTransport();
      if (process.env.NODE_ENV !== 'production') {
        console.debug('XHRTransport enabled');
      }
    }
  }

  public sendEvents = (e: BrowserEvent[]) => {
    this.beaconSeq += 1;
    return this.transport.sendEvents(this.beaconUrl, { e, s: this.beaconSeq });
  };

  public page = (pageDTO: PageDTO) => {
    return this.transport.send(this.pageUrl, JSON.stringify(pageDTO));
  };
}

export default Backend;
