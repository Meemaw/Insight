import { BrowserEvent } from 'event';

class Api {
  private readonly baseURL: string;

  private beaconSeq: number;

  constructor(baseURL: string) {
    this.baseURL = baseURL;
    this.beaconSeq = 0;
  }

  public beacon = (e: BrowserEvent[]) => {
    this.beaconSeq += 1;
    return fetch(`${this.baseURL}/v1/beacon`, {
      body: JSON.stringify({ e, s: this.beaconSeq }),
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      keepalive: true,
    });
  };
}

export default Api;
