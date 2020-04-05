/* eslint-disable no-underscore-dangle */
/* eslint-disable no-console */
import Context from 'context';
import EventQueue from 'queue';
import {
  EventType,
  encodeEventTarget,
  BrowserEventArguments,
  dedupMouseEventSimple,
  mouseEventSimpleArgs,
  mouseEventWithTargetArgs,
} from 'event';
import Backend from 'backend';
import { PageResponse } from 'backend/types';
import Identity from 'identity';
import { MILLIS_IN_SECOND } from 'time';

declare global {
  interface Window {
    _i_org: string;
    _i_host: string;
  }
}

((window, location, compiledTs) => {
  let { href: lastLocation } = location;
  const context = new Context();
  const eventQueue = new EventQueue(context);
  const backend = new Backend(`${process.env.API_BASE_URL}`);
  const UPLOAD_INTERVAL_MILLIS = MILLIS_IN_SECOND * 10;
  const { _i_org: orgId, _i_host: host } = window;
  const identity = Identity.initFromCookie(host, orgId);

  const observer = new PerformanceObserver((performanceEntryList) => {
    performanceEntryList.getEntries().forEach((entry) => {
      eventQueue.enqueue(EventType.PERFORMANCE, [
        entry.name,
        entry.entryType,
        entry.startTime,
        entry.duration,
      ]);
    });
  });

  const entryTypes = ['navigation', 'resource', 'measure', 'mark'];
  observer.observe({ entryTypes });

  const enqueue = (
    eventType: EventType,
    args: BrowserEventArguments,
    eventName: string
  ) => {
    eventQueue.enqueue(eventType, args);
    if (process.env.NODE_ENV !== 'production') {
      console.debug(eventName, args);
    }
  };

  const onResize = () => {
    const { innerWidth, innerHeight } = window;
    const args = [innerWidth, innerHeight];
    enqueue(EventType.RESIZE, args, '[resize]');
  };

  const onNavigationChange = () => {
    const { href: currentLocation } = location;
    if (lastLocation !== currentLocation) {
      lastLocation = currentLocation;
      const args = [currentLocation, document.title];
      enqueue(EventType.NAVIGATE, args, '[navigate]');
    }
  };

  const onMouseDown = (event: MouseEvent) => {
    enqueue(EventType.MOUSEDOWN, mouseEventSimpleArgs(event), '[mousedown]');
  };

  const onMouseUp = (event: MouseEvent) => {
    enqueue(EventType.MOUSEUP, mouseEventSimpleArgs(event), '[mouseup]');
  };

  const {
    onMouseEvent: onMouseMove,
    clearMouseEvent: onMouseMoveClear,
  } = dedupMouseEventSimple(
    (event: MouseEvent, clientX: number, clientY: number) => {
      const args = [clientX, clientY, ...encodeEventTarget(event)];
      enqueue(EventType.MOUSEMOVE, args, '[mousemove]');
    }
  );

  const onClick = (event: MouseEvent) => {
    const args = mouseEventWithTargetArgs(event);
    enqueue(EventType.MOUSEMOVE, args, '[mousemove]');
  };

  window.addEventListener('popstate', onNavigationChange);
  window.addEventListener('resize', onResize);
  window.addEventListener('click', onClick);
  window.addEventListener('mousemove', onMouseMove);
  window.addEventListener('mousedown', onMouseDown);
  window.addEventListener('mouseup', onMouseUp);

  const onUnload = () => {
    const args = [lastLocation];
    eventQueue.enqueue(EventType.UNLOAD, args);
    backend.sendEvents(eventQueue.events());
  };

  const startBeaconing = (page: PageResponse) => {
    identity.handleIdentity(page);
    window.addEventListener('unload', onUnload);
    setInterval(() => {
      const events = eventQueue.drainEvents();
      if (events.length > 0) {
        backend.sendEvents(events);
        onMouseMoveClear();
        if (process.env.NODE_ENV !== 'production') {
          console.debug('[onUploadInterval]', [events.length]);
        }
      }
    }, UPLOAD_INTERVAL_MILLIS);
  };

  backend
    .page({
      orgId,
      uid: identity.uid(),
      compiledTs,
      doctype: '<!DOCTYPE html>',
      height: window.innerHeight,
      width: window.innerWidth,
      screenHeight: window.screen.height,
      screenWidth: window.screen.width,
      referrer: document.referrer,
      url: lastLocation,
    })
    .then(startBeaconing)
    .catch((error) => {
      // TODO: have some error reporting
      console.error('Something went wrong while creating page', error);
    });

  // eslint-disable-next-line no-restricted-globals
})(window, location, (process.env.COMPILED_TS as unknown) as number);
