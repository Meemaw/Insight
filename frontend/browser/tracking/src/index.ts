/* eslint-disable no-console */
import Context from 'context';
import EventQueue from 'queue';
import { EventType, getEventTarget } from 'event';
import Api from 'api';
import { encodeTarget } from 'dom';

((window, location) => {
  let { href: lastLocation } = location;
  const context = new Context();
  const eventQueue = new EventQueue(context);
  const api = new Api('http://localhost:8080/v1/beacon');
  const UPLOAD_INTERVAL_MILLIS = 1000 * 30;

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

  const onUploadInterval = () => {
    const events = eventQueue.drainEvents();
    api.beacon(events);
    if (process.env.NODE_ENV !== 'production') {
      console.debug('[onUploadInterval]', [events.length]);
    }
  };

  setInterval(onUploadInterval, UPLOAD_INTERVAL_MILLIS);

  const onMouseMove = (event: MouseEvent) => {
    const target = getEventTarget(event);
    const { clientX, clientY } = event;
    const params = [clientX, clientY, ...encodeTarget(target)];
    eventQueue.enqueue(EventType.MOUSEMOVE, params);
    if (process.env.NODE_ENV !== 'production') {
      console.debug('[onClick]', params);
    }
  };

  const onClick = (event: MouseEvent) => {
    const target = getEventTarget(event);
    const { clientX, clientY } = event;
    const params = [clientX, clientY, ...encodeTarget(target)];
    eventQueue.enqueue(EventType.CLICK, params);
    if (process.env.NODE_ENV !== 'production') {
      console.debug('[onClick]', params);
    }
  };

  const onUnload = () => {
    const params = [lastLocation];
    eventQueue.enqueue(EventType.UNLOAD, params);
    api.beacon(eventQueue.events());
    if (process.env.NODE_ENV !== 'production') {
      console.debug('[unUnload]', params);
    }
  };

  const onResize = () => {
    const { innerWidth, innerHeight } = window;
    const params = [innerWidth, innerHeight];
    eventQueue.enqueue(EventType.RESIZE, params);
    if (process.env.NODE_ENV !== 'production') {
      console.debug('[onResize]', params);
    }
  };

  const onNavigationChange = () => {
    const { href: currentLocation } = location;
    if (lastLocation !== currentLocation) {
      lastLocation = currentLocation;
      const params = [currentLocation, document.title];
      eventQueue.enqueue(EventType.NAVIGATE, params);
      if (process.env.NODE_ENV !== 'production') {
        console.debug('[onNavigationChange]', params);
      }
    }
  };

  window.addEventListener('popstate', onNavigationChange);
  window.addEventListener('unload', onUnload);
  window.addEventListener('resize', onResize);
  window.addEventListener('click', onClick);
  window.addEventListener('mousemove', onMouseMove);
  // eslint-disable-next-line no-restricted-globals
})(window, location);
