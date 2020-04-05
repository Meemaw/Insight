/* eslint-disable no-console */
/* eslint-disable lodash/prefer-lodash-typecheck */
/* eslint-disable no-restricted-globals */
/* eslint-disable @typescript-eslint/camelcase */
/* eslint-disable no-underscore-dangle */

import { PageResponse } from 'backend/types';
import {
  MILLIS_IN_SECOND,
  currentTimeSeconds,
  yearFromNow,
  expiresUTC,
} from 'time';

import { Cookie, InsightIdentity } from './types';

class Identity {
  private static storageKey = '_is_uid' as const;
  private readonly _cookie: Cookie;

  constructor(cookie: Cookie) {
    this._cookie = cookie;
  }

  public static initFromCookie = (host: string, orgId: string) => {
    const cookies = document.cookie.split('; ').reduce((acc, value) => {
      const valueSplit = value.split('=');
      return { ...acc, [valueSplit[0]]: valueSplit[1] };
    }, {} as { _is_uid?: string });

    console.debug('[initFromCookie]', { cookies, host, orgId });
    let maybeCookie = cookies[Identity.storageKey];
    if (!maybeCookie) {
      try {
        maybeCookie = localStorage[Identity.storageKey];
        if (maybeCookie) {
          console.debug('Restored identity from localStorage', maybeCookie);
        }
      } catch (err) {
        // noop
      }
    } else {
      console.debug('Restored identity from cookie', maybeCookie);
    }

    const decoded = Identity.decodeIdentity(maybeCookie);
    if (decoded) {
      if (decoded.orgId === orgId) {
        console.debug('Matching orgId, setting identity', decoded);
        return new Identity(decoded);
      }
      console.debug('Unmatching identity', { decoded, orgId });
    } else {
      console.debug('Could not parse identity');
    }

    const newIdentity = {
      expiresSeconds: yearFromNow(),
      host,
      orgId,
      uid: '',
      sessionId: '',
    };

    console.debug('Created new identity', newIdentity);
    return new Identity(newIdentity);
  };

  private static decodeIdentity = (
    encoded: string | undefined
  ): InsightIdentity | undefined => {
    if (!encoded) {
      return undefined;
    }
    const [maybeIdentity, maybeExpiresSeconds] = encoded.split('/');
    const expiresSeconds = parseInt(maybeExpiresSeconds, 10);
    if (isNaN(expiresSeconds) || expiresSeconds < currentTimeSeconds()) {
      return undefined;
    }

    const identitySplit = maybeIdentity.split(/[#,]/);
    if (identitySplit.length !== 3) {
      return undefined;
    }

    const [uid, sessionId] = identitySplit[2].split(':');

    return {
      uid,
      sessionId,
      host: identitySplit[0],
      orgId: identitySplit[1],
      expiresSeconds,
    };
  };

  private encode = (expiresSeconds: number) => {
    return `${this._cookie.host}#${this._cookie.orgId}#${this._cookie.uid}:${this._cookie.sessionId}/${expiresSeconds}`;
  };

  public handleIdentity = (pageResponse: PageResponse) => {
    this._cookie.uid = pageResponse.data.uid;
    this._cookie.sessionId = pageResponse.data.sessionId;
    this.writeIdentity();
  };

  private writeIdentity = () => {
    const expiresSeconds = this._cookie.expiresSeconds as number;
    const encoded = this.encode(expiresSeconds);
    const expires = expiresUTC(MILLIS_IN_SECOND * expiresSeconds);
    this.setCookie(encoded, expires);
    try {
      localStorage[Identity.storageKey] = encoded;
    } catch (e) {
      // noop
    }
    console.debug('Wrote identity', encoded);
  };

  private setCookie = (encoded: string, expires: string) => {
    let cookie = `${Identity.storageKey}=${encoded}; domain=; Expires=${expires}; path=/; SameSite=Strict`;
    if (location.protocol === 'https:') {
      cookie += '; Secure';
    }
    document.cookie = cookie;
  };

  public uid = () => {
    return this._cookie.uid;
  };
}

export default Identity;
