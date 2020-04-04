/* eslint-disable no-console */
import Context from 'context';
import EventQueue from 'queue';
import { EventType, encodeEventTarget } from 'event';
import Backend from 'backend';

((window, location) => {
  let { href: lastLocation } = location;
  const context = new Context();
  const eventQueue = new EventQueue(context);
  const backend = new Backend(`${process.env.API_BASE_URL}`);
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
    if (events.length > 0) {
      backend.sendEvents(events);
      if (process.env.NODE_ENV !== 'production') {
        console.debug('[onUploadInterval]', [events.length]);
      }
    }
  };

  setInterval(onUploadInterval, UPLOAD_INTERVAL_MILLIS);

  const onMouseMove = (event: MouseEvent) => {
    const { clientX, clientY } = event;
    const params = [clientX, clientY, ...encodeEventTarget(event)];
    eventQueue.enqueue(EventType.MOUSEMOVE, params);
  };

  const onClick = (event: MouseEvent) => {
    const { clientX, clientY } = event;
    const args = [clientX, clientY, ...encodeEventTarget(event)];
    eventQueue.enqueue(EventType.CLICK, args);
    if (process.env.NODE_ENV !== 'production') {
      console.debug('[click]', args);
    }
  };

  const onUnload = () => {
    const args = [lastLocation];
    eventQueue.enqueue(EventType.UNLOAD, args);
    backend.sendEvents(eventQueue.events());
  };

  const onResize = () => {
    const { innerWidth, innerHeight } = window;
    const args = [innerWidth, innerHeight];
    eventQueue.enqueue(EventType.RESIZE, args);
    if (process.env.NODE_ENV !== 'production') {
      console.debug('[resize]', args);
    }
  };

  const onNavigationChange = () => {
    const { href: currentLocation } = location;
    if (lastLocation !== currentLocation) {
      lastLocation = currentLocation;
      const args = [currentLocation, document.title];
      eventQueue.enqueue(EventType.NAVIGATE, args);
      if (process.env.NODE_ENV !== 'production') {
        console.debug('[navigate]', args);
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
