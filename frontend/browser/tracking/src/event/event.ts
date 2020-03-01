export const enum EventType {
  NAVIGATE = 0,
  UNLOAD = 1,
  RESIZE = 2,
  PERFORMANCE = 3,
}

export type BrowserEventArgument = string | number;

export type BrowserEvent = {
  when: number;
  kind: EventType;
  args: BrowserEventArgument[];
};
