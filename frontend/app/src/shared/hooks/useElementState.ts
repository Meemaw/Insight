import {
  useState,
  useRef,
  MutableRefObject,
  useCallback,
  RefObject,
} from 'react';

type UseElementStateOptions = {
  initialValue?: boolean;
  truthyEventType: 'focus' | 'mouseover';
  falsyEventType: 'blur' | 'mouseout';
};

function useElementState<T extends Element>({
  truthyEventType,
  falsyEventType,
  initialValue = false,
}: UseElementStateOptions): [
  (node: T | null) => void,
  RefObject<T | null>,
  boolean
] {
  const [value, setValue] = useState(initialValue);
  const ref = useRef<T>(null) as MutableRefObject<T>;

  const handleTruthy = () => setValue(true);
  const handleFalsy = () => setValue(false);

  const callbackRef = useCallback(
    node => {
      if (ref.current != null) {
        ref.current.removeEventListener(truthyEventType, handleTruthy);
        ref.current.removeEventListener(falsyEventType, handleFalsy);
      }

      ref.current = node;

      if (ref.current != null) {
        ref.current.addEventListener(truthyEventType, handleTruthy);
        ref.current.addEventListener(falsyEventType, handleFalsy);
      }
    },
    [truthyEventType, falsyEventType]
  );

  return [callbackRef, ref, value];
}

export default useElementState;
