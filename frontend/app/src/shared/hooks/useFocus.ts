import useElementState from 'shared/hooks/useElementState';

function useFocus<T extends Element>() {
  return useElementState<T>({
    truthyEventType: 'focus',
    falsyEventType: 'blur',
  });
}

export default useFocus;
