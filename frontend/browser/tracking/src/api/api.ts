import { BrowserEvent } from 'event';

type PageDTO = {
  orgId: string;
  userId: string;
  url: string;
  width: number;
  height: number;
  screenWidth: number;
  screenHeight: number;
  referrer: string;
  doctype: string;
  compiledTs: number;
};

class Api {
  private readonly baseURL: string;

  private beaconSeq: number;

  constructor(baseURL: string) {
    this.baseURL = baseURL;
    this.beaconSeq = 0;
  }

  private json = <T extends {}>(
    input: RequestInfo,
    payload: T,
    init?: RequestInit
  ) => {
    return fetch(input, {
      body: JSON.stringify(payload),
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      ...init,
    });
  };

  // session
  // rs.fullstory.com#F30Z5#6707089592745984:5400286932205568#6e484e3a#/1602002651
  public beacon = (e: BrowserEvent[]) => {
    this.beaconSeq += 1;
    return this.json(`${this.baseURL}/v1/beacon`, { e, s: this.beaconSeq });
  };

  public page = (pageDTO: PageDTO) => {
    return this.json(`${this.baseURL}/v1/page`, pageDTO);
  };
}

export default Api;
