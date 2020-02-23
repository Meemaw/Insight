import { fireEvent } from '@testing-library/react';

export const typeText = (el: HTMLElement, value: string) => {
  fireEvent.change(el, { target: { value } });
};

export const focusElement = (el: HTMLElement) => {
  fireEvent.focus(el);
};

// tries to mimic the browser click, which actually focuses the DOM element -- https://github.com/testing-library/react-testing-library/issues/276
export const clickElement = (el: HTMLElement) => {
  fireEvent.click(el);
  focusElement(el);
};

export const blurElement = (el: HTMLElement) => {
  fireEvent.blur(el);
};
