/* eslint-disable no-console */
import { BrowserEvent } from 'event';

import { PageDTO } from './types';
import { BeaconTransport } from './transports/beacon';
import { BaseTransport } from './transports/base';
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
    if (BeaconTransport.isSupported()) {
      console.debug('BeaconTransport enabled');
      this.transport = new BeaconTransport();
    } else if (FetchTranport.isSupported()) {
      console.debug('FetchTransport enabled');
      this.transport = new FetchTranport();
    } else {
      console.debug('XHRTransport enabled');
      this.transport = new XHRTransport();
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
