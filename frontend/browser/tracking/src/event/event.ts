import { isHtmlElement } from 'dom';

export const enum EventType {
  NAVIGATE = 0,
  UNLOAD = 1,
  RESIZE = 2,
  PERFORMANCE = 3,
  CLICK = 4,
  MOUSEMOVE = 5,
}

export type BrowserEventArgument = string | number | null;
export type BrowserEventArguments = BrowserEventArgument[];

export type BrowserEvent = {
  when: number;
  kind: EventType;
  args: BrowserEventArguments;
};

export const getEventTarget = (event: MouseEvent): EventTarget | null => {
  if (event.target && event.composed) {
    if (isHtmlElement(event.target) && event.target.shadowRoot) {
      const path = event.composedPath();
      if (path.length > 0) {
        return path[0];
      }
    }
  }
  return event.target;
};
