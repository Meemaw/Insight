import { BrowserEvent } from 'event';

export type PageDTO = {
  orgId: string;
  uid?: string;
  url: string;
  width: number;
  height: number;
  screenWidth: number;
  screenHeight: number;
  referrer: string;
  doctype: string;
  compiledTs: number;
};

export type EventData = {
  e: BrowserEvent[];
  s: number;
};
