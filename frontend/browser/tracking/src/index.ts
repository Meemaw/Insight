/* eslint-disable no-console */
import Context from 'context';
import EventQueue from 'queue';
import { EventType } from 'event';
import Api from 'api';

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

  const onUploadInterval = () => {
    const events = eventQueue.drainEvents();
    api.beacon(events);
    if (process.env.NODE_ENV !== 'production') {
      console.debug('[onUploadInterval]', { numEvents: events.length });
    }
  };

  setInterval(onUploadInterval, UPLOAD_INTERVAL_MILLIS);

  const entryTypes = ['navigation', 'resource', 'measure', 'mark'];
  observer.observe({ entryTypes });

  const onUnload = () => {
    eventQueue.enqueue(EventType.UNLOAD, [lastLocation]);
    api.beacon(eventQueue.events());
    if (process.env.NODE_ENV !== 'production') {
      console.debug(`[unUnload]: [${lastLocation}]`);
    }
  };

  const onResize = () => {
    const { innerWidth, innerHeight } = window;
    eventQueue.enqueue(EventType.RESIZE, [innerWidth, innerHeight]);
    if (process.env.NODE_ENV !== 'production') {
      console.debug(`[onResize]: [${innerWidth}, ${innerHeight}]`);
    }
  };

  const onNavigationChange = () => {
    const { href: currentLocation } = location;
    if (lastLocation !== currentLocation) {
      lastLocation = currentLocation;

      eventQueue.enqueue(EventType.NAVIGATE, [currentLocation, document.title]);
      if (process.env.NODE_ENV !== 'production') {
        console.debug(
          `[onNavigationChange]: [${currentLocation}, ${document.title}]`
        );
      }
    }
  };

  window.addEventListener('popstate', onNavigationChange);
  window.addEventListener('unload', onUnload);
  window.addEventListener('resize', onResize);
  // eslint-disable-next-line no-restricted-globals
})(window, location);
